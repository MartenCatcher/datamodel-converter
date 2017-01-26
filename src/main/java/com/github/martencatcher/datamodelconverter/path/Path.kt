package com.github.martencatcher.datamodelconverter.path

interface Path {
    fun applyPath(path: String): Any?
    fun adjustPath(path: String): String
}