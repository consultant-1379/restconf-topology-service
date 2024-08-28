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

package com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.DatabaseNotAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.DatabaseStatusHolder;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.DependsOnDatabase;

/**
 * Interceptor used to check if the DPS is available.
 */
@Interceptor
@DependsOnDatabase
public class DpsAvailabilityInterceptor {

    @Inject
    private DatabaseStatusHolder databaseStatusHolder;

    /**
     * Interceptor used to check if the DPS is available.
     *
     * @param context context.
     * @return context proceed.
     * @throws DatabaseNotAvailableException when db not available.
     */
    @AroundInvoke
    public Object onIntercept(final InvocationContext context) throws Exception {
        if (!databaseStatusHolder.isAvailable()) {
            throw new DatabaseNotAvailableException("");
        }

        return context.proceed();
    }
}
