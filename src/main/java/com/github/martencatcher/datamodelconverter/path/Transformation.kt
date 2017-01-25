package com.github.martencatcher.datamodelconverter.path

/**
 * Created by mast1016 on 25.01.2017.
 */
open class Transformation(val condition: String?, val expression: String?)

class Rule(val sourcePath: String, val targetPath: String, condition: String?, expression: String?): Transformation(condition, expression)