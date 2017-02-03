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
    fun ciscoTest() {//ip access-list extended Test_extend
        val mappings = listOf<Rule>(
                Rule(null, "$.accessList.type", null, "return 'extended'"),
                Rule(null, "$.accessList.name", null, "return customer"),
                Rule("$.filter[*].source", "$.accessList.rules[*].source", null, "if value == 'ANY' then return 'any' else return 'host '..value end"),
                Rule("$.filter[*].target", "$.accessList.rules[*].target", null, "if value == 'ANY' then return 'any' else return 'host '..value end"),
                Rule("$.filter[*].protocol", "$.accessList.rules[*].protocol", null, null),
                Rule("$.filter[*].port", "$.accessList.rules[*].port", "return value ~= nil", "return 'eq '..value"),
                Rule("$.filter[*].access", "$.accessList.rules[*].access", null, "if value == 'Allow' then return 'permit' else return 'deny' end"),
                Rule(null, "target.type", null, "return 'interface'"),
                Rule(null, "target.name", null, "return 'gig1'"),
                Rule(null, "target.acl", null, "return customer"),
                Rule(null, "target.direction", null, "return 'in'"))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"Allow\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.transform(doc)

        val formatter = Formatter()

        res.let {
            System.out.println(formatter.format(Format.JSON, res))
            //System.out.println(formatter.format(Format.XML, res))
        }
    }

    @Test
    fun juniperTest2() {
        val mappings = listOf<Rule>(
                Rule("$.*", "$.configuration.firewall.family.inet.filter.name", null, "return customer"),
                Rule("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].name", null, "return 'term_'..customer..'_'..index[1]"),
                Rule("$.filter[*].source", "$.configuration.firewall.family.inet.filter.term[*].from.source-address.name", "return value ~= 'ANY'", null),
                Rule("$.filter[*].target", "$.configuration.firewall.family.inet.filter.term[*].from.destination-address.name", "return value ~= 'ANY'", null),
                Rule("$.filter[*].protocol", "$.configuration.firewall.family.inet.filter.term[*].from.protocol", null, null),
                Rule("$.filter[*].port", "$.configuration.firewall.family.inet.filter.term[*].from.port", null, null),
                Rule("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.accept", "return value == 'Allow'", "return nil"),
                Rule("$.filter[*].access", "$.configuration.firewall.family.inet.filter.term[*].then.reject", "return value == 'Deny'", "return nil"),
                Rule(null, "$.configuration.interfaces.interface.name", null, "return 'ge-0/0/0.0'"),
                Rule(null, "$.configuration.interfaces.interface.unit.name", null, "return '0'"),
                Rule(null, "$.configuration.interfaces.interface.unit.family.inet.dhcp", null, "return nil"),
                Rule(null, "$.configuration.interfaces.interface.unit.family.inet.filter.input.filter-name", null, "return customer"))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"22\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"ANY\", \"target\" : \"10.10.0.3\", \"protocol\" : \"icmp\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"192.168.0.3\", \"target\" : \"10.10.0.3\", \"protocol\" : \"tcp\", \"port\" : \"80\", \"access\" : \"Deny\"}]}"

        val ot = ObjectTransformer(mappings, JsonTreeBuilder())
        val res = ot.transform(doc)

        val formatter = Formatter()

        res.let {
            System.out.println(formatter.format(Format.JSON, res))
            System.out.println(formatter.format(Format.XML, res))
        }
    }

    @Test
    fun paloAltoTest2() {
        val mappings = listOf<Rule>(
                Rule("$.filter[*].source", "$.wrapper.entry[].@name", null, "return 'entry_'..customer..'_'..index[1]"),
                Rule("$.filter[*].source", "$.wrapper.entry[].to.member", null, "return 'public'"),
                Rule("$.filter[*].source", "$.wrapper.entry[].from.member", null, "return 'trusted'"),
                Rule("$.filter[*].source", "$.wrapper.entry[].source.member", null, null),
                Rule("$.filter[*].target", "$.wrapper.entry[].destination.member", null, null),
                Rule("$.filter[*].target", "$.wrapper.entry[].source-user.member", null, "return 'any'"),
                Rule("$.filter[*].target", "$.wrapper.entry[].category.member", null, "return 'any'"),
                Rule("$.filter[*].protocol", "$.wrapper.entry[].application.member", "return value == 'ICMP'", "return 'ping'"),
                Rule("$.filter[*].port", "$.wrapper.entry[].application.member", "return value == '22'", "return 'ssh'"),
                Rule("$.filter[*].port", "$.wrapper.entry[].application.member", "return value == '80'", "return 'http'"),
                Rule("$.filter[*].port", "$.wrapper.entry[].application.member", "return value == '443'", "return 'https'"),
                Rule("$.filter[*].port", "$.wrapper.entry[].service.member", null, "return 'any'"),
                Rule("$.filter[*].port", "$.wrapper.entry[].hip-profiles.member", null, "return 'any'"),
                Rule("$.filter[*].access", "$.wrapper.entry[].action", null, "if value == 'Allow' then return 'allow' else return 'deny ' end"),
                Rule("$.filter[*].access", "$.wrapper.entry[].log-start", null, "return 'yes'"),
                Rule("$.filter[*].access", "$.wrapper.entry[].rule-type", null, "return 'universal'"))

        val doc = "{\"filter\" : [" +
                "{ \"source\" : \"192.168.0.1\", \"target\" : \"10.10.0.3\", \"protocol\" : \"TCP\", \"port\" : \"22\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"192.168.0.2\", \"target\" : \"10.10.0.3\", \"protocol\" : \"ICMP\", \"access\" : \"Deny\"}," +
                "{ \"source\" : \"ANY\", \"target\" : \"10.10.0.3\", \"protocol\" : \"ICMP\", \"access\" : \"Deny\"}," +
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