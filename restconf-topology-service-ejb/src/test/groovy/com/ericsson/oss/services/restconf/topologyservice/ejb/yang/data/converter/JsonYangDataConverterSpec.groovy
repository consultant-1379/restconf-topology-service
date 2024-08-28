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

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.SerializationFeature
import spock.lang.Unroll

class JsonYangDataConverterSpec extends CdiSpecification {

    @ObjectUnderTest
    JsonYangDataNodeConverter jsonYangDataNodeConverter

    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT)

    @Unroll
    def "Test json encoding yang data root with interfaces module"() {
        given: "create yang data root"
            YangDataNode yangRoot = new YangDataRoot()
            YangDataNodeBuilder interfacesYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-interfaces", "ietf-interfaces")
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
        and: "encode yang root object to json"
            String jsonData = jsonYangDataNodeConverter.encode(yangRoot)
        then: "verify json data generated"
            assert jsonData != null
            println(mapper.writeValueAsString(mapper.readTree(jsonData)))
            final File file = new File("src/test/resources/output/test_interfaces.json")
            assert mapper.readTree(file.text) == mapper.readTree(jsonData)
    }

    @Unroll
    def "Test json encoding yang data root with networks module"() {
        given: "create yang data root"
            YangDataNode yangRoot = new YangDataRoot()
            YangDataNodeBuilder networksYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-network", "ietf-networks")
            YangDataNodeBuilder nodeYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-node", "ietf-node")
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
            nameLeaf = nodeYangDataNodeBuilder.newInstance("name").leaf("global")
            typeLeaf = nodeYangDataNodeBuilder.newInstance("type").leaf("wan")
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
        and: "encode yang root object to json"
            String jsonData = jsonYangDataNodeConverter.encode(yangRoot)
        then: "verify json data"
            assert jsonData != null
            println(mapper.writeValueAsString(mapper.readTree(jsonData)))
            final File file = new File("src/test/resources/output/test_networks.json")
            assert mapper.readTree(file.text) == mapper.readTree(jsonData)
    }
}
