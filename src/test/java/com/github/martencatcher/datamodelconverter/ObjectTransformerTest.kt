package com.github.martencatcher.datamodelconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.github.martencatcher.datamodelconverter.builders.JPathBuilder
import com.github.martencatcher.datamodelconverter.path.JsonTreeBuilder
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

        val builder = JPathBuilder()
        val res2 = builder.build(res)

        System.out.println(res2)

        val jsonMapper = ObjectMapper()

        System.out.println(jsonMapper.writeValueAsString(res2))

        val module = JacksonXmlModule()
        module.setDefaultUseWrapper(true)
        val xmlMapper = XmlMapper(module)
        val xml = xmlMapper.writer().withoutRootName().writeValueAsString(res2).replace(Regex("<[/]*>"), "")

        System.out.println(xml)

        val yamlMapper = YAMLMapper()
        System.out.println(yamlMapper.writeValueAsString(res2))
    }
}