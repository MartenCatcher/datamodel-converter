package com.github.martencatcher.datamodelconverter.formatters

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper

/**
 * Created by mast1016 on 18.01.2017.
 */
class Formatter {
    fun format(target: Format, data: Any): String {
        when(target) {
            Format.XML  -> {
                val module = JacksonXmlModule()
                module.setDefaultUseWrapper(true)
                val xmlMapper = XmlMapper(module)

                return xmlMapper.writer().withoutRootName().writeValueAsString(data).replace(Regex("<[/]*>"), "")
            }
            Format.JSON -> return ObjectMapper().writeValueAsString(data)
            Format.YAML -> return YAMLMapper().writeValueAsString(data)
        }
    }
}