package com.github.martencatcher.datamodelconverter

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

enum class Format {
    XML, JSON, YAML
}

class Formatter {
    fun format(target: Format, data: Any): String {
        return when(target) {
            Format.XML -> {
                val module = JacksonXmlModule()
                module.setDefaultUseWrapper(true)
                val xmlMapper = XmlMapper(module)

                xmlMapper.writer().withoutRootName()
                        .writeValueAsString(data)
                        .replace(Regex("<[/]*>"), "")
                        .replace(Regex("(><@.+?(<\\/@.*?>|\\/>))")) { matched ->
                            matched.value.replace(Regex("(><@)(.*?)(>)(.*?)(<\\/.*>)")) { attribute ->
                                " " + attribute.groupValues[2] + "=\"" + attribute.groupValues[4] + "\">"
                            }
                        }
            }
            Format.JSON -> ObjectMapper().writeValueAsString(data)
            Format.YAML -> YAMLMapper().writeValueAsString(data)
        }
    }
}