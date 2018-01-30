package io.github.vyo.inspiroquote

import com.mashape.unirest.http.Unirest
import io.github.vyo.twig.logger.Logger

/**
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

private val logger = Logger("cloudvision")

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
    logger.debug(response)

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
