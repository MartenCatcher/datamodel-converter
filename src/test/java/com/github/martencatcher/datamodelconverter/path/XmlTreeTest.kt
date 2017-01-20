package com.github.martencatcher.datamodelconverter.path

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.nio.charset.Charset
import javax.xml.parsers.DocumentBuilderFactory

/**
 * Created by mast1016 on 09.01.2017.
 */
internal class XmlTreeTest {
    @Test
    fun getSubtree() {

    }

    @Test
    fun getValues() {
        //val tree: XPath = XPath()

        //val document: String = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><a><b>1</b><b>2</b><b>3</b><b>4</b></a>"
        val document: String = "<a><b>1</b><b>2</b><b>3</b><b>4</b></a>"
        val xPath: String = "/a/b"

        val inputSource = ByteArrayInputStream(document.toByteArray(Charset.forName("UTF-8")))
        val sourceDoc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputSource)

        //val result = tree.getValues(xPath, sourceDoc)
        Assertions.assertNotNull("")
    }

    @Test
    fun name() {


    }
}