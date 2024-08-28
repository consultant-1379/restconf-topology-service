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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.resolver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;

/**
 * Restconf yang data leaf-ref value resolver.
 */
public class YangDataLeafRefValueResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(YangDataLeafRefValueResolver.class);

    /**
     * Resolve leaf-ref values.
     *
     * @param yangDataNode {@code YangDataNode} object.
     */
    public void resolveLeafRefValues(final YangDataNode yangDataNode) {
        traverseDataNodes(yangDataNode);
    }

    @SuppressWarnings({ "java:S3776", "java:S135" })
    public void resolveLeafRefXPathAndGetValue(final YangDataLeaf yangDataLeaf) {
        // TODO: handle xpaths predicates?
        if (yangDataLeaf.getValue() == null && yangDataLeaf.getPath() != null && !yangDataLeaf.getPath().isEmpty()) {
            if (yangDataLeaf.getPath().startsWith("..")) {
                yangDataLeaf.setValue(processRelativePath(yangDataLeaf));
            } else {
                yangDataLeaf.setValue(processAbsolutePath(yangDataLeaf));
            }
        }
    }

    private void traverseDataNodes(final YangDataNode yangDataNode) {
        for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
            if (dataNode instanceof YangDataSet) {
                for (final YangDataNode dataSetNode : yangDataNode.getDataNodes()) {
                    traverseDataNodes(dataSetNode);
                }
            }
            if (dataNode instanceof YangDataLeaf) {
                final YangDataLeaf yangDataLeaf = (YangDataLeaf) dataNode;
                if ("leafref".equals(yangDataLeaf.getType())) {
                    resolveLeafRefXPathAndGetValue(yangDataLeaf);
                }
            } else {
                traverseDataNodes(dataNode);
            }
        }
    }

    @SuppressWarnings("java:S3776")
    private String processAbsolutePath(final YangDataLeaf yangDataLeaf) {
        final String[] slashTokens = yangDataLeaf.getPath().split("/");
        if (slashTokens.length > 2) {
            final String lastSlashToken = slashTokens[slashTokens.length - 1];
            final String lastNodeToken = slashTokens[slashTokens.length - 2];
            YangDataNode parentNode = yangDataLeaf.getParent();
            while (parentNode.hasParent()) {
                parentNode = parentNode.getParent();
                if (parentNode instanceof YangDataSet) {
                    if (getQName(parentNode.getParent()).equals(lastNodeToken)) {
                        for (final YangDataNode dataNode : parentNode.getDataNodes()) {
                            if (getQName(dataNode).equals(lastSlashToken)) {
                                return getLeafNodeValue(parentNode, lastSlashToken);
                            }
                        }
                    }
                    parentNode = parentNode.getParent();
                }
                if (getQName(parentNode).equals(lastNodeToken)) {
                    break;
                }
            }
            return getLeafNodeValue(parentNode, lastSlashToken);
        }
        LOGGER.debug("Failed to resolve leaf-ref {} xpath {}", yangDataLeaf, yangDataLeaf.getPath());
        return null;
    }

    @SuppressWarnings("java:S3776")
    private String processRelativePath(final YangDataLeaf yangDataLeaf) {
        YangDataNode currentYangDataNode = yangDataLeaf;
        final String[] slashTokens = yangDataLeaf.getPath().split("/");
        if (slashTokens.length > 2) {
            final String lastSlashToken = slashTokens[slashTokens.length - 1];
            final String lastNodeToken = slashTokens[slashTokens.length - 2];
            final List<YangDataNode> nodePathList = new ArrayList<>();
            for (final String slashToken : slashTokens) {
                if ("..".equals(slashToken) && currentYangDataNode != null && currentYangDataNode.hasParent()) {
                    currentYangDataNode = currentYangDataNode.getParent();
                    if (currentYangDataNode instanceof YangDataSet) {
                        for (final YangDataNode dataNode : currentYangDataNode.getDataNodes()) {
                            if (getQName(dataNode).equals(lastNodeToken)) {
                                return getLeafNodeValue(dataNode, lastSlashToken);
                            }
                        }
                        currentYangDataNode = currentYangDataNode.getParent();
                    }
                    nodePathList.add(currentYangDataNode);
                }
            }
            Collections.reverse(nodePathList);
            for (final YangDataNode pathDataNode : nodePathList) {
                for (final YangDataNode dataNode : pathDataNode.getDataNodes()) {
                    if (dataNode instanceof YangDataSet) {
                        for (final YangDataNode dataSetNode : dataNode.getDataNodes()) {
                            if (getQName(dataSetNode).equals(lastNodeToken)) {
                                return getLeafNodeValue(dataSetNode, lastSlashToken);
                            }
                        }
                    }
                }
            }
        }
        LOGGER.debug("Failed to resolve leaf-ref {} xpath {}", yangDataLeaf, yangDataLeaf.getPath());
        return null;
    }

    @SuppressWarnings("java:S3776")
    private String getLeafNodeValue(final YangDataNode yangDataNode, final String xPathElement) {
        for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
            if (dataNode instanceof YangDataSet) {
                for (final YangDataNode dataSetNode : dataNode.getDataNodes()) {
                    if (getQName(dataSetNode).equals(xPathElement) && dataSetNode instanceof YangDataLeaf) {
                        return ((YangDataLeaf) dataSetNode).getValue();
                    }
                }
            } else {
                if (getQName(dataNode).equals(xPathElement) && dataNode instanceof YangDataLeaf) {
                    return ((YangDataLeaf) dataNode).getValue();
                }
            }
        }
        return null;
    }

    private String getQName(final YangDataNode yangDataNode) {
        return yangDataNode.getModule() + ":" + yangDataNode.getNodeName();
    }
}