/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2018
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/

package com.ericsson.oss.services.overload.protection.load;

import com.ericsson.oss.services.overload.protection.configuration.ConfigurationService;
import com.ericsson.oss.services.overload.protection.exceptions.DuplicatedKeyException;
import com.ericsson.oss.services.overload.protection.exceptions.NotEnoughCapacityException;
import com.ericsson.oss.services.overload.protection.service.api.CapacityRequest;
import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;

import javax.ejb.*;
import javax.inject.Inject;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static javax.ejb.ConcurrencyManagementType.CONTAINER;

/**
 * Singleton Bean responsible to control concurrent use cases running on the service group.
 */
@Singleton
@ConcurrencyManagement(CONTAINER)
@AccessTimeout(value=3, unit = TimeUnit.SECONDS)
public class LoadStorage {

    @Inject
    private Logger logger;

    @Inject
    private ConfigurationService configService;

    private Integer usedCapacity = 0;

    private Map<String, CapacityRequest> inUseMap = new LinkedHashMap<>();

    /**
     * <p>Try to acquire the requested capacity from the server.
     * If sucessfull a unique id will be returned to be used to expireCapacityRequests resources in the future.</p>
     *
     * <p>This method has WRITE lock, which means that each thread will get exclusive access.
     * Any other threads calling this method while the lock is active will wait up to 3 seconds.</p>
     *
     * @throws ConcurrentAccessTimeoutException if the request took more than 3 seconds waiting for an active lock to be released.
     * @param request capacity request details
     * @return unique ID identifying this capacity reservation
     */
    @Lock(LockType.WRITE)
    public String acquire(final CapacityRequest request) {

        if (!hasCapacity(request.getPoints())) {
            throw new NotEnoughCapacityException();
        }

        String id = request.getId();
        if (StringUtils.isEmpty(id)) {
            id = UUID.randomUUID().toString();
            request.setId(id);

        } else if (inUseMap.containsKey(id)) {
            throw new DuplicatedKeyException(id);
        }
        request.setAcquiredTime(Calendar.getInstance().getTime());
        inUseMap.put(id, request);
        usedCapacity += request.getPoints();
        return id;

    }

    /**
     * <p>Try to expireCapacityRequests the capacity request with the given ID.</p>
     *
     * <p>If no capacity reservation exists with the goven ID, returns false.</p>
     *
     * <p>A capacity requests will expire automatically after 5 minutes if not released</p>
     *
     * <p>This method has WRITE lock, which means that each thread will get exclusive access.
     * Any other threads calling this method while the lock is active will wait up to 3 seconds.</p>
     *
     * @throws ConcurrentAccessTimeoutException if the request took more than 3 seconds waiting for an active lock to be released.
     * @param id unique id to be released
     */
    @Lock(LockType.WRITE)
    public void release(final String id) {
        final CapacityRequest request =  inUseMap.remove(id);
        if (request != null) {
            usedCapacity -= request.getPoints();
        }
    }

    /**
     * Gets the capacity available in this server instance
     * @return available capacity value
     */
    @Lock(LockType.READ)
    public Integer getAvailableCapacity() {
        return configService.getConfiguration().getCapacity() - usedCapacity;
    }

    /**
     * Gets the used capacity in this server instance
     * @return used capacity value
     */
    @Lock(LockType.READ)
    public Integer getUsedCapacity() {
        return usedCapacity;
    }

    /**
     * Gets the total capacity supported in this server instance
     * @return total capacity value
     */
    @Lock(LockType.READ)
    public Integer getTotalCapacity() {
        return configService.getConfiguration().getCapacity();
    }

    /**
     * Returns an immutable Map containing all the CapacityRequests currently allocated in this server instance
     * @return a Map have the unique id as key and the capacity request as value
     */
    @Lock(LockType.READ)
    public Map<String, CapacityRequest> getRunningRequests() {
        return ImmutableMap.copyOf(inUseMap);
    }

    private boolean hasCapacity(Integer points) {
        return (points + usedCapacity) < configService.getConfiguration().getCapacity();
    }

}
