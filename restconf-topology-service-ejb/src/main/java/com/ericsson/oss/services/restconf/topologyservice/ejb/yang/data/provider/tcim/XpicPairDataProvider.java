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
 * Xpic pair data provider.
 */
@YangDataProvider(yangModel = "ietf-microwave-radio-link:xpic-pair", eModelMapping = @EModelMapping(eModel = "XpicPair", mapping = {
        @AttributeMap(yang = "name", eModel = "name"), @AttributeMap(yang = "enabled", eModel = "enable"),
        @AttributeMap(yang = "members", eModel = "xpic-members") }))
public class XpicPairDataProvider extends AbstractRestconfDataProvider {
    public XpicPairDataProvider() {
        super(XpicPairDataProvider.class);
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
                    extractInterfaceDetailsAndSet((YangDataLeafList) childDataNode);
                }
            }
        }
    }
}
