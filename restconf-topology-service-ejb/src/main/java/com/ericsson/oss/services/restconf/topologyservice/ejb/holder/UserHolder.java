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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Singleton;

/**
 * Restconf user holder.
 */
@Singleton
public class UserHolder {
    private static final Map<String, List<String>> CACHE = new ConcurrentHashMap<>();

    /**
     * Add user capacity id.
     *
     * @param user user.
     * @param userCapacityId user capacity id.
     */
    @Lock(LockType.WRITE)
    public void addUser(final String user, final String userCapacityId) {
        if (CACHE.containsKey(user)) {
            CACHE.get(user).add(userCapacityId);
        }
        final List<String> idList = new ArrayList<>();
        idList.add(userCapacityId);
        CACHE.put(user, idList);
    }

    /**
     * Remove user capacity id.
     *
     * @param user user.
     * @param userCapacityId user capacity id.
     */
    @Lock(LockType.WRITE)
    public void removeUser(final String user, final String userCapacityId) {
        if (CACHE.containsKey(user)) {
            if (CACHE.get(user).isEmpty()) {
                CACHE.remove(user);
            } else {
                CACHE.get(user).remove(userCapacityId);
            }
        }
    }

    /**
     * Get user index.
     * @param user user.
     * @return user index in cache.
     */
    @Lock(LockType.READ)
    public long getUserIndex(final String user) {
        long result = 0;
        for (final String item : CACHE.keySet()) {
            if (item.equals(user)) {
                return result;
            }
            result++;
        }
        return -1L;
    }
}
