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
 * This enum provides the error codes supported by this service
 */
public enum ErrorCode {

    UNKNOWN_ERROR(-1),
    ACQUIRE_TIMEOUT(10100),
    NO_CAPACITY_AVAILABLE(10101),
    DUPLICATED_KEY(10102),
    CONFIG_FILE_NOT_FOUND(10103),
    JNDI_LOOKUP_NAME_NOT_FOUND(10104),
    CONFIGURATION_ERROR(10105);

    private final Integer code;

    ErrorCode(Integer code) {
        this.code = code;
    }

    public int getIntValue() {
        return this.code;
    }
}
