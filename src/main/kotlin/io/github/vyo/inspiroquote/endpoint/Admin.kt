package io.github.vyo.inspiroquote.endpoint

import io.github.vyo.inspiroquote.exists
import io.github.vyo.inspiroquote.getAll
import io.github.vyo.inspiroquote.toJSON
import io.github.vyo.twig.logger.Level
import spark.Spark.*

/**
 * All available admin endpoints.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 03.02.18.
 */

private val logger = io.github.vyo.inspiroquote.get("admin")

data class LoggerInfo(val name: String, val level: String)

fun setLogLevel() {
    post("admin/log/:logger/:level", { req, res ->
        val loggerName = req.params("logger")
                ?: throw halt(400, "missing required path parameter 'logger'")

        val logger = if (exists(loggerName)) {
            io.github.vyo.inspiroquote.get(loggerName)
        } else {
            throw halt(400, "invalid logger $loggerName")
        }
        val logLevel = Level.valueOf(req.params("level"))

        logger.level = logLevel

        res.type("application/json")

        return@post LoggerInfo(logger.caller.toString(), logger.level.name)
    }, { it -> toJSON(it) })
}

fun getLoggerInfo() {
    get("admin/log", { _, res ->

        res.type("application/json")

        return@get getAll()
                .map { LoggerInfo(it.caller.toString(), logger.level.name) }
    }, { it -> toJSON(it) })

    get("admin/log/:logger", { req, res ->
        val loggerName = req.params("logger")
                ?: throw halt(400, "missing required path parameter 'logger'")

        val logger = if (exists(loggerName)) {
            io.github.vyo.inspiroquote.get(loggerName)
        } else {
            throw halt(400, "invalid logger $loggerName")
        }

        res.type("application/json")

        return@get LoggerInfo(logger.caller.toString(), logger.level.name)
    }, { it -> toJSON(it) })
}
