package io.github.vyo.inspiroquote

import io.github.vyo.twig.logger.Logger
import spark.Filter
import spark.Request
import spark.Response
import spark.Spark.*

/**
 * Filters for access control, logging and exception handling.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */
private val logger = Logger("filter")

fun apiKeyFilter() {
    before(Filter({ request: Request, _: Response ->
        if (request.headers(App.INSPIROQUOTE_API_KEY_HEADER) != App.INSPIROQUOTE_API_KEY) {
            halt(403, "forbidden")
        }
    }))
}

fun loggingFilters() {
    before(Filter({ request: Request, _: Response -> logger.debug("request", Pair("before", request)) }))
    after(Filter({ _: Request, response: Response -> logger.debug("request", Pair("after", response)) }))
}

fun exceptionFilter() {
    exception(Exception::class.java, { exc, _, res ->
        res.body("internal server error")
        res.status(500)
        logger.error(exc)
    })
}
