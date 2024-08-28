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
 * Link Source data provider.
 */
@YangDataProvider(yangModel = "ietf-network-topology:source", eModelMapping = @EModelMapping())
public class LinkSourceDataProvider extends AbstractRestconfDataProvider {
    public LinkSourceDataProvider() {
        super(LinkSourceDataProvider.class);
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException, DatabaseNotAvailableException {
        setLinkSourceValues(yangDataNode);
    }

    private void setLinkSourceValues(final YangDataNode yangDataNode) {
        final YangDataNode linkYangDataNode = yangDataNode.getParent().getParent();
        final Map<String, String> cacheAttributes = ((AbstractYangDataNode) linkYangDataNode).getCacheAttributes();
        if (cacheAttributes != null && !cacheAttributes.isEmpty()) {
            final String sourceTp = cacheAttributes.get("source-tp");
            if (sourceTp != null) {
                yangDataNode.addDataNode(new YangDataNodeBuilder(yangDataNode, "source-node").leaf(getNodeName(sourceTp)));
                yangDataNode.addDataNode(new YangDataNodeBuilder(yangDataNode, "source-tp").leaf(getFdnValue(sourceTp)));
            }
        }
    }
}
