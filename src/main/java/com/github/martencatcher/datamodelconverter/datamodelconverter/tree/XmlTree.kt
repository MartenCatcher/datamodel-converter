package com.github.martencatcher.datamodelconverter.datamodelconverter.tree

/**
 * Created by mast1016 on 09.01.2017.
 */
class XmlTree(doc: Any): Tree {
    private val document = when(doc) {
        is String -> doc //TODO: parse string value to xpath-ready object
        else -> doc
    }

    override fun applyPath(path: String): Any? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun adjustPath(path: String) = if (path.startsWith("$")) path else "$" + path
}