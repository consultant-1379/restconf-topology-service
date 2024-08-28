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
 * No object, Singleton class in replacement of null value for any {@code YangDataNode} objects.
 */
public final class YangDataNone extends AbstractYangDataNode {
    private static final long serialVersionUID = -4243954860529320794L;

    private YangDataNone() {
        super();
    }

    public static YangDataNone none() {
        return InstanceHolder.INSTANCE;
    }

    @Override
    public String toString() {
        return "none";
    }

    private static class InstanceHolder {
        private static final YangDataNone INSTANCE = new YangDataNone();
    }
}
