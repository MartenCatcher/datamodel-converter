package com.github.martencatcher.datamodelconverter.tree

import com.jayway.jsonpath.Configuration
import com.jayway.jsonpath.JsonPath

/**
 * Created by mast1016 on 09.01.2017.
 */
class JsonTree(doc: Any) : Tree {
    private val document = when(doc) {
        is String -> Configuration.defaultConfiguration().jsonProvider().parse(doc)
        else -> doc
    }

    override fun applyPath(path: String): Any? {
        try {
            return JsonPath.read<Any>(document, path, null)
        } catch (e: Exception) {
            //TODO: log
            return null
        }
    }

    override fun adjustPath(path: String) = if (path.startsWith("$")) path else "$" + path
}