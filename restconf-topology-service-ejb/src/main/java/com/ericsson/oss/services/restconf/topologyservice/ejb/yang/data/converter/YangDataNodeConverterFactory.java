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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter;

import javax.inject.Singleton;

import com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.YangDataNodeConverterException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.converter.YangDataNodeConverter;

/**
 * Yang data node converter utility.
 */
@Singleton
public final class YangDataNodeConverterFactory {

    private YangDataNodeConverterFactory() {
    }

    /**
     * Get {@code YangDataNodeConverter} object based on media type.
     *
     * @param mediaType Response media type.
     * @return {@code YangDataNodeConverter} object
     * @throws {@code YangDataNodeConverterException} for unsupported media type.
     */
    public static YangDataNodeConverter getConverter(final String mediaType) throws YangDataNodeConverterException {
        if (mediaType.equals(RestconfRestConstants.APPLICATION_YANG_JSON)) {
            return new JsonYangDataNodeConverter();
        } else if (mediaType.equals(RestconfRestConstants.APPLICATION_YANG_XML)) {
            return new XmlYangDataNodeConverter();
        } else {
            throw new YangDataNodeConverterException("Unsupported media type: " + mediaType);
        }
    }
}
