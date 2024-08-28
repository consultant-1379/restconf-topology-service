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

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;

/**
 * Total time taken interceptor.
 */
@Interceptor
@TotalTimeTaken
public class TotalTimeTakenInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(TotalTimeTakenInterceptor.class);

    /**
     * Interceptor used to calculate total time taken to execute a method.
     *
     * @param context context.
     * @return context proceed.
     * @throws Exception when something fails.
     */
    @AroundInvoke
    public Object onIntercept(final InvocationContext context) throws Exception {
        final long startTime = System.currentTimeMillis();
        final TotalTimeTaken annotation = context.getMethod().getAnnotation(TotalTimeTaken.class);
        final String task = annotation.task().isEmpty() ? "executing " + context.getMethod().getName() : annotation.task();
        try {
            return context.proceed();
        } finally {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("TTT for [{}]: {} (ms)", task , System.currentTimeMillis() - startTime);
            }
        }
    }
}
