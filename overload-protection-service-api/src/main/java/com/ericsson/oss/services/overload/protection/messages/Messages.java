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

import ch.qos.cal10n.BaseName;
import ch.qos.cal10n.Locale;
import ch.qos.cal10n.LocaleData;

/**
 * <p>This enum provides the internationalizable messages for this service.</p>
 * <p>Each intem on this enum should have a message with the same key in the resource bundle file defined by the annotation @BaseName</p>
 */
@BaseName("messages")
@LocaleData( { @Locale("en")})
public enum Messages {

    UNKNOWN_EXCEPTION,
    ACQUIRE_TIMEOUT_EXCEPTION,
    NO_CAPACITY_EXCEPTION,
    DUPLICATED_KEY_EXCEPTION,
    CONFIG_FILE_NOT_FOUND_EXCEPTION,
    FAILURE_TO_READ_JSON_OBJECT,
    JNDI_LOOKUP_NAME_NOT_FOUND_EXCEPTION
}
