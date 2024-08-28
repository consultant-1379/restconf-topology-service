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

package com.ericsson.oss.services.overload.protection.json;

import com.ericsson.oss.services.overload.protection.exceptions.JsonParserException;
import com.ericsson.oss.services.overload.protection.messages.MessageService;
import com.ericsson.oss.services.overload.protection.messages.Messages;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.File;
import java.io.IOException;

/**
 * Parser used to read the JSON files.
 */
public class JsonParser {

    /**
     * Return an object representation of the given JSON content
     * @param jsonFile json content to be parsed
     * @param destinationClass class to be deserialized.
     * @param <T> generic type of the class to be deserialized.
     * @return An instance of T representing the JSON contents.
     * @throws JsonParserException when the parser fails to deserialize the json content.
     */
    public <T> T parseToObject(final File jsonFile, final Class<T> destinationClass) {
        try {
            return new ObjectMapper().readValue(jsonFile, destinationClass);
        } catch (final IOException e) {
            throw new JsonParserException(new MessageService().getLocalizedMessage(Messages.FAILURE_TO_READ_JSON_OBJECT, destinationClass), e);
        }
    }

}
