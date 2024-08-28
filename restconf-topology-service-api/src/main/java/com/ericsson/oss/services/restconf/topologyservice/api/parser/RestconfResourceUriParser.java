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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.inject.Singleton;

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.ResourceUriParserException;

/**
 * Restconf data resource uri parser.
 * <br/>
 * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-3.5.3">RFC 8040, section 3.5.3</a>
 */
@Singleton
public class RestconfResourceUriParser implements ResourceUriParser {

    @Override
    public RestconfResourceUri parse(final String resourceUri) throws ResourceUriParserException {
        // TODO: Last slash should be trimmed
        if (resourceUri == null || resourceUri.isEmpty()) {
            throw new ResourceUriParserException("Empty or null api-path: " + resourceUri);
        }
        try {
            final RestconfResourceUri restconfResourceUri = new RestconfResourceUri(doubleDecode(resourceUri));
            final String[] pathToken = resourceUri.split("\\?");
            final String[] stepTokens = pathToken[0].split("/");
            for (final String stepToken : stepTokens) {
                parseStepToken(doubleDecode(stepToken), restconfResourceUri);
            }
            return restconfResourceUri;
        } catch (final ResourceUriParserException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new ResourceUriParserException(exception);
        }
    }

    private String doubleDecode(final String resourceUri) throws UnsupportedEncodingException {
        return URLDecoder.decode(URLDecoder.decode(resourceUri, "UTF-8"), "UTF-8");
    }

    private void parseStepToken(final String stepToken, final RestconfResourceUri restconfResourceUri) throws ResourceUriParserException {
        if (ResourceUriRegEx.isListInstance(stepToken)) {
            restconfResourceUri.addListInstanceStep(stepToken, ResourceUriRegEx.getListInstanceMatcherGroups(stepToken));
        } else if (ResourceUriRegEx.isApiIdentifier(stepToken)) {
            restconfResourceUri.addApiIdentifierStep(stepToken, ResourceUriRegEx.getApiIdentifierMatcherGroups(stepToken));
        } else {
            throw new ResourceUriParserException("Invalid api-path token: " + stepToken);
        }
    }
}