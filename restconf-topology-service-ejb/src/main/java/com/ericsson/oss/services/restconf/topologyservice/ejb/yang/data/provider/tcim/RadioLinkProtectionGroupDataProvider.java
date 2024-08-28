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
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EnumMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Radio link protection group data provider.
 */
@YangDataProvider(yangModel = "ietf-microwave-radio-link:protection-group",
        eModelMapping = @EModelMapping(eModel = "RadioLinkProtectionGroup", mapping = {
        @AttributeMap(yang = "name", eModel = "name"),
        @AttributeMap(yang = "protection-architecture-type", eModel = "protection-type", enumMapping = {
                @EnumMap(yang = "one-plus-one-type",eModel = "ONE-PLUS-ONE"),
                @EnumMap(yang = "one-to-n-type", eModel = "ONE-TO-N"),
                @EnumMap(yang = "", eModel = "OTHER"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ZERO"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ONE-RLP"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ONE-RLP-H"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ONE-RLP-W"),
                @EnumMap(yang = "", eModel = "UNPROTECTED-SDC"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ONE-E-RLB"),
                @EnumMap(yang = "", eModel = "TWO-PLUS-ZERO-E-RLB"),
                @EnumMap(yang = "", eModel = "TWO-PLUS-ZERO-RLB"),
                @EnumMap(yang = "", eModel = "ONE-PLUS-ONE-RLP-EQP"),
                @EnumMap(yang = "", eModel = "TWO-PLUS-ZERO-RLB-EQP"),
                @EnumMap(yang = "", eModel = "TWO-PLUS-TWO-RLP-EQP"),
                @EnumMap(yang = "", eModel = "FOUR-PLUS-ZERO-RLB-EQP")
        }),
        @AttributeMap(yang = "operation-type", eModel = "operation-type", enumMapping = {
                @EnumMap(yang = "revertive",eModel = "REVERTIVE"),
                @EnumMap(yang = "non-revertive", eModel = "NON_REVERTIVE"),
                @EnumMap(yang = "", eModel = "OTHER"),
                @EnumMap(yang = "", eModel = "MANUAL"),
                @EnumMap(yang = "", eModel = "AUTOMATIC"),
                @EnumMap(yang = "", eModel = "AUTO_REVERTIVE"),
                @EnumMap(yang = "", eModel = "TX_SWITCH_DISABLE")
        }),
        @AttributeMap(yang = "working-entity", eModel = "working-entity"),
        @AttributeMap(yang = "members", eModel = "rl-protection-member") }))
public class RadioLinkProtectionGroupDataProvider extends AbstractRestconfDataProvider {
    public RadioLinkProtectionGroupDataProvider() {
        super(RadioLinkProtectionGroupDataProvider.class);
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
                switch (childDataNode.getNodeName()) {
                    case "members":
                    case "working-entity":
                        extractInterfaceDetailsAndSet((YangDataLeafList) childDataNode);
                        break;
                    default:
                        // Blank on purpose
                }
            }
        }
    }


}
