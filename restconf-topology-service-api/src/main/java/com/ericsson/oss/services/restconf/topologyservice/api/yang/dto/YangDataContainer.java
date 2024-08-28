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
 * Yang container data holder.
 */
public final class YangDataContainer extends AbstractYangDataNode {
    private static final long serialVersionUID = -4593783711181981458L;
    private String fdn;

    public YangDataContainer(final YangDataNodeBuilder yangDataNodeBuilder) {
        super(yangDataNodeBuilder);
    }

    public String getFdn() {
        return fdn;
    }

    public void setFdn(final String fdn) {
        this.fdn = fdn;
    }
}
