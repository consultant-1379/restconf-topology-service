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
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.spi.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.recording.CommandPhase;
import com.ericsson.oss.itpf.sdk.recording.ErrorSeverity;
import com.ericsson.oss.itpf.sdk.recording.SystemRecorder;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.parser.ResourceUriParser;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.converter.YangDataNodeConverter;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.ejb.RestconfTopologyServiceBean;
import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.UserHolder;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter.YangDataNodeConverterFactory;
import com.ericsson.oss.services.restconf.topologyservice.rest.utils.RequestProvider;
import com.ericsson.oss.services.restconf.topologyservice.rest.utils.RestconfResourceUtility;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.SecurityViolationException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.AccessDeniedException;

/**
 * Abstract restconf topology service resource.
 */
@RequestScoped
public abstract class AbstractRestconfTopologyServiceResource {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestconfTopologyServiceResource.class);
    private static final String RESTCONF_COMMAND_PREFIX = "RESTCONF.";

    @Inject
    protected ResourceUriParser resourceUriParser;

    @Inject
    protected RestconfTopologyServiceBean restconfTopologyServiceBean;

    @Inject
    protected SystemRecorder systemRecorder;

    @Context
    protected HttpRequest httpRequest;

    @Inject
    protected RestconfContext restconfContext;

    @Inject
    protected RequestProvider requestProvider;

    @Inject
    private UserHolder userHolder;

    @PostConstruct
    public void initialize() {
        restconfContext.setRestconfInstrumentationBuilder(new RestconfInstrumentationBuilder());
        requestProvider.setHttpRequest(httpRequest);
    }

    protected void recordCommandStarted(final String commandName, final String additionalInfo) {
        systemRecorder.recordCommand(RESTCONF_COMMAND_PREFIX + commandName, CommandPhase.STARTED, "REST_NBI", "NONE", additionalInfo);
    }

    protected void recordCommandFinishedWithSuccess(final String commandName, final String additionalInfo) {
        systemRecorder.recordCommand(RESTCONF_COMMAND_PREFIX + commandName, CommandPhase.FINISHED_WITH_SUCCESS, "REST_NBI", "NONE", additionalInfo);
    }

    protected void recordCommandFinishedWithError(final String commandName, final String additionalInfo, final Exception exception) {
        systemRecorder.recordError(RESTCONF_COMMAND_PREFIX + commandName, ErrorSeverity.ERROR, "REST_NBI", "NONE",
                additionalInfo + " due to [" + exception.getMessage() + "]");
    }

    protected void recordRestconfEventData() {
        final Map<String, Object> eventData = restconfContext.getRestconfInstrumentationBuilder().build();
        LOGGER.debug("eventData: {}", eventData);
        systemRecorder.recordEventData("RESTCONF_NBI.REQUEST_TOTALS", eventData);
    }

    protected String yangDataNodeToResponse(final YangDataNode yangDataNode) throws RestconfException {
        final YangDataNodeConverter converter = YangDataNodeConverterFactory.getConverter(RestconfResourceUtility.getAcceptType(httpRequest));
        return converter.encode(yangDataNode);
    }

    protected RestconfInstrumentationBuilder populateAndGetRequestInstrumentation() {
        final RestconfInstrumentationBuilder instrumentation =  restconfContext.getRestconfInstrumentationBuilder();
        instrumentation.userIndex(userHolder.getUserIndex(RestconfResourceUtility.getUserId(httpRequest)));
        instrumentation.reqMethod(httpRequest.getHttpMethod());
        instrumentation.reqMediaType(RestconfResourceUtility.getShortMediaType(httpRequest));
        instrumentation.reqAcceptType(RestconfResourceUtility.getShortAcceptType(httpRequest));
        instrumentation.totalReqDataSize(0L);
        instrumentation.yangRequestUri(httpRequest.getUri().getPath());
        instrumentation.totalReqQueryParam(httpRequest.getUri().getQueryParameters().size());
        return instrumentation;
    }

    /**
     * Get module name from {@code RestconfResourceUri} object.
     * @param resourceUri {@code RestconfResourceUri} object
     * @return module name.
     */
    protected String getRootModuleName(final RestconfResourceUri resourceUri) {
        if (resourceUri.getStep(0) != null) {
            return resourceUri.getStep(0).getQName();
        }
        return "UNKNOWN";
    }

    /**
     * Generate {@code Response} object with response and code.
     *
     * @param payload Response payload
     * @return {@code Response} object
     */
    protected Response getCodeBasedResponseWithMessage(final String payload) {
        final Response response;
        if (payload == null || payload.isEmpty()) {
            restconfContext.getRestconfInstrumentationBuilder().resStatus("OPERATION_FAILED");
            response = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
        } else {
            restconfContext.getRestconfInstrumentationBuilder().resStatus("SUCCESS");
            restconfContext.getRestconfInstrumentationBuilder().totalResDataSize(payload.length());
            response = Response.status(Response.Status.OK).entity(payload).build();
        }
        return response;
    }

    /**
     * Generate {@code Response} object with input stream and code.
     *
     * @param inputStream Response stream
     * @return {@code Response} object
     */
    protected Response getCodeBasedResponseWithStream(final InputStream inputStream) {
        restconfContext.getRestconfInstrumentationBuilder().resStatus("SUCCESS");
        return Response.status(Response.Status.OK).entity(inputStream).build();
    }

    /**
     * Generate {@code Response} object with exception.
     *
     * @param exception exception
     * @return {@code Response} object
     * @throws RestconfException throw if not SecurityVoilationException
     */
    protected Response getExceptionResponse(final Exception exception) throws RestconfException {
        if (exception.getCause() instanceof SecurityViolationException) {
            throw new AccessDeniedException("");
        } else if (exception instanceof RestconfException) {
            throw (RestconfException) exception;
        } else {
            throw new RestconfException(exception);
        }
    }
}
