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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.DatabaseNotAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfMoThresholdException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.AttributeMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * MimoGroup data provider.
 */
@YangDataProvider(yangModel = "ietf-microwave-radio-link:mimo-group", eModelMapping = @EModelMapping(eModel = "MimoGroup", mapping = {
        @AttributeMap(yang = "name", eModel = "name"), @AttributeMap(yang = "enabled", eModel = "enable"),
        @AttributeMap(yang = "members", eModel = "mimo-members") }))
public class MimoGroupDataProvider extends AbstractRestconfDataProvider {
    public MimoGroupDataProvider() {
        super(MimoGroupDataProvider.class);
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException, DatabaseNotAvailableException, RestconfMoThresholdException {
        super.read(yangDataNode);
        generateAndSetDataLeafValue(yangDataNode);
        processFdnBasedAttributes(yangDataNode);
    }

    private void processFdnBasedAttributes(final YangDataNode yangDataNode) {
        for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
            for (final YangDataNode childDataNode : dataNode.getDataNodes()) {
                if ("members".equals(childDataNode.getNodeName())) {
                    extractMembersDetailsAndSet((YangDataLeafList) childDataNode);
                }
            }
        }
    }

    private void extractMembersDetailsAndSet(final YangDataLeafList yangDataLeafList) {
        if (!yangDataLeafList.getItems().isEmpty()) {
            final List<String> modifiedItems = new ArrayList<>();
            for (final String itemValue : yangDataLeafList.getItems()) {
                if (itemValue.startsWith("Network=")) {
                    final String[] tokens = itemValue.split(";");
                    for (final String interfaceFdn : tokens) {
                        modifiedItems.add(getNodeName(interfaceFdn) + ":" + getInterfaceName(interfaceFdn));
                    }
                } else {
                    modifiedItems.add(itemValue);
                }
            }
            yangDataLeafList.setItems(modifiedItems);
        }
    }
}
