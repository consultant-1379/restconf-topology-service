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
 * Termination point data provider.
 */
@YangDataProvider(yangModel = "ietf-network-topology:termination-point", eModelMapping = @EModelMapping(eModel = "TerminationPoint", mapping = {
        @AttributeMap(yang = "tp-id", eModel = "tp-id"), @AttributeMap(yang = "", eModel = "if-ref"), @AttributeMap(yang = "", eModel = "link") }))
public class TerminationPointDataProvider extends AbstractRestconfDataProvider {

    public TerminationPointDataProvider() {
        super(TerminationPointDataProvider.class);
    }
}
