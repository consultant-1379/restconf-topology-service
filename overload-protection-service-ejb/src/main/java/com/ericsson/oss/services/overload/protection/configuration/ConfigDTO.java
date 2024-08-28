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

package com.ericsson.oss.services.overload.protection.configuration;

import java.io.Serializable;

public class ConfigDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * The total capacity the server can handle
     */
    private Integer capacity;

    /**
     * Time to wait (in miliseconds) until a expireCapacityRequests should be "forced".
     */
    private Integer forceReleaseTimeout;

    /**
     * Number of retries in case of failure in the expiration timer.
     */
    private Integer timerRetryCount;

    /**
     * Time to wait (in miliseconds) before retry the timer.
     */
    private Integer timerRetryDelay;

    /**
     * Enables or Disable the service
     */
    private boolean enabled;

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Integer getForceReleaseTimeout() {
        return forceReleaseTimeout;
    }

    public void setForceReleaseTimeout(Integer forceReleaseTimeout) {
        this.forceReleaseTimeout = forceReleaseTimeout;
    }

    public Integer getTimerRetryCount() {
        return timerRetryCount;
    }

    public void setTimerRetryCount(Integer timerRetryCount) {
        this.timerRetryCount = timerRetryCount;
    }

    public Integer getTimerRetryDelay() {
        return timerRetryDelay;
    }

    public void setTimerRetryDelay(Integer timerRetryDelay) {
        this.timerRetryDelay = timerRetryDelay;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ConfigDTO configDTO = (ConfigDTO) o;

        if (capacity != null ? !capacity.equals(configDTO.capacity) : configDTO.capacity != null) {
            return false;
        }
        if (forceReleaseTimeout != null ? !forceReleaseTimeout.equals(configDTO.forceReleaseTimeout) : configDTO.forceReleaseTimeout != null) {
            return false;
        }
        if (timerRetryCount != null ? !timerRetryCount.equals(configDTO.timerRetryCount) : configDTO.timerRetryCount != null) {
            return false;
        }

        return timerRetryDelay != null ? timerRetryDelay.equals(configDTO.timerRetryDelay) : configDTO.timerRetryDelay == null;
    }

    @Override
    public int hashCode() {
        int result = capacity != null ? capacity.hashCode() : 0;
        result = 31 * result + (forceReleaseTimeout != null ? forceReleaseTimeout.hashCode() : 0);
        result = 31 * result + (timerRetryCount != null ? timerRetryCount.hashCode() : 0);
        result = 31 * result + (timerRetryDelay != null ? timerRetryDelay.hashCode() : 0);
        return result;
    }
}
