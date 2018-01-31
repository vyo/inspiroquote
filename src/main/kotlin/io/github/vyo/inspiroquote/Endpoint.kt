package io.github.vyo.inspiroquote

import io.github.vyo.twig.logger.Logger
import spark.Spark.get
import spark.Spark.halt

/**
 * All available InspiroQuote endpoints.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */

private val logger = Logger("endpoint")

data class InspiroQuote(val quote: String, val id: String)

fun inspiration() {
    get("inspiration", { req, res ->
        val language = req.queryParamOrDefault("language", "en")

        val (base64Image, imageID) = retrieveQuoteImage()
        val quote = extractText(base64Image, App.GOOGLE_API_KEY)
        logger.info("generated quote", Pair("id", imageID), Pair("text", quote))

        res.type("application/json")

        return@get when (language) {
            "", "en" -> InspiroQuote(quote, imageID)
            "de" -> {
                val translation = translateText(quote, "de", App.GOOGLE_API_KEY)
                logger.info("translated quote", Pair("id", imageID), Pair("text", translation))
                return@get InspiroQuote(translation, imageID)
            }
            else -> halt(400, "unsupported target language $language}")
        }
    }, { it -> toJSON(it) })
}
