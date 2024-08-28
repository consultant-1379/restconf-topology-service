/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.overload.protection.service.api;

/**
 * Builder class used to simplify the construction of CapacityRequest instances
 */
public class CapacityRequestBuilder {

    private CapacityRequest instance;

    private CapacityRequestBuilder() {
        instance = new CapacityRequest();
    }

    public static CapacityRequestBuilder application(final String application) {
        CapacityRequestBuilder builder = new CapacityRequestBuilder();
        builder.instance.setApplication(application);

        return builder;
    }

    public CapacityRequestBuilder useCase(final String useCase) {

        instance.setUseCase(useCase);
        return this;
    }

    public CapacityRequestBuilder id(final String id) {

        instance.setId(id);
        return this;
    }

    public CapacityRequestBuilder points(final Integer points) {

        instance.setPoints(points);
        return this;
    }

    public CapacityRequest build() {
        return instance;
    }
}
