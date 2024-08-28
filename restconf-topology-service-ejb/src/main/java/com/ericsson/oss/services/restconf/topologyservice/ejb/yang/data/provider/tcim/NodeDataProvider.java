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

import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.AttributeMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Node data provider.
 */
@YangDataProvider(yangModel = "ietf-network:node", eModelMapping = @EModelMapping(eModel = "Node", mapping = {
        @AttributeMap(yang = "node-id", eModel = "node-id"), @AttributeMap(yang = "", eModel = "lldp-sys-name"),
        @AttributeMap(yang = "", eModel = "neType") }))
public class NodeDataProvider extends AbstractRestconfDataProvider {

    public NodeDataProvider() {
        super(NodeDataProvider.class);
    }

}
