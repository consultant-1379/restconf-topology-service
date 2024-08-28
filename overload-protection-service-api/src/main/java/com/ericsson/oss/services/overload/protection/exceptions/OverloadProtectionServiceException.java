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

import javax.ejb.ApplicationException;

@ApplicationException(rollback = true, inherited = true)
public class OverloadProtectionServiceException extends RuntimeException {

    private final Integer errorCode;

    public OverloadProtectionServiceException(String message, Integer errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    public OverloadProtectionServiceException(String message, Throwable cause, Integer errorCode) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public OverloadProtectionServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = ErrorCode.UNKNOWN_ERROR.getIntValue();
    }

    public Integer getErrorCode() {
        return errorCode;
    }

}
