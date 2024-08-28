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

package com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.restconf;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.AccessDeniedException;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * Exception handler for {@code AccessDeniedException} type.
 * <br/>
 * Status Code: 401.
 * <br/>
 * Exception Description: Restconf related exceptions.
 */
@Provider
public class AccessDeniedExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<AccessDeniedException> {

    @Override
    public Response toResponse(final AccessDeniedException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.ACCESS_DENIED_401);
    }
}
