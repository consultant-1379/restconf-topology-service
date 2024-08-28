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

package com.ericsson.oss.services.overload.protection.timer;

import com.ericsson.oss.services.overload.protection.configuration.ConfigurationService;
import com.ericsson.oss.services.overload.protection.load.LoadStorage;
import com.ericsson.oss.services.overload.protection.service.api.CapacityRequest;
import org.slf4j.Logger;

import javax.annotation.Resource;
import javax.ejb.*;
import javax.inject.Inject;

/**
 * EJB timer implementation responsible to expireCapacityRequests expired resource locks
 */
@Stateless
public class CapacityReleaseScheduler {

    @Inject
    private LoadStorage loadStorage;

    @Inject
    private Logger logger;

    @Resource
    private TimerService timerService;

    @Inject
    private ConfigurationService configService;

    /**
     * Method used to schedule a resource reservation to expire.
     * @param capacityRequest - Details of request to reserve points
     */
    public void scheduleTimeout(final CapacityRequest capacityRequest) {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerConfig.setInfo(capacityRequest);

        timerService.createSingleActionTimer(configService.getConfiguration().getForceReleaseTimeout(), timerConfig );
    }

    /**
     * Method called when the scheduled timeout time is achieved. If the request was not released until this time, then the timer will expireCapacityRequests it.
     * @param timer timer instance
     */
    @Timeout
    public void expireCapacityRequest(final Timer timer) {

        final CapacityRequest request = (CapacityRequest) timer.getInfo();
        if (loadStorage.getRunningRequests().get(request.getId()) != null) {
            logger.warn("The request {} [{} - {}] started at {} has expired and is being forced to expireCapacityRequests {} points",
                    request.getId(), request.getApplication(), request.getUseCase(), request.getAcquiredTime(), request.getPoints());

            try {
                loadStorage.release(request.getId());
            } catch (ConcurrentAccessTimeoutException exception) {
                logger.error("A failure was triggered while the request {} was being released.", request.getId());
                logger.error(exception.getMessage(), exception);

                handleReleaseFailure(request);

            }
        } else {
            logger.info("The request with ID {} was already released.", request.getId());
        }

    }

    private void handleReleaseFailure(final CapacityRequest capacityRequest) {

        Integer currentRetryCount = capacityRequest.getNumberRetries();
        if (currentRetryCount < configService.getConfiguration().getTimerRetryCount()) {

            capacityRequest.setNumberRetries(currentRetryCount+1);

            final TimerConfig timerConfig = new TimerConfig();
            timerConfig.setPersistent(false);
            timerConfig.setInfo(capacityRequest);

            timerService.createSingleActionTimer(configService.getConfiguration().getTimerRetryDelay(), timerConfig);

        } else {
            logger.error("The request with ID {} has exceeded the maximum of {} retry attempts and will not be executed again!",
                    capacityRequest.getId(), configService.getConfiguration().getTimerRetryCount());
        }

    }

}
