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

package com.ericsson.oss.services.restconf.topologyservice.ejb.holder;

import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Database status holder.
 */
@Singleton
public class DatabaseStatusHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseStatusHolder.class);
    private static final Long NOT_TRACKING = Long.MAX_VALUE;
    private boolean available;
    private Long unavailabilityStartTimeMs;

    /**
     * Retrieves the database current status.
     * Any thread can read as long as there's no write thread running
     *
     * @return database current status
     */
    @Lock(LockType.READ)
    public boolean isAvailable() {
        return available;
    }

    /**
     * Updates the database status.
     * Concurrent access to this method is blocked.
     *
     * @param newAvailability new status (available = true; unavailable = false)
     */
    @Lock(LockType.WRITE)
    public void setAvailable(final boolean newAvailability) {
        if (newAvailability) {
            if (unavailabilityStartTimeMs == null) {
                LOGGER.warn("Database is available for the first time");
            } else {
                final long unavailableTime = System.currentTimeMillis() - unavailabilityStartTimeMs;
                LOGGER.warn("Database was unavailable for {} ms", unavailableTime);
            }
            unavailabilityStartTimeMs = NOT_TRACKING;
        } else {
            if (unavailabilityStartTimeMs == null) {
                LOGGER.warn("Database unavailable but never received availability");
            } else {
                LOGGER.warn("Database now unavailable");
                unavailabilityStartTimeMs = System.currentTimeMillis();
            }
        }

        available = newAvailability;
    }
}
