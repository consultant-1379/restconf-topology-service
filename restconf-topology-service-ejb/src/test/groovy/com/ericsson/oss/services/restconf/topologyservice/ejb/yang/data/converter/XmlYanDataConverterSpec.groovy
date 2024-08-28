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
package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

import org.custommonkey.xmlunit.DetailedDiff
import org.custommonkey.xmlunit.Diff
import org.custommonkey.xmlunit.Difference
import org.custommonkey.xmlunit.XMLUnit
import org.w3c.dom.Document

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot
import spock.lang.Unroll

class XmlYanDataConverterSpec extends CdiSpecification {

    @ObjectUnderTest
    XmlYangDataNodeConverter xmlYangDataNodeConverter

    DocumentBuilder db

    def setup() {
        XMLUnit.setNormalizeWhitespace(Boolean.TRUE)
        XMLUnit.setIgnoreWhitespace(true)
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
        dbf.setIgnoringElementContentWhitespace(true)
        dbf.setNamespaceAware(true)
        db = dbf.newDocumentBuilder()
    }

    @Unroll
    def "Test xml encoding yang data root with interfaces module"() {
        given: "create yang data root"
            YangDataNode yangRoot = new YangDataRoot()
            AbstractYangDataNode.YangDataNodeBuilder interfacesYangDataNodeBuilder = new AbstractYangDataNode.YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-interfaces", "ietf-interfaces")
        when: "create and add interfaces container under yang data root"
            YangDataNode interfacesContainer = interfacesYangDataNodeBuilder.newInstance("interfaces").container()
            ((YangDataRoot) yangRoot).addDataNode(interfacesContainer)
        and: "create and add interface list under interfaces container"
            YangDataNode interfaceList = interfacesYangDataNodeBuilder.newInstance("interface").list()
            interfacesContainer.addDataNode(interfaceList)
        and: "create lag interface list item"
            YangDataNode nameLeaf = interfacesYangDataNodeBuilder.newInstance("name").leaf("1/1")
            YangDataNode typeLeaf = interfacesYangDataNodeBuilder.newInstance("type").leaf("lag")
            YangDataNode errorsLeafList = interfacesYangDataNodeBuilder.newInstance("errors").leafList("error1", "error2")
            YangDataNode lagInterface = interfacesYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf, errorsLeafList)
        and: "add lag interface list item under interface list"
            interfaceList.addDataNode(lagInterface)
        and: "create ethernet interface list item"
            nameLeaf = interfacesYangDataNodeBuilder.newInstance("name").leaf("1/2")
            typeLeaf = interfacesYangDataNodeBuilder.newInstance("type").leaf("ethernet")
            YangDataNode ethernetInterface = interfacesYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf)
        and: "add ethernet interface list item under interface list"
            interfaceList.addDataNode(ethernetInterface)
        and: "encode yang root object to xml"
            String xmlData = xmlYangDataNodeConverter.encode(yangRoot)
        then: "verify xml data generated"
            xmlData != null
            prettyPrintXml(xmlData)
            assertXml("src/test/resources/output/test_interfaces.xml", xmlData)
    }

    @Unroll
    def "Test xml encoding yang data root with networks module"() {
        given: "create yang data root"
            YangDataNode yangRoot = new YangDataRoot()
            AbstractYangDataNode.YangDataNodeBuilder networksYangDataNodeBuilder = new AbstractYangDataNode.YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-network", "ietf-networks")
            AbstractYangDataNode.YangDataNodeBuilder nodeYangDataNodeBuilder = new AbstractYangDataNode.YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-node", "ietf-node")
        when: "crete and add networks container under yang data root"
            YangDataNode networksContainer = networksYangDataNodeBuilder.newInstance("networks").container()
            ((YangDataRoot) yangRoot).addDataNode(networksContainer)
        and: "create and add network list under networks container"
            YangDataNode networkList = networksYangDataNodeBuilder.newInstance("network").list()
            networksContainer.addDataNode(networkList)
        and: "create local network list item and add transport node list under it"
            YangDataNode nameLeaf = networksYangDataNodeBuilder.newInstance("name").leaf("local")
            YangDataNode typeLeaf = networksYangDataNodeBuilder.newInstance("type").leaf("lan")
            YangDataNode errorsLeafList = networksYangDataNodeBuilder.newInstance("errors").leafList("error1", "error2")
            YangDataNode transportNodeList = nodeYangDataNodeBuilder.newInstance("node").list()
            YangDataNode localNetworkDataSet = networksYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf, errorsLeafList, transportNodeList)
        and: "add local network under network list"
            networkList.addDataNode(localNetworkDataSet)
        and: "create router node list item"
            nameLeaf = nodeYangDataNodeBuilder.newInstance("name").leaf("node123")
            typeLeaf = nodeYangDataNodeBuilder.newInstance("type").leaf("router")
            YangDataNode routerNodeDataSet = nodeYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf)
        and: "add router node under transport node list"
            transportNodeList.addDataNode(routerNodeDataSet)
        and: "create switch node list item"
            nameLeaf = nodeYangDataNodeBuilder.newInstance("name").leaf("node456")
            typeLeaf = nodeYangDataNodeBuilder.newInstance("type").leaf("switch")
            YangDataNode switchNodeDataSet = nodeYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf)
        and: "add switch node under transport node list"
            transportNodeList.addDataNode(switchNodeDataSet)
        and: "create global network list item and add radio node list under it"
            nameLeaf = networksYangDataNodeBuilder.newInstance("name").leaf("global")
            typeLeaf = networksYangDataNodeBuilder.newInstance("type").leaf("wan")
            YangDataNode radioNodeList = nodeYangDataNodeBuilder.newInstance("node").list()
            YangDataNode globalNetworkDataSet = networksYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf, radioNodeList)
        and: "add global network under network list"
            networkList.addDataNode(globalNetworkDataSet)
        and: "create radio node list item"
            nameLeaf = nodeYangDataNodeBuilder.newInstance("name").leaf("node789")
            typeLeaf = nodeYangDataNodeBuilder.newInstance("type").leaf("radio")
            YangDataNode radioNodeDataSet = nodeYangDataNodeBuilder.newInstance().dataSet(nameLeaf, typeLeaf)
        and: "add switch node under node list"
            radioNodeList.addDataNode(radioNodeDataSet)
        and: "encode yang root object to xml"
            String xmlData = xmlYangDataNodeConverter.encode(yangRoot)
        then: "verify xml data"
            xmlData != null
            prettyPrintXml(xmlData)
            assertXml("src/test/resources/output/test_networks.xml", xmlData)
    }

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

    def prettyPrintXml(String input) {
        final Source xmlInput = new StreamSource(new StringReader(input))
        try {
            final TransformerFactory transformerFactory = TransformerFactory.newInstance()
            final Transformer transformer = transformerFactory.newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4")
            transformer.transform(xmlInput, new StreamResult(System.out))
        } catch (Exception exception) {
            throw new RuntimeException(exception)
        }
        return true
    }

    def assertXml(String filePath, String xmlData) {
        try {
            final Diff xmlDiff = new Diff(getXmlDocumentFromFile(filePath), getXmlDocumentFromString(xmlData))
            final DetailedDiff detailXmlDiff = new DetailedDiff(xmlDiff)
            if (detailXmlDiff.getAllDifferences().isEmpty()) {
                return true
            } else {
                final List differences = detailXmlDiff.getAllDifferences();
                for (Object object : differences) {
                    final Difference difference = (Difference) object;
                    println(difference)
                }
                return false
            }
        } catch (Exception exception) {
            exception.printStackTrace()
            return false
        }
    }
}
