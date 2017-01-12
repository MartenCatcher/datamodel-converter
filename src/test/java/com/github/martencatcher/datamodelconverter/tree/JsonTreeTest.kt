package com.github.martencatcher.datamodelconverter.tree

import com.github.martencatcher.datamodelconverter.datamodelconverter.tree.JsonTree
import org.junit.jupiter.api.Test

/**
 * Created by mast1016 on 10.01.2017.
 */
internal class JsonTreeTest {
    val json = "{ 'a' : { 'b' : 1, 'c' : 2 }, 'd' : 4, 'e' : [10, 20, 30], 'f' : [{'a':1}, {'a':0}, {'a':2}] }"
    val tree = JsonTree(json)

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