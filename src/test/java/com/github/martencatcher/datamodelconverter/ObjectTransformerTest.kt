package com.github.martencatcher.datamodelconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.github.martencatcher.datamodelconverter.formatters.Format
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
        val res2 = ot.generateCompletePaths(doc)

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

    @Test
    fun realTest() {
        val mappings = HashMap<String, String>()
        mappings.put("$.filter[*].source", "$.accessList.rules[*].source")
        mappings.put("$.filter[*].target", "$.accessList.rules[*].target")
        mappings.put("$.filter[*].protocol", "$.accessList.rules[*].protocol")
        mappings.put("$.filter[*].port", "$.accessList.rules[*].port")
        mappings.put("$.filter[*].access", "$.accessList.rules[*].access")

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"allow\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res2 = ot.generateCompletePaths(doc)

        val formatter = com.github.martencatcher.datamodelconverter.formatters.Formatter()

        res2.let {
            System.out.println(formatter.format(Format.JSON, res2))
            System.out.println(formatter.format(Format.XML, res2))
        }
    }
}