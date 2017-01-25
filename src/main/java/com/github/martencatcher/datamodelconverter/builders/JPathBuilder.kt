package com.github.martencatcher.datamodelconverter.builders

import com.github.martencatcher.datamodelconverter.path.Empty

/**
 * Created by mast1016 on 12.01.2017.
 */

class JPathBuilder {
    fun build(doc: Any?): Any? {
        when(doc) {
            is Empty -> return null
            else -> return doc
        }
    }

    fun russianDoll(key: String, value: Any?) : Map<String, Any?> {
        return russianDoll(split(clean(key)), build(value))
    }

    fun russianDoll(key: List<String>, value: Any?) : Map<String, Any?> {
        when(key.size) {
            1 -> return mapOf(key.first() to value)
            else -> return mapOf(key.first() to russianDoll(key.drop(1), value))
        }
    }

    fun clean(path: String) = path.replace(Regex("(^[$.]*)"), "")
    fun split(path: String) = path.split(".")
}