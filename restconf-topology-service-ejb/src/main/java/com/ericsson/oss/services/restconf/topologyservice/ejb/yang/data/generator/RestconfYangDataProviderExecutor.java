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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataGeneratorException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfMoThresholdException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.model.RestconfDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.operation.RestconfDataOperation;
import com.ericsson.oss.services.restconf.topologyservice.ejb.utils.ObjectsProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.AbstractRestconfDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.registry.RestconfDataProviderRegistry;

/**
 * Restconf yang data providers executor.
 */
public class RestconfYangDataProviderExecutor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfYangDataProviderExecutor.class);

    @Inject
    private RestconfDataProviderRegistry restconfDataProviderRegistry;

    @Inject
    private ObjectsProvider objectsProvider;

    /**
     * Execute data providers.
     *
     * @param yangDataNode {@code YangDataNode} root data node.
     * @param type         Type of operation.
     * @throws RestconfDataGeneratorException when failed to execute data provider.
     * @throws RestconfMoThresholdException
     */
    public void executeDataProviders(final YangDataNode yangDataNode, final RestconfDataOperation.Type type) throws RestconfDataGeneratorException, RestconfMoThresholdException {
        traverseAndExecuteDataProviders(yangDataNode, type);
    }

    private void traverseAndExecuteDataProviders(final YangDataNode yangDataNode, final RestconfDataOperation.Type type)
            throws RestconfDataGeneratorException, RestconfMoThresholdException {
        if (yangDataNode.getDataNodes().isEmpty()) {
            executeDataProviderOperation(yangDataNode, type);
            for (final YangDataNode dataChildNode : yangDataNode.getDataNodes()) {
                traverseAndExecuteDataProviders(dataChildNode, type);
            }
        } else {
            for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
                executeDataProviderOperation(dataNode, type);
                for (final YangDataNode dataChildNode : dataNode.getDataNodes()) {
                    traverseAndExecuteDataProviders(dataChildNode, type);
                }
            }
        }
    }

    private void executeDataProviderOperation(final YangDataNode yangDataNode, final RestconfDataOperation.Type type)
            throws RestconfDataGeneratorException, RestconfMoThresholdException {
        if (yangDataNode instanceof YangDataContainer || yangDataNode instanceof YangDataList) {
            for (final Class<? extends RestconfDataProvider> dataProvider : restconfDataProviderRegistry.getDataProviders(yangDataNode)) {
                LOGGER.trace("Executing data provider {} for {}", dataProvider.getName(), yangDataNode);
                try {
                    final RestconfDataProvider restconfDataProvider = dataProvider.newInstance();
                    ((AbstractRestconfDataProvider) restconfDataProvider).setObjectsProvider(objectsProvider);
                    switch (type) {
                        case READ:
                            restconfDataProvider.read(yangDataNode);
                            break;
                        case CREATE:
                            restconfDataProvider.create(yangDataNode);
                            break;
                        case UPDATE:
                            restconfDataProvider.update(yangDataNode);
                            break;
                        case DELETE:
                            restconfDataProvider.delete(yangDataNode);
                            break;
                        default:
                            throw new RestconfDataGeneratorException("Unknown operation type " + type);
                    }
                } catch (final RestconfMoThresholdException exception) {
                    throw exception;
                } catch (final InstantiationException | IllegalAccessException | RestconfException exception) {
                    throw new RestconfDataGeneratorException(exception);
                }
            }
        }
    }
}