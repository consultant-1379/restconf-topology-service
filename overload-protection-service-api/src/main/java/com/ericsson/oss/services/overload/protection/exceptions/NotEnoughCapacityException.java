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
 * Exception thrown when there's not enough capacity in the server to handle the requested points.
 */
public class NotEnoughCapacityException extends OverloadProtectionServiceException {

    public NotEnoughCapacityException() {
        super(new MessageService().getLocalizedMessage(Messages.NO_CAPACITY_EXCEPTION),
                ErrorCode.NO_CAPACITY_AVAILABLE.getIntValue());
    }

}
