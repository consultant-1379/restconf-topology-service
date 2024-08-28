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

import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.builder.RestconfErrorYangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.converter.YangDataNodeConverter;
import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter.YangDataNodeConverterFactory;
import com.ericsson.oss.services.restconf.topologyservice.rest.utils.RestconfResourceUtility;

/**
 * Abstract exception handler.
 */
public abstract class AbstractExceptionHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractExceptionHandler.class);

    @Context
    private HttpRequest request;

    @Inject
    private RestconfContext restconfContext;

    /**
     * Prepare restconf response.
     *
     * @param clientException {@code Exception} object.
     * @return {@code Response} object.
     */
    protected Response toRestResponse(final Exception clientException, final RestconfResponseTag responseTag) {
        try {
            LOGGER.debug("", clientException);
            final String mediaType = RestconfResourceUtility.getAcceptType(request);
            final YangDataNodeConverter yangDataNodeConverter = YangDataNodeConverterFactory.getConverter(mediaType);
            final RestconfErrorYangDataNodeBuilder restconfErrorYangDataNodeBuilder = new RestconfErrorYangDataNodeBuilder().responseTag(responseTag)
                    .errorPath(request.getUri().getPath(true)).errorInfoMessage(clientException.getMessage());
            final String response = yangDataNodeConverter.encode(restconfErrorYangDataNodeBuilder.build());
            if (restconfContext != null && restconfContext.getRestconfInstrumentationBuilder() != null) {
                restconfContext.getRestconfInstrumentationBuilder().resStatus(responseTag);
            }
            return Response.status(responseTag.getStatusCode()).entity(response).build();
        } catch (final Exception exception) {
            LOGGER.error("Failed to prepare restconf response due to {}", exception.getMessage());
            LOGGER.debug("", exception);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(clientException.getMessage()).build();
        }
    }
}
