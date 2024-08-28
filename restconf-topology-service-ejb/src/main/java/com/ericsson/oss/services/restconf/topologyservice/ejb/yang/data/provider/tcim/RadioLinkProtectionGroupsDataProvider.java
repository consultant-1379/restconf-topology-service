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

import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Radio link protection groups data provider.
 */
@YangDataProvider(yangModel = "ietf-microwave-radio-link:radio-link-protection-groups", eModelMapping = @EModelMapping())
public class RadioLinkProtectionGroupsDataProvider extends AbstractRestconfDataProvider {
    public RadioLinkProtectionGroupsDataProvider() {
        super(RadioLinkProtectionGroupsDataProvider.class);
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        copyAndPopulateDataSet(yangDataNode);
    }
}
