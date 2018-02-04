package io.github.vyo.inspiroquote

import io.github.vyo.twig.logger.Logger

/**
 * Simple logging service abstraction
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 04.02.18.
 */
private val loggers = HashMap<String, Logger>()

/**
 * returns the [Logger] as requested by name; if such a [Logger] does not yet
 * exist it will be created first
 */
fun get(byName: String): Logger {
    val logger = loggers.get(byName)
    return when (logger) {
        is Logger -> logger
        else -> {
            loggers.put(byName, Logger(byName))
            get(byName)
        }
    }
}

/**
 * returns all currently registered loggers
 */
fun getAll(): List<Logger> {
    return ArrayList(loggers.values)
}

/**
 * returns whether the requested [Logger] exists
 */
fun exists(logger: String): Boolean {
    return loggers.containsKey(logger)
}
