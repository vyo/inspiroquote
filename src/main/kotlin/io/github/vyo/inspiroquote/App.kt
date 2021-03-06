package io.github.vyo.inspiroquote

import io.github.vyo.inspiroquote.endpoint.getLoggerInfo
import io.github.vyo.inspiroquote.endpoint.inspiration
import io.github.vyo.inspiroquote.endpoint.setLogLevel
import spark.Spark.port

/**
 * InspiroQuote RESTful-ish app to retrieve InspiroBot quotes as machine readable text.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */

object App {

    private val PORT = System.getenv("PORT")
            ?: throw InitialisationException("no PORT variable defined")
    val INSPIROQUOTE_API_KEY = System.getenv("INSPIROQUOTE_API_KEY")
            ?: throw InitialisationException("no InspiroQuote API key defined")
    val INSPIROQUOTE_ADMIN_TOKEN = System.getenv("INSPIROQUOTE_ADMIN_TOKEN")
            ?: throw InitialisationException("no InspiroQuote admin token defined")
    val GOOGLE_API_KEY = System.getenv("GOOGLE_API_KEY")
            ?: throw InitialisationException("no Google API key defined")

    const val INSPIROQUOTE_API_KEY_HEADER = "x-inspiroquote-apikey"
    private val logger = get("app")

    @JvmStatic
    fun main(args: Array<String>) {

        val port = try {
            PORT.toInt()
        } catch (e: NumberFormatException) {
            throw InitialisationException("failed to parse PORT env var: $PORT", e)
        }

        logger.info("starting on port $port")
        port(port)

        logger.info("setting up API key filter")
        apiKeyFilter()
        logger.info("setting up authorization filter")
        authorizationFilter()
        logger.info("setting up logging filters")
        loggingFilters()
        logger.info("setting up exception filter")
        exceptionFilter()

        logger.info("setting up inspiration endpoint")
        inspiration()

        logger.info("setting up admin endpoints")
        getLoggerInfo()
        setLogLevel()

        logger.info("startup complete")
    }
}