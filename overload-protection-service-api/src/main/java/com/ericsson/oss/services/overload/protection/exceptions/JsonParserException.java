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

package com.ericsson.oss.services.overload.protection.exceptions;

/**
 * Exception thrown when the Json Parser fails
 */
public class JsonParserException extends OverloadProtectionServiceException {

    /**
     * @param message {String}
     * @param cause {Throwable}
     */
    public JsonParserException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
