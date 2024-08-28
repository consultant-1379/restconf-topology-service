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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.link;

import java.util.Map;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.DatabaseNotAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Link Destination data provider.
 */
@YangDataProvider(yangModel = "ietf-network-topology:destination", eModelMapping = @EModelMapping())
public class LinkDestinationDataProvider extends AbstractRestconfDataProvider {
    public LinkDestinationDataProvider() {
        super(LinkDestinationDataProvider.class);
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException, DatabaseNotAvailableException {
        setLinkDestinationValues(yangDataNode);
    }

    private void setLinkDestinationValues(final YangDataNode yangDataNode) {
        final YangDataNode linkYangDataNode = yangDataNode.getParent().getParent();
        final Map<String, String> cacheAttributes = ((AbstractYangDataNode) linkYangDataNode).getCacheAttributes();
        if (cacheAttributes != null && !cacheAttributes.isEmpty()) {
            final String destTp = cacheAttributes.get("dest-tp");
            if (destTp != null) {
                yangDataNode.addDataNode(new YangDataNodeBuilder(yangDataNode, "dest-node").leaf(getNodeName(destTp)));
                yangDataNode.addDataNode(new YangDataNodeBuilder(yangDataNode, "dest-tp").leaf(getFdnValue(destTp)));
            }
            ((AbstractYangDataNode) linkYangDataNode).resetCacheAttributes();
        }
    }
}
