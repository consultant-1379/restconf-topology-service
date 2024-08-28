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
package com.ericsson.oss.services.restconf.topologyservice.api.yang.dto

import com.ericsson.cds.cdi.support.spock.CdiSpecification
import spock.lang.Unroll

class YangDataNodeSpec extends CdiSpecification {

    @Unroll
    def "Test yang node object creation"() {
        given: "create yang data root"
            YangDataNode yangRoot = new AbstractYangDataNode.YangDataNodeBuilder().root()
        when: "create and add interfaces container under yang data root"
            YangDataNode interfacesContainer = new AbstractYangDataNode.YangDataNodeBuilder("module", "interfaces").container()
            ((YangDataRoot) yangRoot).addDataNode(interfacesContainer)
        and: "create and add interface list under interfaces container"
            YangDataNode interfaceList = new AbstractYangDataNode.YangDataNodeBuilder("module", "interface").list()
            interfacesContainer.addDataNode(interfaceList)
        and: "create lag interface list item"
            YangDataNode nameLeaf = new AbstractYangDataNode.YangDataNodeBuilder("module", "name").leaf("1/1")
            YangDataNode typeLeaf = new AbstractYangDataNode.YangDataNodeBuilder("module", "type").leaf("lag")
            YangDataNode errorsLeafList = new AbstractYangDataNode.YangDataNodeBuilder("module", "errors").leafList(Arrays.asList("error1", "error2"))
            YangDataNode lagInterface = new AbstractYangDataNode.YangDataNodeBuilder().dataSet(nameLeaf, typeLeaf, errorsLeafList)
        and: "add lag interface list item under interface list"
            interfaceList.addDataNode(lagInterface)
        and: "create ethernet interface list item"
            nameLeaf = new AbstractYangDataNode.YangDataNodeBuilder("module", "name").leaf("1/2")
            typeLeaf = new AbstractYangDataNode.YangDataNodeBuilder("module", "type").leaf("ethernet")
            YangDataNode ethernetInterface = new AbstractYangDataNode.YangDataNodeBuilder().dataSet(nameLeaf, typeLeaf)
        and: "add ethernet interface list item under interface list"
            interfaceList.addDataNode(ethernetInterface)
        then: "verify root yang node created"
            assert yangRoot != null
    }
}
