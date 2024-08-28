/*
 * ------------------------------------------------------------------------------
 * ******************************************************************************
 *  COPYRIGHT Ericsson 2022
 *
 *  The copyright to the computer program(s) herein is the property of
 *  Ericsson Inc. The programs may be used and/or copied only with written
 *  permission from Ericsson Inc. or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the
 *  program(s) have been supplied.
 * ******************************************************************************
 * ------------------------------------------------------------------------------
 */

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser

import javax.inject.Inject
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference
import org.custommonkey.xmlunit.XMLUnit
import org.w3c.dom.Document

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter.JsonYangDataNodeConverter
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter.XmlYangDataNodeConverter
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.provider.RestconfYangDataNodeTemplateProvider
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

class RestconfYangModulesParserSpec extends CdiSpecification {

    @ObjectUnderTest
    RestconfYangModulesParser restconfYangModulesParser

    @Inject
    RestconfYangDataNodeTemplateProvider restconfYangDataNodeTemplateProvider

    @Inject
    JsonYangDataNodeConverter jsonYangDataNodeConverter

    @Inject
    XmlYangDataNodeConverter xmlYangDataNodeConverter

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    DocumentBuilder db

    def setup() {
        XMLUnit.setNormalizeWhitespace(Boolean.TRUE)
        XMLUnit.setIgnoreWhitespace(true)
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
        dbf.setIgnoringElementContentWhitespace(true)
        dbf.setNamespaceAware(true)
        db = dbf.newDocumentBuilder()
    }

    /*@Unroll
    def "Test yang data root node generation for all yang modules"() {
        when: "PostConstruct method execution parses yang modules and generates yang data root node"
            final YangDataNode yangDataRootNode = restconfYangDataNodeTemplateProvider.getYangDataRootNode()
        then: "Verify json data generated"
            yangDataRootNode != null
            String jsonData = jsonYangDataNodeConverter.encode(yangDataRootNode)
            final File file = new File("src/test/resources/output/test_yang_data_node_template.json")
            // println(jsonData)
            // assert mapper.readTree(file.text) == mapper.readTree(jsonData)
        and: "Verify xml data generated"
            String xmlData = xmlYangDataNodeConverter.encode(yangDataRootNode)
            // println(xmlData);
            // assertXml("src/test/resources/output/test_yang_data_node_template.xml", "<root>" + xmlData + "</root>")
    }*/

    def getXmlDocumentFromFile(String filePath) {
        Document doc = db.parse(new File(filePath))
        doc.normalizeDocument()
        return doc
    }

    def getXmlDocumentFromString(String xmlData) {
        Document doc = db.parse(new ByteArrayInputStream(xmlData.getBytes()))
        doc.normalizeDocument()
        return doc
    }

    def assertXml(String filePath, String xmlData) {
        try {
            final Diff xmlDiff = new Diff(getXmlDocumentFromFile(filePath), getXmlDocumentFromString(xmlData))
            final DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff)
            if (detailXmlDiff.getAllDifferences().isEmpty()) {
                return true
            } else {
                final List differences = detailXmlDiff.getAllDifferences()
                for (Object object : differences) {
                    final Difference difference = (Difference) object
                    // println(difference)
                }
                return false
            }
        } catch (Exception exception) {
            exception.printStackTrace()
            return false
        }
    }
}
