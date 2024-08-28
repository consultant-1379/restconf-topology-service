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
 * Ethernet data provider.
 */
@YangDataProvider(yangModel = "ietf-interfaces:interface", eModelMapping = @EModelMapping(eModel = "Interface", mapping = {
        @AttributeMap(yang = "name", eModel = "name") }))
public class EthernetDataProvider extends AbstractRestconfDataProvider {

    public EthernetDataProvider() {
        super(EthernetDataProvider.class);
    }
}
