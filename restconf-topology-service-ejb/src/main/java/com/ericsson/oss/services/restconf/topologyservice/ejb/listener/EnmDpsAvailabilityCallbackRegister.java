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

package com.ericsson.oss.services.restconf.topologyservice.ejb.listener;

import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommand;
import com.ericsson.oss.itpf.sdk.core.retry.RetriableCommandException;
import com.ericsson.oss.itpf.sdk.core.retry.RetryContext;
import com.ericsson.oss.itpf.sdk.core.retry.RetryManager;
import com.ericsson.oss.itpf.sdk.core.retry.RetryPolicy;

/**
 * Registers DPS availability callback.
 */
@Singleton
@Startup
public class EnmDpsAvailabilityCallbackRegister {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnmDpsAvailabilityCallbackRegister.class);
    private static final int TIMER_STARTUP_DELAY_IN_MILLISECONDS = 3000;
    private static final Integer MAX_ATTEMPTS = 120;
    private static final Integer WAIT_TIME_IN_SECONDS = 2;

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    @Inject
    private EnmDpsAvailabilityCallback enmDpsAvailabilityCallback;

    @Inject
    private TimerService timerService;

    @Inject
    private RetryManager retryManager;

    @PostConstruct
    public void scheduleDpsAvailabilityCallbackRegistration() {
        LOGGER.info("Scheduling DPS availability callback registration in {} ms", TIMER_STARTUP_DELAY_IN_MILLISECONDS);
        initializeTimer();
    }

    @PreDestroy
    public void deregisterDpsAvailabilityCallback() {
        dataPersistenceService.deregisterDpsAvailabilityCallback("De-registering DPS availability callback");
    }

    @Timeout
    public void registerDpsAvailabilityCallback(final Timer timer) {
        final RetryPolicy retryPolicy = RetryPolicy.builder().attempts(MAX_ATTEMPTS).waitInterval(WAIT_TIME_IN_SECONDS, TimeUnit.SECONDS)
                .retryOn(Exception.class).build();
        try {
            retryManager.executeCommand(retryPolicy, new RegisterDpsAvailabilityCallbackCommand());
        } catch (final RetriableCommandException exception) {
            LOGGER.error("DPS availability callback registration failed within {} seconds due to {}", MAX_ATTEMPTS * WAIT_TIME_IN_SECONDS,
                    exception.getMessage());
            LOGGER.debug("", exception);
        } catch (final Exception exception) {
            LOGGER.error("DPS availability callback registration failed due to {}", exception.getMessage());
            LOGGER.debug("", exception);
        }
    }

    /**
     * Initializes startup timer.
     */
    private void initializeTimer() {
        final TimerConfig timerConfig = new TimerConfig();
        timerConfig.setPersistent(false);
        timerService.createSingleActionTimer(EnmDpsAvailabilityCallbackRegister.TIMER_STARTUP_DELAY_IN_MILLISECONDS, timerConfig);
    }

    /**
     * Command to register the DPS availability callback implementing the {@link RetriableCommand} interface.
     */
    class RegisterDpsAvailabilityCallbackCommand implements RetriableCommand<Object> {
        @Override
        public Object execute(final RetryContext retryContext) throws Exception {
            final int currentAttempt = retryContext.getCurrentAttempt();
            final String message = "Registering DPS availability callback. Attempt " + currentAttempt + " of " + MAX_ATTEMPTS;
            if (currentAttempt == 1) {
                LOGGER.info(message);
            } else {
                LOGGER.warn(message);
            }
            dataPersistenceService.registerDpsAvailabilityCallback(enmDpsAvailabilityCallback);
            return null;
        }
    }
}
