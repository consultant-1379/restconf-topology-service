/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.restconf.topologyservice.api.exception;

import java.io.Serializable;

/**
 * General restconf exception.
 */
public class RestconfException extends Exception implements Serializable {
    private static final long serialVersionUID = 1L;

    public RestconfException(final String message) {
        super(message);
    }

    public RestconfException(final Exception exception) {
        super(exception);
    }
}
