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

import java.util.ArrayList;
import java.util.List;

/**
 * Yang list data holder.
 */
public final class YangDataList extends AbstractYangDataNode {
    private static final long serialVersionUID = 86829129314306653L;
    private List<String> keyValues;

    public YangDataList(final YangDataNodeBuilder yangDataNodeBuilder) {
        super(yangDataNodeBuilder);
    }

    public List<String> getKeyValues() {
        if (keyValues == null) {
            keyValues = new ArrayList<>();
        }
        return keyValues; //NOSONAR
    }

    public void setKeyValues(final List<String> keyValues) {
        this.keyValues = new ArrayList<>(keyValues);
    }
}
