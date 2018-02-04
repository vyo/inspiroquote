package io.github.vyo.inspiroquote

import com.mashape.unirest.http.Unirest
import spark.Spark.halt

/**
 * Extracts text from a given base64 image string via the Google Cloud Vision API
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */

data class CloudVisionImage(val content: String)

data class CloudVisionFeature(val type: String)
data class CloudVisionRequest(val image: CloudVisionImage, val features: List<CloudVisionFeature>)
data class CloudPlatformRequestPayload(val requests: List<CloudVisionRequest>)


data class CloudVisionTextAnnotation(val locale: String, val description: String)
data class CloudVisionFullTextAnnotation(val text: String)
data class CloudVisionDocumentTextResponse(val textAnnotations: List<CloudVisionTextAnnotation>,
                                           val fullTextAnnotation: CloudVisionFullTextAnnotation)

data class CloudVisionResponsePayload(val responses: List<CloudVisionDocumentTextResponse>)

data class CloudVisionErrorResponse(val code: Int, val message: String)

private val logger = get("cloudplatform")

fun extractText(base64Image: String, apiKey: String): String {
    val cloudPlatformPayload = toJSON(
            CloudPlatformRequestPayload(
                    listOf(CloudVisionRequest(
                            CloudVisionImage(base64Image),
                            listOf(CloudVisionFeature("DOCUMENT_TEXT_DETECTION"))))))

    val request = Unirest.post("https://vision.googleapis.com/v1/images:annotate")
            .queryString("key", apiKey)
            .body(cloudPlatformPayload)
            .httpRequest
    logger.debug("cloud vision request",
            Pair("payload", CloudPlatformRequestPayload(
                    listOf(CloudVisionRequest(
                            CloudVisionImage("${base64Image.substring(0..15)}..."),
                            listOf(CloudVisionFeature("DOCUMENT_TEXT_DETECTION")))))))


    val response = request.asString()
    val json = response.body
    logger.debug("cloud vision response", Pair("payload", response))

    return when (response.status) {
        200 -> fromJSON<CloudVisionResponsePayload>(json)
                .responses[0].fullTextAnnotation.text.replace('\n', ' ')
        else -> {
            val error = fromJSON<CloudVisionErrorResponse>(json)
            logger.error("failed to extract text via Cloud Vision API",
                    Pair("status", error.code),
                    Pair("message", error.message))
            throw ServiceException(error.message)
        }
    }
}

data class CloudTranslationResponse(val translatedText: String)
data class CloudTranslationResponseContainer(val translations: List<CloudTranslationResponse>)
data class CloudTranslationResponsePayload(val data: CloudTranslationResponseContainer)

data class CloudTranslationErrorPayload(val code: Int, val message: String)
data class CloudTranslationErrorResponse(val error: CloudTranslationErrorPayload)

fun translateText(text: String, language: String, apiKey: String): String {
    val request = Unirest.post("https://translation.googleapis.com/language/translate/v2")
            .queryString("q", text)
            .queryString("source", "en")
            .queryString("target", when (language) {
                "de" -> "de"
                "ja" -> "ja"
                else -> halt(400, "unsupported target language $language")
            })
            .queryString("format", "text")
            .queryString("key", apiKey)
            .httpRequest
    logger.debug("cloud translation $request")


    val response = request.asString()
    val json = response.body
    logger.debug("cloud translation response", Pair("payload", response))

    return when (response.status) {
        200 -> fromJSON<CloudTranslationResponsePayload>(json)
                .data.translations[0].translatedText
        else -> {
            val error = fromJSON<CloudTranslationErrorResponse>(json)
            logger.error("failed to translate text via Cloud Translation API",
                    Pair("status", error.error.code),
                    Pair("message", error.error.message))
            throw ServiceException(error.error.message)
        }
    }
}
