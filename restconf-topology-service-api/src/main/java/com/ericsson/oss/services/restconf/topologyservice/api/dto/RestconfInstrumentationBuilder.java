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

package com.ericsson.oss.services.restconf.topologyservice.api.dto;

import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;

/**
 * Restconf request instrumentation builder.
 */
public class RestconfInstrumentationBuilder {
    private static final String UNKNOWN = "UNKNOWN";
    private long userIndex;
    private String reqMethod;
    private String reqType;
    private String reqMediaType;
    private String reqAcceptType;
    private long totalReqDataSize;
    private String rootModuleName;
    private String yangRequestUri;
    private long totalReqQueryParam;
    private String resStatus;
    private long totalResDataSize;
    private long totalNodes;
    private long totalSkippedNodes;
    private long totalMOsRead;
    private long moPerSecRead;
    private long totalMOsCreated;
    private long moPerSecCreate;
    private long totalMOsUpdated;
    private long moPerSecUpdate;
    private long totalMOsDeleted;
    private long moPerSecDelete;
    private long totalDpsTxTime;
    private long totalResTime;

    public RestconfInstrumentationBuilder userIndex(final long userIndex) {
        this.userIndex = userIndex;
        return this;
    }

    public RestconfInstrumentationBuilder reqMethod(final String reqMethod) {
        this.reqMethod = reqMethod;
        return this;
    }

    public RestconfInstrumentationBuilder reqType(final String reqType) {
        this.reqType = reqType;
        return this;
    }

    public RestconfInstrumentationBuilder reqMediaType(final String reqMediaType) {
        this.reqMediaType = reqMediaType;
        return this;
    }

    public RestconfInstrumentationBuilder reqAcceptType(final String reqAcceptType) {
        this.reqAcceptType = reqAcceptType;
        return this;
    }

    public RestconfInstrumentationBuilder totalReqDataSize(final long totalReqDataSize) {
        this.totalReqDataSize = totalReqDataSize;
        return this;
    }

    public RestconfInstrumentationBuilder rootModuleName(final String rootModuleName) {
        this.rootModuleName = rootModuleName;
        if ("ietf-yang-library:modules-state".equals(rootModuleName)) {
            this.reqType = "MODULE";
        } else if ("ietf-restconf-monitoring:restconf-state".equals(rootModuleName)) {
            this.reqType = "HELLO";
        }
        return this;
    }

    public RestconfInstrumentationBuilder yangRequestUri(final String yangRequestUri) {
        this.yangRequestUri = yangRequestUri;
        return this;
    }

    public RestconfInstrumentationBuilder totalReqQueryParam(final long totalReqQueryParam) {
        this.totalReqQueryParam = totalReqQueryParam;
        return this;
    }

    public RestconfInstrumentationBuilder resStatus(final String resStatus) {
        this.resStatus = resStatus;
        return this;
    }

    public RestconfInstrumentationBuilder resStatus(final RestconfResponseTag responseTag) {
        this.resStatus = responseTag.getTagName().toLowerCase(Locale.ROOT).replaceAll("-", "_");
        return this;
    }

    public RestconfInstrumentationBuilder totalResDataSize(final long totalResDataSize) {
        this.totalResDataSize = totalResDataSize;
        return this;
    }

    public RestconfInstrumentationBuilder incrementTotalNodes() {
        this.totalNodes++;
        return this;
    }

    public RestconfInstrumentationBuilder incrementTotalSkippedNodes() {
        this.totalSkippedNodes++;
        return this;
    }

    public RestconfInstrumentationBuilder incrementTotalMOsRead() {
        this.totalMOsRead++;
        return this;
    }

    public RestconfInstrumentationBuilder moPerSecRead(final long moPerSecRead) {
        this.moPerSecRead = moPerSecRead;
        return this;
    }

    public RestconfInstrumentationBuilder totalMOsCreated(final long totalMOsCreated) {
        this.totalMOsCreated = totalMOsCreated;
        return this;
    }

    public RestconfInstrumentationBuilder moPerSecCreate(final long moPerSecCreate) {
        this.moPerSecCreate = moPerSecCreate;
        return this;
    }

    public RestconfInstrumentationBuilder totalMOsUpdated(final long totalMOsUpdated) {
        this.totalMOsUpdated = totalMOsUpdated;
        return this;
    }

    public RestconfInstrumentationBuilder moPerSecUpdate(final long moPerSecUpdate) {
        this.moPerSecUpdate = moPerSecUpdate;
        return this;
    }

    public RestconfInstrumentationBuilder totalMOsDeleted(final long totalMOsDeleted) {
        this.totalMOsDeleted = totalMOsDeleted;
        return this;
    }

    public RestconfInstrumentationBuilder moPerSecDelete(final long moPerSecDelete) {
        this.moPerSecDelete = moPerSecDelete;
        return this;
    }

    public void calcTotalDpsTxTime(final long startTime) {
        this.totalDpsTxTime = System.currentTimeMillis() - startTime;
    }

    public void calcTotalResTime(final long startTime) {
        this.totalResTime = System.currentTimeMillis() - startTime;
    }

    public Map<String, Object> build() {
        final Map<String, Object> attributes = new LinkedHashMap<>();
        attributes.put("userIndex", userIndex);
        attributes.put("reqMethod", reqMethod != null ? reqMethod : UNKNOWN);
        attributes.put("reqType", reqType != null ? reqType : UNKNOWN);
        attributes.put("reqMediaType", reqMediaType != null ? reqMediaType : UNKNOWN);
        attributes.put("reqAcceptType", reqAcceptType != null ? reqAcceptType : UNKNOWN);
        attributes.put("totalReqDataSize", totalReqDataSize);
        attributes.put("rootModuleName", rootModuleName != null ? rootModuleName : UNKNOWN);
        attributes.put("yangRequestUri", yangRequestUri != null ? yangRequestUri : UNKNOWN);
        attributes.put("totalReqQueryParam", totalReqQueryParam);
        attributes.put("resStatus", resStatus != null ? resStatus : UNKNOWN);
        attributes.put("totalResDataSize", totalResDataSize);
        attributes.put("totalNodes", totalNodes);
        attributes.put("totalSkippedNodes", totalSkippedNodes);
        attributes.put("totalMOsRead", totalMOsRead);
        attributes.put("moPerSecRead", moPerSecRead);
        attributes.put("totalMOsCreated", totalMOsCreated);
        attributes.put("moPerSecCreate", moPerSecCreate);
        attributes.put("totalMOsUpdated", totalMOsUpdated);
        attributes.put("moPerSecUpdate", moPerSecUpdate);
        attributes.put("totalMOsDeleted", totalMOsDeleted);
        attributes.put("moPerSecDelete", moPerSecDelete);
        attributes.put("totalDpsTxTime", totalDpsTxTime);
        attributes.put("totalResTime", totalResTime);
        return attributes;
    }
}
