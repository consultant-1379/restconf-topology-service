/*
 * ------------------------------------------------------------------------------
 * ******************************************************************************
 *  COPYRIGHT Ericsson 2022
 *
 *  The copyright to the computer program(s) herein is the property of
 *  Ericsson Inc. The programs may be used and/or copied only with written
 *  permission from Ericsson Inc. or in accordance with the terms and
 *  conditions stipulated in the agreement/contract under which the
 *  program(s) have been supplied.
 * ******************************************************************************
 * ------------------------------------------------------------------------------
 */

package com.ericsson.oss.services.restconf.topologyservice.api.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.ResourceUriParserException;

/**
 * Resource URI regex utility.
 */
@SuppressWarnings("java:S1214")
public interface ResourceUriRegEx {
    String COLON = ":";
    String EQUALS_SYMBOL = "=";
    String COMMA = ",";
    String IDENTIFIER = group("[a-zA-Z_][a-zA-Z0-9_\\-\\.]*");
    String AN_UNQUOTED_STRING = group("\\b([^\\\"\\']*)\\b");
    String MODULE_NAME = IDENTIFIER;
    String API_IDENTIFIER = group(group(noneOrOnce(group(MODULE_NAME + COLON))) + IDENTIFIER);
    String KEY_VALUE = AN_UNQUOTED_STRING;
    String LIST_INSTANCE = group(API_IDENTIFIER + EQUALS_SYMBOL + group(KEY_VALUE + (noneOrMore(group(COMMA + KEY_VALUE)))));

    Pattern API_IDENTIFIER_PATTERN = Pattern.compile(API_IDENTIFIER);
    Pattern LIST_INSTANCE_PATTERN = Pattern.compile(LIST_INSTANCE);

    static String group(final String regex) {
        return "(" + regex + ")";
    }

    static String noneOrOnce(final String regex) {
        return regex + "?";
    }

    static String noneOrMore(final String regex) {
        return regex + "*";
    }

    static boolean isApiIdentifier(final String token) {
        return API_IDENTIFIER_PATTERN.matcher(token).matches();
    }

    static boolean isListInstance(final String token) {
        return LIST_INSTANCE_PATTERN.matcher(token).matches();
    }

    static String[] getApiIdentifierMatcherGroups(final String token) throws ResourceUriParserException {
        final Matcher matcher = ResourceUriRegEx.API_IDENTIFIER_PATTERN.matcher(token);
        if (matcher.find() && matcher.groupCount() == 5) {
            return new String[] { matcher.group(4), matcher.group(5) };
        }
        throw new ResourceUriParserException("Invalid api-identifier: " + token);
    }

    static String[] getListInstanceMatcherGroups(final String token) throws ResourceUriParserException {
        final Matcher matcher = ResourceUriRegEx.LIST_INSTANCE_PATTERN.matcher(token);
        if (matcher.find() && matcher.groupCount() == 12) {
            return new String[] { matcher.group(5), matcher.group(6), matcher.group(7) };
        }
        throw new ResourceUriParserException("Invalid list-instance: " + token);
    }
}
