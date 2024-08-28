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

import com.ericsson.oss.services.overload.protection.exceptions.NotEnoughCapacityException;
import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * {@code NotEnoughCapacityException} exception handler.
 */
@Provider
public class NotEnoughCapacityExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<NotEnoughCapacityException> {

    @Override
    public Response toResponse(final NotEnoughCapacityException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.OPERATION_FAILED_500);
    }
}
