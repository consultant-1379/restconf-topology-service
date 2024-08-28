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

package com.ericsson.oss.services.restconf.topologyservice.ejb.utils;

import javax.inject.Singleton;

import org.slf4j.Logger;

/**
 * Logging utility.
 */
@Singleton
public final class LoggingUtility {

    private LoggingUtility() {
    }

    /**
     * Calculate and DEBUG log TTT (Total Time Taken) for a task.
     *
     * @param startTime  Start time (milliseconds) of the task.
     * @param taskFormat Task string format.
     * @param args       Variable args for string format.
     */
    public static void calculateAndDebugLogTotalTimeTaken(final Logger logger, final long startTime, final String taskFormat, final Object... args) {
        if (logger.isDebugEnabled()) {
            logger.debug("TTT for [{}]: {} (ms).", String.format(taskFormat, args), System.currentTimeMillis() - startTime);
        }
    }

    public static void logException(final Logger logger, final String task, final Exception exception) {
        logger.error("Failed to [{}] due to [{}]", task, exception.getMessage());
        logger.debug("", exception);
    }
}
