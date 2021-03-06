package com.github.martencatcher.datamodelconverter.path

import com.github.martencatcher.datamodelconverter.PathException
import java.util.*

fun deleteCounter(path: String): String {
    return path.replace(Regex("\\[.*\\]"), "")
}

fun needCounter(path: String): Boolean {
    return path.endsWith("]")
}

fun splitPath(path: String): List<String> {
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
                        throw PathException("Invalid path expression, wrong symbol ']'")
                        //TODO: log
                    }
                }
            }
        }
    }

    if (count > 0) {
        throw PathException("Invalid path expression, wrong symbol '['")
        //TODO: log
    }

    if (buffer.isNotEmpty()) {
        result.add(buffer.toString())
    }

    return result
}