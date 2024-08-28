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

import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_JSON
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_XML

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
import org.jboss.resteasy.core.Dispatcher
import org.jboss.resteasy.mock.MockDispatcherFactory
import org.jboss.resteasy.mock.MockHttpResponse
import org.w3c.dom.Document

import com.ericsson.cds.cdi.support.configuration.InjectionProperties
import com.ericsson.cds.cdi.support.providers.custom.model.RealModelServiceProvider
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps
import com.ericsson.oss.services.restconf.topologyservice.resources.TcimFileDynamicPopulator
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.DatabaseNotAvailableExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.NoDataAvailableExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.ResourceUriParserExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfDataGeneratorExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfDataOperationExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfMoThresholdExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfYangHierarchyValidationExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.RestconfYangModulesParserExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf.YangDataNodeConverterExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.BadRequestExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.InternalServerErrorExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.NotAcceptableExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.NotAllowedExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.NotFoundExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.NotSupportedExceptionHandler
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web.ServiceUnavailableExceptionHandler
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature

class BaseRestconfTopologyServiceResourceSpec extends CdiSpecification {

    Dispatcher dispatcher = MockDispatcherFactory.createDispatcher()

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
        dispatcher.getProviderFactory().registerProvider(BadRequestExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(InternalServerErrorExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(NotAcceptableExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(NotAllowedExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(NotFoundExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(NotSupportedExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(ServiceUnavailableExceptionHandler.class)

        dispatcher.getProviderFactory().registerProvider(DatabaseNotAvailableExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(ResourceUriParserExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfDataGeneratorExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfYangHierarchyValidationExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfDataOperationExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfYangModulesParserExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(YangDataNodeConverterExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(NoDataAvailableExceptionHandler.class)
        dispatcher.getProviderFactory().registerProvider(RestconfMoThresholdExceptionHandler.class)

        XMLUnit.setNormalizeWhitespace(Boolean.TRUE)
        XMLUnit.setIgnoreWhitespace(true)
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance()
        dbf.setIgnoringElementContentWhitespace(true)
        dbf.setNamespaceAware(true)
        db = dbf.newDocumentBuilder()

        runtimeDps = cdiInjectorRule.getService(RuntimeConfigurableDps.class)
        runtimeDps.withTransactionBoundaries()
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

    def assertResponse(String type, MockHttpResponse response, String response_file) {
        if (type == APPLICATION_YANG_JSON) {
            final File file = new File("src/test/resources/output/" + response_file)
            println(mapper.writeValueAsString(mapper.readTree(response.getContentAsString())))
            return mapper.readTree(file.text) == mapper.readTree(response.getContentAsString())
        } else if (type == APPLICATION_YANG_XML) {
            prettyPrintXml(response.getContentAsString())
            return assertXml("src/test/resources/output/" + response_file, response.getContentAsString())
        }
        return false
    }

    def assertResponseStream(MockHttpResponse response, String response_file) {
        final File file = new File("src/test/resources/output/" + response_file)
        file.text.trim() == response.getContentAsString().trim()
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
}
