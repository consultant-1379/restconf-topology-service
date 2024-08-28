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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic;

import static com.ericsson.oss.services.restconf.topologyservice.api.provider.EnmGlobalPropertiesProvider.getEnmHost;

import java.util.ArrayList;
import java.util.List;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Restconf server capabilities data provider.
 */
@YangDataProvider(yangModel = "ietf-restconf-monitoring:capabilities", eModelMapping = @EModelMapping())
public class RestconfCapabilitiesDataProvider extends AbstractRestconfDataProvider {

    public RestconfCapabilitiesDataProvider() {
        super(RestconfCapabilitiesDataProvider.class);
    }

    @Override
    @TotalTimeTaken(task = "Generate restconf capabilities yang data node")
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        try {
            final List<String> capabilitiesSet = new ArrayList<>();
            final YangDataNodeBuilder capabilitiesYangDataNodeBuilder =
                    new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-restconf-monitoring", "restconf-state", "capability");
            for (final YangModelInput yangModelInput : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                capabilitiesSet.add(getSchema(getEnmHost(), yangModelInput));
            }
            yangDataNode.addDataNode(capabilitiesYangDataNodeBuilder.leafList(capabilitiesSet));
        } catch (final Exception exception) {
            throw new RestconfDataOperationException(exception);
        }
    }

    private String getModuleName(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getModuleName();
    }

    private String getRevision(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getRevision();
    }

    private String getSchema(final String hostUri, final YangModelInput yangModelInput) {
        final String moduleName = getModuleName(yangModelInput);
        return hostUri + "/restconf/yang/" + moduleName + "?module=" + moduleName + "&amp;revision=" + getRevision(yangModelInput);
    }
}
