package com.github.martencatcher.datamodelconverter.datamodelconverter

import com.github.martencatcher.datamodelconverter.datamodelconverter.tree.*
import java.util.*

/**
 * Created by mast1016 on 29.12.2016.
 */
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: String): Map<String, Any> {
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
                is Collection<*> -> {
                    val key = changeCounter(targetPath)
                    it.forEachIndexed { i, any -> any?.let { value -> extracted.put(key.format(i), value) } }
                }
                else -> {
                    val key = if (needCounter(targetPath)) targetPath.format(1) else targetPath
                    extracted.put(key, it)
                }
            }

            if (source.size > 1) {
                if(extracted.isNotEmpty()) {
                    val unwinding = HashMap<String, Any>()
                    for ((parent, pDoc) in extracted) {
                        for ((child, cDoc) in extract(source.drop(1), target.drop(1), pDoc)) {
                            unwinding.put(parent + child, cDoc)
                        }
                    }
                    return unwinding
                }
            }
        }

        return extracted
    }
}