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

import javax.enterprise.context.RequestScoped;

import org.jboss.resteasy.spi.HttpRequest;

/**
 * Http request provider.
 */
@RequestScoped
public class RequestProvider {
    private HttpRequest httpRequest;

    public HttpRequest getHttpRequest() {
        return httpRequest;
    }

    public void setHttpRequest(final HttpRequest httpRequest) {
        this.httpRequest = httpRequest;
    }
}
