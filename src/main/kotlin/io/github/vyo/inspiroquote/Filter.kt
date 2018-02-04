package io.github.vyo.inspiroquote

import spark.Filter
import spark.Request
import spark.Response
import spark.Spark.*

/**
 * Filters for access control, logging and exception handling.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */
private val logger = get("filter")
private const val AUTH_HEADER_NAME = "Authorization"
private const val TOKEN_PREFIX = "Bearer "

fun apiKeyFilter() {
    before(Filter({ request: Request, _: Response ->
        if (request.headers(App.INSPIROQUOTE_API_KEY_HEADER) != App.INSPIROQUOTE_API_KEY) {
            halt(403, "forbidden")
        }
    }))
}

fun authorizationFilter() {
    before("/admin/*", { request: Request, _: Response ->
        if (request.headers(AUTH_HEADER_NAME) != "$TOKEN_PREFIX${App.INSPIROQUOTE_ADMIN_TOKEN}") {
            halt(401, "unauthorized")
        }
    })
}

fun loggingFilters() {
    before(Filter({ request: Request, _: Response -> logger.debug("request", Pair("before", request)) }))
    after(Filter({ _: Request, response: Response -> logger.debug("request", Pair("after", response)) }))
}

fun exceptionFilter() {
    exception(IllegalArgumentException::class.java, { exc, _, res ->
        res.body("bad request")
        res.status(400)
        logger.debug(exc)
    })
    exception(Exception::class.java, { exc, _, res ->
        res.body("internal server error")
        res.status(500)
        logger.error(exc)
    })
}
