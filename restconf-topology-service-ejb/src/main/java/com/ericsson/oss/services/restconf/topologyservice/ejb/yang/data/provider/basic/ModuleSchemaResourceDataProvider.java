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

import java.util.List;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.YangModelRoot;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Schema resource data provider.
 */
@YangDataProvider(yangModel = "ietf-yang-library:module", eModelMapping = @EModelMapping())
public class ModuleSchemaResourceDataProvider extends AbstractRestconfDataProvider {

    public ModuleSchemaResourceDataProvider() {
        super(ModuleSchemaResourceDataProvider.class);
    }

    @Override
    @TotalTimeTaken(task = "Generate restconf yang modules yang data node")
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        try {
            for (final YangModelInput yangModelInput : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                yangDataNode.addDataNode(createAndGetYangModuleDataNode(getEnmHost(), yangModelInput));
            }
        } catch (final Exception exception) {
            throw new RestconfDataOperationException(exception);
        }
    }

    private YangDataNode createAndGetYangModuleDataNode(final String hostUri, final YangModelInput yangModelInput) {
        final YangDataNodeBuilder moduleYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-yang-library",
                "ietf-yang-library", "module");
        final YangDataSet moduleDataSet = moduleYangDataNodeBuilder.dataSet();
        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("name").leaf(getModuleName(yangModelInput), true));
        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("revision").leaf(getRevision(yangModelInput), true));
        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("schema").leaf(getSchema(hostUri, yangModelInput)));
        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("namespace").leaf(getNamespace(yangModelInput)));
        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("feature").leafList());

        final YangDataList deviationListNode = moduleYangDataNodeBuilder.newInstance("deviation").list();
        deviationListNode.addDataNode(moduleYangDataNodeBuilder.newInstance("name").leaf(""));
        deviationListNode.addDataNode(moduleYangDataNodeBuilder.newInstance("revision").leaf(""));
        moduleDataSet.addDataNode(deviationListNode);

        moduleDataSet.addDataNode(moduleYangDataNodeBuilder.newInstance("conformance-type").leaf(getConformanceType(yangModelInput)));
        final List<YangModelRoot> submoduleList = yangModelInput.getYangModelRoot().getOwnedSubmodules();
        for (final YangModelRoot modelRoot : submoduleList) {
            final YangDataList submoduleListNode = moduleYangDataNodeBuilder.newInstance("submodule").list();
            submoduleListNode.addDataNode(moduleYangDataNodeBuilder.newInstance("name").leaf(getModuleName(modelRoot.getYangFile()), true));
            submoduleListNode.addDataNode(moduleYangDataNodeBuilder.newInstance("revision").leaf(getRevision(modelRoot.getYangFile()), true));
            submoduleListNode.addDataNode(moduleYangDataNodeBuilder.newInstance("schema").leaf(getSchema(hostUri, modelRoot.getYangFile())));
            moduleDataSet.addDataNode(submoduleListNode);
        }

        return moduleDataSet;
    }

    private String getModuleName(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getModuleName();
    }

    private String getRevision(final YangModelInput yangModelInput) {
        return yangModelInput.getModuleIdentity().getRevision();
    }

    private String getNamespace(final YangModelInput yangModelInput) {
        return yangModelInput.getYangModelRoot().getNamespace();
    }

    private String getSchema(final String hostUri, final YangModelInput yangModelInput) {
        final String moduleName = getModuleName(yangModelInput);
        return hostUri + "/restconf/yang/" + moduleName + "?module=" + moduleName + "&amp;revision=" + getRevision(yangModelInput);
    }

    private String getConformanceType(final YangModelInput yangModelInput) {
        return yangModelInput.getConformanceType() == ConformanceType.IMPLEMENT ? "implement" : "import";
    }
}
