package com.github.martencatcher.datamodelconverter.path

import java.util.*

/**
 * Created by mast1016 on 11.01.2017.
 */

abstract class TreeBuilder {
    private val pathHolder: MutableMap<Any, Path> = HashMap()

    fun buildTree(doc: Any): Path {
        val tree = build(doc)
        if(!pathHolder.containsKey(doc)) {
            pathHolder.put(doc, tree)
        }

        return tree
    }

    abstract protected fun build(doc: Any): Path
}

class JsonTreeBuilder : TreeBuilder() {
    override fun build(doc: Any): Path = JPath(doc)
}

class XmlTreeBuilder : TreeBuilder() {
    override fun build(doc: Any): Path = XPath(doc)
}