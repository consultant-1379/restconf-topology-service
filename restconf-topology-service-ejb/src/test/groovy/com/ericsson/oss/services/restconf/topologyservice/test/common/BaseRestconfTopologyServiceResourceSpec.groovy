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

package com.ericsson.oss.services.restconf.topologyservice.test.common

import java.util.concurrent.atomic.AtomicInteger

import javax.inject.Inject
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

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.providers.custom.model.RealModelServiceProvider
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot
import com.ericsson.oss.services.restconf.topologyservice.ejb.TcimFileDynamicPopulator
import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

class BaseRestconfTopologyServiceResourceSpec extends CdiSpecification {

    @Inject
    RestconfContext restconfContext

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    DocumentBuilder db

    RuntimeConfigurableDps runtimeDps

    private static RealModelServiceProvider realModelServiceProvider = new RealModelServiceProvider([
            // This allows you to filter the models you want.
    ])

    @Override
    def addAdditionalInjectionProperties(final InjectionProperties injectionProperties) {
        // Specify that we want to use the real model instead mocking model service
        injectionProperties.addInjectionProvider(realModelServiceProvider)
    }

    def setup() {
        XMLUnit.setIgnoreWhitespace(true)
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
        dbf.setIgnoringElementContentWhitespace(true)
        dbf.setNamespaceAware(true)
        db = dbf.newDocumentBuilder()

        runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        runtimeDps.withTransactionBoundaries()

        restconfContext.setRestconfInstrumentationBuilder(new RestconfInstrumentationBuilder())
    }

    def cleanup() {
        println(restconfContext.getRestconfInstrumentationBuilder().build())
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
        println(input)
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
                final List differences = detailXmlDiff.getAllDifferences()
                for (Object object : differences) {
                    final Difference difference = (Difference) object
                    println(difference)
                }
                return false
            }
        } catch (Exception exception) {
            exception.printStackTrace()
            return false
        }
    }

    def populateDbFromTcimDynamic(String filePath) {
        new TcimFileDynamicPopulator(runtimeDps).parseFile(filePath)
    }

    def calculateDataNodesLevel(final YangDataNode yangDataNode, final AtomicInteger level, final AtomicInteger indent) {
        Set<YangDataNode> dataNodes = yangDataNode instanceof YangDataRoot ? yangDataNode.getDataNodes() :
                ((AbstractYangDataNode) yangDataNode).getModelSet().getDataNodes()
        for (final YangDataNode childDataNode : dataNodes) {
            if (childDataNode instanceof YangDataContainer || childDataNode instanceof YangDataList) {
                println(getTabs(indent.get()) + childDataNode.getModule() + ":" + childDataNode.getNodeName())
                level.incrementAndGet()
                indent.incrementAndGet()
                calculateDataNodesLevel(childDataNode, level, indent)
                indent.decrementAndGet()
            } else if (childDataNode instanceof YangDataLeaf || childDataNode instanceof YangDataLeafList) {
                indent.incrementAndGet()
                println(getTabs(indent.get()) + childDataNode.getModule() + ":" + childDataNode.getNodeName())
                indent.decrementAndGet()
            }
        }
        return true
    }

    def getTabs(final int indent) {
        final StringBuilder builder = new StringBuilder()
        for (int index = 0; index < indent; index++) {
            builder.append("\t")
        }
        return builder.toString()
    }
}
