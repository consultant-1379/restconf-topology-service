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
 * Yang leaf-list data holder.
 */
public final class YangDataLeafList extends AbstractYangDataNode {
    private static final long serialVersionUID = -6762774531630302364L;
    private List<String> items;
    private String type;

    public YangDataLeafList(final YangDataNodeBuilder yangDataNodeBuilder) {
        super(yangDataNodeBuilder);
    }

    public List<String> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items; //NOSONAR
    }

    public void setItems(final List<String> items) {
        this.items = items; //NOSONAR
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }
}
