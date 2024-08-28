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

import java.io.InputStream;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataGeneratorException;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser.RestconfYangModulesParser;

/**
 * Restconf yang schema generator.
 */
public class RestconfYangSchemaGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfYangSchemaGenerator.class);

    @Inject
    private RestconfYangModulesParser restconfYangModulesParser;

    /**
     * Generate yang schema stream.
     *
     * @param moduleName module name.
     * @param revision module revision.
     *
     * @return input stream.
     */
    @TotalTimeTaken(task = "Generate yang schema stream")
    public InputStream generate(final String moduleName, final String revision) throws RestconfDataGeneratorException {
        try {
            final YangDeviceModel yangDeviceModel = restconfYangModulesParser.getYangDeviceModel();
            LOGGER.debug("Searching {}@{} schema", moduleName, revision);
            for (final YangModelInput yangModule : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                if (moduleName.equals(getModuleName(yangModule)) && revision.equals(getRevision(yangModule))) {
                    LOGGER.debug("Found {}@{} schema", moduleName, revision);
                    return yangModule.getYangInput().getInputStream();
                }
            }
            throw new RestconfDataGeneratorException("Failed to get schema: " + moduleName + "@" + revision);
        } catch (final RestconfDataGeneratorException exception) {
            throw exception;
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

}
