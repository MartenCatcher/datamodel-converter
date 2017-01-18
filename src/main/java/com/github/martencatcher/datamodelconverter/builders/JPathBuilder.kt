package com.github.martencatcher.datamodelconverter.builders

import java.util.*

/**
 * Created by mast1016 on 12.01.2017.
 */

class JPathBuilder {
    fun build(paths: Map<String, Any>): Map<String, Any> {
        val document = HashMap<String, Any>()
        for ((path, value) in paths) {
            createNode(split(clean(path)), document, value)
        }

        return document
    }

    fun createNode(path: List<String>, document: MutableMap<String, Any>, value: Any): MutableMap<String, Any> {
        when (path.size.compareTo(1)) {
            0 -> {
                when (value) {
                    is Collection<*> -> {
                        val collection = (document[path.first()] ?: ArrayList<Any>()) as MutableList<Any>
                        value.forEachIndexed {
                            index, element -> run {
                                when (element) {
                                    is Map<*, *> -> {
                                        val saved = if(collection.size <= index) {
                                            HashMap<String, Any>()
                                        } else {
                                            (collection[index] ?: HashMap<String, Any>()) as MutableMap<String, Any>
                                        }
                                        collection.add(index, enrichNode((element as Map<String, Any>).entries, saved))
                                    }
                                    else -> element?.let { collection.add(index, element) }
                                }
                            }
                        }
                        document.put(path.first(), collection)
                    }
                    else -> document.put(path.first(), value)
                }
            }
            1 -> {
                val doc = (document[path.first()] ?: HashMap<String, Any>()) as MutableMap<String, Any>
                document.put(path.first(), createNode(path.drop(1), doc, value))
            }
        }

        return document
    }

    fun enrichNode(new: Set<Map.Entry<String, Any>>, exist: MutableMap<String, Any>): MutableMap<String, Any> {
        when(new.size) {
            0 -> {
                return exist
            }
            else -> {
                val set = new as MutableSet<Map.Entry<String, Any>>
                return enrichNode(set.drop(1).toSet(), createNode(split(clean(set.first().key)), exist, set.first().value))
            }
        }
    }

    fun clean(path: String) = path.replace(Regex("(^[$.]*)"), "")
    fun split(path: String) = path.split(".")
}