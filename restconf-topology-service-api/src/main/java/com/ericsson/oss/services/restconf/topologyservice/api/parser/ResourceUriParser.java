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

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.ResourceUriParserException;

/**
 * Data resource URI parser.
 */
public interface ResourceUriParser {

    /**
     * Parse restconf resource uri.
     *
     * @param resourceId resource uri.
     * @return {@code restconfResourceUri} object.
     * @throws ResourceUriParserException when resource uri parsing fails.
     */
    RestconfResourceUri parse(String resourceId) throws ResourceUriParserException;
}
