package com.github.martencatcher.datamodelconverter.path

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath

class JPath(doc: Any) : Path {
    private val document = when(doc) {
        is String -> Configuration.defaultConfiguration().jsonProvider().parse(doc)
        else -> doc
    }

    override fun applyPath(path: String): Any? {
        try {
            return when(document) {
                is NumeratedMonad -> {
                    val result = JsonPath.read<Any>(document.value, path, null)
                    when(result) {
                        is Collection<*> -> result.mapIndexed { index: Int, any: Any? -> NumeratedMonad(listOf(index) + document.index, any) }
                        else -> NumeratedMonad(document.index, result)
                    }
                }
                else -> JsonPath.read<Any>(document, path, null)
            }
        } catch (e: Exception) {
            //TODO: log
            return null
        }
    }

    override fun adjustPath(path: String) = if (path.startsWith("$")) path else "$" + path
}