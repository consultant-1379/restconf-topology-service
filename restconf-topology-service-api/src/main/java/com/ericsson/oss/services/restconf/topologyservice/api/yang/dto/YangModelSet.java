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

package com.ericsson.oss.services.restconf.topologyservice.api.yang.dto;

/**
 * Yang model set holder.
 */
public class YangModelSet extends AbstractYangDataNode {

    private static final long serialVersionUID = -1940036350155367813L;

    public YangModelSet() {
        // empty on purpose.
    }

    @Override
    public String toString() {
        return "[" + getDataNodes() + "]";
    }
}
