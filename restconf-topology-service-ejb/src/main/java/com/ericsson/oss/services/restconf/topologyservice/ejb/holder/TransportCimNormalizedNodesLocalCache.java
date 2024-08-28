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

package com.ericsson.oss.services.restconf.topologyservice.ejb.holder;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCIM normalized nodes local cache storage.
 */
@Singleton
public class TransportCimNormalizedNodesLocalCache {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransportCimNormalizedNodesLocalCache.class);
    private static final Map<String, Long> CACHE = new ConcurrentHashMap<>();

    /**
     * Add normalized node name in the cache.
     *
     * @param nodeName Node name.
     */
    @Lock(LockType.WRITE)
    public void addNormalizedNode(final String nodeName) {
        if (CACHE.containsKey(nodeName)) {
            LOGGER.debug("nodeName: {} is already cached", nodeName);
            return;
        }
        CACHE.put(nodeName, 1L);
    }

    /**
     * Remove node name from the cache.
     *
     * @param nodeName Node name.
     */
    @Lock(LockType.WRITE)
    public void removeNode(final String nodeName) {
        CACHE.remove(nodeName);
    }

    /**
     * Check if node contains in the cache.
     * @param nodeName node name.
     * @return true if node exists, else false.
     */
    @Lock(LockType.READ)
    public boolean containsNode(final String nodeName) {
        return CACHE.containsKey(nodeName);
    }
}
