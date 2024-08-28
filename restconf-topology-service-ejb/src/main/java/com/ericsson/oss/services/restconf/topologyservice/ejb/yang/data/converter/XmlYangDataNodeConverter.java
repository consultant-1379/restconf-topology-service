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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.converter;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;

/**
 * {@code YangDataNode} converter to/from XML.
 */
@SuppressWarnings("java:S2629")
public final class XmlYangDataNodeConverter extends AbstractYangDataNodeConverter {

    @Override
    @TotalTimeTaken(task = "encoding to XML")
    protected String encodeYangDataNode(final YangDataNode yangDataNode) {
        return encodeYangDataNodeToXml(yangDataNode);
    }

    private String encodeYangDataNodeToXml(final YangDataNode yangDataNode) {
        final StringBuilder jsonBuilder = new StringBuilder();
        if (yangDataNode instanceof YangDataRoot) {
            jsonBuilder.append(encodeChildren(yangDataNode));
        } else if (yangDataNode instanceof YangDataContainer) {
            final YangDataContainer yangDataContainer = (YangDataContainer) yangDataNode;
            jsonBuilder.append(wrapAngleBrackets(yangDataContainer, encodeChildren(yangDataContainer)));
        } else if (yangDataNode instanceof YangDataList) {
            final YangDataList yangDataList = (YangDataList) yangDataNode;
            jsonBuilder.append(encodeChildren(yangDataList, true));
        } else if (yangDataNode instanceof YangDataSet) {
            jsonBuilder.append(encodeChildren(yangDataNode));
        } else if (yangDataNode instanceof YangDataLeaf) {
            final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataNode;
            jsonBuilder.append(wrapAngleBrackets(yangDataLeaf, yangDataLeaf.getValue()));
        } else if (yangDataNode instanceof YangDataLeafList) {
            final YangDataLeafList yangDataLeafList = (YangDataLeafList) yangDataNode;
            final String items = yangDataLeafList.getItems().stream().map(item -> wrapAngleBrackets(yangDataLeafList.getNodeName(), item))
                    .collect(Collectors.joining(""));
            jsonBuilder.append(items);
        }

        return jsonBuilder.toString();
    }

    private String encodeChildren(final YangDataNode yangDataNode, final boolean wrapAngleBrackets) {
        final StringBuilder jsonBuilder = new StringBuilder();
        Collection<YangDataNode> children = Collections.emptyList();
        if (yangDataNode instanceof YangDataRoot) {
            final YangDataRoot yangDataRoot = (YangDataRoot) yangDataNode;
            children = yangDataRoot.getDataNodes();
        } else if (yangDataNode instanceof YangDataContainer) {
            final YangDataContainer yangDataSet = (YangDataContainer) yangDataNode;
            children = yangDataSet.getDataNodes();
        } else if (yangDataNode instanceof YangDataList) {
            final YangDataList yangDataSet = (YangDataList) yangDataNode;
            children = yangDataSet.getDataNodes();
        } else if (yangDataNode instanceof YangDataSet) {
            final YangDataSet yangDataSet = (YangDataSet) yangDataNode;
            children = yangDataSet.getDataNodes();
        }

        if (children != null && !children.isEmpty()) {
            if (wrapAngleBrackets) {
                children.forEach(child -> jsonBuilder.append(wrapAngleBrackets(yangDataNode, encodeYangDataNodeToXml(child))));
            } else {
                children.forEach(child -> jsonBuilder.append(encodeYangDataNodeToXml(child)));
            }
        }
        return jsonBuilder.toString();
    }

    private String encodeChildren(final YangDataNode yangDataNode) {
        return encodeChildren(yangDataNode, false);
    }

    private String getParentModule(final YangDataNode yangDataNode) {
        if (yangDataNode.hasParent()) {
            final YangDataNode parent = yangDataNode.getParent();
            if (parent instanceof YangDataSet) {
                return getParentModule(parent);
            }
            return parent.getModule();
        }
        return null;
    }

    private String wrapAngleBrackets(final YangDataNode yangDataNode, final String data) {
        final String nodeName = yangDataNode.getNodeName();
        final String module = yangDataNode.getModule();
        final String parentModule = getParentModule(yangDataNode);
        if (parentModule != null && parentModule.equals(module)) {
            return wrapAngleBrackets(nodeName, data);
        } else {
            return "<" + nodeName + " xmlns=\"" + yangDataNode.getNamespace() + "\">" + data + "</" + nodeName + ">";
        }
    }

    private String wrapAngleBrackets(final String name, final String data) {
        return "<" + name + ">" + data + "</" + name + ">";
    }
}
