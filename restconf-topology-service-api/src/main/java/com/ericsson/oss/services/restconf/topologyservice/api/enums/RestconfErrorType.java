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

package com.ericsson.oss.services.restconf.topologyservice.api.enums;

/**
 * Restconf error type enumeration.
 */
public enum RestconfErrorType {
    /**
     * Errors relating to the transport layer.
     */
    TRANSPORT("transport"),
    /**
     * Errors relating to the RPC or notification layer.
     */
    RPC("rpc"),
    /**
     * Errors relating to the protocol operation layer.
     */
    PROTOCOL("protocol"),
    /**
     * Errors relating to the server application layer.
     */
    APPLICATION("application");

    private final String name;

    RestconfErrorType(final String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
