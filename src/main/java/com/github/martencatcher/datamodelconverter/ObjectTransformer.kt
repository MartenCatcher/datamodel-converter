package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.exceptions.PathException
import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: Any): Any {
        val preparedMappings = HashMap<String, List<Leaf>>() //TODO: map<string, map>

        for ((sourcePath, targetPath) in mappings) {
            val sourceParts = split(sourcePath)
            val targetParts = split(targetPath)

            mergePaths(preparedMappings, sourceParts, targetParts)
        }

        val targets = preparedMappings.map { mappings -> extract(mappings.key, mappings.value, doc) }.toList(); //TODO: list???

        return when (targets.size) {
            0 -> return Empty()
            1 -> return targets.first()
            else -> targets
        }
    }

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

    /*fun extract(sourcePath: String, leafs: List<Leaf>, doc: Any): Any {
        return when (doc) {
            is Collection<*> -> doc.filterNotNull().map { element -> extractOne(sourcePath, leafs, element) }.toList()
            else -> extractOne(sourcePath, leafs, doc)
        }
    }*/

    fun extract(sourcePath: String, leafs: List<Leaf>, doc: Any): Map<String, Any> {
        val tree = builder.buildTree(doc)
        val expression = tree.adjustPath(sourcePath)

        tree.applyPath(expression)?.let { found ->
            return extract(leafs, found)
        }

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
                                val t = leaf.mappings
                                        .map { mapping -> extract(mapping.key, mapping.value, element) }
                                        .flatMap { map -> map.entries }
                                        .map { entity -> Pair(entity.key, entity.value) }.toMap()
                                t
                            } ?: leaf.mappings.map { mapping -> extract(mapping.key, mapping.value, found) }
                }
                is Leaf -> if (isArray) found as? Collection<*> ?: listOf<Any>(found) else found
                else -> throw PathException("Unknown tree element type!")
            }
            Pair(key, value)
        }.toMap()

        return obj
    }
}