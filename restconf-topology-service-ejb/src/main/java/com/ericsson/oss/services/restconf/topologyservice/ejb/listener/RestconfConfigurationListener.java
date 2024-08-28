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

package com.ericsson.oss.services.restconf.topologyservice.ejb.listener;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.config.annotation.ConfigurationChangeNotification;
import com.ericsson.oss.itpf.sdk.config.annotation.Configured;

/**
 * Restconf configuration listener.
 */
@ApplicationScoped
public class RestconfConfigurationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfConfigurationListener.class);
    private static final String RECEIVED_NOTIFICATION_VALUE_CHANGED = "Received value changed notification for property: {}, new value: {}";

    @Inject
    @Configured(propertyName = "restconfNbiMaximumParallelGetRequests")
    private Integer restconfNbiMaximumParallelGetRequests;

    @Inject
    @Configured(propertyName = "restconfNbiMaximumParallelUserRequests")
    private Integer restconfNbiMaximumParallelUserRequests;

    @Inject
    @Configured(propertyName = "restconfNbiMaximumThreadPoolSize")
    private Integer restconfNbiMaximumThreadPoolSize;

    @Inject
    @Configured(propertyName = "restconfNbiMaximumNodesHierarchyLevel")
    private Integer restconfNbiMaximumNodesHierarchyLevel;

    @Inject
    @Configured(propertyName = "restconfNbiMaximumMoProcess")
    private Integer restconfNbiMaximumMoProcess;

    public void listenForRestconfNbiMaximumParallelGetRequests(
            @Observes @ConfigurationChangeNotification(propertyName = "restconfNbiMaximumParallelGetRequests") final Integer value) {
        LOGGER.info(RECEIVED_NOTIFICATION_VALUE_CHANGED, "restconfNbiMaximumParallelGetRequests", value);
        restconfNbiMaximumParallelGetRequests = value;
    }

    public void listenForRestconfNbiMaximumParallelUserRequests(
            @Observes @ConfigurationChangeNotification(propertyName = "restconfNbiMaximumParallelUserRequests") final Integer value) {
        LOGGER.info(RECEIVED_NOTIFICATION_VALUE_CHANGED, "restconfNbiMaximumParallelUserRequests", value);
        restconfNbiMaximumParallelUserRequests = value;
    }

    public void listenForRestconfNbiMaximumThreadPoolSize(
            @Observes @ConfigurationChangeNotification(propertyName = "restconfNbiMaximumThreadPoolSize") final Integer value) {
        LOGGER.info(RECEIVED_NOTIFICATION_VALUE_CHANGED, "restconfNbiMaximumThreadPoolSize", value);
        restconfNbiMaximumThreadPoolSize = value;
    }

    public void listenForRestconfNbiMaximumNodesHierarchyLevel(
            @Observes @ConfigurationChangeNotification(propertyName = "restconfNbiMaximumNodesHierarchyLevel") final Integer value) {
        LOGGER.info(RECEIVED_NOTIFICATION_VALUE_CHANGED, "restconfNbiMaximumNodesHierarchyLevel", value);
        restconfNbiMaximumNodesHierarchyLevel = value;
    }

    public void listenForRestconfNbiMaximumMoProcess(
            @Observes @ConfigurationChangeNotification(propertyName = "restconfNbiMaximumMoProcess") final Integer value) {
        LOGGER.info(RECEIVED_NOTIFICATION_VALUE_CHANGED, "restconfNbiMaximumMoProcess", value);
        restconfNbiMaximumMoProcess = value;
    }

    public Integer getRestconfNbiMaximumParallelGetRequests() {
        return restconfNbiMaximumParallelGetRequests;
    }

    public Integer getRestconfNbiMaximumParallelUserRequests() {
        return restconfNbiMaximumParallelUserRequests;
    }

    public Integer getRestconfNbiMaximumThreadPoolSize() {
        return restconfNbiMaximumThreadPoolSize;
    }

    public Integer getRestconfNbiMaximumNodesHierarchyLevel() {
        return restconfNbiMaximumNodesHierarchyLevel;
    }

    public Integer getRestconfNbiMaximumMoProcess() {
        return restconfNbiMaximumMoProcess;
    }
}