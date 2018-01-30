package io.github.vyo.inspiroquote

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

/**
 * JSON transformation helpers
 *
 * Created by Manuel Weidmann <manuel.weidmann@7p-group.com> on 25.01.18.
 */

val gson: Gson = GsonBuilder().create()

/**
 * this little piece of magic provides some nice type inference info to the compiler
 *
 * courtesy of [StackOverflow](http://stackoverflow.com/a/33420043)
 */
inline fun <reified T> fromJSON(json: String): T = gson.fromJson<T>(json, object : TypeToken<T>() {}.type)

val toJSON = { obj: Any -> gson.toJson(obj) }
