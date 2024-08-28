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
 * Radio link terminal data provider.
 */
@YangDataProvider(yangModel = "ietf-interfaces:interface", eModelMapping = @EModelMapping(eModel = "RadioLinkTerminal", mapping = {
        @AttributeMap(yang = "id", eModel = "terminal-id"), @AttributeMap(yang = "", eModel = "terminal-Id"),
        @AttributeMap(yang = "mode", eModel = "mode"), @AttributeMap(yang = "", eModel = "node-name"),
        @AttributeMap(yang = "carrier-terminations", eModel = "carrier-terminations"), @AttributeMap(yang = "rlp-groups", eModel = "rlp-groups"),
        @AttributeMap(yang = "xpic-pairs", eModel = "xpic-pairs"), @AttributeMap(yang = "mimo-groups", eModel = "mimo-group"),
        @AttributeMap(yang = "", eModel = "far-end-terminal-id"), @AttributeMap(yang = "", eModel = "far-end-terminal-Id"),
        @AttributeMap(yang = "", eModel = "far-end-node-name"), @AttributeMap(yang = "tdm-connections", eModel = "tdm-connection") }))
public class RadioLinkTerminalDataProvider extends AbstractRestconfDataProvider {
    public RadioLinkTerminalDataProvider() {
        super(RadioLinkTerminalDataProvider.class);
    }
}
