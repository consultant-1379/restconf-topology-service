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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.provider

import java.util.concurrent.atomic.AtomicInteger

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfYangHierarchyValidationException
import com.ericsson.oss.services.restconf.topologyservice.api.parser.RestconfResourceUriParser
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot
import com.ericsson.oss.services.restconf.topologyservice.ejb.listener.RestconfConfigurationListener
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.provider.RestconfYangDataNodeTemplateProvider
import com.ericsson.oss.services.restconf.topologyservice.test.common.BaseRestconfTopologyServiceResourceSpec
import spock.lang.Unroll

class RestconfYangDataNodeTemplateProviderSpec extends BaseRestconfTopologyServiceResourceSpec {

    @ObjectUnderTest
    RestconfYangDataNodeTemplateProvider restconfYangDataNodeTemplateProvider

    @Inject
    RestconfResourceUriParser restconfResourceUriParser

    @SpyImplementation
    RestconfConfigurationListener restconfConfigurationListener

    @Unroll
    def "Test valid #resourceUri template generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Create yang data root node"
            YangDataNode yangDataRootNode = new YangDataRoot()
        and: "Mock restconfNbiMaximumNodesHierarchyLevel PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> hierarchyLevel
        when: "Generate template as per given restconf resource uri"
            restconfYangDataNodeTemplateProvider.generateAndGetTemplateDataNode(restconfResourceUri, yangDataRootNode)
        and: "Calculate yang data tree level"
            AtomicInteger level = new AtomicInteger(0)
            AtomicInteger indent = new AtomicInteger(0)
            calculateDataNodesLevel(yangDataRootNode, level, indent)
        then: "Verify generated template"
            yangDataRootNode != null
            yangDataRootNode.getDataNodes().size() != 0
            level.get() == maxDataNodeLevel
        where:
            resourceUri                                                                              | hierarchyLevel || maxDataNodeLevel
            "ietf-interfaces:interfaces"                                                             | 1              || 8
            "ietf-interfaces:interfaces/interface=LAN-1"                                             | 1              || 8
            "ietf-network:networks"                                                                  | 1              || 436
            "ietf-network:networks/network=1"                                                        | 1              || 402
            "ietf-network:networks/network=1/node=node1"                                             | 1              || 326
            "ietf-network:networks/network=1/node=node1/ietf-network-topology:termination-point=tp1" | 1              || 24
            "ietf-interfaces:interfaces"                                                             | 10             || 8
            "ietf-interfaces:interfaces/interface=LAN-1"                                             | 10             || 8
            "ietf-network:networks"                                                                  | 10             || 436
            "ietf-network:networks/network=1"                                                        | 10             || 402
            "ietf-network:networks/network=1/node=node1"                                             | 10             || 326
            "ietf-network:networks/network=1/node=node1/ietf-network-topology:termination-point=tp1" | 10             || 24
            "ietf-interfaces:interfaces"                                                             | -1             || 8
            "ietf-interfaces:interfaces/interface=LAN-1"                                             | -1             || 8
            "ietf-network:networks"                                                                  | -1             || 436
            "ietf-network:networks/network=1"                                                        | -1             || 402
            "ietf-network:networks/network=1/node=node1"                                             | -1             || 326
            "ietf-network:networks/network=1/node=node1/ietf-network-topology:termination-point=tp1" | -1             || 24
    }

    @Unroll
    def "Test invalid hierarchy #resourceUri template generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Create yang data root node"
            YangDataNode yangDataRootNode = new YangDataRoot()
        when: "Generate template as per given restconf resource uri"
            restconfYangDataNodeTemplateProvider.generateAndGetTemplateDataNode(restconfResourceUri, yangDataRootNode)
        then: "Verify exception thrown while generating template"
            def exception = thrown(RestconfYangHierarchyValidationException)
            exception.message.contains(message)
        where:
            resourceUri                                                     || message
            "ietf-networks:networks"                                        || "ietf-networks:networks"
            "ietf-network:invalid"                                          || "ietf-network:invalid"
            "ietf-invalid:invalid"                                          || "ietf-invalid:invalid"
            "ietf-network:networks/network=1/invalid=Transport77ML6691-001" || "ietf-network:invalid"
    }

    @Unroll
    def "Test invalid data construct types #resourceUri template generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Create yang data root node"
            YangDataNode yangDataRootNode = new YangDataRoot()
        when: "Generate template as per given restconf resource uri"
            restconfYangDataNodeTemplateProvider.generateAndGetTemplateDataNode(restconfResourceUri, yangDataRootNode)
        then: "Verify exception thrown while generating template"
            def exception = thrown(RestconfYangHierarchyValidationException)
            exception.message.contains(message)
        where:
            resourceUri                                                             || message
            "ietf-network:networks=1"                                               || "ietf-network:networks=1; expecting an api-identifier type!"
            "ietf-network:networks/network/node=Transport77ML6691-001"              || "network; expecting list type!"
            "ietf-network:networks/network=1,2/node=Transport77ML6691-001"          || "network=1,2; expecting 1 key(s)!"
            "ietf-network:networks/network=1/node=Transport77ML6691-001/node-id=10" || "Unexpected values for a leaf at step: [ietf-network:node-id]"
    }
}
