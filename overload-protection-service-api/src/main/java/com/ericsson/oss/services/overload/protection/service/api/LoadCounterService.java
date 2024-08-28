/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.overload.protection.service.api;

import com.ericsson.oss.services.overload.protection.exceptions.LockTimeoutException;
import com.ericsson.oss.services.overload.protection.exceptions.NotEnoughCapacityException;

import javax.ejb.Local;
import javax.ejb.Remote;
import java.util.Map;

/**
 * Load Counter Service is used to hold the server capacity and protect it from overload.
 */
@Local
public interface LoadCounterService {

    /**
     * <p>Try to acquire the requested capacity from the server.
     * If sucessfull a unique id will be returned to be used to release resources in the future.</p>
     *
     * <p>This method has an exclusive lock and will wait up to 3 seconds to acquire the capacity.
     * If after this time period the lock was not released, the operation fails.</p>
     *
     * @throws LockTimeoutException
     *      if the request took more than 3 seconds waiting for an active lock to be released.
     * @throws NotEnoughCapacityException
     *      if there's no capacity available to serve this request
     * @param request capacity request details
     * @return unique ID identifying this capacity reservation
     */
    String requestCapacity(final CapacityRequest request);

    void releaseCapacity(final String id);

    Integer getAvailableCapacity();

    Integer getUsedCapacity();

    Integer getTotalCapacity();

    Map<String,CapacityRequest> getRunningRequests();

}
