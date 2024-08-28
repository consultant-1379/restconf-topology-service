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
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;

/**
 * Restconf yang data nodes cleaner.
 */
public class RestconfYangDataNodeCleaner {

    @SuppressWarnings("java:S3776")
    public void cleanUpEmptyDataNodes(final Iterator<YangDataNode> iterator) {
        while (iterator.hasNext()) {
            final YangDataNode yangDataNode = iterator.next();
            if (isInstanceOfAny(yangDataNode, YangDataContainer.class, YangDataList.class, YangDataSet.class)) {
                if (yangDataNode.getDataNodes().isEmpty()) {
                    iterator.remove();
                } else {
                    cleanUpEmptyDataNodes(yangDataNode.getDataNodes().iterator());
                    if (yangDataNode.getDataNodes().isEmpty()) {
                        iterator.remove();
                    }
                }
            } else if (yangDataNode instanceof YangDataLeaf) {
                final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataNode;
                if (yangDataLeaf.getValue() == null || yangDataLeaf.getValue().isEmpty()) {
                    iterator.remove();
                }
            } else if (yangDataNode instanceof YangDataLeafList) {
                final YangDataLeafList yangDataLeafList = (YangDataLeafList) yangDataNode;
                yangDataLeafList.getItems().removeIf(Objects::isNull);
                yangDataLeafList.getItems().removeIf(String::isEmpty);
                if (yangDataLeafList.getItems().isEmpty()) {
                    iterator.remove();
                }
            }
        }
    }

    public void cleanUpDuplicateDataNodes(final YangDataNode yangDataNode) {
        // TODO: have a blacklist of modules that could potentially have duplicates?
        if (isInstanceOfAny(yangDataNode, YangDataLeaf.class, YangDataLeafList.class)) {
            return;
        }
        checkAndRemoveDuplicateDataNodes(yangDataNode.getDataNodes().iterator());
        for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
            if (dataNode instanceof YangDataList) {
                for (final YangDataNode listItem : dataNode.getDataNodes()) {
                    cleanUpDuplicateDataNodes(listItem);
                }
                checkAndRemoveDuplicateDataNodes(dataNode.getDataNodes().iterator());
            } else if (dataNode instanceof YangDataContainer) {
                for (final YangDataNode childDataNode : dataNode.getDataNodes()) {
                    cleanUpDuplicateDataNodes(childDataNode);
                }
            }
        }
    }

    private void checkAndRemoveDuplicateDataNodes(final Iterator<YangDataNode> iterator) {
        final List<YangDataNode> encounteredNodes = new ArrayList<>();
        boolean encountered;
        while (iterator.hasNext()) {
            final YangDataNode dataNode = iterator.next();
            encountered = false;
            for (final YangDataNode encounteredNode : encounteredNodes) {
                if (isEquals(encounteredNode, dataNode)) {
                    iterator.remove();
                    encountered = true;
                    break;
                }
            }
            if (!encountered) {
                encounteredNodes.add(dataNode);
            }
        }
    }

    @SafeVarargs
    private final boolean isInstanceOfAny(final YangDataNode yangDataNode, final Class<? extends YangDataNode>... classes) {
        for (final Class<? extends YangDataNode> clazz : classes) {
            if (clazz.isInstance(yangDataNode)) {
                return true;
            }
        }
        return false;
    }

    private boolean isEquals(final YangDataNode first, final YangDataNode second) {
        if (first == null) {
            return false;
        } else if (second == null) {
            return false;
        } else if (!first.getClass().equals(second.getClass())) {
            return false;
        } else {
            if (first instanceof YangDataSet) {
                if (!Arrays.equals(getKeyQNames(first), getKeyQNames(second))) {
                    return false;
                }
                return Arrays.equals(getKeyValues(first), getKeyValues(second));
            } else {
                return first.getNamespace().equals(second.getNamespace()) && first.getModule().equals(second.getModule()) && first.getNodeName()
                        .equals(second.getNodeName());
            }
        }
    }

    private String[] getKeyValues(final YangDataNode yangDataNode) {
        final List<String> keyValues = new ArrayList<>();
        if (yangDataNode instanceof YangDataSet) {
            for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
                if (dataNode instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) dataNode;
                    if (yangDataLeaf.isKey()) {
                        keyValues.add(yangDataLeaf.getValue());
                    }
                }
            }
        }

        return keyValues.toArray(new String[0]);
    }

    private String[] getKeyQNames(final YangDataNode yangDataNode) {
        final List<String> keyQNames = new ArrayList<>();
        if (yangDataNode instanceof YangDataSet) {
            for (final YangDataNode dataNode : yangDataNode.getDataNodes()) {
                if (dataNode instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) dataNode;
                    if (yangDataLeaf.isKey()) {
                        final String parentName = getParentName(yangDataLeaf);
                        keyQNames.add(yangDataLeaf.getModule() + ":" + (parentName != null ? parentName + ":" + yangDataLeaf.getNodeName() :
                                yangDataLeaf.getNodeName()));
                    }
                }
            }
        }

        return keyQNames.toArray(new String[0]);
    }

    private String getParentName(final YangDataNode yangDataNode) {
        if (yangDataNode.hasParent()) {
            final YangDataNode parent = yangDataNode.getParent();
            if (parent instanceof YangDataSet) {
                return parent.getParent().getNodeName();
            }
            return parent.getNodeName();
        }
        return null;
    }
}
