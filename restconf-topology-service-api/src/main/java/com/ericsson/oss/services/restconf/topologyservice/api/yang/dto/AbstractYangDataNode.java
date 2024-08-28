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

package com.ericsson.oss.services.restconf.topologyservice.api.yang.dto;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Yang data node abstract class.
 */
public abstract class AbstractYangDataNode implements YangDataNode {

    private static final long serialVersionUID = -6741752490921257538L;
    private final String module;
    private final String namespace;
    private final String nodeName;
    private YangDataNode parent;
    private Set<YangDataNode> dataNodes;
    private final YangModelSet modelSet;
    private Map<String, String> cacheAttributes;

    public AbstractYangDataNode() {
        this.module = "";
        this.namespace = "";
        this.nodeName = "";
        this.parent = YangDataNone.none();
        this.dataNodes = null;
        this.modelSet = null;
        this.cacheAttributes = null;
    }

    public AbstractYangDataNode(final YangDataNodeBuilder yangDataNodeBuilder) {
        this.module = yangDataNodeBuilder.module;
        this.namespace = yangDataNodeBuilder.namespace;
        this.nodeName = yangDataNodeBuilder.nodeName;
        this.parent = yangDataNodeBuilder.parent;
        this.dataNodes = yangDataNodeBuilder.dataNodes;
        this.modelSet = yangDataNodeBuilder.modelSet;
        this.cacheAttributes = null;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

    @Override
    public String getModule() {
        return module;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public YangDataNode getParent() {
        return parent;
    }

    @Override
    public void setParent(final YangDataNode parent) {
        this.parent = parent;
    }

    @Override
    public boolean hasParent() {
        return parent != null && !(parent instanceof YangDataNone);
    }

    @Override
    public Set<YangDataNode> getDataNodes() {
        if (dataNodes == null) {
            dataNodes = new LinkedHashSet<>();
        }
        return dataNodes; //NOSONAR
    }

    @Override
    public void addDataNode(final YangDataNode yangDataNode) {
        getDataNodes().add(yangDataNode);
        yangDataNode.setParent(this);
    }

    public void setDataNodes(final Set<YangDataNode> dataNodes) {
        this.dataNodes = dataNodes; //NOSONAR
    }

    public YangModelSet getModelSet() {
        return modelSet;
    }

    public void addCacheAttribute(final String attribute, final String value) {
        if (cacheAttributes == null) {
            cacheAttributes = new HashMap<>();
        }
        cacheAttributes.put(attribute, value);
    }

    public Map<String, String> getCacheAttributes() {
        return cacheAttributes;
    }

    public void resetCacheAttributes() {
        cacheAttributes = null;
    }

    public String[] getKeyNames() {
        final List<String> keyNames = new ArrayList<>();
        if (this instanceof YangDataList) {
            final YangModelSet yangModelSet = getModelSet();
            for (final YangDataNode listChildNode : yangModelSet.getDataNodes()) {
                if (listChildNode instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) listChildNode;
                    if (yangDataLeaf.isKey()) {
                        keyNames.add(yangDataLeaf.getNodeName());
                    }
                }
            }
        }

        return keyNames.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return (module != null && !module.isEmpty()) ? (module + ":" + nodeName) : nodeName;
    }

    /**
     * Yang data node builder.
     */
    public static class YangDataNodeBuilder {
        private static final String STRING_YANG_TYPE = "string";

        private String module;
        private String namespace;
        private String nodeName;
        private YangDataNode parent;
        private Set<YangDataNode> dataNodes;
        private YangModelSet modelSet;

        public YangDataNodeBuilder() {
        }

        public YangDataNodeBuilder(final String namespace, final String module) {
            this.namespace = namespace;
            this.module = module;
        }

        public YangDataNodeBuilder(final YangDataNode yangDataNode, final String nodeName) {
            this.namespace = yangDataNode.getNamespace();
            this.module = yangDataNode.getModule();
            this.nodeName = nodeName;
        }

        public YangDataNodeBuilder(final String namespace, final String module, final String nodeName) {
            this.namespace = namespace;
            this.module = module;
            this.nodeName = nodeName;
        }

        public YangDataNodeBuilder newInstance(final String nodeName) {
            return new YangDataNodeBuilder(namespace, module, nodeName);
        }

        public YangDataNodeBuilder newInstance() {
            return new YangDataNodeBuilder(namespace, module);
        }

        public YangDataNodeBuilder module(final String module) {
            this.module = module;
            return this;
        }

        public YangDataNodeBuilder namespace(final String namespace) {
            this.namespace = namespace;
            return this;
        }

        public YangDataNodeBuilder nodeName(final String nodeName) {
            this.nodeName = nodeName;
            return this;
        }

        public YangDataNodeBuilder parent(final YangDataNode parent) {
            this.parent = parent;
            return this;
        }

        public YangDataNodeBuilder dataNodes(final Set<YangDataNode> dataNodes) {
            this.dataNodes = dataNodes; //NOSONAR
            return this;
        }

        public YangDataNodeBuilder modelSet(final YangModelSet modelSet) {
            this.modelSet = modelSet;
            return this;
        }

        public YangDataContainer container() {
            modelSet(new YangModelSet());
            final YangDataContainer yangDataContainer = new YangDataContainer(this);
            yangDataContainer.getModelSet().setParent(yangDataContainer);
            return yangDataContainer;
        }

        public YangDataContainer container(final YangDataNode yangDataNode) {
            if (!(yangDataNode instanceof YangDataContainer)) {
                throw new IllegalArgumentException("");
            }
            copy(yangDataNode);
            final YangDataContainer yangDataContainer = new YangDataContainer(this);
            yangDataContainer.getModelSet().setParent(yangDataContainer);
            return yangDataContainer;
        }

        public YangDataList list() {
            modelSet(new YangModelSet());
            final YangDataList yangDataList = new YangDataList(this);
            yangDataList.getModelSet().setParent(yangDataList);
            return yangDataList;
        }

        public YangDataList list(final YangDataNode yangDataNode) {
            if (!(yangDataNode instanceof YangDataList)) {
                throw new IllegalArgumentException("");
            }
            copy(yangDataNode);
            final YangDataList yangDataList = new YangDataList(this);
            yangDataList.setKeyValues(((YangDataList) yangDataNode).getKeyValues());
            yangDataList.getModelSet().setParent(yangDataList);
            return yangDataList;
        }

        public YangDataLeaf leaf() {
            final YangDataLeaf yangDataLeaf = new YangDataLeaf(this);
            yangDataLeaf.setType(STRING_YANG_TYPE);
            return yangDataLeaf;
        }

        public YangDataLeaf leaf(final String value) {
            final YangDataLeaf yangDataLeaf = new YangDataLeaf(this);
            yangDataLeaf.setType(STRING_YANG_TYPE);
            yangDataLeaf.setValue(value);
            return yangDataLeaf;
        }

        public YangDataLeaf leaf(final String value, final boolean key) {
            final YangDataLeaf yangDataLeaf = new YangDataLeaf(this);
            yangDataLeaf.setType(STRING_YANG_TYPE);
            yangDataLeaf.setValue(value);
            yangDataLeaf.setKey(key);
            return yangDataLeaf;
        }

        public YangDataLeaf leaf(final YangDataNode yangDataNode) {
            if (!(yangDataNode instanceof YangDataLeaf)) {
                throw new IllegalArgumentException("");
            }
            copy(yangDataNode);
            final YangDataLeaf yangDataLeaf = new YangDataLeaf(this);
            yangDataLeaf.setType(((YangDataLeaf) yangDataNode).getType());
            yangDataLeaf.setPath(((YangDataLeaf) yangDataNode).getPath());
            yangDataLeaf.setKey(((YangDataLeaf) yangDataNode).isKey());
            yangDataLeaf.setDefaultValue(((YangDataLeaf) yangDataNode).getDefaultValue());
            yangDataLeaf.setValue(((YangDataLeaf) yangDataNode).getValue());
            return yangDataLeaf;
        }

        public YangDataLeafList leafList() {
            final YangDataLeafList yangDataLeafList = new YangDataLeafList(this);
            yangDataLeafList.setType(STRING_YANG_TYPE);
            return yangDataLeafList;
        }

        public YangDataLeafList leafList(final String... items) {
            final YangDataLeafList yangDataLeafList = new YangDataLeafList(this);
            yangDataLeafList.setType(STRING_YANG_TYPE);
            yangDataLeafList.setItems(Arrays.asList(items));
            return yangDataLeafList;
        }

        public YangDataLeafList leafList(final List<String> items) {
            final YangDataLeafList yangDataLeafList = new YangDataLeafList(this);
            yangDataLeafList.setType(STRING_YANG_TYPE);
            yangDataLeafList.setItems(items);
            return yangDataLeafList;
        }

        public YangDataLeafList leafList(final YangDataNode yangDataNode) {
            if (!(yangDataNode instanceof YangDataLeafList)) {
                throw new IllegalArgumentException("");
            }
            copy(yangDataNode);
            final YangDataLeafList yangDataLeafList = new YangDataLeafList(this);
            yangDataLeafList.setType(((YangDataLeafList) yangDataNode).getType());
            yangDataLeafList.setItems(((YangDataLeafList) yangDataNode).getItems());
            return yangDataLeafList;
        }

        public YangDataSet dataSet() {
            return new YangDataSet();
        }

        public YangDataSet dataSet(final YangDataNode... dataNodes) {
            final YangDataSet yangDataSet = new YangDataSet();
            for (final YangDataNode yangDataNode : dataNodes) {
                yangDataSet.addDataNode(yangDataNode);
            }
            return yangDataSet;
        }

        public YangDataRoot root() {
            return new YangDataRoot();
        }

        public YangDataRoot root(final YangDataNode... dataNodes) {
            final YangDataRoot yangDataRoot = new YangDataRoot();
            for (final YangDataNode yangDataNode : dataNodes) {
                yangDataRoot.addDataNode(yangDataNode);
            }
            return yangDataRoot;
        }

        public YangDataNode clone(final YangDataNode yangDataNode) throws CloneNotSupportedException {
            if (yangDataNode instanceof YangDataContainer) {
                return container(yangDataNode);
            } else if (yangDataNode instanceof YangDataList) {
                return list(yangDataNode);
            } else if (yangDataNode instanceof YangDataLeaf) {
                return leaf(yangDataNode);
            } else if (yangDataNode instanceof YangDataLeafList) {
                return leafList(yangDataNode);
            } else if (yangDataNode instanceof YangDataSet) {
                return new YangDataSet();
            } else if (yangDataNode instanceof YangModelSet) {
                final YangModelSet yangModelSet = new YangModelSet();
                yangModelSet.setParent(yangDataNode.getParent());
                return yangModelSet;
            }

            throw new CloneNotSupportedException("Cannot clone class " + yangDataNode.getClass());
        }

        private void copy(final YangDataNode yangDataNode) {
            this.module(yangDataNode.getModule()).namespace(yangDataNode.getNamespace()).nodeName(yangDataNode.getNodeName())
                    .parent(yangDataNode.getParent()).modelSet(((AbstractYangDataNode) yangDataNode).getModelSet())
                    .dataNodes(new LinkedHashSet<>(yangDataNode.getDataNodes()));
        }
    }
}
