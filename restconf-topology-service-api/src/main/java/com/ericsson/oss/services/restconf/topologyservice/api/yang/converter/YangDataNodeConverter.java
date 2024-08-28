/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2022
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.restconf.topologyservice.api.yang.converter;

import com.ericsson.oss.services.restconf.topologyservice.api.exception.YangDataNodeConverterException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;

/**
 * {@code YangDataNode} converter.
 */
public interface YangDataNodeConverter {

    /**
     * Decode string into {@code YangDataNode}.
     *
     * @param data String data.
     * @return Decoded {@code YangDataNode} object.
     * @throws YangDataNodeConverterException when decode fails.
     */
    YangDataNode decode(String data) throws YangDataNodeConverterException;

    /**
     * Encode {@code YangDataNode} into string.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @return Encoded string.
     */
    String encode(YangDataNode yangDataNode);
}
