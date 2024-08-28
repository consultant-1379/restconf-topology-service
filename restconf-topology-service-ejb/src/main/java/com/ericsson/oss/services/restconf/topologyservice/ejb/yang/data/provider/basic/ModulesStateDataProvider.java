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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;

/**
 * Modules state data provider.
 */
@YangDataProvider(yangModel = "ietf-yang-library:modules-state", eModelMapping = @EModelMapping())
public class ModulesStateDataProvider extends AbstractRestconfDataProvider {

    public ModulesStateDataProvider() {
        super(ModulesStateDataProvider.class);
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        final Map<String, Object> attributeMap = new HashMap<>();
        attributeMap.put("module-set-id", getModelSetId());
        super.copyAndPopulateDataSet(yangDataNode, attributeMap);
    }

    private String getModelSetId() {
        try {
            final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            for (final YangModelInput yangModelInput : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                final String module = yangModelInput.getModuleIdentity().toString();
                messageDigest.update(module.getBytes(StandardCharsets.UTF_8), 0, module.length());
            }

            final StringBuilder result = new StringBuilder();
            for (final byte digestByte : messageDigest.digest()) {
                result.append(String.format("%02x", digestByte));
            }
            return result.toString();
        } catch (final NoSuchAlgorithmException exception) {
            LOGGER.trace("Ignored exception on purpose: {}", exception.getMessage());
            return "0";
        }
    }
}
