package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: Any): Any {
        val preparedMappings = HashMap<String, List<Leaf>>()

        for ((sourcePath, targetPath) in mappings) {
            val sourceParts = splitPath(sourcePath)
            val targetParts = splitPath(targetPath)

            mergePaths(preparedMappings, sourceParts, targetParts)
        }

        val targets = preparedMappings.map { mappings -> extract(mappings.key, mappings.value, doc) }.toList()
        val unwrapped = if (targets.size == 1) targets.first() else targets
        return cleanKeys(unwrapped) ?: mapOf<String, Any>()
    }

    // 1. prepare

    fun mergePaths(tree: MutableMap<String, List<Leaf>>, source: List<String>, target: List<String>): MutableMap<String, List<Leaf>> {
        if (source.size != target.size || source.isEmpty()) {
            throw IllegalArgumentException("Array with different sizes or empty doesn't support: source $source, target: $target")
        }

        val leafs: MutableList<Leaf> = if (tree.containsKey(source.first())) tree[source.first()] as MutableList<Leaf> else ArrayList<Leaf>()

        if (source.size == 1) {  //TODO: in case with exist leaf need to throw Exception
            leafs.add(Leaf(target.first(), null, null))
        } else {
            val leaf = leafs.firstOrNull { leaf -> leaf.targetPath == target.first() }
            if (leaf == null) {
                leafs.add(Branch(target.first(), null, null, mergePaths(HashMap<String, List<Leaf>>(), source.drop(1), target.drop(1))))
            } else {
                when (leaf) {
                    is Branch -> leaf.mappings = mergePaths(leaf.mappings, source.drop(1), target.drop(1))
                    is Leaf -> throw PathException("Duplicate path!")
                }
            }
        }

        tree.put(source.first(), leafs)
        return tree
    }

    // 2. construct

    fun extract(sourcePath: String, leafs: List<Leaf>, doc: Any): Map<String, Any> {
        val tree = builder.buildTree(doc)
        val expression = tree.adjustPath(sourcePath)

        tree.applyPath(expression)?.let { return extract(leafs, it) }

        return mapOf()
    }

    fun extract(leafs: List<Leaf>, found: Any): Map<String, Any> {
        val obj = leafs.map { leaf ->
            val isArray = needCounter(leaf.targetPath)
            val key = if (isArray) deleteCounter(leaf.targetPath) else leaf.targetPath
            val value = when (leaf) {
                is Branch -> {
                    (found as? Collection<*>)
                            ?.filterNotNull()
                            ?.map { element ->
                                leaf.mappings
                                        .map { mapping -> extract(mapping.key, mapping.value, element) }
                                        .flatMap { map -> map.entries }
                                        .map { entity -> Pair(entity.key, entity.value) }
                                        .toMap()
                            } ?: leaf.mappings.map { mapping -> extract(mapping.key, mapping.value, found) }
                }
                is Leaf -> if (isArray) found as? Collection<*> ?: listOf<Any>(found) else found
                else -> throw PathException("Unknown tree element type!")
            }
            Pair(key, value)
        }.toMap()

        return obj
    }

    // 3. clean

    fun clean(path: String) = path.replace(Regex("(^[$.]*)"), "")
    fun split(path: String) = path.split(".")

    fun cleanKeys(doc: Any?): Any? {
        return when (doc) {
            is Map<*, *> -> {
                doc.map { element -> wrap(clean(element.key as String), cleanKeys(element.value)) }.toMap()
            }
            is Collection<*> -> doc.filterNotNull().map { cleanKeys(it) }
            else -> doc
        }
    }

    fun wrap(key: String, value: Any?) : Pair<String, Any?> {
        val parts = split(key)
        return if(parts.size == 1) {
            Pair(key, value)
        } else {
            return Pair(parts.first(), wrap(parts.drop(1), value))
        }
    }

    fun wrap(key: List<String>, value: Any?) : Map<String, Any?> {
        return when(key.size) {
            1 -> mapOf(key.first() to value)
            else -> mapOf(key.first() to wrap(key.drop(1), value))
        }
    }
}