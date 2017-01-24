package com.github.martencatcher.datamodelconverter.path

/**
 * Created by mast1016 on 20.01.2017.
 */
open class Leaf(val targetPath: String,
                val valueConversion: String?,
                val valueCondition: String?) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as Leaf

        if (targetPath != other.targetPath) return false

        return true
    }

    override fun hashCode(): Int {
        return targetPath.hashCode()
    }
}

class Branch(targetPath: String,
             valueConversion: String?,
             valueCondition: String?,
             var mappings: MutableMap<String, List<Leaf>>) : Leaf(targetPath, valueConversion, valueCondition)

open class TargetNode

class TargetEmpty: TargetNode()

class TargetValue(val path: String, val value: Any): TargetNode()

class TargetList(val values: List<TargetNode>): TargetNode()