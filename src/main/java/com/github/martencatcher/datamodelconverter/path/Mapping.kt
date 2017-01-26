package com.github.martencatcher.datamodelconverter.path

import com.github.martencatcher.datamodelconverter.IgnoreException
import org.luaj.vm2.LuaBoolean
import javax.script.ScriptEngineManager

/**
 * Created by mast1016 on 20.01.2017.
 */
open class Leaf(val targetPath: String,
                val expression: String?,
                val condition: String?) {

    val sem = ScriptEngineManager()

    fun apply(found: Any): Any? {
        val transformed = when(found) {
            is Collection<*> -> found.filterNotNull().mapNotNull { transform(it) }
            else -> transform(found)
        }

        return if(needCounter(targetPath) && transformed != null) transformed as? Collection<*> ?: listOf(transformed) else transformed
    }

    fun transform(found: Any): Any? {
        val checked = condition?.let {
            val e = sem.getEngineByExtension(".lua")
            e.put("value", found)
            e.put("customer", "user2")
            val res = e.eval(condition)
            (res as? LuaBoolean)?.v ?: true
        } ?: true

        if(checked) {
            expression?.let {
                val e = sem.getEngineByExtension(".lua")
                e.put("value", found)
                e.put("customer", "user2")
                val res = e.eval(expression)
                return res
            }
        } else {
            throw IgnoreException()
        }

        return found
    }
}

class Branch(targetPath: String,
             expression: String?,
             condition: String?,
             var mappings: MutableMap<String, List<Leaf>>) : Leaf(targetPath, expression, condition)