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

import com.ericsson.oss.services.overload.protection.messages.MessageService;
import com.ericsson.oss.services.overload.protection.messages.Messages;

/**
 * Exception thrown when a lock timeout occurs in the release or acquire methods.
 */
public class LockTimeoutException extends OverloadProtectionServiceException {

    public LockTimeoutException(Throwable cause) {
        super(new MessageService().getLocalizedMessage(Messages.ACQUIRE_TIMEOUT_EXCEPTION), cause, ErrorCode.ACQUIRE_TIMEOUT.getIntValue());
    }
}
