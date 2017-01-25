package com.github.martencatcher.datamodelconverter

import com.github.martencatcher.datamodelconverter.path.JsonTreeBuilder
import com.github.martencatcher.datamodelconverter.path.Rule
import org.junit.jupiter.api.Test


/**
 * Created by mast1016 on 10.01.2017.
 */
internal class ObjectTransformerTest {

    /*@Test
    fun simpleTest() {
        val mappings = HashMap<String, String>()
        mappings.put("$.a[*].c[*].b[*]", "$.q1.q2[*].w1.w2[*].e1.e2[*]")
        val doc = "{ 'a' : [ {'c' : [{ 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }, { 'b' : [1, 2, 3] }]}, {'c' : [ { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }, { 'b' : [4, 5, 6] }]}] }";

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res2 = ot.transform(doc)

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
        val res2 = ot.transform(doc)

        val formatter = Formatter()

        res2.let {
            System.out.println(formatter.format(Format.JSON, res2))
            System.out.println(formatter.format(Format.XML, res2))
        }
    }*/

    @Test
    fun realTest2() {
        /*val mappings = HashMap<String, String>()
        mappings.put("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].from.source-address.name")
        mappings.put("$.filter[*].target", "$.configuration.firewall.family.inet.filter.term[*].from.destination-address.name")
        mappings.put("$.filter[*].protocol", "$.configuration.firewall.family.inet.filter.term[*].from.protocol")
        mappings.put("$.filter[*].port", "$.configuration.firewall.family.inet.filter.term[*].from.port")
        mappings.put("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.accept")*/
        val mappings = listOf<Rule>(
                Rule("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].from.source-address.name", null, null),
                Rule("$.filter[*].target", "$.configuration.firewall.family.inet.filter.term[*].from.destination-address.name", null, null),
                Rule("$.filter[*].protocol", "$.configuration.firewall.family.inet.filter.term[*].from.protocol", null, null),
                Rule("$.filter[*].port", "$.configuration.firewall.family.inet.filter.term[*].from.port", null, null),
                Rule("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.accept", null, "return nil"))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"allow\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res2 = ot.transform(doc)

        val formatter = Formatter()

        res2.let {
            System.out.println(formatter.format(Format.JSON, res2))
            System.out.println(formatter.format(Format.XML, res2))
        }
    }
}