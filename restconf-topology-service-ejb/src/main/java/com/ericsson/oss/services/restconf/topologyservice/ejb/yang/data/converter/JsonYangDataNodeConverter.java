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
 * {@code YangDataNode} converter to/from JSON.
 */
@SuppressWarnings("java:S2629")
public final class JsonYangDataNodeConverter extends AbstractYangDataNodeConverter {

    private static final String QUOTE_SYMBOL = "\"";
    private static final String COLON_SYMBOL = ":";

    @Override
    @TotalTimeTaken(task = "encoding to JSON")
    protected String encodeYangDataNode(final YangDataNode yangDataNode) {
        return encodeYangDataNodeToJson(yangDataNode);
    }

    private String encodeYangDataNodeToJson(final YangDataNode yangDataNode) {
        final StringBuilder jsonBuilder = new StringBuilder();
        if (yangDataNode instanceof YangDataRoot) {
            jsonBuilder.append(encodeChildren(yangDataNode));
        } else if (yangDataNode instanceof YangDataContainer) {
            final YangDataContainer yangDataContainer = (YangDataContainer) yangDataNode;
            jsonBuilder.append(wrapDoubleQuote(yangDataContainer)).append(":{");
            jsonBuilder.append(encodeChildren(yangDataContainer));
            jsonBuilder.append("}");
        } else if (yangDataNode instanceof YangDataList) {
            final YangDataList yangDataList = (YangDataList) yangDataNode;
            jsonBuilder.append(wrapDoubleQuote(yangDataList)).append(":[");
            jsonBuilder.append(encodeChildren(yangDataList));
            jsonBuilder.append("]");
        } else if (yangDataNode instanceof YangDataSet) {
            jsonBuilder.append(encodeChildren(yangDataNode));
        } else if (yangDataNode instanceof YangDataLeaf) {
            final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataNode;
            jsonBuilder.append(wrapDoubleQuoteKeyValue(yangDataLeaf.getNodeName(), yangDataLeaf.getValue()));
        } else if (yangDataNode instanceof YangDataLeafList) {
            final YangDataLeafList yangDataLeafList = (YangDataLeafList) yangDataNode;
            final String items = yangDataLeafList.getItems().stream().map(this::wrapDoubleQuote).collect(Collectors.joining(","));
            jsonBuilder.append(wrapDoubleQuoteOnlyKey(yangDataLeafList.getNodeName(), wrapSquareBrackets(items)));
        }

        return jsonBuilder.toString();
    }

    private String encodeChildren(final YangDataNode yangDataNode) {
        final StringBuilder jsonBuilder = new StringBuilder();
        Collection<YangDataNode> children = Collections.emptyList();
        boolean wrapWithCurlyBraces = false;
        if (yangDataNode instanceof YangDataRoot) {
            final YangDataRoot yangDataRoot = (YangDataRoot) yangDataNode;
            children = yangDataRoot.getDataNodes();
            wrapWithCurlyBraces = true;
        } else if (yangDataNode instanceof YangDataContainer) {
            final YangDataContainer yangDataSet = (YangDataContainer) yangDataNode;
            children = yangDataSet.getDataNodes();
        } else if (yangDataNode instanceof YangDataList) {
            final YangDataList yangDataSet = (YangDataList) yangDataNode;
            children = yangDataSet.getDataNodes();
        } else if (yangDataNode instanceof YangDataSet) {
            final YangDataSet yangDataSet = (YangDataSet) yangDataNode;
            children = yangDataSet.getDataNodes();
            wrapWithCurlyBraces = true;
        }

        if (children != null && !children.isEmpty()) {
            jsonBuilder.append(wrapWithCurlyBraces ? "{" : "");
            final int size = children.size();
            if (size > 1) {
                children.stream().limit(size - 1L).forEach(child -> jsonBuilder.append(encodeYangDataNodeToJson(child)).append(","));
                children.stream().skip(size - 1L).forEach(child -> jsonBuilder.append(encodeYangDataNodeToJson(child)));
            } else {
                children.forEach(child -> jsonBuilder.append(encodeYangDataNodeToJson(child)));
            }
            jsonBuilder.append(wrapWithCurlyBraces ? "}" : "");
        }
        return jsonBuilder.toString();
    }

    private String wrapSquareBrackets(final String data) {
        return "[" + data + "]";
    }

    private String wrapDoubleQuote(final String data) {
        return QUOTE_SYMBOL + data + QUOTE_SYMBOL;
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

    private String wrapDoubleQuote(final YangDataNode yangDataNode) {
        final String nodeName = yangDataNode.getNodeName();
        final String module = yangDataNode.getModule();
        final String parentModule = getParentModule(yangDataNode);
        if (parentModule != null && parentModule.equals(module)) {
            return wrapDoubleQuote(nodeName);
        }
        return wrapDoubleQuote(module + COLON_SYMBOL + nodeName);
    }

    private String wrapDoubleQuoteKeyValue(final String key, final String value) {
        return wrapDoubleQuote(key) + COLON_SYMBOL + wrapDoubleQuote(value);
    }

    private String wrapDoubleQuoteOnlyKey(final String key, final String value) {
        return wrapDoubleQuote(key) + COLON_SYMBOL + value;
    }
}
