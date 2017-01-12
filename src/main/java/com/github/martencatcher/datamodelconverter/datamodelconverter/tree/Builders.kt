package com.github.martencatcher.datamodelconverter.datamodelconverter.tree

import java.util.*

/**
 * Created by mast1016 on 11.01.2017.
 */

abstract class TreeBuilder {
    private val treeHolder: MutableMap<Any, Tree> = HashMap()

    fun buildTree(doc: Any): Tree {
        val tree = build(doc)
        if(!treeHolder.containsKey(doc)) {
            treeHolder.put(doc, tree)
        }

        return tree
    }

    abstract protected fun build(doc: Any): Tree
}

class JsonTreeBuilder : TreeBuilder() {
    override fun build(doc: Any): Tree = JsonTree(doc)
}

class XmlTreeBuilder : TreeBuilder() {
    override fun build(doc: Any): Tree = XmlTree(doc)
}