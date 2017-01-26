package com.github.martencatcher.datamodelconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.github.martencatcher.datamodelconverter.path.JsonTreeBuilder
import com.github.martencatcher.datamodelconverter.path.Rule
import org.junit.jupiter.api.Test

internal class ObjectTransformerTest {

    @Test
    fun simpleTest() {
        val mappings = listOf<Rule>(
            Rule("$.a[*].c[*].b[*]", "$.q1.q2[*].w1.w2[*].e1.e2[*]", null, null))

        val doc = "{ 'a' : [ {'c' : [{ 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }]}, {'c' : [ { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }]}] }";

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.transform(doc)

        System.out.println(res)

        val jsonMapper = ObjectMapper()

        System.out.println(jsonMapper.writeValueAsString(res))

        val module = JacksonXmlModule()
        module.setDefaultUseWrapper(true)
        val xmlMapper = XmlMapper(module)
        val xml = xmlMapper.writer().withoutRootName().writeValueAsString(res).replace(Regex("<[/]*>"), "")

        System.out.println(xml)

        val yamlMapper = YAMLMapper()
        System.out.println(yamlMapper.writeValueAsString(res))
    }

    @Test
    fun realTest() {
        val mappings = listOf<Rule>(
                Rule("$.filter[*].source", "$.accessList.rules[*].source", null, null),
                Rule("$.filter[*].target", "$.accessList.rules[*].target", null, null),
                Rule("$.filter[*].protocol", "$.accessList.rules[*].protocol", null, null),
                Rule("$.filter[*].port", "$.accessList.rules[*].port", null, null),
                Rule("$.filter[*].access", "$.accessList.rules[*].access", null, null))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"allow\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.transform(doc)

        val formatter = Formatter()

        res.let {
            System.out.println(formatter.format(Format.JSON, res))
            System.out.println(formatter.format(Format.XML, res))
        }
    }

    @Test
    fun realTest2() {
        val mappings = listOf<Rule>(
                Rule(null, "$.configuration.firewall.family.inet.filter.name", null, "return customer"),
                Rule("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].name", null, "return 'term_'..customer..'_'..index[1]"),
                Rule("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].from.source-address.name", null, null),
                Rule("$.filter[*].target", "$.configuration.firewall.family.inet.filter.term[*].from.destination-address.name", null, null),
                Rule("$.filter[*].protocol", "$.configuration.firewall.family.inet.filter.term[*].from.protocol", null, null),
                Rule("$.filter[*].port", "$.configuration.firewall.family.inet.filter.term[*].from.port", null, null),
                Rule("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.accept", "return value == 'Allow'", "return nil"),
                Rule("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.reject", "return value == 'Deny'", "return nil"))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"Allow\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"Deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.transform(doc)

        val formatter = Formatter()

        res.let {
            System.out.println(formatter.format(Format.JSON, res))
            System.out.println(formatter.format(Format.XML, res))
        }
    }
}