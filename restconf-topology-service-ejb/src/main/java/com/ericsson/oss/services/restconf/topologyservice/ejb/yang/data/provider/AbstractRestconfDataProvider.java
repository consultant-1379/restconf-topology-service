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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.DatabaseNotAvailableException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfDataOperationException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfMoThresholdException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.AttributeMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.EnumMap;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.WhenEModelMapping;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangModelSet;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.model.RestconfDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext;
import com.ericsson.oss.services.restconf.topologyservice.ejb.facade.DpsFacade;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.TransportCimNormalizedNodesLocalCache;
import com.ericsson.oss.services.restconf.topologyservice.ejb.utils.ObjectsProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.resolver.YangDataLeafRefValueResolver;

/**
 * Abstract class for basic restconf operations.
 */
public abstract class AbstractRestconfDataProvider implements RestconfDataProvider {
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestconfDataProvider.class);
    private static final String TCIM_NAMESPACE = "OSS_TCIM";

    protected DpsFacade dpsFacade;
    protected YangDeviceModel yangDeviceModel;
    protected TransportCimNormalizedNodesLocalCache transportCimNormalizedNodesLocalCache;
    protected RestconfContext restconfContext;
    protected YangDataLeafRefValueResolver yangDataLeafRefValueResolver;

    private final Class<? extends RestconfDataProvider> yangDataProviderClass;
    private Map<String, String> mappedEModelAttributeMap;
    private Map<String, EnumMap[]> mappedEModelAttributeEnumMap;
    private List<String> cacheEModelAttributesList;
    private final Map<String, String> whenConditionEModelAttributeMap = new HashMap<>();
    private static long moDataCount;
    private static int counter;
    private static final Map<String, Integer> MO_THRESHOLD_MAP = new HashMap<>();

    static {
        MO_THRESHOLD_MAP.put("Network", 1);
        MO_THRESHOLD_MAP.put("Node", 1000);
        MO_THRESHOLD_MAP.put("TerminationPoint", 3000);
        MO_THRESHOLD_MAP.put("Link", 1500);
        MO_THRESHOLD_MAP.put("Interface", 800);
        MO_THRESHOLD_MAP.put("TdmConnections", 1);
        MO_THRESHOLD_MAP.put("XpicPair", 8000);
        MO_THRESHOLD_MAP.put("MimoGroup", 8000);
        MO_THRESHOLD_MAP.put("RadioLinkProtectionGroup", 8000);
    }

    protected AbstractRestconfDataProvider(final Class<? extends RestconfDataProvider> yangDataProviderClass) {
        this.yangDataProviderClass = yangDataProviderClass;
        initializeMappedEModelAttributeMap();
    }

    /**
     * Set objects needed for yang data processing.
     *
     * @param objectsProvider {@code ObjectsProvider} object
     * @throws RestconfException when something fails.
     */
    public void setObjectsProvider(final ObjectsProvider objectsProvider) throws RestconfException {
        this.dpsFacade = objectsProvider.getDpsFacade();
        this.yangDeviceModel = objectsProvider.getRestconfYangModulesParser().getYangDeviceModel();
        this.transportCimNormalizedNodesLocalCache = objectsProvider.getTransportCimNormalizedNodesLocalCache();
        this.restconfContext = objectsProvider.getRestconfContext();
        this.yangDataLeafRefValueResolver = objectsProvider.getYangDataLeafRefValueResolver();
    }

    @Override
    public void create(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        throw new RestconfDataOperationException("Unsupported create operation on " + yangDataNode.getModule() + ":" + yangDataNode.getNodeName());
    }

    @Override
    public void update(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        throw new RestconfDataOperationException("Unsupported update operation on " + yangDataNode.getModule() + ":" + yangDataNode.getNodeName());
    }

    @Override
    public void delete(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        throw new RestconfDataOperationException("Unsupported delete operation on " + yangDataNode.getModule() + ":" + yangDataNode.getNodeName());
    }

    public static long getMoDataCount() {
        return moDataCount;
    }

    public static void setMoDataCount(long moDataCount) {
        if (counter == 0) {
            AbstractRestconfDataProvider.moDataCount = moDataCount;
        } else {
            AbstractRestconfDataProvider.moDataCount += moDataCount;
        }
    }

    public static int getCounter() {
        return counter++;
    }

    public static void setCounter(int counter) {
        AbstractRestconfDataProvider.counter = counter;
    }

    @Override
    public void read(final YangDataNode yangDataNode) throws RestconfDataOperationException, DatabaseNotAvailableException, RestconfMoThresholdException {
        final YangDataNode parentNode = yangDataNode.getParent();
        if (parentNode instanceof YangDataRoot) {
            copyAndPopulateDataSet(yangDataNode);
        } else if (parentNode instanceof YangDataContainer) {
            final YangDataContainer yangDataContainer = (YangDataContainer) parentNode;
            final String parentFdn = yangDataContainer.getFdn();
            getAndTraverseManagedObjects(yangDataNode, parentFdn);
        } else if (parentNode instanceof YangDataSet) {
            final YangDataSet yangDataContainer = (YangDataSet) parentNode;
            final String parentFdn = yangDataContainer.getFdn();
            getAndTraverseManagedObjects(yangDataNode, parentFdn);
        } else if (parentNode instanceof YangDataList) {
            for (final YangDataNode listDataNode : parentNode.getDataNodes()) {
                if (listDataNode instanceof YangDataSet) {
                    final String parentFdn = ((YangDataSet) listDataNode).getFdn();
                    getAndTraverseManagedObjects(yangDataNode, parentFdn);
                }
            }
        }
    }

    @SuppressWarnings("java:S3776")
    protected void copyAndPopulateDataSet(final YangDataNode yangDataNode, final String fdn, final Map<String, Object> attributeMap,
                                          final WhenMappingCondition matchedWhenCondition, final Map<String, Object> childAttributeMap)
            throws RestconfDataOperationException {
        final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
        final YangDataSet yangDataSet = new YangDataSet();
        yangDataSet.setFdn(fdn);
        if (yangDataNode instanceof YangDataList) {
            yangDataNode.addDataNode(yangDataSet);
        }
        for (final YangDataNode childNode : yangModelSet.getDataNodes()) {
            try {
                final YangDataNode yangDataSetChild = new YangDataNodeBuilder().clone(childNode);
                if (yangDataNode instanceof YangDataContainer) {
                    yangDataNode.addDataNode(yangDataSetChild);
                } else if (yangDataNode instanceof YangDataList) {
                    yangDataSet.addDataNode(yangDataSetChild);
                }
                Object value = null;
                String attribute = getMappedEModelAttribute(yangDataSetChild.getNodeName());
                if (attribute == null || attribute.isEmpty()) {
                    if (matchedWhenCondition != null && childAttributeMap != null) {
                        attribute = getWhenMappedEModelAttribute(matchedWhenCondition, yangDataSetChild.getNodeName());
                        if (attribute != null && !attribute.isEmpty()) {
                            value = childAttributeMap.get(attribute);
                        }
                    }
                } else {
                    value = attributeMap.get(attribute);
                }
                if (yangDataSetChild instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataSetChild;
                    if (yangDataLeaf.getType().equals("leafref")) {
                        yangDataLeafRefValueResolver.resolveLeafRefXPathAndGetValue((YangDataLeaf) yangDataSetChild);
                    } else {
                        if (value != null) {
                            if (yangDataLeaf.getType().equals("enumeration") || yangDataLeaf.getType().equals("identityref")) {
                                final Object yangEnumVal = getEnumMappedYangVal(attribute, value);
                                ((YangDataLeaf) yangDataSetChild).setValue(String.valueOf(yangEnumVal == null ? "" : yangEnumVal));
                            } else {
                                ((YangDataLeaf) yangDataSetChild).setValue(String.valueOf(value));
                            }
                        }
                    }
                } else if (yangDataSetChild instanceof YangDataLeafList && value != null) {
                    if (value instanceof String) {
                        if (((String) value).length() > 0) {
                            ((YangDataLeafList) yangDataSetChild).setItems(Arrays.asList(value.toString()));
                        }
                    } else {
                        ((YangDataLeafList) yangDataSetChild).setItems((List<String>) value);
                    }
                }

            } catch (final Exception exception) {
                throw new RestconfDataOperationException(exception);
            }
        }
    }

    @SuppressWarnings("java:S3776")
    protected void copyAndPopulateDataSet(final YangDataNode yangDataNode, final Map<String, Object> attributeMap)
            throws RestconfDataOperationException {
        final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
        final YangDataSet yangDataSet = new YangDataSet();
        if (yangDataNode instanceof YangDataList) {
            yangDataNode.addDataNode(yangDataSet);
        }
        for (final YangDataNode childNode : yangModelSet.getDataNodes()) {
            try {
                final YangDataNode yangDataSetChild = new YangDataNodeBuilder().clone(childNode);
                if (yangDataNode instanceof YangDataContainer) {
                    yangDataNode.addDataNode(yangDataSetChild);
                } else if (yangDataNode instanceof YangDataList) {
                    yangDataSet.addDataNode(yangDataSetChild);
                }
                final Object value = attributeMap.get(yangDataSetChild.getNodeName());
                if (yangDataSetChild instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataSetChild;
                    if (yangDataLeaf.getType().equals("leafref")) {
                        yangDataLeafRefValueResolver.resolveLeafRefXPathAndGetValue((YangDataLeaf) yangDataSetChild);
                    } else {
                        if (value != null) {
                            ((YangDataLeaf) yangDataSetChild).setValue((String) value);
                        }
                    }
                } else if (yangDataSetChild instanceof YangDataLeafList && value != null) {
                    if (value instanceof String) {
                        // TODO: unit test purpose
                    } else {
                        ((YangDataLeafList) yangDataSetChild).setItems((List<String>) value);
                    }
                }
            } catch (final Exception exception) {
                throw new RestconfDataOperationException(exception);
            }
        }
    }

    protected void copyAndPopulateDataSet(final YangDataNode yangDataNode) throws RestconfDataOperationException {
        final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
        final YangDataSet yangDataSet = new YangDataSet();
        if (yangDataNode instanceof YangDataList) {
            yangDataNode.addDataNode(yangDataSet);
        }
        for (final YangDataNode childNode : yangModelSet.getDataNodes()) {
            try {
                final YangDataNode yangDataSetChild = new YangDataNodeBuilder().clone(childNode);
                if (yangDataNode instanceof YangDataContainer) {
                    yangDataNode.addDataNode(yangDataSetChild);
                } else {
                    yangDataSet.addDataNode(yangDataSetChild);
                }
                if (yangDataSetChild instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) yangDataSetChild;
                    if (yangDataLeaf.getType().equals("leafref")) {
                        yangDataLeafRefValueResolver.resolveLeafRefXPathAndGetValue((YangDataLeaf) yangDataSetChild);
                    }
                }
            } catch (final Exception exception) {
                throw new RestconfDataOperationException(exception);
            }
        }
    }

    protected void generateAndSetDataLeafValue(final YangDataNode yangDataNode) {
        for (final YangDataNode childNode : yangDataNode.getDataNodes()) {
            if (childNode instanceof YangDataSet) {
                final String nodeName = getNodeName(((YangDataSet) childNode).getFdn());
                for (final YangDataNode dataSetNode : childNode.getDataNodes()) {
                    if (dataSetNode instanceof YangDataLeaf && dataSetNode.getNodeName().equals("name")) {
                        final YangDataLeaf yangDataLeaf = (YangDataLeaf) dataSetNode;
                        yangDataLeaf.setValue(nodeName + ":" + yangDataLeaf.getValue());
                    }
                }
            }
        }
    }

    @SuppressWarnings("java:S3776")
    private void getAndTraverseManagedObjects(final YangDataNode yangDataNode, final String parentFdn) throws RestconfDataOperationException, RestconfMoThresholdException {
        final long startTime = System.currentTimeMillis();
        try {
            final Iterator<ManagedObject> managedObjectIterator = getManagedObjectIterator(yangDataNode, parentFdn);
            if (managedObjectIterator.hasNext()) {
                final List<WhenMappingCondition> whenMappings = getWhenMapping();
                final List<String> attributeNames = getMappedEModelAttributes();
                while (managedObjectIterator.hasNext()) {
                    final ManagedObject managedObject = managedObjectIterator.next();
                    final Map<String, Object> moAttributeMap = managedObject.getAttributes(attributeNames);
                    populateCacheAttributes(yangDataNode, moAttributeMap);
                    final String fdn = managedObject.getFdn();
                    LOGGER.debug("Processing FDN: {} with attributes: {}", fdn, moAttributeMap);
                    final Map<String, Object> moChildAttributeMap = new HashMap<>();
                    WhenMappingCondition matchedCondition = null;
                    for (final WhenMappingCondition condition : whenMappings) {
                        final Object value = moAttributeMap.get(condition.attribute);
                        LOGGER.debug("Checking condition attribute: {}, value: {}; mo value: {}", condition.attribute, condition.value, value);
                        if (value != null && value.equals(condition.value)) {
                            matchedCondition = condition;
                            moChildAttributeMap.putAll(getChildManagedObjectAttributes(managedObject.getFdn(), condition.mappedEModel.eModel()));
                            LOGGER.debug("Condition matched for child type: {} with attributes: {}", condition.mappedEModel.eModel(),
                                    moChildAttributeMap);
                            break;
                        }
                    }
                    restconfContext.getRestconfInstrumentationBuilder().incrementTotalMOsRead();
                    // TODO: overload protection on MOs
                    if (fdn.contains("Node=")) {
                        restconfContext.getRestconfInstrumentationBuilder().incrementTotalNodes();
                        final String nodeName = getNodeName(fdn);
                        if (transportCimNormalizedNodesLocalCache.containsNode(nodeName)) {
                            copyAndPopulateDataSet(yangDataNode, fdn, moAttributeMap, matchedCondition, moChildAttributeMap);
                        } else {
                            final String normalizedState = (String) dpsFacade.getValueFromFdn("Network=1,Node=" + nodeName, "normalization-state");
                            if ("NORMALIZED".equals(normalizedState)) {
                                transportCimNormalizedNodesLocalCache.addNormalizedNode(nodeName);
                                copyAndPopulateDataSet(yangDataNode, fdn, moAttributeMap, matchedCondition, moChildAttributeMap);
                            } else {
                                restconfContext.getRestconfInstrumentationBuilder().incrementTotalSkippedNodes();
                                LOGGER.warn("Skipping yang data populate for [{}] FDN as it is not normalized!", fdn);
                            }
                        }
                    } else {
                        copyAndPopulateDataSet(yangDataNode, fdn, moAttributeMap, matchedCondition, moChildAttributeMap);
                    }
                }
            } else {
                LOGGER.debug("No children found under FDN {}", parentFdn);
            }
        } catch (final RestconfMoThresholdException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new RestconfDataOperationException(exception);
        } finally {
            restconfContext.getRestconfInstrumentationBuilder().calcTotalDpsTxTime(startTime);
        }
    }

    protected String getNodeName(final String fdn) {
        if (fdn == null || fdn.isEmpty()) {
            return "";
        }
        final String nodeName = fdn.split("Node=")[1];
        return nodeName.split(",")[0];
    }

    protected String getFdnValue(final String fdn) {
        if (fdn == null || fdn.isEmpty()) {
            return "";
        }
        final int lastEqualsIndex = fdn.lastIndexOf('=');
        return fdn.substring(lastEqualsIndex + 1);
    }

    protected String getInterfaceName(final String interfaceFdn) {
        final String[] tokens = interfaceFdn.split("Interface=");
        if (tokens[1].contains(",")) {
            final int commaIndex = tokens[1].indexOf(',');
            return tokens[1].substring(0, commaIndex);
        }
        return tokens[1];
    }

    protected void extractInterfaceDetailsAndSet(final YangDataLeafList yangDataLeafList) {
        if (!yangDataLeafList.getItems().isEmpty()) {
            final List<String> modifiedItems = new ArrayList<>();
            for (final String itemValue : yangDataLeafList.getItems()) {
                if (itemValue.startsWith("Network=")) {
                    modifiedItems.add(getNodeName(itemValue) + ":" + getInterfaceName(itemValue));
                } else {
                    modifiedItems.add(itemValue);
                }
            }
            yangDataLeafList.setItems(modifiedItems);
        }
    }

    @SuppressWarnings("java:S3776")
    private Iterator<ManagedObject> getManagedObjectIterator(final YangDataNode yangDataNode, final String baseFdn) throws RestconfDataOperationException, RestconfMoThresholdException {
        final String moType = getMappedEModel();
        if (moType == null) {
            return Collections.emptyIterator();
        }
        int moThresholdCount = 0;
        if (MO_THRESHOLD_MAP.containsKey(moType)) {
            moThresholdCount = MO_THRESHOLD_MAP.get(moType);
        } else {
            throw new RestconfDataOperationException("");
        }
        final Map<String, String> attributes = new HashMap<>();
        String modifiedBaseFdn = baseFdn;
        if (yangDataNode instanceof YangDataList) {
            final YangDataList yangDataList = (YangDataList) yangDataNode;
            final String[] keyNames = getKeyNames(yangDataList.getModelSet());
            final List<String> keyValues = yangDataList.getKeyValues();
            for (int index = 0; index < keyValues.size() && index < keyNames.length; index++) {
                final String value = yangDataList.getKeyValues().get(index);
                if (value != null) {
                    if (value.contains(":")) {
                        final String[] tokens = value.split(":");
                        attributes.put(getMappedEModelAttribute(keyNames[index]), tokens[1]);
                        if (modifiedBaseFdn == null) {
                            modifiedBaseFdn = "Network=1,Node=" + tokens[0];
                        }
                    } else {
                        attributes.put(getMappedEModelAttribute(keyNames[index]), value);
                    }
                }
            }
        }
        if (moType.equals("TerminationPoint")) {
            if (counter == 0) {
                setMoDataCount(0);
            }
            getCounter();
        } else {
            setCounter(0);
        }
        setMoDataCount(dpsFacade.getMoByTypeCount(TCIM_NAMESPACE, moType, modifiedBaseFdn, attributes));
        if (getMoDataCount() > moThresholdCount) {
            throw new RestconfMoThresholdException("");
        }
        return dpsFacade.getMoByType(TCIM_NAMESPACE, moType, modifiedBaseFdn, attributes);
    }

    private Map<String, Object> getChildManagedObjectAttributes(final String baseFdn, final String mappedEModel) {
        if (mappedEModel == null) {
            return Collections.emptyMap();
        }
        final Iterator<ManagedObject> managedObjectIterator = dpsFacade.getMoByType(TCIM_NAMESPACE, mappedEModel, baseFdn);
        if (managedObjectIterator.hasNext()) {
            final ManagedObject managedObject = managedObjectIterator.next();
            restconfContext.getRestconfInstrumentationBuilder().incrementTotalMOsRead();
            return managedObject.getAllAttributes();
        }
        return Collections.emptyMap();
    }

    private void populateCacheAttributes(final YangDataNode yangDataNode, final Map<String, Object> moAttributes) {
        if (!cacheEModelAttributesList.isEmpty()) {
            for (final String attribute : cacheEModelAttributesList) {
                ((AbstractYangDataNode) yangDataNode).addCacheAttribute(attribute, (String) moAttributes.get(attribute));
            }
        }
    }

    private void initializeMappedEModelAttributeMap() {
        mappedEModelAttributeMap = new HashMap<>();
        mappedEModelAttributeEnumMap = new HashMap<>();
        cacheEModelAttributesList = new ArrayList<>();
        final YangDataProvider yangDataProvider = yangDataProviderClass.getAnnotation(YangDataProvider.class);
        final EModelMapping eModelMapping = yangDataProvider.eModelMapping();
        if (eModelMapping != null) {
            for (final AttributeMap map : eModelMapping.mapping()) {
                if (!map.yang().isEmpty()) {
                    mappedEModelAttributeMap.put(map.yang(), map.eModel());
                    mappedEModelAttributeEnumMap.put(map.eModel(), map.enumMapping());
                }
            }
            cacheEModelAttributesList.addAll(Arrays.asList(eModelMapping.cacheAttributes()));
        }
    }

    private String getMappedEModelAttribute(final String yangAttribute) {
        if (mappedEModelAttributeMap.containsKey(yangAttribute)) {
            final String attribute = mappedEModelAttributeMap.get(yangAttribute);
            return attribute != null && !attribute.isEmpty() ? attribute : null;
        }
        return null;
    }

    private Object getEnumMappedYangVal(final String emodelAttribute, final Object emodelVal) {
        if (mappedEModelAttributeEnumMap.containsKey(emodelAttribute)) {
            for (final EnumMap map : mappedEModelAttributeEnumMap.get(emodelAttribute)) {
                if (!map.yang().isEmpty() && map.eModel().equals(emodelVal)) {
                    return map.yang();
                }
            }
        }
        return null;
    }

    private String getMappedEModel() {
        final YangDataProvider yangDataProvider = yangDataProviderClass.getAnnotation(YangDataProvider.class);
        final EModelMapping eModelMapping = yangDataProvider.eModelMapping();
        if (eModelMapping != null) {
            return eModelMapping.eModel();
        }
        return null;
    }

    private String getWhenMappedEModelAttribute(final WhenMappingCondition matchedWhenCondition, final String yangAttribute) {
        if (whenConditionEModelAttributeMap.isEmpty()) {
            final EModelMapping eModelMapping = matchedWhenCondition.mappedEModel;
            if (eModelMapping != null) {
                for (final AttributeMap map : eModelMapping.mapping()) {
                    if (!map.yang().isEmpty()) {
                        whenConditionEModelAttributeMap.put(map.yang(), map.eModel());
                        mappedEModelAttributeEnumMap.put(map.eModel(), map.enumMapping());
                    }
                }
            }
        }
        if (whenConditionEModelAttributeMap.containsKey(yangAttribute)) {
            final String attribute = whenConditionEModelAttributeMap.get(yangAttribute);
            return attribute != null && !attribute.isEmpty() ? attribute : null;
        }
        return null;
    }

    private List<WhenMappingCondition> getWhenMapping() {
        final YangDataProvider yangDataProvider = yangDataProviderClass.getAnnotation(YangDataProvider.class);
        final WhenEModelMapping[] whenEModelMappings = yangDataProvider.whenMapping();
        final List<WhenMappingCondition> whenMappingConditions = new ArrayList<>();
        if (whenEModelMappings != null) {
            for (final WhenEModelMapping whenEModelMapping : whenEModelMappings) {
                final EModelMapping eModelMapping = whenEModelMapping.eModelMapping();
                whenMappingConditions.add(new WhenMappingCondition(whenEModelMapping.attribute(), whenEModelMapping.value(), eModelMapping));
            }
        }
        return whenMappingConditions;
    }

    private String[] getKeyNames(final YangDataNode yangDataNode) {
        final List<String> keyNames = new ArrayList<>();
        if (yangDataNode instanceof YangModelSet) {
            final YangModelSet yangModelSet = (YangModelSet) yangDataNode;
            for (final YangDataNode childDataNode : yangModelSet.getDataNodes()) {
                if (childDataNode instanceof YangDataLeaf) {
                    final YangDataLeaf yangDataLeaf = (YangDataLeaf) childDataNode;
                    if (yangDataLeaf.isKey()) {
                        keyNames.add(yangDataLeaf.getNodeName());
                    }
                }
            }
        }
        return keyNames.toArray(new String[0]);
    }

    private ArrayList<String> getMappedEModelAttributes() {
        final YangDataProvider yangDataProvider = yangDataProviderClass.getAnnotation(YangDataProvider.class);
        final EModelMapping eModelMapping = yangDataProvider.eModelMapping();
        final ArrayList<String> attributeNames = new ArrayList<>();
        if (eModelMapping != null) {
            for (final AttributeMap map : eModelMapping.mapping()) {
                attributeNames.add(map.eModel());
            }
        }
        return attributeNames;
    }

    /**
     * When mapping condition DTO.
     */
    private static class WhenMappingCondition {
        private final String attribute;
        private final String value;
        private final EModelMapping mappedEModel;

        WhenMappingCondition(final String attribute, final String value, final EModelMapping mappedEModel) {
            this.attribute = attribute;
            this.value = value;
            this.mappedEModel = mappedEModel;
        }
    }
}