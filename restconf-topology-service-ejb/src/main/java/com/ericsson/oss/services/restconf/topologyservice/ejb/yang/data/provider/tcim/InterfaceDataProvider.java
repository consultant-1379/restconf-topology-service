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
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EnumMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.WhenEModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Interface data provider.
 */
@YangDataProvider(yangModel = "ietf-interfaces:interface", eModelMapping = @EModelMapping(eModel = "Interface", mapping = {
        @AttributeMap(yang = "name", eModel = "name"), @AttributeMap(yang = "description", eModel = "description"),
        @AttributeMap(yang = "type", eModel = "type", enumMapping = {
                @EnumMap(yang = "ethernetCsmacd",eModel = "ethernetCsmacd"),
                @EnumMap(yang = "ds1", eModel = "ds1"),
                @EnumMap(yang = "sonet", eModel = "sonet"),
                @EnumMap(yang = "ieee8023adLag",eModel = "ieee8023adLag"),
                @EnumMap(yang = "", eModel = "cpri"),
                @EnumMap(yang = "microwaveRadioLinkTerminal", eModel = "radioLinkTerminal"),
                @EnumMap(yang = "microwaveCarrierTermination", eModel = "carrierTermination"),
                @EnumMap(yang = "",eModel = "sdh"),
                @EnumMap(yang = "hdlc", eModel = "hdlc"),
                @EnumMap(yang = "propMultiplexor", eModel = "propMultiplexor")
        }), @AttributeMap(yang = "enabled", eModel = "enabled"),
        @AttributeMap(yang = "", eModel = "layer-rate"), @AttributeMap(yang = "", eModel = "link-tp"),
        @AttributeMap(yang = "higher-layer-if", eModel = "higher-layer-if"), @AttributeMap(yang = "lower-layer-if", eModel = "lower-layer-if"),
        @AttributeMap(yang = "speed", eModel = "speed"), @AttributeMap(yang = "", eModel = "alarm-status"),
        @AttributeMap(yang = "", eModel = "link-type"), }),
        whenMapping = {
        @WhenEModelMapping(attribute = "type", value = "carrierTermination", eModelMapping = @EModelMapping(eModel = "CarrierTermination", mapping = {
            @AttributeMap(yang = "carrier-id", eModel = "carrier-id"), @AttributeMap(yang = "tx-enabled", eModel = "tx-enabled"),
            @AttributeMap(yang = "tx-oper-status", eModel = ""), @AttributeMap(yang = "tx-frequency", eModel = "tx-frequency"),
            @AttributeMap(yang = "rx-frequency", eModel = "rx-frequency"), @AttributeMap(yang = "", eModel = "rx-frequency-config"),
            @AttributeMap(yang = "duplex-distance", eModel = "duplex-distance"),
            @AttributeMap(yang = "channel-separation", eModel = "channel-separation"),
            @AttributeMap(yang = "polarization", eModel = "polarization", enumMapping = {
                    @EnumMap(yang = "horizontal",eModel = "HORIZONTAL"),
                    @EnumMap(yang = "vertical", eModel = "VERTICAL"),
                    @EnumMap(yang = "not-specified", eModel = "NOT_SPECIFIED")
            }),
            @AttributeMap(yang = "", eModel = "coding-modulation-mode"),
            @AttributeMap(yang = "selected-cm", eModel = "selected-cm", enumMapping = {
                    @EnumMap(yang = "half-bpsk-strong",eModel = "HALF-BPSK-STRONG"),
                    @EnumMap(yang = "half-bpsk", eModel = "HALF-BPSK"),
                    @EnumMap(yang = "half-bpsk-light", eModel = "HALF-BPSK-LIGHT"),
                    @EnumMap(yang = "bpsk-strong",eModel = "BPSK-STRONG"),
                    @EnumMap(yang = "bpsk", eModel = "BPSK"),
                    @EnumMap(yang = "bpsk-light", eModel = "BPSK-LIGHT"),
                    @EnumMap(yang = "qpsk",eModel = "QPSK"),
                    @EnumMap(yang = "qam-4", eModel = "QAM-4"),
                    @EnumMap(yang = "qam-4-light", eModel = "QAM-4-LIGHT"),
                    @EnumMap(yang = "qam-16-strong",eModel = "QAM-16-STRONG"),
                    @EnumMap(yang = "qam-16", eModel = "QAM-16"),
                    @EnumMap(yang = "qam-16-light", eModel = "QAM-16-LIGHT"),
                    @EnumMap(yang = "qam-32-strong",eModel = "QAM-32-STRONG"),
                    @EnumMap(yang = "qam-32", eModel = "QAM-32"),
                    @EnumMap(yang = "qam-32-light", eModel = "QAM-32-LIGHT"),
                    @EnumMap(yang = "qam-64-strong",eModel = "QAM-64-STRONG"),
                    @EnumMap(yang = "qam-64", eModel = "QAM-64"),
                    @EnumMap(yang = "qam-64-light", eModel = "QAM-64-LIGHT"),
                    @EnumMap(yang = "qam-128-strong",eModel = "QAM-128-STRONG"),
                    @EnumMap(yang = "qam-128", eModel = "QAM-128"),
                    @EnumMap(yang = "qam-128-light", eModel = "QAM-128-LIGHT"),
                    @EnumMap(yang = "qam-256-strong",eModel = "QAM-256-STRONG"),
                    @EnumMap(yang = "qam-256", eModel = "QAM-256"),
                    @EnumMap(yang = "qam-256-light", eModel = "QAM-256-LIGHT"),
                    @EnumMap(yang = "qam-512-strong",eModel = "QAM-512-STRONG"),
                    @EnumMap(yang = "qam-512", eModel = "QAM-512"),
                    @EnumMap(yang = "qam-512-light", eModel = "QAM-512-LIGHT"),
                    @EnumMap(yang = "qam-1024-strong",eModel = "QAM-1024-STRONG"),
                    @EnumMap(yang = "qam-1024", eModel = "QAM-1024"),
                    @EnumMap(yang = "qam-1024-light", eModel = "QAM-1024-LIGHT"),
                    @EnumMap(yang = "qam-2048-strong",eModel = "QAM-2048-STRONG"),
                    @EnumMap(yang = "qam-2048", eModel = "QAM-2048"),
                    @EnumMap(yang = "qam-2048-light", eModel = "QAM-2048-LIGHT"),
                    @EnumMap(yang = "qam-4096-strong",eModel = "QAM-4096-STRONG"),
                    @EnumMap(yang = "qam-4096", eModel = "QAM-4096"),
                    @EnumMap(yang = "qam-4096-light", eModel = "QAM-4096-LIGHT"),
                    @EnumMap(yang = "",eModel = "QAM-8"),
                    @EnumMap(yang = "", eModel = "CQPSK"),
                    @EnumMap(yang = "", eModel = "OTHER"),
                    @EnumMap(yang = "qam-4-strong",eModel = "QAM-4-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192"),
                    @EnumMap(yang = "",eModel = "QAM-8192-LIGHT"),
                    @EnumMap(yang = "", eModel = "QAM-16384-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-16384"),
                    @EnumMap(yang = "",eModel = "QAM-16384-LIGHT")
            }),
            @AttributeMap(yang = "selected-min-acm", eModel = "selected-min-acm" , enumMapping = {
                    @EnumMap(yang = "half-bpsk-strong",eModel = "HALF-BPSK-STRONG"),
                    @EnumMap(yang = "half-bpsk", eModel = "HALF-BPSK"),
                    @EnumMap(yang = "half-bpsk-light", eModel = "HALF-BPSK-LIGHT"),
                    @EnumMap(yang = "bpsk-strong",eModel = "BPSK-STRONG"),
                    @EnumMap(yang = "bpsk", eModel = "BPSK"),
                    @EnumMap(yang = "bpsk-light", eModel = "BPSK-LIGHT"),
                    @EnumMap(yang = "qpsk",eModel = "QPSK"),
                    @EnumMap(yang = "qam-4", eModel = "QAM-4"),
                    @EnumMap(yang = "qam-4-light", eModel = "QAM-4-LIGHT"),
                    @EnumMap(yang = "qam-16-strong",eModel = "QAM-16-STRONG"),
                    @EnumMap(yang = "qam-16", eModel = "QAM-16"),
                    @EnumMap(yang = "qam-16-light", eModel = "QAM-16-LIGHT"),
                    @EnumMap(yang = "qam-32-strong",eModel = "QAM-32-STRONG"),
                    @EnumMap(yang = "qam-32", eModel = "QAM-32"),
                    @EnumMap(yang = "qam-32-light", eModel = "QAM-32-LIGHT"),
                    @EnumMap(yang = "qam-64-strong",eModel = "QAM-64-STRONG"),
                    @EnumMap(yang = "qam-64", eModel = "QAM-64"),
                    @EnumMap(yang = "qam-64-light", eModel = "QAM-64-LIGHT"),
                    @EnumMap(yang = "qam-128-strong",eModel = "QAM-128-STRONG"),
                    @EnumMap(yang = "qam-128", eModel = "QAM-128"),
                    @EnumMap(yang = "qam-128-light", eModel = "QAM-128-LIGHT"),
                    @EnumMap(yang = "qam-256-strong",eModel = "QAM-256-STRONG"),
                    @EnumMap(yang = "qam-256", eModel = "QAM-256"),
                    @EnumMap(yang = "qam-256-light", eModel = "QAM-256-LIGHT"),
                    @EnumMap(yang = "qam-512-strong",eModel = "QAM-512-STRONG"),
                    @EnumMap(yang = "qam-512", eModel = "QAM-512"),
                    @EnumMap(yang = "qam-512-light", eModel = "QAM-512-LIGHT"),
                    @EnumMap(yang = "qam-1024-strong",eModel = "QAM-1024-STRONG"),
                    @EnumMap(yang = "qam-1024", eModel = "QAM-1024"),
                    @EnumMap(yang = "qam-1024-light", eModel = "QAM-1024-LIGHT"),
                    @EnumMap(yang = "qam-2048-strong",eModel = "QAM-2048-STRONG"),
                    @EnumMap(yang = "qam-2048", eModel = "QAM-2048"),
                    @EnumMap(yang = "qam-2048-light", eModel = "QAM-2048-LIGHT"),
                    @EnumMap(yang = "qam-4096-strong",eModel = "QAM-4096-STRONG"),
                    @EnumMap(yang = "qam-4096", eModel = "QAM-4096"),
                    @EnumMap(yang = "qam-4096-light", eModel = "QAM-4096-LIGHT"),
                    @EnumMap(yang = "",eModel = "QAM-8"),
                    @EnumMap(yang = "", eModel = "CQPSK"),
                    @EnumMap(yang = "", eModel = "OTHER"),
                    @EnumMap(yang = "qam-4-strong",eModel = "QAM-4-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192"),
                    @EnumMap(yang = "",eModel = "QAM-8192-LIGHT"),
                    @EnumMap(yang = "", eModel = "QAM-16384-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-16384"),
                    @EnumMap(yang = "",eModel = "QAM-16384-LIGHT")
            }),
            @AttributeMap(yang = "selected-max-acm", eModel = "selected-max-acm" , enumMapping = {
                    @EnumMap(yang = "half-bpsk-strong",eModel = "HALF-BPSK-STRONG"),
                    @EnumMap(yang = "half-bpsk", eModel = "HALF-BPSK"),
                    @EnumMap(yang = "half-bpsk-light", eModel = "HALF-BPSK-LIGHT"),
                    @EnumMap(yang = "bpsk-strong",eModel = "BPSK-STRONG"),
                    @EnumMap(yang = "bpsk", eModel = "BPSK"),
                    @EnumMap(yang = "bpsk-light", eModel = "BPSK-LIGHT"),
                    @EnumMap(yang = "qpsk",eModel = "QPSK"),
                    @EnumMap(yang = "qam-4", eModel = "QAM-4"),
                    @EnumMap(yang = "qam-4-light", eModel = "QAM-4-LIGHT"),
                    @EnumMap(yang = "qam-16-strong",eModel = "QAM-16-STRONG"),
                    @EnumMap(yang = "qam-16", eModel = "QAM-16"),
                    @EnumMap(yang = "qam-16-light", eModel = "QAM-16-LIGHT"),
                    @EnumMap(yang = "qam-32-strong",eModel = "QAM-32-STRONG"),
                    @EnumMap(yang = "qam-32", eModel = "QAM-32"),
                    @EnumMap(yang = "qam-32-light", eModel = "QAM-32-LIGHT"),
                    @EnumMap(yang = "qam-64-strong",eModel = "QAM-64-STRONG"),
                    @EnumMap(yang = "qam-64", eModel = "QAM-64"),
                    @EnumMap(yang = "qam-64-light", eModel = "QAM-64-LIGHT"),
                    @EnumMap(yang = "qam-128-strong",eModel = "QAM-128-STRONG"),
                    @EnumMap(yang = "qam-128", eModel = "QAM-128"),
                    @EnumMap(yang = "qam-128-light", eModel = "QAM-128-LIGHT"),
                    @EnumMap(yang = "qam-256-strong",eModel = "QAM-256-STRONG"),
                    @EnumMap(yang = "qam-256", eModel = "QAM-256"),
                    @EnumMap(yang = "qam-256-light", eModel = "QAM-256-LIGHT"),
                    @EnumMap(yang = "qam-512-strong",eModel = "QAM-512-STRONG"),
                    @EnumMap(yang = "qam-512", eModel = "QAM-512"),
                    @EnumMap(yang = "qam-512-light", eModel = "QAM-512-LIGHT"),
                    @EnumMap(yang = "qam-1024-strong",eModel = "QAM-1024-STRONG"),
                    @EnumMap(yang = "qam-1024", eModel = "QAM-1024"),
                    @EnumMap(yang = "qam-1024-light", eModel = "QAM-1024-LIGHT"),
                    @EnumMap(yang = "qam-2048-strong",eModel = "QAM-2048-STRONG"),
                    @EnumMap(yang = "qam-2048", eModel = "QAM-2048"),
                    @EnumMap(yang = "qam-2048-light", eModel = "QAM-2048-LIGHT"),
                    @EnumMap(yang = "qam-4096-strong",eModel = "QAM-4096-STRONG"),
                    @EnumMap(yang = "qam-4096", eModel = "QAM-4096"),
                    @EnumMap(yang = "qam-4096-light", eModel = "QAM-4096-LIGHT"),
                    @EnumMap(yang = "",eModel = "QAM-8"),
                    @EnumMap(yang = "", eModel = "CQPSK"),
                    @EnumMap(yang = "", eModel = "OTHER"),
                    @EnumMap(yang = "qam-4-strong",eModel = "QAM-4-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-8192"),
                    @EnumMap(yang = "",eModel = "QAM-8192-LIGHT"),
                    @EnumMap(yang = "", eModel = "QAM-16384-STRONG"),
                    @EnumMap(yang = "", eModel = "QAM-16384"),
                    @EnumMap(yang = "",eModel = "QAM-16384-LIGHT")
            }),
            @AttributeMap(yang = "", eModel = "ct-distinguished-name"), @AttributeMap(yang = "", eModel = "far-end-ct-distinguished-name") })),
        @WhenEModelMapping(attribute = "type", value = "radioLinkTerminal", eModelMapping = @EModelMapping(eModel = "RadioLinkTerminal", mapping = {
                @AttributeMap(yang = "id", eModel = "terminal-id"), @AttributeMap(yang = "", eModel = "terminal-Id"),
                @AttributeMap(yang = "mode", eModel = "mode", enumMapping = {
                        @EnumMap(yang = "one-plus-zero",eModel = "ONE-PLUS-ZERO"),
                        @EnumMap(yang = "one-plus-one", eModel = "ONE-PLUS-ONE"),
                        @EnumMap(yang = "two-plus-zero", eModel = "TWO-PLUS-ZERO"),
                        @EnumMap(yang = "",eModel = "FOUR-PLUS-ZERO"),
                        @EnumMap(yang = "", eModel = "EIGHT-PLUS-ZERO"),
                        @EnumMap(yang = "", eModel = "OTHER"),
                        @EnumMap(yang = "",eModel = "TWO_PLUS_ONE_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "TWO_PLUS_TWO_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "THREE_PLUS_ONE_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "",eModel = "THREE_PLUS_TWO_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "THREE_PLUS_THREE_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_ONE_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "",eModel = "FOUR_PLUS_TWO_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_THREE_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_FOUR_RADIO_LINK_PROTECTION"),
                        @EnumMap(yang = "",eModel = "THREE_PLUS_ZERO_ADVANCED_RADIO_LINK_BONDING"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_ZERO_ADVANCED_RADIO_LINK_BONDING"),
                        @EnumMap(yang = "", eModel = "ONE_PLUS_ONE_COLD_STANDBY"),
                        @EnumMap(yang = "",eModel = "TWO_PLUS_ONE_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "TWO_PLUS_TWO_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "THREE_PLUS_ONE_COLD_STANDBY"),
                        @EnumMap(yang = "",eModel = "THREE_PLUS_TWO_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "THREE_PLUS_THREE_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_ONE_COLD_STANDBY"),
                        @EnumMap(yang = "",eModel = "FOUR_PLUS_TWO_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_THREE_COLD_STANDBY"),
                        @EnumMap(yang = "", eModel = "FOUR_PLUS_FOUR_COLD_STANDBY"),
                        @EnumMap(yang = "",eModel = "UNKNOWN"),
                        @EnumMap(yang = "", eModel = "NOT_AVAILABLE")
                }),
                @AttributeMap(yang = "", eModel = "node-name"),
                @AttributeMap(yang = "carrier-terminations", eModel = "carrier-terminations"),
                @AttributeMap(yang = "rlp-groups", eModel = "rlp-groups"),
                @AttributeMap(yang = "xpic-pairs", eModel = "xpic-pairs"), @AttributeMap(yang = "mimo-groups", eModel = "mimo-group"),
                @AttributeMap(yang = "", eModel = "far-end-terminal-id"), @AttributeMap(yang = "", eModel = "far-end-terminal-Id"),
                @AttributeMap(yang = "", eModel = "far-end-node-name"), @AttributeMap(yang = "tdm-connections", eModel = "tdm-connection") }))
        })
public class InterfaceDataProvider extends AbstractRestconfDataProvider {

    public InterfaceDataProvider() {
        super(InterfaceDataProvider.class);
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
                    case "higher-layer-if":
                    case "lower-layer-if":
                        extractInterfaceDetailsAndSet((YangDataLeafList) childDataNode);
                        break;
                    case "xpic-pairs":
                    case "mimo-groups":
                    case "rlp-groups":
                        extractDetailsAndSet((YangDataLeafList) childDataNode);
                        break;
                    case "carrier-terminations":
                        extractCarrierTerminationsDetailsAndSet((YangDataLeafList) childDataNode);
                        break;
                    default:
                        // Blank on purpose
                }
            }
        }
    }

    private void extractDetailsAndSet(final YangDataLeafList yangDataLeafList) {
        if (!yangDataLeafList.getItems().isEmpty()) {
            final List<String> modifiedItems = new ArrayList<>();
            for (final String itemValue : yangDataLeafList.getItems()) {
                if (itemValue.startsWith("Network=")) {
                    modifiedItems.add(getNodeName(itemValue) + ":" + getFdnValue(itemValue));
                } else {
                    modifiedItems.add(itemValue);
                }
            }
            yangDataLeafList.setItems(modifiedItems);
        }
    }

    private void extractCarrierTerminationsDetailsAndSet(final YangDataLeafList yangDataLeafList) {
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
