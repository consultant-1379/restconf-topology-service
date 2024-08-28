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

import java.io.InputStream;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.rest.interceptor.binding.RestconfRequestValidation;

/**
 * Restconf topology service General REST endpoints API.
 */
@Path("/")
public class RestconfTopologyServiceResource extends AbstractRestconfTopologyServiceResource {

    /**
     * Get restconf top level resource.
     * <br/>
     * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-3.3">RFC 8040, section-3.3</a>
     *
     * @return Success/failure {@code Response} object.
     * @throws RestconfException when failed to process request.
     */
    @GET
    @Produces({ RestconfRestConstants.APPLICATION_YANG_JSON, RestconfRestConstants.APPLICATION_YANG_XML })
    @RestconfRequestValidation
    public Response getRestconfTopLevelResource() throws RestconfException {
        final long startTime = System.currentTimeMillis();
        final RestconfInstrumentationBuilder instrumentation = populateAndGetRequestInstrumentation().reqType("TOP_LEVEL");
        final String commandName = "GET_TOP_LEVEL_RESOURCE";
        try {
            recordCommandStarted(commandName, "Requested restconf top level resource");
            final YangDataNode yangDataNode = restconfTopologyServiceBean.getRestconfTopLevelResource();
            recordCommandFinishedWithSuccess(commandName, "Retrieved restconf top level resource successfully");
            return getCodeBasedResponseWithMessage(yangDataNodeToResponse(yangDataNode));
        } catch (final Exception exception) {
            recordCommandFinishedWithError(commandName, "Failed to get restconf top level resource", exception);
            return getExceptionResponse(exception);
        } finally {
            instrumentation.calcTotalResTime(startTime);
            recordRestconfEventData();
        }
    }

    /**
     * Get restconf yang library version resource.
     * <br/>
     * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-3.3.3">RFC 8040, section-3.3.3</a>
     *
     * @return Success/failure {@code Response} object.
     * @throws RestconfException when failed to process request.
     */
    @GET
    @Path("/yang-library-version")
    @Produces({ RestconfRestConstants.APPLICATION_YANG_JSON, RestconfRestConstants.APPLICATION_YANG_XML })
    @RestconfRequestValidation
    public Response getRestconfYangLibraryVersionResource() throws RestconfException {
        final long startTime = System.currentTimeMillis();
        final RestconfInstrumentationBuilder instrumentation = populateAndGetRequestInstrumentation().reqType("YANG_VERSION");
        final String commandName = "GET_YANG_LIBRARY_VERSION_RESOURCE";
        try {
            recordCommandStarted(commandName, "Requested restconf yang library version");
            final YangDataNode yangDataNode = restconfTopologyServiceBean.getRestconfYangLibraryVersionResource();
            recordCommandFinishedWithSuccess(commandName, "Retrieved restconf yang library version successfully");
            return getCodeBasedResponseWithMessage(yangDataNodeToResponse(yangDataNode));
        } catch (final Exception exception) {
            recordCommandFinishedWithError(commandName, "Failed to get restconf yang library version", exception);
            return getExceptionResponse(exception);
        } finally {
            instrumentation.calcTotalResTime(startTime);
            recordRestconfEventData();
        }
    }

    /**
     * Get restconf yang schema resource.
     * <br/>
     * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-3.7">RFC 8040, section-3.7</a>
     *
     * @return Success/failure {@code Response} object.
     * @throws RestconfException when failed to process request.
     */
    @GET
    @Path("/yang/{module}")
    @Produces(RestconfRestConstants.APPLICATION_YANG)
    @RestconfRequestValidation
    public Response getRestconfYangSchemaResource(@QueryParam("module") final String moduleName,
                                                  @QueryParam("revision") final String revision) throws RestconfException {
        final long startTime = System.currentTimeMillis();
        final RestconfInstrumentationBuilder instrumentation = populateAndGetRequestInstrumentation().reqType("YANG_SCHEMA");
        final String commandName = "GET_YANG_SCHEMA_RESOURCE";
        try {
            recordCommandStarted(commandName, "Requested restconf yang schema");
            final InputStream inputStream = restconfTopologyServiceBean.getRestconfYangSchemaResource(moduleName, revision);
            recordCommandFinishedWithSuccess(commandName, "Retrieved restconf yang schema successfully");
            return getCodeBasedResponseWithStream(inputStream);
        } catch (final Exception exception) {
            recordCommandFinishedWithError(commandName, "Failed to get restconf yang schema", exception);
            return getExceptionResponse(exception);
        } finally {
            instrumentation.calcTotalResTime(startTime);
            recordRestconfEventData();
        }
    }
}
