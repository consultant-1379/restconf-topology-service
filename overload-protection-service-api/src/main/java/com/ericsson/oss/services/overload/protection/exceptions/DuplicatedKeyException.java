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
 * Exception thrown when a client tries to publish a capacity request with an ID already present in the service.
 */
public class DuplicatedKeyException extends OverloadProtectionServiceException {

    public DuplicatedKeyException(final String id) {
        super(new MessageService().getLocalizedMessage(Messages.DUPLICATED_KEY_EXCEPTION, id),
                ErrorCode.DUPLICATED_KEY.getIntValue());
    }

}
