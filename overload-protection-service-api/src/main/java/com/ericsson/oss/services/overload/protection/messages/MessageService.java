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

package com.ericsson.oss.services.overload.protection.messages;

import ch.qos.cal10n.IMessageConveyor;
import ch.qos.cal10n.MessageConveyor;

import java.util.Locale;

/**
 * Service responsible to load internatinalizable messages from the resource bundle provided with the application.
 */
public class MessageService {

    private IMessageConveyor messageConveyor;

    /**
     * Constructs an instance using the given Loacale
     * @param locale locale to be used to retrieve the messages
     */
    public MessageService(Locale locale) {
        messageConveyor = new MessageConveyor(locale);
    }

    /**
     * Default constructor. Uses by default the locale <strong>en</strong>
     */
    public MessageService() {
        this(Locale.ENGLISH);
    }

    /**
     * Gets a localized message in the locale used to create the service instance.
     * @param message message key to be used
     * @param args arguments to be replaced in the message
     * @return An interpolated message according to the resource file used (based on the chosen locale)
     */
    public String getLocalizedMessage(final Messages message, Object...args) {
        return messageConveyor.getMessage(message, args);
    }

}
