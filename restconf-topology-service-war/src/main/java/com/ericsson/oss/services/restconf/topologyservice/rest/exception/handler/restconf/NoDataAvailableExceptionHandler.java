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

import com.ericsson.oss.services.restconf.topologyservice.api.exception.NoDataAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * Exception handler for {@code NoDataAvailableException} type.
 * <br/>
 * Status Code: 204.
 * <br/>
 * Exception Description: Restconf related exceptions.
 */
@Provider
public class NoDataAvailableExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<NoDataAvailableException> {

    @Override
    public Response toResponse(final NoDataAvailableException exception) {
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
