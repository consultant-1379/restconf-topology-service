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
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfYangHierarchyValidationException;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

/**
 * Exception handler for {@code RestconfYangHierarchyValidationException} type.
 * <br/>
 * Status Code: 500.
 * <br/>
 * Exception Description: Restconf related exceptions.
 */
@Provider
public class RestconfYangHierarchyValidationExceptionHandler extends AbstractExceptionHandler
        implements ExceptionMapper<RestconfYangHierarchyValidationException> {
    @Override
    public Response toResponse(final RestconfYangHierarchyValidationException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.BAD_ELEMENT_400);
    }
}
