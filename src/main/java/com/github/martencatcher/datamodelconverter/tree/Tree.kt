package com.github.martencatcher.datamodelconverter.tree

/**
 * Created by mast1016 on 09.01.2017.
 */
interface Tree {
    fun applyPath(path: String): Any?
    fun adjustPath(path: String): String
}