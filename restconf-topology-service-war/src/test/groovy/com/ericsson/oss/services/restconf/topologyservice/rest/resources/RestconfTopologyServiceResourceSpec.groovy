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

package com.ericsson.oss.services.restconf.topologyservice.rest.resources

import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_JSON
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_XML

import javax.inject.Inject

import org.jboss.resteasy.mock.MockHttpRequest
import org.jboss.resteasy.mock.MockHttpResponse

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder
import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext
import com.ericsson.oss.services.restconf.topologyservice.test.common.BaseRestconfTopologyServiceResourceSpec
import spock.lang.Unroll

class RestconfTopologyServiceResourceSpec extends BaseRestconfTopologyServiceResourceSpec {

    @ObjectUnderTest
    RestconfTopologyServiceResource restconfTopologyServiceResource

    @Inject
    RestconfContext restconfContext

    def setup() {
        dispatcher.getRegistry().addSingletonResource(restconfTopologyServiceResource)

        restconfContext.setRestconfInstrumentationBuilder(new RestconfInstrumentationBuilder())
    }

    @Unroll
    def "Test GET top level api with accept type JSON"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            final File file = new File("src/test/resources/output/" + response_file)
            println(mapper.writeValueAsString(mapper.readTree(response.getContentAsString())))
            assert mapper.readTree(file.text) == mapper.readTree(response.getContentAsString())
        where:
            path | accept_type           || status || response_file
            "/"  | APPLICATION_YANG_JSON || 200    || "json/top_level_api_response.json"
    }

    @Unroll
    def "Test GET top level api with accept type XML"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            prettyPrintXml(response.getContentAsString())
            assertXml("src/test/resources/output/" + response_file, response.getContentAsString())
        where:
            path | accept_type          || status || response_file
            "/"  | APPLICATION_YANG_XML || 200    || "xml/top_level_api_response.xml"
    }

    @Unroll
    def "Test GET yang library version with accept type JSON"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            final File file = new File("src/test/resources/output/" + response_file)
            println(mapper.writeValueAsString(mapper.readTree(response.getContentAsString())))
            assert mapper.readTree(file.text) == mapper.readTree(response.getContentAsString())
        where:
            path                    | accept_type           || status || response_file
            "/yang-library-version" | APPLICATION_YANG_JSON || 200    || "json/yang_library_version_response.json"
    }

    @Unroll
    def "Test GET yang library version with accept type XML"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            prettyPrintXml(response.getContentAsString())
            assertXml("src/test/resources/output/" + response_file, response.getContentAsString())
        where:
            path                    | accept_type          || status || response_file
            "/yang-library-version" | APPLICATION_YANG_XML || 200    || "xml/yang_library_version_response.xml"
    }

    @Unroll
    def "Test GET yang schema request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponseStream(response, response_file)
        where:
            path                                                         | accept_type      || status || response_file
            "/yang/ietf-network?module=ietf-network&revision=2018-02-26" | APPLICATION_YANG || 200    || "yang/ietf-network@2018-02-26.yang"
    }

    @Unroll
    def "Test GET invalid yang schema request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponse(APPLICATION_YANG_JSON, response, response_file)
        where:
            path                                                         | accept_type      || status || response_file
            "/yang/ietf-network?module=ietf-network&revision=2000-02-26" | APPLICATION_YANG || 500    || "error/invalid_networks_yang_schema.json"
    }

    @Unroll
    def "Test GET invalid top level api with accept type JSON"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            final File file = new File("src/test/resources/output/" + response_file)
            println(mapper.writeValueAsString(mapper.readTree(response.getContentAsString())))
            assert mapper.readTree(file.text) == mapper.readTree(response.getContentAsString())
        where:
            path       | accept_type           || status || response_file
            "/invalid" | APPLICATION_YANG_JSON || 404    || "error/top_level_api_error_response.json"
    }

    @Unroll
    def "Test GET invalid top level api with accept type XML"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response);
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            prettyPrintXml(response.getContentAsString())
            assertXml("src/test/resources/output/" + response_file, response.getContentAsString())
        where:
            path       | accept_type          || status || response_file
            "/invalid" | APPLICATION_YANG_XML || 404    || "error/top_level_api_error_response.xml"
    }
}
