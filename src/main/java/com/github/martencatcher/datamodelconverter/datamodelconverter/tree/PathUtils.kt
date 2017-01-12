package com.github.martencatcher.datamodelconverter.datamodelconverter.tree

import java.util.*

/**
 * Created by mast1016 on 10.01.2017.
 */
fun changeCounter(path: String): String {
    return path.replace(Regex("\\[.*\\]"), "") + "[%d]"
}

fun replaceCounter(path: String): String {
    return path.replace(Regex("\\[.*\\]"), "[%d]")
}

fun deleteCounter(path: String): String {
    return path.replace(Regex("\\[.*\\]"), "")
}

fun needCounter(path: String): Boolean {
    return path.endsWith("]")
}

fun split(path: String): List<String> {
    val result = ArrayList<String>()
    val buffer = StringBuilder()
    var count: Int = 0
    for (symbol in path) {
        buffer.append(symbol)
        when (symbol) {
            '[' -> {
                count++
            }
            ']' -> {
                count--
                when (count) {
                    0 -> {
                        result.add(buffer.toString())
                        buffer.setLength(0)
                    }
                    -1 -> {
                        throw RuntimeException("Invalid path expression, wrong symbol ']'")
                        //TODO: log
                    }
                }
            }
        }
    }

    if (count > 0) {
        throw RuntimeException("Invalid path expression, wrong symbol '['")
        //TODO: log
    }

    if (buffer.isNotEmpty()) {
        result.add(buffer.toString())
    }

    return result
}