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

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.availability.DpsAvailabilityCallback;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.DatabaseStatusHolder;

/**
ENM DPS component availability callback.
 */
@ApplicationScoped
public class EnmDpsAvailabilityCallback implements DpsAvailabilityCallback {
    private final Logger logger = LoggerFactory.getLogger(EnmDpsAvailabilityCallback.class);

    @Inject
    private DatabaseStatusHolder databaseStatusHolder;

    @Override
    public void onServiceAvailable() {
        logger.info("DPS is available again.");
        databaseStatusHolder.setAvailable(true);
    }

    @Override
    public void onServiceUnavailable() {
        logger.warn("DPS is unavailable.");
        databaseStatusHolder.setAvailable(false);
    }

    @Override
    public String getCallbackName() {
        return EnmDpsAvailabilityCallback.class.getCanonicalName();
    }
}
