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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.registry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.Startup;
import javax.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.services.restconf.topologyservice.api.yang.annotation.YangDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.model.RestconfDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.utils.LoggingUtility;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.DefaultDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.FiltersDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.ModuleSchemaResourceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.ModulesStateDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.RestconfCapabilitiesDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.RestconfStateDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.basic.RestconfSubscriptionsDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.EthernetTransportServiceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.InterfacesStateDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.L2vpnServiceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.L3vpnServiceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.link.LinkDestinationDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.link.LinkSourceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.NetworkStateDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.NetworksDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.TrafficEngineeringDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.rfc.TransportClientServiceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.HrlbGroupDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.HrlbGroupsDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.HrlbMemberDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.InterfaceDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.InterfacesDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.LinkDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.LinkGroupProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.LldpDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.MimoGroupDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.MimoGroupsDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.NetworkDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.NodeDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.PendingTerminationPointDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.RadioLinkProtectionGroupDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.RadioLinkProtectionGroupsDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.TdmConnectionsDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.TerminationPointDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.XpicPairDataProvider;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.tcim.XpicPairsDataProvider;

/**
 * Restconf data provider classes registry.
 */
@Startup
@ApplicationScoped
public class RestconfDataProviderRegistry {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfDataProviderRegistry.class);

    // Initial capacity to be updated for new data providers registered.
    private static final Set<Class<? extends RestconfDataProvider>> RESTCONF_DATA_PROVIDER_CLASSES = new LinkedHashSet<>(40);

    static {
        RESTCONF_DATA_PROVIDER_CLASSES.add(EthernetTransportServiceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(FiltersDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(HrlbGroupDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(HrlbGroupsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(HrlbMemberDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(InterfaceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(InterfacesDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(InterfacesStateDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(L2vpnServiceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(L3vpnServiceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(LinkDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(LinkGroupProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(LldpDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(MimoGroupDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(MimoGroupsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(NetworkDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(NetworksDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(NetworkStateDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(NodeDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(PendingTerminationPointDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(RadioLinkProtectionGroupDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(RadioLinkProtectionGroupsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(RestconfStateDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(RestconfSubscriptionsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(TdmConnectionsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(TerminationPointDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(TrafficEngineeringDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(TransportClientServiceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(XpicPairDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(XpicPairsDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(ModuleSchemaResourceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(ModulesStateDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(RestconfCapabilitiesDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(LinkSourceDataProvider.class);
        RESTCONF_DATA_PROVIDER_CLASSES.add(LinkDestinationDataProvider.class);

    }

    // Initial capacity to be updated for new data providers registered.
    private final Map<String, List<Class<? extends RestconfDataProvider>>> dataProviderRegistry = new HashMap<>(35);

    @PostConstruct
    private void initialize() {
        try {
            registerDataProviders();
        } catch (final Exception exception) {
            LoggingUtility.logException(LOGGER, "register data providers", exception);
        }
    }

    /**
     * Get data providers for {@code YangDataNode} object.
     *
     * @param yangDataNode {@code YangDataNode} object.
     * @return List of {@code RestconfDataProvider} classes.
     */
    public List<Class<? extends RestconfDataProvider>> getDataProviders(final YangDataNode yangDataNode) {
        final List<Class<? extends RestconfDataProvider>> classes = dataProviderRegistry.get(
                yangDataNode.getModule() + ":" + yangDataNode.getNodeName());
        return classes == null ? getDefaultDataProvider(yangDataNode.getModule()) : classes;
    }

    private List<Class<? extends RestconfDataProvider>> getDefaultDataProvider(final String yangModule) {
        switch (yangModule) {
            case "ietf-network":
            case "ietf-network-topology":
            case "ietf-interfaces":
            case "ietf-microwave-radio-link":
                return Collections.singletonList(DefaultDataProvider.class);
            default:
                return Collections.emptyList();
        }
    }

    private void registerDataProviders() {
        for (final Class<? extends RestconfDataProvider> modelClazz : RESTCONF_DATA_PROVIDER_CLASSES) {
            final YangDataProvider annotation = modelClazz.getAnnotation(YangDataProvider.class);
            if (annotation != null) {
                if (dataProviderRegistry.containsKey(annotation.yangModel())) {
                    final List<Class<? extends RestconfDataProvider>> operations = dataProviderRegistry.get(annotation.yangModel());
                    operations.add(modelClazz);
                } else {
                    final List<Class<? extends RestconfDataProvider>> operations = new ArrayList<>();
                    operations.add(modelClazz);
                    dataProviderRegistry.put(annotation.yangModel(), operations);
                }
            }
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Total/Registered data providers: {}/{}", RESTCONF_DATA_PROVIDER_CLASSES.size(), dataProviderRegistry.size());
        }
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("Registered data providers {}", dataProviderRegistry);
        }
    }
}
