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

package com.ericsson.oss.services.restconf.topologyservice.ejb.facade;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.overload.protection.service.api.CapacityRequest;
import com.ericsson.oss.services.overload.protection.service.api.CapacityRequestBuilder;
import com.ericsson.oss.services.overload.protection.service.api.LoadCounterService;

/**
 * Overload protection facade.
 */
public class OverloadProtectionFacade {
    private static final Logger LOGGER = LoggerFactory.getLogger(OverloadProtectionFacade.class);

    @Inject
    private LoadCounterService loadCounterService;

    /**
     * Acquires the requested capacity for use case from the server.
     *
     * @param application application name
     * @param useCase     use case that is reserving capacity
     * @param points      points for use case
     * @return {String} unique request capacity id
     */
    public String requestCapacity(final String application, final String useCase, final Integer points) {
        final CapacityRequestBuilder capacityRequestBuilder = CapacityRequestBuilder.application(application);
        final CapacityRequest capacityRequest = capacityRequestBuilder.useCase(useCase).points(points).id(null).build();
        LOGGER.debug("Requesting capacity: {}", capacityRequest);
        final String requestId = loadCounterService.requestCapacity(capacityRequest);
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("TotalCapacity: {}, AvailableCapacity: {}, UsedCapacity: {}, RunningRequests: {}", loadCounterService.getTotalCapacity(),
                    loadCounterService.getAvailableCapacity(), loadCounterService.getUsedCapacity(), loadCounterService.getRunningRequests());
        }
        return requestId;
    }

    /**
     * Release requested capacity.
     *
     * @param requestCapacityId request capacity id
     */
    public void releaseCapacity(final String requestCapacityId) {
        if (requestCapacityId == null) {
            LOGGER.trace("No capacity reserved, requestCapacityId is null");
            return;
        }
        LOGGER.debug("Releasing capacity with ID: {}", requestCapacityId);
        loadCounterService.releaseCapacity(requestCapacityId);
    }

    public LoadCounterService getLoadCounterService() {
        return loadCounterService;
    }
}
