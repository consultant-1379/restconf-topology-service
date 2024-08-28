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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc;

import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Ethernet Transport data provider.
 */
@YangDataProvider(yangModel = "ietf-eth-tran-service:etht-svc", eModelMapping = @EModelMapping(eModel = "", mapping = {}))
public class EthernetTransportServiceDataProvider extends AbstractRestconfDataProvider {

    public EthernetTransportServiceDataProvider() {
        super(EthernetTransportServiceDataProvider.class);
    }
}
