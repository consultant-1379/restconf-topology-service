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

package com.ericsson.oss.services.restconf.topologyservice.ejb.context;

import javax.enterprise.context.RequestScoped;

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfInstrumentationBuilder;

/**
 * Restconf context implementation.
 */
@RequestScoped
public class RestconfContext {

    private RestconfInstrumentationBuilder restconfInstrumentationBuilder;

    public RestconfContext() {
        // Left empty on purpose as it is needed for RequestScoped annotation.
    }

    public RestconfInstrumentationBuilder getRestconfInstrumentationBuilder() {
        return restconfInstrumentationBuilder;
    }

    public void setRestconfInstrumentationBuilder(final RestconfInstrumentationBuilder restconfInstrumentationBuilder) {
        this.restconfInstrumentationBuilder = restconfInstrumentationBuilder;
    }
}
