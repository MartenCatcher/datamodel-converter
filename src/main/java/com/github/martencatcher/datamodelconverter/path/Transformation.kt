package com.github.martencatcher.datamodelconverter.path

import com.github.martencatcher.datamodelconverter.IgnoreException
import org.luaj.vm2.LuaBoolean
import javax.script.ScriptEngineManager

open class Transformation(val condition: String?, val expression: String?)

class Rule(val sourcePath: String?, val targetPath: String, condition: String?, expression: String?): Transformation(condition, expression)

object Transformer {
    val sem = ScriptEngineManager()

    fun transform(condition: String?, expression: String?, found: Any?): Any? {

        val data = (found as? NumeratedMonad)?.extract()?.first ?: found
        val array = (found as? NumeratedMonad)?.extract()?.second?.toIntArray() ?: emptyArray<Int>()

        val checked = condition?.let {
            val e = sem.getEngineByExtension(".lua")
            e.put("value", data)
            e.put("index", array)
            e.put("customer", "user2")
            val res = e.eval(condition)
            (res as? LuaBoolean)?.v ?: true
        } ?: true

        if (checked) {
            expression?.let {
                val e = sem.getEngineByExtension(".lua")
                e.put("value", data)
                e.put("index", array)
                e.put("customer", "user2")
                val res = e.eval(expression)
                return res
            }
        } else {
            throw IgnoreException()
        }

        return data
    }
}