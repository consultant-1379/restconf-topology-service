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

package com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.web;

import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * Exception handler for {@code NotAcceptableException} type.
 * <br/>
 * Status Code: 406.
 * <br/>
 * Exception Description: Client media type requested not supported.
 */
@Provider
public class NotAcceptableExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<NotAcceptableException> {

    @Override
    public Response toResponse(final NotAcceptableException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.INVALID_VALUE_406);
    }
}
