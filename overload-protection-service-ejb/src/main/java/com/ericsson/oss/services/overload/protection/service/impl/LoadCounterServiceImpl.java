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

package com.ericsson.oss.services.overload.protection.service.impl;

import com.ericsson.oss.services.overload.protection.configuration.ConfigurationService;
import com.ericsson.oss.services.overload.protection.exceptions.LockTimeoutException;
import com.ericsson.oss.services.overload.protection.load.LoadStorage;
import com.ericsson.oss.services.overload.protection.service.api.CapacityRequest;
import com.ericsson.oss.services.overload.protection.service.api.LoadCounterService;
import com.ericsson.oss.services.overload.protection.timer.CapacityReleaseScheduler;
import org.slf4j.Logger;

import javax.ejb.ConcurrentAccessTimeoutException;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Map;
import java.util.UUID;

/**
 * Implementation class for Load Counter Service
 */
@Stateless(name = "LoadCounterService")
public class LoadCounterServiceImpl implements LoadCounterService {

    @EJB
    private LoadStorage storage;

    @Inject
    private Logger logger;

    @Inject
    private CapacityReleaseScheduler scheduler;

    @Inject
    private ConfigurationService configService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String requestCapacity(CapacityRequest request) {

        logger.debug("request: '{}', service enabled? {}", request, configService.getConfiguration().isEnabled());

        if (!configService.getConfiguration().isEnabled()) {
            // For the clients looks like the service is enabled but we are not monitoring
            return UUID.randomUUID().toString();
        }

        try {

            final String id = storage.acquire(request);
            scheduler.scheduleTimeout(request);

            return id;

        } catch (ConcurrentAccessTimeoutException exception) {
            logger.error("An timeout error was caught trying to reserve capacity for {}, use case {} with {} points",
                    request.getApplication(), request.getUseCase(), request.getPoints());

            throw new LockTimeoutException(exception);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void releaseCapacity(String id) {

        try {
            if (configService.getConfiguration().isEnabled()) {
                storage.release(id);
            }

        } catch (ConcurrentAccessTimeoutException exception) {
            logger.error("An timeout error was caught trying to expireCapacityRequests capacity {}", id);
            throw new LockTimeoutException(exception);
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getAvailableCapacity() {
        return storage.getAvailableCapacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getUsedCapacity() {
        return storage.getUsedCapacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getTotalCapacity() {
        return storage.getTotalCapacity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CapacityRequest> getRunningRequests() {
        return storage.getRunningRequests();
    }

}
