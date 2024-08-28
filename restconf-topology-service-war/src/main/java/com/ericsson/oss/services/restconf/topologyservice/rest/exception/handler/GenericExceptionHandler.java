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

package com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler;

import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;

/**
 * Exception handler for {@code Throwable} type. <br/>
 * Status Code: 500. <br/>
 * Exception Description: Unhandled internal server exceptions.
 */
@Provider
public class GenericExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<Throwable> {
    @Override
    public Response toResponse(final Throwable throwable) {
        final InternalServerErrorException exception = new InternalServerErrorException(throwable);
        return super.toRestResponse(exception, RestconfResponseTag.OPERATION_FAILED_500);
    }
}
