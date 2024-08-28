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

import javax.ws.rs.BadRequestException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * Exception handler for {@code BadRequestException} type.
 * <br/>
 * Status Code: 400.
 * <br/>
 * Exception Description: Malformed message.
 */
@Provider
public class BadRequestExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<BadRequestException> {

    @Override
    public Response toResponse(final BadRequestException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.BAD_ELEMENT_400);
    }
}
