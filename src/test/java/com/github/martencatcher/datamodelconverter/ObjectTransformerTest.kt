package com.github.martencatcher.datamodelconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.github.martencatcher.datamodelconverter.datamodelconverter.ObjectTransformer
import com.github.martencatcher.datamodelconverter.datamodelconverter.builders.JsonBuilder
import com.github.martencatcher.datamodelconverter.datamodelconverter.tree.JsonTreeBuilder
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Created by mast1016 on 10.01.2017.
 */
internal class ObjectTransformerTest {

    @Test
    fun simpleTest() {
        val mappings = HashMap<String, String>()
        mappings.put("$.a[*].c[*].b[*]", "$.q1.q2[*].w1.w2[*].e1.e2[*]")
        val doc = "{ 'a' : [ {'c' : [{ 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }]}, {'c' : [ { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }]}] }";


        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.generateCompletePaths(doc)
        System.out.println(res)


        val builder = JsonBuilder()

        val res2 = builder.build(res)

        System.out.println(res2)

        val om = ObjectMapper()
        System.out.println(om.writeValueAsString(res2))

        //val path = "$.book[?(@.price <= $['expensive'].)].book[?(@.price <= $['expensive'])].wefdwe"

        //val res: List<String> = ot.split(path)

        /*Assertions.assertNotNull(res)
        Assertions.assertEquals(res.size, 3)

        for(i in res) {
            System.out.println("part of path: " + i)
        }*/
    }
}