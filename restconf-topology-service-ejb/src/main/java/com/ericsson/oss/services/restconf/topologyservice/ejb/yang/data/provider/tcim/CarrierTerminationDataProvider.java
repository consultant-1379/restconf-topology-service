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
 * CarrierTermination data provider.
 */
@YangDataProvider(yangModel = "ietf-interfaces:interface", eModelMapping = @EModelMapping(eModel = "CarrierTermination", mapping = {
        @AttributeMap(yang = "carrier-id", eModel = "carrier-id"), @AttributeMap(yang = "tx-enabled", eModel = "tx-enabled"),
        @AttributeMap(yang = "tx-oper-status", eModel = ""), @AttributeMap(yang = "tx-frequency", eModel = "tx-frequency"),
        @AttributeMap(yang = "rx-frequency", eModel = "rx-frequency"), @AttributeMap(yang = "", eModel = "rx-frequency-config"),
        @AttributeMap(yang = "duplex-distance", eModel = "duplex-distance"),
        @AttributeMap(yang = "channel-separation", eModel = "channel-separation"), @AttributeMap(yang = "polarization", eModel = "polarization"),
        @AttributeMap(yang = "", eModel = "coding-modulation-mode"), @AttributeMap(yang = "selected-cm", eModel = "selected-cm"),
        @AttributeMap(yang = "selected-min-acm", eModel = "selected-min-acm"), @AttributeMap(yang = "selected-max-acm", eModel = "selected-max-acm"),
        @AttributeMap(yang = "", eModel = "ct-distinguished-name"),
        @AttributeMap(yang = "", eModel = "far-end-ct-distinguished-name") }))
public class CarrierTerminationDataProvider extends AbstractRestconfDataProvider {

    public CarrierTerminationDataProvider() {
        super(CarrierTerminationDataProvider.class);
    }
}
