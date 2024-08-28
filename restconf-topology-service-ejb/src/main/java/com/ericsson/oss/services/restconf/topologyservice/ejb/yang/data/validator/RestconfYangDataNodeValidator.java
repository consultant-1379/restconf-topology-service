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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.validator;

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfYangHierarchyValidationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;

/**
 * Restconf yang data node validator.
 */
public class RestconfYangDataNodeValidator {

    /**
     * Validate {@code RestconfResourceUri.Step} step with given {@code YangDataNode} object.
     *
     * @param step         {@code RestconfResourceUri.Step} object
     * @param yangDataNode {@code YangDataNode} object
     */
    public void validateYangDataNode(final RestconfResourceUri.Step step, final YangDataNode yangDataNode)
            throws RestconfYangHierarchyValidationException {
        if (yangDataNode instanceof YangDataList) {
            validateYangDataList(step, yangDataNode);
        } else if (yangDataNode instanceof YangDataLeafList) {
            validateYangDataLeafList(step);
        } else if (yangDataNode instanceof YangDataLeaf) {
            validateYangDataLeaf(step);
        } else if (yangDataNode instanceof YangDataContainer && !step.isApiIdentifier()) {
            throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting an api-identifier type!");
        }
    }

    private void validateYangDataList(final RestconfResourceUri.Step step, final YangDataNode yangDataNode)
            throws RestconfYangHierarchyValidationException {
        if (!step.isListInstance()) {
            throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting list type!");
        } else {
            final int keyNameCount = ((AbstractYangDataNode) yangDataNode).getKeyNames().length;
            if (step.getKeyValues().length != keyNameCount) {
                throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting " + keyNameCount + " key(s)!");
            }
        }
    }

    private void validateYangDataLeafList(final RestconfResourceUri.Step step) throws RestconfYangHierarchyValidationException {
        if (!step.isLeafList()) {
            throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting leaf-list type!");
        } else {
            if (step.getKeyValues().length == 0) {
                throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting one or more values in leaf-list!");
            }
        }
    }

    private void validateYangDataLeaf(final RestconfResourceUri.Step step) throws RestconfYangHierarchyValidationException {
        if (!step.isLeaf()) {
            throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; expecting leaf type!");
        } else {
            if (step.getKeyValues().length != 0) {
                throw new RestconfYangHierarchyValidationException(step.getOriginalStep() + "; unexpected one or more values in leaf!");
            }
        }
    }
}