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

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.NoDataAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataGeneratorException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.operation.RestconfDataOperation;
import com.ericsson.oss.services.restconf.topologyservice.ejb.utils.LoggingUtility;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.resolver.YangDataLeafRefValueResolver;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.validator.RestconfYangDataNodeValidator;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.provider.RestconfYangDataNodeTemplateProvider;

/**
 * Restconf data generator.
 */
@Stateless
public class RestconfYangDataGenerator {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfYangDataGenerator.class);

    @Inject
    private RestconfYangDataNodeTemplateProvider restconfYangDataNodeTemplateProvider;

    @Inject
    private RestconfYangDataProviderExecutor restconfYangDataProviderExecutor;

    @Inject
    private YangDataLeafRefValueResolver yangDataLeafRefValueResolver;

    @Inject
    private RestconfYangDataNodeCleaner restconfYangDataNodeCleaner;

    @Inject
    private RestconfYangDataNodeTrimmer restconfYangDataNodeTrimmer;

    @Inject
    private RestconfYangDataNodeValidator restconfYangDataNodeValidator;

    /**
     * Generate {@code YangDataNode} object for given {@code restconfResourceUri} object.
     *
     * @param restconfResourceUri {@code restconfResourceUri} object.
     * @param type                Type of operation.
     * @return {@code YangDataNode} object
     * @throws RestconfException when generation fails.
     */
    public YangDataNode generate(final RestconfResourceUri restconfResourceUri, final RestconfDataOperation.Type type) throws RestconfException {
        final long startTime = System.currentTimeMillis();
        try {
            final YangDataNode rootYangDataNode = new YangDataNodeBuilder().root();
            restconfYangDataNodeTemplateProvider.generateAndGetTemplateDataNode(restconfResourceUri, rootYangDataNode);
            restconfYangDataProviderExecutor.executeDataProviders(rootYangDataNode, type);
            yangDataLeafRefValueResolver.resolveLeafRefValues(rootYangDataNode);
            restconfYangDataNodeTrimmer.trimAndGetYangDataNode(restconfResourceUri, rootYangDataNode);
            restconfYangDataNodeCleaner.cleanUpEmptyDataNodes(rootYangDataNode.getDataNodes().iterator());
            restconfYangDataNodeCleaner.cleanUpDuplicateDataNodes(rootYangDataNode);
            if (rootYangDataNode.getDataNodes().isEmpty()) {
               throw new NoDataAvailableException("No data is available for requested resource uri " + restconfResourceUri);
            }
            return rootYangDataNode;
        } catch (final UnsupportedOperationException exception) {
            throw new RestconfDataGeneratorException(exception);
        } finally {
            LoggingUtility.calculateAndDebugLogTotalTimeTaken(LOGGER, startTime, "Generate yang data node for resourceId: %s", restconfResourceUri);
        }
    }
}
