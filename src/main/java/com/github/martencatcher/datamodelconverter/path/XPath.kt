package com.github.martencatcher.datamodelconverter.path

class XPath(doc: Any): Path {
    private val document = when(doc) {
        is String -> doc //TODO: parse string value to xpath-ready object
        else -> doc
    }

    override fun applyPath(path: String): Any? {
        throw UnsupportedOperationException("not implemented")
    }

    override fun adjustPath(path: String) = if (path.startsWith("/")) path else "/" + path
}