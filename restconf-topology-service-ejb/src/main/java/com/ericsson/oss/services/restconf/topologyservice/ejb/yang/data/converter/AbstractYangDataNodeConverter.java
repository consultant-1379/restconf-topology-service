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

import com.ericsson.oss.services.restconf.topologyservice.api.yang.converter.YangDataNodeConverter;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNone;

/**
 * Abstract yang data node converter.
 */
public abstract class AbstractYangDataNodeConverter implements YangDataNodeConverter {
    @Override
    public YangDataNode decode(final String data) {
        return YangDataNone.none();
    }

    @Override
    public String encode(final YangDataNode yangDataNode) {
        return encodeYangDataNode(yangDataNode);
    }

    protected abstract String encodeYangDataNode(YangDataNode yangDataNode);
}
