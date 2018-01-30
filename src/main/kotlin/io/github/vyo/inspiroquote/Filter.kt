package io.github.vyo.inspiroquote

import io.github.vyo.twig.logger.Logger
import spark.Spark
import spark.Spark.*

/**
 * Filters for access control, logging and exception handling.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */
private val logger = Logger("filter")

fun apiKeyFilter() {
    before { request, _ ->
        if (request.headers(App.INSPIROQUOTE_API_KEY_HEADER) != App.INSPIROQUOTE_API_KEY) {
            Spark.halt(403, "forbidden")
        }
    }
}

fun loggingFilters() {
    before { request, _ -> logger.debug("request", Pair("before", request)) }
    after { _, response -> logger.debug("request", Pair("after", response)) }
}

fun exceptionFilter() {
    exception(Exception::class.java, { exc, _, res ->
        res.body("internal server error")
        res.status(500)
        logger.error(exc)
    })
}
