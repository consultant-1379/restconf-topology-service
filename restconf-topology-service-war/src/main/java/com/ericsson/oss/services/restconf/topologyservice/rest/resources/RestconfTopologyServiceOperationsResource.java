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

package com.ericsson.oss.services.restconf.topologyservice.rest.resources;

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.rest.interceptor.binding.RestconfRequestValidation;

/**
 * Restconf topology service Operations REST endpoints API.
 */
@Path("/operations")
public class RestconfTopologyServiceOperationsResource extends AbstractRestconfTopologyServiceResource {

    /**
     * Execute operation on restconf configuration or state yang data resource. <br/>
     * Reference:
     * <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-3.3.2">RFC 8040, section 3.3.2</a>
     *
     * @return Success/failure {@code Response} object.
     * @throws RestconfException when failed to process request.
     */
    @GET
    @Path("{resourceUri:.+}")
    @Encoded
    @Produces({ RestconfRestConstants.APPLICATION_YANG_JSON, RestconfRestConstants.APPLICATION_YANG_XML })
    @RestconfRequestValidation
    public Response executeRestconfOperationResource(@PathParam("resourceUri") final String resourceUriStr) throws RestconfException {
        final long startTime = System.currentTimeMillis();
        final RestconfInstrumentationBuilder instrumentation = populateAndGetRequestInstrumentation().reqType("OPERATION");
        final String commandName = "EXECUTE_OPERATION_RESOURCE";
        try {
            recordCommandStarted(commandName, "Requested restconf operation on " + resourceUriStr);
            final RestconfResourceUri resourceUri = resourceUriParser.parse(resourceUriStr);
            final YangDataNode yangDataNode = restconfTopologyServiceBean.getRestconfOperationsResource(resourceUri);
            recordCommandFinishedWithSuccess(commandName, "Executed restconf operation on " + resourceUriStr);
            return getCodeBasedResponseWithMessage(yangDataNodeToResponse(yangDataNode));
        } catch (final Exception exception) {
            recordCommandFinishedWithError(commandName, "Failed to execute restconf operation on " + resourceUriStr, exception);
            return getExceptionResponse(exception);
        } finally {
            instrumentation.calcTotalResTime(startTime);
            recordRestconfEventData();
        }
    }
}
