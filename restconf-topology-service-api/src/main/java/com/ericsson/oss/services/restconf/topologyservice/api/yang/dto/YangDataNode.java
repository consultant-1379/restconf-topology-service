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

import java.io.Serializable;
import java.util.Set;

/**
 * Yang data node.
 */
public interface YangDataNode extends Serializable {
    /**
     * Get yang node name (yang construct name).
     *
     * @return node name.
     */
    String getNodeName();

    /**
     * Get yang module name.
     *
     * @return module name.
     */
    String getModule();

    /**
     * Get parent yang data node.
     *
     * @return {@code YangDataNode} object.
     */
    YangDataNode getParent();

    /**
     * Set parent yang data node.
     *
     * @param parent {@code YangDataNode} object.
     */
    void setParent(YangDataNode parent);

    /**
     * Is parent null or {@code YangDataNone} instance type?
     *
     * @return true if not null and not {@code YangDataNone} instance type, else false.
     */
    boolean hasParent();

    /**
     * Get yang module namespace.
     *
     * @return module namespace.
     */
    String getNamespace();

    /**
     * Get {@code YangDataNode} objects.
     *
     * @return Collection of {@code YangDataNode} objects.
     */
    Set<YangDataNode> getDataNodes();

    /**
     * Add {@code YangDataNode} object.
     *
     * @param yangDataNode {@code YangDataNode} object.
     */
    void addDataNode(YangDataNode yangDataNode);

    /**
     * To string.
     *
     * @return String module:nodeName
     */
    String toString();
}
