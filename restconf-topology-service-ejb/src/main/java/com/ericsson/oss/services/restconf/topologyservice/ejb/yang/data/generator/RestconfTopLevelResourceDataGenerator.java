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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator;

import javax.inject.Inject;

import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataGeneratorException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser.RestconfYangModulesParser;

/**
 * Restconf top level resource data generator.
 */
public class RestconfTopLevelResourceDataGenerator {
    @Inject
    private RestconfYangModulesParser restconfYangModulesParser;

    /**
     * Generate top level resource {@code YangDataNode} object.
     *
     * @return {@code YangDataNode} object.
     */
    @TotalTimeTaken(task = "Generate top level resource yang data node")
    public YangDataNode generate() throws RestconfDataGeneratorException {
        try {
            final YangDeviceModel yangDeviceModel = restconfYangModulesParser.getYangDeviceModel();
            for (final YangModelInput yangModule : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                if ("ietf-yang-library".equals(getModuleName(yangModule))) {
                    return createAndGetRestconfTopLevelNode(getRevision(yangModule));
                }
            }
            throw new RestconfDataGeneratorException("Failed to get ietf-yang-library revision");
        } catch (final Exception exception) {
            throw new RestconfDataGeneratorException(exception);
        }
    }

    private String getModuleName(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getModuleName();
    }

    private String getRevision(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getRevision();
    }

    private YangDataNode createAndGetRestconfTopLevelNode(final String yangLibraryVersion) {
        final YangDataNodeBuilder restconfYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-restconf", "ietf-restconf");
        final YangDataNode restconfContainer = restconfYangDataNodeBuilder.newInstance("restconf").container();
        restconfContainer.addDataNode(restconfYangDataNodeBuilder.newInstance("data").container());
        restconfContainer.addDataNode(restconfYangDataNodeBuilder.newInstance("operations").container());
        restconfContainer.addDataNode(restconfYangDataNodeBuilder.newInstance("yang-library-version").leaf(yangLibraryVersion));
        return new YangDataNodeBuilder().root(restconfContainer);
    }
}
