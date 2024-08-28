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
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EnumMap;

/**
 * Tdm connections data provider.
 */
@YangDataProvider(yangModel = "ietf-microwave-radio-link:tdm-connections", eModelMapping = @EModelMapping(eModel = "TdmConnections", mapping = {
        @AttributeMap(yang = "tdm-type", eModel = "type", enumMapping = { @EnumMap(yang = "E1", eModel = "E1"),
                @EnumMap(yang = "STM-1", eModel = "STM1") }),
        @AttributeMap(yang = "tdm-connections", eModel = "connections") }))
public class TdmConnectionsDataProvider extends AbstractRestconfDataProvider {
    public TdmConnectionsDataProvider() {
        super(TdmConnectionsDataProvider.class);
    }
}
