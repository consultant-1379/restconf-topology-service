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

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.LicenseValidationException;
import com.ericsson.oss.services.restconf.topologyservice.rest.exception.handler.AbstractExceptionHandler;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

/**
 * Exception handler for {@code LicenseValidationException} type.
 * <br/>
 * Status Code: 412.
 * <br/>
 * Exception Description: Restconf related exceptions.
 */
@Provider
public class LicenseValidationExceptionHandler extends AbstractExceptionHandler implements ExceptionMapper<LicenseValidationException> {

    @Override
    public Response toResponse(final LicenseValidationException exception) {
        return super.toRestResponse(exception, RestconfResponseTag.OPERATION_FAILED_412);
    }
}
