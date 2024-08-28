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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator

import java.util.concurrent.atomic.AtomicInteger

import javax.inject.Inject

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri
import com.ericsson.oss.services.restconf.topologyservice.api.parser.RestconfResourceUriParser
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.operation.RestconfDataOperation
import com.ericsson.oss.services.restconf.topologyservice.ejb.listener.RestconfConfigurationListener
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter.JsonYangDataNodeConverter
import com.ericsson.oss.services.restconf.topologyservice.test.common.BaseRestconfTopologyServiceResourceSpec
import spock.lang.Ignore
import spock.lang.Unroll

class RestconfYangDataGeneratorSpec extends BaseRestconfTopologyServiceResourceSpec {

    @ObjectUnderTest
    RestconfYangDataGenerator restconfYangDataGenerator

    @Inject
    RestconfResourceUriParser restconfResourceUriParser

    @Inject
    JsonYangDataNodeConverter jsonYangDataNodeConverter

    @SpyImplementation
    RestconfConfigurationListener restconfConfigurationListener

    @Unroll
    def "Test #resourceUri data generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Populate TCIM database"
            populateDbFromTcimDynamic("src/test/resources/input/tcim_data/export_dynamic_sample1.txt")
        and: "Mock restconfNbiMaximumNodesHierarchyLevel and restconfNbiMaximumMoProcess PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> 1
            restconfConfigurationListener.getRestconfNbiMaximumMoProcess() >> 1000
        when: "Generate template as per given restconf resource uri"
            YangDataNode yangDataRootNode = restconfYangDataGenerator.generate(restconfResourceUri, RestconfDataOperation.Type.READ)
        then: "Verify generated data"
            yangDataRootNode != null
            yangDataRootNode.getDataNodes().size() != 0
            AtomicInteger level = new AtomicInteger(0)
            AtomicInteger indent = new AtomicInteger(0)
            calculateDataNodesLevel(yangDataRootNode, level, indent)
            println(mapper.writeValueAsString(mapper.readTree(jsonYangDataNodeConverter.encode(yangDataRootNode))))
        and: "Assert instrumentation data"
            final Map<String, Object> instr = restconfContext.getRestconfInstrumentationBuilder().build()
            instr.get("totalMOsRead") == totalMOsRead
            instr.get("totalNodes") == totalNodes
        where:
            resourceUri                                                                                                            || totalMOsRead || totalNodes
            "ietf-interfaces:interfaces"                                                                                           || 26           || 21
            "ietf-interfaces:interfaces/interface=ml6352-02:LAG-1"                                                                 || 1            || 1
            "ietf-network:networks"                                                                                                || 12           || 10
            "ietf-network:networks/network=1"                                                                                      || 12           || 10
            "ietf-network:networks/network=1/node=ml6352-02"                                                                       || 3            || 2
            "ietf-network:networks/network=1/node=ml6352-02/ietf-network-topology:termination-point=ml6352-02_1_LAN-1%252F1%252F4" || 3            || 2
            "ietf-microwave-radio-link:xpic-pairs"                                                                                 || 2            || 2
            "ietf-microwave-radio-link:xpic-pairs/xpic-pair=ml6352-01:1"                                                           || 1            || 1
    }

    @Ignore
    @Unroll
    def "Performance test on 20K network data generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Populate TCIM database"
            create20KNetwork()
        and: "Mock restconfNbiMaximumNodesHierarchyLevel PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> 1
        when: "Generate template as per given restconf resource uri"
            YangDataNode yangDataRootNode = restconfYangDataGenerator.generate(restconfResourceUri, RestconfDataOperation.Type.READ)
        then: "Verify generated data"
            yangDataRootNode != null
            yangDataRootNode.getDataNodes().size() != 0
            println("Total yang data object size: " + getObjectSize(yangDataRootNode))
        and: "Assert instrumentation data"
            final Map<String, Object> instr = restconfContext.getRestconfInstrumentationBuilder().build()
            instr.get("totalMOsRead") == totalMOsRead
            instr.get("totalNodes") == totalNodes
        where:
            resourceUri             || totalMOsRead || totalNodes
            "ietf-network:networks" || 20_001       || 20_000
    }

    @Ignore
    @Unroll
    def "Performance test on 50K interfaces data generation"() {
        given: "Create restconf resource uri object"
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        and: "Populate TCIM database"
            create20KNetworkWith50KInterfaces()
        and: "Mock restconfNbiMaximumNodesHierarchyLevel PIB"
            restconfConfigurationListener.getRestconfNbiMaximumNodesHierarchyLevel() >> 1
        when: "Generate template as per given restconf resource uri"
            YangDataNode yangDataRootNode = restconfYangDataGenerator.generate(restconfResourceUri, RestconfDataOperation.Type.READ)
        then: "Verify generated data"
            yangDataRootNode != null
            yangDataRootNode.getDataNodes().size() != 0
            println("Total yang data object size: " + getObjectSize(yangDataRootNode))
        and: "Assert instrumentation data"
            final Map<String, Object> instr = restconfContext.getRestconfInstrumentationBuilder().build()
            instr.get("totalMOsRead") == totalMOsRead
            instr.get("totalNodes") == totalNodes
        where:
            resourceUri                  || totalMOsRead || totalNodes
            "ietf-interfaces:interfaces" || 20_001       || 20_000
    }

    def create20KNetwork() {
        final Map<String, Object> attributes = new HashMap<>()
        attributes.put("network-id", "1")
        attributes.put("network-type", "")
        final ManagedObject networkMo = runtimeDps.addManagedObject()
                .withFdn("Network=1").addAttributes(attributes).namespace("OSS_TCIM").version("1.0.0").build()

        attributes.clear()
        attributes.put("normalization-state", "NORMALIZED")
        for (int index = 0; index < 20_000; index++) {
            attributes.put("node-id", "node" + index)
            runtimeDps.addManagedObject()
                    .withFdn("Network=1" + ",Node=node" + index).addAttributes(attributes)
                    .parent(networkMo).namespace("OSS_TCIM").version("1.0.0").build()
        }
    }

    def create20KNetworkWith50KInterfaces() {
        final Map<String, Object> networkAttributes = new HashMap<>()
        networkAttributes.put("network-id", "1")
        networkAttributes.put("network-type", "")
        final ManagedObject networkMo = runtimeDps.addManagedObject()
                .withFdn("Network=1").addAttributes(networkAttributes).namespace("OSS_TCIM").version("1.0.0").build()

        final Map<String, Object> nodeAttributes = new HashMap<>()
        nodeAttributes.put("normalization-state", "NORMALIZED")

        final Map<String, Object> interfacesAttributes = new HashMap<>()
        interfacesAttributes.put("interfaces-id", "1")

        final Map<String, Object> interfaceAttributes = new HashMap<>()
        for (int nodeIndex = 0; nodeIndex < 20_000; nodeIndex++) {
            nodeAttributes.put("node-id", "node" + nodeIndex)
            final ManagedObject nodeMo = runtimeDps.addManagedObject()
                    .withFdn("Network=1" + ",Node=node" + nodeIndex).addAttributes(nodeAttributes)
                    .parent(networkMo).namespace("OSS_TCIM").version("1.0.0").build()


            final ManagedObject interfacesMo = runtimeDps.addManagedObject()
                    .withFdn(nodeMo.getFdn() + ",Interfaces=1").addAttributes(interfacesAttributes)
                    .parent(nodeMo).namespace("OSS_TCIM").version("1.0.0").build()

            for (int ifIndex = 0; ifIndex < 2_500; ifIndex++) {
                final String ifName = "IF-" + nodeIndex + "/" + ifIndex + "/0"
                interfaceAttributes.put("name", ifName)
                runtimeDps.addManagedObject()
                        .withFdn(interfacesMo.getFdn() + ",Interface=" + ifName).addAttributes(interfaceAttributes)
                        .parent(networkMo).namespace("OSS_TCIM").version("1.0.0").build()
            }
        }
    }


    def getObjectSize(YangDataNode yangDataNode) {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream()
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream)

        objectOutputStream.writeObject(yangDataNode)
        objectOutputStream.flush()
        objectOutputStream.close()

        return byteOutputStream.toByteArray().length
    }
}