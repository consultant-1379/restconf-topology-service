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


import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_JSON
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_XML

import org.jboss.resteasy.mock.MockHttpRequest
import org.jboss.resteasy.mock.MockHttpResponse

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.oss.services.restconf.topologyservice.api.parser.RestconfResourceUriParser
import com.ericsson.oss.services.restconf.topologyservice.ejb.listener.RestconfConfigurationListener
import com.ericsson.oss.services.restconf.topologyservice.test.common.BaseRestconfTopologyServiceResourceSpec
import spock.lang.Unroll

class RestconfTopologyServiceDataResourceSpec extends BaseRestconfTopologyServiceResourceSpec {

    @ObjectUnderTest
    RestconfTopologyServiceDataResource restconfTopologyServiceDataResource

    @SpyImplementation
    RestconfResourceUriParser restconfResourceUriParser

    @SpyImplementation
    RestconfConfigurationListener restconfConfigurationListener

    def setup() {
        dispatcher.getRegistry().addSingletonResource(restconfTopologyServiceDataResource)
    }

    @Unroll
    def "Test GET modules-state request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponse(accept_type, response, response_file)
        where:
            path                                                                           | accept_type           || status || response_file
            "/data/ietf-yang-library:modules-state"                                        | APPLICATION_YANG_JSON || 200    || "json/modules_state_response.json"
            "/data/ietf-yang-library:modules-state/module=ietf-restconf,2017-01-26/"       | APPLICATION_YANG_JSON || 200    || "json/modules_state_ietf_restconf_response.json"
            "/data/ietf-yang-library:modules-state/module=ietf-restconf,2017-01-26/schema" | APPLICATION_YANG_JSON || 200    || "json/modules_state_ietf_restconf_schema_response.json"
            "/data/ietf-yang-library:modules-state"                                        | APPLICATION_YANG_XML  || 200    || "xml/modules_state_response.xml"
            "/data/ietf-yang-library:modules-state/module=ietf-restconf,2017-01-26/"       | APPLICATION_YANG_XML  || 200    || "xml/modules_state_ietf_restconf_response.xml"
            "/data/ietf-yang-library:modules-state/module=ietf-restconf,2017-01-26/schema" | APPLICATION_YANG_XML  || 200    || "xml/modules_state_ietf_restconf_schema_response.xml"
    }

    @Unroll
    def "Test GET capabilities request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponse(accept_type, response, response_file)
        where:
            path                                                         | accept_type           || status || response_file
            "/data/ietf-restconf-monitoring:restconf-state/capabilities" | APPLICATION_YANG_JSON || 200    || "json/capabilities_response.json"
            "/data/ietf-restconf-monitoring:restconf-state/capabilities" | APPLICATION_YANG_XML  || 200    || "xml/capabilities_response.xml"
    }

    @Unroll
    def "Test GET networks request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Populate TCIM database"
            populateDbFromTcimDynamic("src/test/resources/input/tcim_data/export_dynamic_sample1.txt")
        and: "Mock restconfNbiMaximumNodesHierarchyLevel and restconfNbiMaximumMoProcess PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> -1
            restconfConfigurationListener.getRestconfNbiMaximumMoProcess() >> 1000
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponse(accept_type, response, response_file)
        where:
            path                                                                                                                                  | accept_type           || status || response_file
            "/data/ietf-network:networks"                                                                                                         | APPLICATION_YANG_JSON || 200    || "json/data_networks_response.json"
            "/data/ietf-network:networks/network=1"                                                                                               | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_response.json"
            "/data/ietf-network:networks/network=1?with-defaults=report-all"                                                                      | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_response.json"
            "/data/ietf-network:networks/network=1/node=ml6352-02"                                                                                | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_node_response.json"
            "/data/ietf-network:networks/network=1/node=ml6352-02/node-id"                                                                        | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_node_nodeid_response.json"
            "/data/ietf-network:networks/network=1/node=ml6352-02/ietf-network-topology:termination-point=ml6352-02_1_LAN-1%252F1%252F4"          | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_node_tp_response.json"
            "/data/ietf-network:networks/network=1/ietf-network-topology:link=Id-ml6352-02%252FLAN-1%252F1%252F4-ml6352-01%252FLAN-1%252F1%252F4" | APPLICATION_YANG_JSON || 200    || "json/data_networks_network_link_response.json"
            "/data/ietf-interfaces:interfaces"                                                                                                    | APPLICATION_YANG_JSON || 200    || "json/data_interfaces_response.json"
            "/data/ietf-interfaces:interfaces/interface=ml6352-02:RLT-1%252F1%252F1"                                                              | APPLICATION_YANG_JSON || 200    || "json/data_interfaces_interface_rlt_response.json"
            "/data/ietf-interfaces:interfaces/interface=ml6352-02:LAG-1"                                                                          | APPLICATION_YANG_JSON || 200    || "json/data_interfaces_interface_lag_response.json"
            "/data/ietf-interfaces:interfaces/interface=ml6352-02:LAG-1/type"                                                                     | APPLICATION_YANG_JSON || 200    || "json/data_interfaces_interface_lag_type_response.json"
            "/data/ietf-microwave-radio-link:xpic-pairs"                                                                                          | APPLICATION_YANG_JSON || 200    || "json/data_xpicpairs_response.json"
            "/data/ietf-microwave-radio-link:xpic-pairs/xpic-pair=ml6352-01:1"                                                                    | APPLICATION_YANG_JSON || 200    || "json/data_xpicpairs_xpicpair_response.json"
            "/data/ietf-microwave-radio-link:xpic-pairs/xpic-pair=ml6352-01:1/name"                                                               | APPLICATION_YANG_JSON || 200    || "json/data_xpicpairs_xpicpair_name_response.json"
            "/data/ietf-microwave-radio-link:xpic-pairs"                                                                                          | APPLICATION_YANG_XML  || 200    || "xml/data_xpicpairs_response.xml"
            "/data/ietf-microwave-radio-link:xpic-pairs/xpic-pair=ml6352-02:1"                                                                    | APPLICATION_YANG_XML  || 200    || "xml/data_xpicpairs_xpicpair_response.xml"
            "/data/ietf-microwave-radio-link:xpic-pairs/xpic-pair=ml6352-02:1/name"                                                               | APPLICATION_YANG_XML  || 200    || "xml/data_xpicpairs_xpicpair_name_response.xml"
            "/data/ietf-microwave-radio-link:mimo-groups"                                                                                         | APPLICATION_YANG_XML  || 200    || "xml/data_mimogroups_response.xml"
    }

    @Unroll
    def "Test if No Data Avaiable in server #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Mock enm host url"
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        where:
            path                                                         | accept_type           || status
            "/data/ietf-network:networks/network=2"                      | APPLICATION_YANG_JSON || 204
            "/data/ietf-network:networks/network=1/node=invalidNodeName" | APPLICATION_YANG_JSON || 204
            "/data/ietf-interfaces:interfaces/interface=invalidName"     | APPLICATION_YANG_JSON || 204
    }

    @Unroll
    def "Invalid GET xpicpair request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Mock enm host url"
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        where:
            path                                                   | accept_type           || status
            "/data/ietf-microwave-radio-link:xpic-pairs/xpic-pair" | APPLICATION_YANG_JSON || 400
    }

    @Unroll
    def "Test for invalid hierarchy #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Mock enm host url"
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        where:
            path                                                                                                            | accept_type           || status
            "/data/ietf-network:networks/network=1/node=ml6352-02/termination-point=ml6352-02_1_LAN-1%252F1%252F4"          | APPLICATION_YANG_JSON || 400
            "/data/ietf-network:networks/network=1/link=Id-ml6352-02%252FLAN-1%252F1%252F4-ml6352-01%252FLAN-1%252F1%252F4" | APPLICATION_YANG_JSON || 400
    }

    @Unroll
    def "Test for invalid uri #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Mock enm host url"
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        where:
            path                                      | accept_type           || status || response_file
            "/data/ietf-network:networks/network=1,2" | APPLICATION_YANG_JSON || 400    || "json/network_error_response.json"
    }

    @Unroll
    def "Test for invalid mediaType #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Mock enm host url"
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        where:
            path                                    | accept_type        || status
            "/data/ietf-network:networks/network=1" | "application/json" || 406
    }

    @Unroll
    def "Test GET RLPG request #path with accept type #accept_type"() {
        given: "create request object"
            final MockHttpRequest request = MockHttpRequest.get(path)
            request.accept(accept_type)
            request.header("X-Tor-userId", "administrator")
            final MockHttpResponse response = new MockHttpResponse()
        and: "Populate TCIM database"
            populateDbFromTcimDynamic("src/test/resources/input/tcim_data/export_dynamic_sample1.txt")
        and: "Mock restconfNbiMaximumNodesHierarchyLevel and restconfNbiMaximumMoProcess PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> -1
            restconfConfigurationListener.getRestconfNbiMaximumMoProcess() >> 1000
        when: "Execute request path"
            dispatcher.invoke(request, response)
        then: "assert status code"
            response.getStatus() == status
        and: "assert response message"
            assertResponse(accept_type, response, response_file)
        where:
            path                                                           | accept_type           || status || response_file
            "/data/ietf-microwave-radio-link:radio-link-protection-groups" | APPLICATION_YANG_JSON || 200    || "json/data_rlgp_response.json"
    }
}