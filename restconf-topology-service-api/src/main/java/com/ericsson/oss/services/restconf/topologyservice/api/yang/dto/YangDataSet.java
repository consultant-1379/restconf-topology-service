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
 * Yang data set holder.
 */
public class YangDataSet extends AbstractYangDataNode {
    private static final long serialVersionUID = 7521812294594147139L;
    private String fdn;

    public YangDataSet() {
        // empty on purpose.
    }

    public String getFdn() {
        return fdn;
    }

    public void setFdn(final String fdn) {
        this.fdn = fdn;
    }

    @Override
    public String toString() {
        return "[" + getDataNodes() + "]";
    }
}
