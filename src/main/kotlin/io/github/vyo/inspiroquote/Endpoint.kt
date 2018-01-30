package io.github.vyo.inspiroquote

import io.github.vyo.twig.logger.Logger
import spark.Spark.get

/**
 * All available InspiroQuote endpoints.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */

private val logger = Logger("endpoint")

data class InspiroQuote(val quote: String, val id: String)

fun inspiration() {
    get("inspiration", { _, res ->

        val (base64Image, imageID) = retrieveQuoteImage()
        val quote = extractText(base64Image, App.GOOGLE_API_KEY)
        logger.info("generated quote", Pair("id", imageID), Pair("text", quote))

        res.type("application/json")

        return@get InspiroQuote(quote, imageID)
    }, { it -> toJSON(it) })
}