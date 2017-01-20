package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

/**
 * Created by mast1016 on 29.12.2016.
 */
//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: Any): Map<String, Any> {
        val finalMappings = HashMap<String, Any>()

        for ((sourcePath, targetPath) in mappings) {
            val sourceParts = split(sourcePath)
            val targetParts = split(targetPath)

            finalMappings.putAll(extract(sourceParts, targetParts, doc))
        }

        return finalMappings
    }

    fun extract(source: List<String>, target: List<String>, doc: Any): Map<String, Any> {
        val tree = builder.buildTree(doc)

        val targetPath: String = replaceCounter(target.first())
        val expression = tree.adjustPath(source.first())

        val extracted = HashMap<String, Any>()

        tree.applyPath(expression)?.let {
            when (it) {
                is Collection<*> -> extracted.put(deleteCounter(targetPath), it)
                else -> {
                    val isArray = needCounter(targetPath)
                    val key = if (isArray) deleteCounter(targetPath) else targetPath
                    val value = if (isArray) listOf<Any>(it) else it
                    extracted.put(key, value)
                }
            }

            if (source.size > 1) {
                if(extracted.isNotEmpty()) {
                    val unwinding = HashMap<String, Any>()
                    for ((parent, pDoc) in extracted) {
                        when (pDoc) {
                            is Collection<*> -> {
                                unwinding.put(parent, pDoc.map { value -> value?.let { extract(source.drop(1), target.drop(1), value) } }.toList())
                            }
                            else -> unwinding.put(parent, extract(source.drop(1), target.drop(1), pDoc))
                        }
                    }
                    return unwinding
                }
            }
        }

        return extracted
    }
}