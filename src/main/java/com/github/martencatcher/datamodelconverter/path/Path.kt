package com.github.martencatcher.datamodelconverter.path

/**
 * Created by mast1016 on 09.01.2017.
 */
interface Path {
    fun applyPath(path: String): Any?
    fun adjustPath(path: String): String
}