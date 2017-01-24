package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.exceptions.PathException
import com.github.martencatcher.datamodelconverter.path.*
import java.util.*

//TODO: change type of mapping to List of (source: String, target: String, transformationExpression: String)
class ObjectTransformer constructor(val mappings: Map<String, String>, val builder: TreeBuilder) {

    fun generateCompletePaths(doc: Any): TargetNode {
        val preparedMappings = HashMap<String, List<Leaf>>()

        for ((sourcePath, targetPath) in mappings) {
            val sourceParts = split(sourcePath)
            val targetParts = split(targetPath)

            mergePaths(sourceParts, targetParts, preparedMappings)
        }

        val targets = preparedMappings.map { mappings -> extract(mappings.key, mappings.value, doc) }.toList();

        when(targets.size) {
            0 -> return TargetEmpty()
            1 -> return targets.first()
            else -> return TargetList(targets)
        }
    }

    fun mergePaths(source: List<String>, target: List<String>, tree: MutableMap<String, List<Leaf>>): MutableMap<String, List<Leaf>> {
        if (source.size != target.size || source.isEmpty()) {
            throw IllegalArgumentException("Array with different sizes or empty doesn't support: source $source, target: $target")
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
                    is Branch -> leaf.mappings = mergePaths(source.drop(1), target.drop(1), leaf.mappings)
                    is Leaf -> throw PathException("Duplicate path!")
                }
            }
        }

        tree.put(source.first(), leafs)
        return tree
    }

    fun extract(sourcePath: String, leafs: List<Leaf>, doc: Any): TargetNode {
        val tree = builder.buildTree(doc)
        val expression = tree.adjustPath(sourcePath)

        tree.applyPath(expression)?.let { found ->
            if (found is Collection<*>) {
                if(leafs.size == 1 && leafs.first() is Branch) {
                    val targetPath = leafs.first().targetPath
                    val childs = found.filterNotNull().map { doc -> extractBranch(leafs.first() as Branch, doc) }.toList()
                    return TargetValue(deleteCounter(targetPath), childs)
                } else {
                    val targets = found.filterNotNull().map { doc -> extract(leafs, doc) }.toList()
                    return if(targets.size > 1) TargetList(targets) else targets.first()
                }
            } else {
                return extract(leafs, found)
            }
        }

        return TargetEmpty()
    }

    fun extract(leafs: List<Leaf>, found: Any): TargetNode {
        val branch = ArrayList<TargetNode>();
        leafs.forEach { leaf ->
            val targetPath = leaf.targetPath
            when (leaf) {
                is Branch -> branch.add(TargetValue(deleteCounter(targetPath), extractBranch(leaf, found)))
                is Leaf -> {
                    val isArray = needCounter(targetPath)
                    val key = if (isArray) deleteCounter(targetPath) else targetPath
                    val value = if (isArray) found as? Collection<*> ?: listOf<Any>(found) else found
                    branch.add(TargetValue(key, value))
                }
            }
        }
        return if (branch.size > 1) TargetList(branch) else branch.first()
    }

    fun extractBranch(leaf: Branch, found: Any): List<TargetNode> {
        return leaf.mappings.map { child -> extract(child.key, child.value, found) }.toList()
    }
}