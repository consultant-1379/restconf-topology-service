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

package com.ericsson.oss.services.overload.protection.service.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Represents a single request to reserve capacity on the server
 */
public class CapacityRequest implements Serializable {

    /**
     * Unique identifier for the request
     */
    private String id;

    /**
     * Application that requested the resource reservation
     */
    private String application;

    /**
     * Use case that nrequested the capacity reservation
     */
    private String useCase;

    /**
     * Points required to be reserved
     */
    private Integer points;

    /**
     * The time when the resource reservation was acquired.
     */
    private Date acquiredTime;

    /**
     * Number of times the release of this request was rescheduled due to a failure on release
     */
    private Integer numberRetries = 0;

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getUseCase() {
        return useCase;
    }

    public void setUseCase(String useCase) {
        this.useCase = useCase;
    }

    public Integer getPoints() {
        return points;
    }

    public void setPoints(Integer points) {
        this.points = points;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getAcquiredTime() {
        return acquiredTime;
    }

    public void setAcquiredTime(Date acquiredTime) {
        this.acquiredTime = acquiredTime;
    }

    public Integer getNumberRetries() {
        return numberRetries;
    }

    public void setNumberRetries(Integer numberRetries) {
        this.numberRetries = numberRetries;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)  {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        CapacityRequest that = (CapacityRequest) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }
        if (application != null ? !application.equals(that.application) : that.application != null) {
            return false;
        }
        if (useCase != null ? !useCase.equals(that.useCase) : that.useCase != null) {
            return false;
        }
        if (points != null ? !points.equals(that.points) : that.points != null) {
            return false;
        }
        if (acquiredTime != null ? !acquiredTime.equals(that.acquiredTime) : that.acquiredTime != null) {
            return false;
        }

        return numberRetries != null ? numberRetries.equals(that.numberRetries) : that.numberRetries == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (application != null ? application.hashCode() : 0);
        result = 31 * result + (useCase != null ? useCase.hashCode() : 0);
        result = 31 * result + (points != null ? points.hashCode() : 0);
        result = 31 * result + (acquiredTime != null ? acquiredTime.hashCode() : 0);
        result = 31 * result + (numberRetries != null ? numberRetries.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "CapacityRequest{" +
                "id='" + id + '\'' +
                ", application='" + application + '\'' +
                ", useCase='" + useCase + '\'' +
                ", points=" + points +
                ", acquiredTime=" + acquiredTime +
                ", numberRetries=" + numberRetries +
                '}';
    }
}
