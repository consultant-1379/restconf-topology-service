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
 * Link data provider.
 */
@YangDataProvider(yangModel = "ietf-network-topology:link", eModelMapping = @EModelMapping(eModel = "Link", mapping = {
        @AttributeMap(yang = "link-id", eModel = "link-id"), @AttributeMap(yang = "", eModel = "link-name"),
        @AttributeMap(yang = "", eModel = "description"), @AttributeMap(yang = "", eModel = "active"),
        @AttributeMap(yang = "", eModel = "link-state"), @AttributeMap(yang = "", eModel = "source-tp"), @AttributeMap(yang = "", eModel = "dest-tp"),
        @AttributeMap(yang = "", eModel = "type"), @AttributeMap(yang = "", eModel = "layer-rate"),
        @AttributeMap(yang = "", eModel = "sdn-applicable"), @AttributeMap(yang = "", eModel = "sdn-populated"),
        @AttributeMap(yang = "", eModel = "supporting-links-number"), @AttributeMap(yang = "", eModel = "supporting-links-layer-rate"),
        @AttributeMap(yang = "", eModel = "supporting-links"), @AttributeMap(yang = "", eModel = "supported-links"),
        @AttributeMap(yang = "", eModel = "alarm-status"), @AttributeMap(yang = "", eModel = "created-by"),
        @AttributeMap(yang = "", eModel = "creation-date"), @AttributeMap(yang = "", eModel = "discovery-state") }, cacheAttributes = { "source-tp",
        "dest-tp" }))
public class LinkDataProvider extends AbstractRestconfDataProvider {
    public LinkDataProvider() {
        super(LinkDataProvider.class);
    }
}
