/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.overload.protection.service.api;

public enum JmxObjectNames {

    JMX_OVERLOAD_PROTECTION_MANAGEMENT("com.ericsson.oss.services.overload.protection.jmx:type=OverloadProtectionManagement");

    private final String jmxObjectName;

    JmxObjectNames(final String jmxObjectName) {
        this.jmxObjectName = jmxObjectName;
    }

    @Override public String toString() {
        return jmxObjectName;
    }
}
