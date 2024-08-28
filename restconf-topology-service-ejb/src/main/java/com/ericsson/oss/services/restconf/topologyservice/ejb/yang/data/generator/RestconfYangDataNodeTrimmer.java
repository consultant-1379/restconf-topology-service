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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;

/**
 * Restconf yang data node trimmer as per resource uri.
 */
public class RestconfYangDataNodeTrimmer {

    /**
     * Trim {@code YangDataNode} object as per resource uri.
     *
     * @param restconfResourceUri {@code restconfResourceUri} object.
     * @param yangDataNode        {@code YangDataNode} object.
     */
    public void trimAndGetYangDataNode(final RestconfResourceUri restconfResourceUri, final YangDataNode yangDataNode) {
        final YangDataNode trimmedYangDataNode = new YangDataRoot();
        final RestconfResourceUri.Step firstStep = restconfResourceUri.getSteps().get(0);
        if (firstStep != null && firstStep.hasNext()) {
            traverseAndTrimYangDataNodes(firstStep, yangDataNode.getDataNodes(), trimmedYangDataNode);
            ((AbstractYangDataNode) yangDataNode).setDataNodes(trimmedYangDataNode.getDataNodes());
        }
    }

    @SuppressWarnings("java:S3776")
    private void traverseAndTrimYangDataNodes(final RestconfResourceUri.Step step, final Set<YangDataNode> dataNodes,
                                              final YangDataNode trimmedYangDataNode) {
        if (step != null) {
            for (final YangDataNode childDataNode : dataNodes) {
                if (childDataNode instanceof YangDataSet) {
                    traverseAndTrimYangDataNodes(step, childDataNode.getDataNodes(), trimmedYangDataNode);
                } else {
                    if (isEqualsQName(step, childDataNode)) {
                        if (childDataNode instanceof YangDataList) {
                            for (final YangDataNode listItem : childDataNode.getDataNodes()) {
                                if (Arrays.equals(step.getKeyValues(), getKeyNames(listItem))) {
                                    if (!step.hasNext()) {
                                        final YangDataList yangDataList = new AbstractYangDataNode.YangDataNodeBuilder().list(childDataNode);
                                        yangDataList.getDataNodes().clear();
                                        yangDataList.addDataNode(listItem);
                                        trimmedYangDataNode.addDataNode(yangDataList);
                                    } else {
                                        traverseAndTrimYangDataNodes(step.getNextStep(), listItem.getDataNodes(), trimmedYangDataNode);
                                    }
                                }
                            }
                        } else {
                            if (!step.hasNext()) {
                                trimmedYangDataNode.addDataNode(childDataNode);
                            } else {
                                traverseAndTrimYangDataNodes(step.getNextStep(), childDataNode.getDataNodes(), trimmedYangDataNode);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isEqualsQName(final RestconfResourceUri.Step step, final YangDataNode yangDataNode) {
        return step.getModuleName().equals(yangDataNode.getModule()) && step.getIdentifier().equals(yangDataNode.getNodeName());
    }

    private String[] getKeyNames(final YangDataNode yangDataNode) {
        final List<String> keyValues = new ArrayList<>();
        if (yangDataNode instanceof YangDataSet) {
            final YangDataSet yangDataSet = (YangDataSet) yangDataNode;
            for (final YangDataNode childDataNode : yangDataSet.getDataNodes()) {
                if (childDataNode instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) childDataNode;
                    if (yangDataLeaf.isKey()) {
                        keyValues.add(yangDataLeaf.getValue());
                    }
                }
            }
        }
        return keyValues.toArray(new String[0]);
    }
}
