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

package com.ericsson.oss.services.restconf.topologyservice.rest.interceptor;

import javax.inject.Inject;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.ejb.facade.OverloadProtectionFacade;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.UserHolder;
import com.ericsson.oss.services.restconf.topologyservice.ejb.license.RestconfLicenseValidation;
import com.ericsson.oss.services.restconf.topologyservice.rest.interceptor.binding.RestconfRequestValidation;
import com.ericsson.oss.services.restconf.topologyservice.rest.utils.RequestProvider;

/**
 * Interceptor used to validate RESTCONF requests.
 */
@Interceptor
@RestconfRequestValidation
public class RestconfRequestValidationInterceptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfRequestValidationInterceptor.class);
    private static final String RESTCONF_NBI_USER_CAPACITY_PREFIX = "restconf_nbi_user_capacity_";
    private static final String RESTCONF_NBI_REQUEST_CAPACITY_PREFIX = "restconf_nbi_request_capacity_";

    @Inject
    private RequestProvider requestProvider;

    @Inject
    private OverloadProtectionFacade overloadProtectionFacade;

    @Inject
    private UserHolder userHolder;

    @Inject
    private RestconfLicenseValidation restconfLicenseValidation;

    private HttpRequest httpRequest;

    /**
     * Interceptor used to validate parallel RESTCONF requests.
     *
     * @param context context.
     * @return context proceed.
     * @throws Exception when something fails.
     */
    @AroundInvoke
    public Object onIntercept(final InvocationContext context) throws Exception {
        httpRequest = requestProvider.getHttpRequest();
        String userCapacityId = null;
        String requestCapacityId = null;

        try {
            restconfLicenseValidation.validateLicense();
            userCapacityId = requestUsersCapacity();
            requestCapacityId = requestRestconfRequestsCapacity();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Capacity available to execute request {}", getRequestLog());
            }
            userHolder.addUser(getUserId(), userCapacityId);
            return context.proceed();
        } finally {
            userHolder.removeUser(getUserId(), userCapacityId);
            overloadProtectionFacade.releaseCapacity(userCapacityId);
            overloadProtectionFacade.releaseCapacity(requestCapacityId);
        }
    }

    private String requestUsersCapacity() {
        try {
            return overloadProtectionFacade.requestCapacity("RESTCONF_NBI_USERS", getUserCapacityUseCase(), 1);
        } catch (final Exception exception) {
            LOGGER.error("Failed to acquire capacity for {}", getUserCapacityUseCase());
            throw exception;
        }
    }

    private String requestRestconfRequestsCapacity() {
        try {
            return overloadProtectionFacade.requestCapacity("RESTCONF_NBI_REQUESTS", getRequestCapacityUseCase(), 1);
        } catch (final Exception exception) {
            LOGGER.error("Failed to acquire capacity for {}", getRequestCapacityUseCase());
            throw exception;
        }
    }

    private String getUserId() {
        return httpRequest.getHttpHeaders().getHeaderString("X-Tor-userId");
    }

    private String getUserCapacityUseCase() {
        return RESTCONF_NBI_USER_CAPACITY_PREFIX + getUserId();
    }

    private String getRequestCapacityUseCase() {
        return RESTCONF_NBI_REQUEST_CAPACITY_PREFIX + httpRequest.getHttpMethod();
    }

    private String getRequestLog() {
        return httpRequest.getHttpMethod() + "; " + httpRequest.getHttpHeaders().getAcceptableMediaTypes() + "; " + httpRequest.getUri().getPath();
    }
}
