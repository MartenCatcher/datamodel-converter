package com.github.martencatcher.datamodelconverter.path

/**
 * Created by mast1016 on 20.01.2017.
 */
open class Leaf(val targetPath: String,
                val valueConversion: String?,
                val valueCondition: String?)

class Branch(targetPath: String,
             valueConversion: String?,
             valueCondition: String?,
             var mappings: MutableMap<String, List<Leaf>>) : Leaf(targetPath, valueConversion, valueCondition)