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
 * Exception is raised when no local EJB binding is found for LoadCounterService.
 */
public class NoJndiLookupNameFoundException extends OverloadProtectionServiceException{
    public NoJndiLookupNameFoundException(){
        super(new MessageService().getLocalizedMessage(Messages.JNDI_LOOKUP_NAME_NOT_FOUND_EXCEPTION),ErrorCode.JNDI_LOOKUP_NAME_NOT_FOUND.getIntValue());
    }

}
