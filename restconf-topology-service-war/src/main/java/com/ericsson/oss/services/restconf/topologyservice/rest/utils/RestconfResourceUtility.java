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

package com.ericsson.oss.services.restconf.topologyservice.rest.utils;

import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_JSON;
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_XML;

import java.util.List;
import javax.ws.rs.NotAcceptableException;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.spi.HttpRequest;

import com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants;

/**
 * Rest response utility.
 */
public final class RestconfResourceUtility {

    private RestconfResourceUtility() {
    }

    /**
     * Get User ID from the {@code HttpRequest} object.
     * @param httpRequest {@code HttpRequest} object.
     * @return User ID
     */
    public static String getUserId(final HttpRequest httpRequest) {
        return httpRequest.getHttpHeaders().getHeaderString("X-Tor-userId");
    }

    /**
     * Get response accept type for the request.
     *
     * @param request {@code HttpRequest} object.
     * @return accept type.
     * @throws NotAcceptableException when no or unsupported accept type.
     */
    public static String getAcceptType(final HttpRequest request) {
        final List<MediaType> acceptedTypes = request.getHttpHeaders().getAcceptableMediaTypes();
        if (acceptedTypes == null || acceptedTypes.isEmpty()) {
            throw new NotAcceptableException();
        }
        if (acceptedTypes.contains(MediaType.valueOf(APPLICATION_YANG_JSON))) {
            return APPLICATION_YANG_JSON;
        } else if (acceptedTypes.contains(MediaType.valueOf(RestconfRestConstants.APPLICATION_YANG_XML))) {
            return RestconfRestConstants.APPLICATION_YANG_XML;
        }
        return APPLICATION_YANG_JSON;
    }

    /**
     * Get response short accept type for the request.
     *
     * @param request {@code HttpRequest} object.
     * @return short accept type.
     */
    public static String getShortAcceptType(final HttpRequest request) {
        final List<MediaType> acceptedTypes = request.getHttpHeaders().getAcceptableMediaTypes();
        if (acceptedTypes == null || acceptedTypes.isEmpty()) {
            return "NONE";
        }
        if (acceptedTypes.contains(MediaType.valueOf(APPLICATION_YANG_JSON))) {
            return "JSON";
        } else if (acceptedTypes.contains(MediaType.valueOf(RestconfRestConstants.APPLICATION_YANG_XML))) {
            return "XML";
        }
        return "UNKNOWN";
    }

    /**
     * Get request short media type for the request.
     *
     * @param request {@code HttpRequest} object.
     * @return short media type.
     */
    public static String getShortMediaType(final HttpRequest request) {
        final MediaType mediaType = request.getHttpHeaders().getMediaType();
        if (mediaType == null) {
            return "NONE";
        }
        if (APPLICATION_YANG_JSON.equals(mediaType.getType())) {
            return "JSON";
        } else if (APPLICATION_YANG_XML.equals(mediaType.getType())) {
            return "XML";
        }
        return "UNKNOWN";
    }
}
