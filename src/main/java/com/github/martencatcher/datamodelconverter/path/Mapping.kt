package com.github.martencatcher.datamodelconverter.path

open class Leaf(val targetPath: String,
                val expression: String?,
                val condition: String?) {

    val t = Transformer

    fun apply(found: Any): Any? {
        val transformed = when(found) {
            is Collection<*> -> found.filterNotNull().mapNotNull { t.transform(condition, expression, it) }
            else -> t.transform(condition, expression, found)
        }

        return if(needCounter(targetPath) && transformed != null) transformed as? Collection<*> ?: listOf(transformed) else transformed
    }
}

class Branch(targetPath: String,
             expression: String?,
             condition: String?,
             var mappings: MutableMap<String, List<Leaf>>) : Leaf(targetPath, expression, condition)

class NumeratedMonad(val index: List<Int>, val value: Any?) {
    fun extract(): Pair<Any?, List<Int>> = when(value) {
        is NumeratedMonad -> {
            val stored = value.extract()
            stored.first to index + stored.second
        }
        else -> value to index
    }
}