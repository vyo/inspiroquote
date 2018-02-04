package io.github.vyo.inspiroquote;

import com.mashape.unirest.http.Unirest
import com.mashape.unirest.http.utils.Base64Coder
import java.io.ByteArrayOutputStream
import java.net.URL


/**
 * Retrieves quote images from InspiroBot as base64 strings; also returns
 * image ID/path.
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 30.01.18.
 */

private val logger = get("inspirobot")

fun retrieveQuoteImage(): Pair<String, String> {
    val imageURL = Unirest.get("http://inspirobot.me/api?generate=true")
            .asString().body
    val imageID = URL(imageURL).path

    val contentLength: Int = Unirest.head(imageURL)
            .asString()
            .headers["Content-Length"]?.get(0)?.toIntOrNull() ?: "-1".toInt()

    logger.debug(imageURL, Pair("content length", contentLength))

    val outputStream = ByteArrayOutputStream(1024)

    Unirest.get(imageURL)
            .asBinary()
            .rawBody
            .copyTo(out = outputStream)

    val data = outputStream.toByteArray()
    return Pair(
            Base64Coder.encode(data)
                    .fold("", { acc, c -> "$acc$c" }),
            imageID)
}
