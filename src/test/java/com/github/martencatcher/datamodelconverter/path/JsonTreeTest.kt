package com.github.martencatcher.datamodelconverter.path

import org.junit.jupiter.api.Test

internal class JsonTreeTest {
    val json = "{ 'a' : { 'b' : 1, 'c' : 2 }, 'd' : 4, 'e' : [10, 20, 30], 'f' : [{'a':1}, {'a':0}, {'a':2}] }"
    val tree = JPath(json)

    @Test
    fun getManyValuesTest() {
        val path = "$.f[*].a"

    }

    @Test
    fun getOneValueTest() {

    }

    @Test
    fun NotFoundValueTest() {

    }

}