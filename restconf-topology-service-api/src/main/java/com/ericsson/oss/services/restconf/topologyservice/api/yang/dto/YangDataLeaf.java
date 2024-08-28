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
 * Yang leaf data holder.
 */
public final class YangDataLeaf extends AbstractYangDataNode {
    private static final long serialVersionUID = 5396031947983539211L;
    private String value;
    private String type;
    private boolean key;
    private String path;
    private String defaultValue;

    public YangDataLeaf(final YangDataNodeBuilder yangDataNodeBuilder) {
        super(yangDataNodeBuilder);
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getPath() {
        return path;
    }

    public void setPath(final String path) {
        this.path = path;
    }

    public boolean isKey() {
        return key;
    }

    public void setKey(final boolean key) {
        this.key = key;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }
}