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

package com.ericsson.oss.services.restconf.topologyservice.ejb.utils;

import javax.inject.Inject;

import com.ericsson.oss.services.restconf.topologyservice.ejb.context.RestconfContext;
import com.ericsson.oss.services.restconf.topologyservice.ejb.facade.DpsFacade;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.TransportCimNormalizedNodesLocalCache;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.provider.registry.RestconfDataProviderRegistry;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.resolver.YangDataLeafRefValueResolver;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser.RestconfYangModulesParser;

/**
 * Provide useful objects to classes that are initialized with new keyword.
 */
public class ObjectsProvider {

    @Inject
    private DpsFacade dpsFacade;

    @Inject
    private RestconfYangModulesParser restconfYangModulesParser;

    @Inject
    private RestconfDataProviderRegistry restconfDataProviderRegistry;

    @Inject
    private TransportCimNormalizedNodesLocalCache transportCimNormalizedNodesLocalCache;

    @Inject
    private RestconfContext restconfContext;

    @Inject
    private YangDataLeafRefValueResolver yangDataLeafRefValueResolver;

    public DpsFacade getDpsFacade() {
        return dpsFacade;
    }

    public RestconfYangModulesParser getRestconfYangModulesParser() {
        return restconfYangModulesParser;
    }

    public RestconfDataProviderRegistry getRestconfDataProviderRegistry() {
        return restconfDataProviderRegistry;
    }

    public TransportCimNormalizedNodesLocalCache getTransportCimNormalizedNodesLocalCache() {
        return transportCimNormalizedNodesLocalCache;
    }

    public RestconfContext getRestconfContext() {
        return restconfContext;
    }

    public YangDataLeafRefValueResolver getYangDataLeafRefValueResolver() {
        return yangDataLeafRefValueResolver;
    }
}
