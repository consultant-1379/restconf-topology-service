/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.operation;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.DatabaseNotAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfMoThresholdException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;

/**
 * Basic restconf operations.
 */
public interface RestconfDataOperation {

    /**
     * Supported operation types.
     */
    enum Type {
        READ, CREATE, UPDATE, DELETE
    }

    /**
     * Performs create operation.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @throws RestconfDataOperationException when operation fails.
     */
    void create(YangDataNode yangDataNode) throws RestconfDataOperationException;

    /**
     * Performs update operation.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @throws RestconfDataOperationException when operation fails.
     */
    void update(YangDataNode yangDataNode) throws RestconfDataOperationException;

    /**
     * Performs delte operation.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @throws RestconfDataOperationException when operation fails.
     */
    void delete(YangDataNode yangDataNode) throws RestconfDataOperationException;

    /**
     * Performs read operation.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @throws RestconfDataOperationException when operation fails.
     * @throws RestconfException
     */
    void read(YangDataNode yangDataNode) throws RestconfDataOperationException, DatabaseNotAvailableException, RestconfMoThresholdException;
}
