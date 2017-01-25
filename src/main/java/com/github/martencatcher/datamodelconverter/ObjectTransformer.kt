package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun transform(doc: Any): Any {
        val preparedMappings = HashMap<String, List<Leaf>>()

        for ((sourcePath, targetPath) in mappings) {
            mergePaths(preparedMappings, splitPath(sourcePath), splitPath(targetPath))
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
        return tree.applyPath(expression)?.let { extract(leafs, it) } ?: mutableMapOf<String, Any>()
    }

    fun extract(leafs: List<Leaf>, found: Any): Map<String, Any> {
        return leafs.map { leaf ->
            val isArray = needCounter(leaf.targetPath)
            val key = if (isArray) deleteCounter(leaf.targetPath) else leaf.targetPath
            val value = when (leaf) {
                is Branch -> {
                    (found as? Collection<*>)
                            ?.filterNotNull()
                            ?.map { element ->
                                leaf.mappings
                                        .flatMap { mapping -> extract(mapping.key, mapping.value, element).entries }
                                        .fold(mutableMapOf<String, Any>()) { m, it -> m.put(it.key, it.value); m }
                            } ?: leaf.mappings.map { mapping -> extract(mapping.key, mapping.value, found) }
                }
                is Leaf -> if (isArray) found as? Collection<*> ?: listOf<Any>(found) else found
                else -> throw PathException("Unknown tree element type!")
            }
            (key to value)
        }.toMap()
    }

    // 3. clean

    fun clean(path: String) = path.replace(Regex("(^[$.]*)"), "")
    fun split(path: String) = path.split(".")

    fun cleanKeys(doc: Any?): Any? {
        return when (doc) {
            is Map<*, *> -> {
                val accumulator = HashMap<String, Any?>()
                doc.map { element -> wrap(clean(element.key as String), cleanKeys(element.value)) }
                        .forEach { merge(accumulator, it) }
                accumulator
            }
            is Collection<*> -> doc.filterNotNull().map { cleanKeys(it) }
            else -> doc
        }
    }

    fun wrap(key: String, value: Any?): Pair<String, Any?> {
        val parts = split(key)
        return if (parts.size == 1) {
            key to value
        } else {
            parts.first() to wrap(parts.drop(1), value)
        }
    }

    fun wrap(key: List<String>, value: Any?): Map<String, Any?> {
        return if (key.size == 1) {
            mutableMapOf(key.first() to value)
        } else {
            mutableMapOf(key.first() to wrap(key.drop(1), value))
        }
    }

    fun merge(accumulator: MutableMap<String, Any?>, element: Pair<String, Any?>) {
        val key = element.first
        if(accumulator.contains(key)) {
            if(accumulator[key] is MutableMap<*, *>) {
                val second = (element.second as? MutableMap<String, Any?>)?.entries?.first() ?: throw PathException("Wrong path expression: ")
                merge(accumulator[key] as MutableMap<String, Any?>, second.toPair())
            } else {
                throw PathException("Wrong path expression")
            }
        } else {
            accumulator.put(element.first, element.second)
        }
    }
}