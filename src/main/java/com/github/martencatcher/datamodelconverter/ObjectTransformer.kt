package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.exceptions.PathException
import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

/**
 * Created by mast1016 on 29.12.2016.
 */
//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: Any): Map<String, Any> {
        val finalMappings = HashMap<String, Any>()
        val preparedMappings = HashMap<String, List<Leaf>>()

        for ((sourcePath, targetPath) in mappings) {
            val sourceParts = split(sourcePath)
            val targetParts = split(targetPath)

            mergePaths(sourceParts, targetParts, preparedMappings)
        }

        for((source, target) in preparedMappings) {
            finalMappings.putAll(extract(source, target, doc))
        }

        return finalMappings
    }

    fun mergePaths(source: List<String>, target: List<String>, tree: MutableMap<String, List<Leaf>>): MutableMap<String, List<Leaf>> {
        if (source.size != target.size || source.isEmpty()) {
            throw IllegalArgumentException("Array with different sizes doesn't support: source $source, target: $target")
        }

        val leafs: MutableList<Leaf> = if (tree.containsKey(source.first())) tree[source.first()] as MutableList<Leaf> else ArrayList<Leaf>()

        if (source.size == 1) {  //TODO: in case with exist leaf need to throw Exception
            leafs.add(Leaf(target.first(), null, null))
        } else {
            val leaf = leafs.firstOrNull { leaf -> leaf.targetPath == target.first() }
            if (leaf == null) {
                leafs.add(Branch(target.first(), null, null, mergePaths(source.drop(1), target.drop(1), HashMap<String, List<Leaf>>())))
            } else {
                when (leaf) {
                    is Branch -> {
                        leaf.mappings = mergePaths(source.drop(1), target.drop(1), leaf.mappings)
                    }
                    is Leaf -> { /*TODO: log & throw*/
                        throw PathException("Duplicate path!")
                    }
                }
            }
        }

        tree.put(source.first(), leafs)
        return tree
    }

    fun extract(sourcePath: String, leafs: List<Leaf>, doc: Any): Map<String, Any> {
        val tree = builder.buildTree(doc)
        val expression = tree.adjustPath(sourcePath)

        val extracted = HashMap<String, Any>()

        tree.applyPath(expression)?.let { found ->
            leafs.forEach { leaf ->
                when(leaf) {
                    is Branch -> {

                    }
                    is Leaf -> {

                    }
                }

            }



            when (found) {
                is Collection<*> -> {
                    leafs.forEach { leaf ->
                        extracted.put(deleteCounter(leaf), it)
                    }
                }
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