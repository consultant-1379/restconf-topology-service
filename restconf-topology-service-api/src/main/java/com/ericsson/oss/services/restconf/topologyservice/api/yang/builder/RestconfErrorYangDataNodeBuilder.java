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

package com.ericsson.oss.services.restconf.topologyservice.api.yang.builder;

import static com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;

import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfErrorType;
import com.ericsson.oss.services.restconf.topologyservice.api.enums.RestconfResponseTag;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataSet;

/**
 * Restconf error yang data node DTO.
 */
public class RestconfErrorYangDataNodeBuilder {
    private RestconfErrorType errorType;
    private RestconfResponseTag responseTag;
    private String errorPath;
    private String errorMessage;
    private String errorInfoMessage;

    public RestconfErrorYangDataNodeBuilder() {
        errorType = RestconfErrorType.PROTOCOL;
        responseTag = RestconfResponseTag.OK_200;
        errorPath = null;
        errorMessage = null;
        errorInfoMessage = null;
    }

    public RestconfErrorYangDataNodeBuilder errorType(final RestconfErrorType errorType) {
        this.errorType = errorType;
        return this;
    }

    public RestconfErrorYangDataNodeBuilder responseTag(final RestconfResponseTag responseTag) {
        this.responseTag = responseTag;
        return this;
    }

    public RestconfErrorYangDataNodeBuilder errorPath(final String errorPath) {
        this.errorPath = errorPath;
        return this;
    }

    public RestconfErrorYangDataNodeBuilder errorMessage(final String errorMessage) {
        this.errorMessage = errorMessage;
        return this;
    }

    public RestconfErrorYangDataNodeBuilder errorInfoMessage(final String errorInfoMessage) {
        this.errorInfoMessage = errorInfoMessage;
        return this;
    }

    public YangDataNode build() {
        final YangDataNodeBuilder restconfYangDataNodeBuilder = new YangDataNodeBuilder("urn:ietf:params:xml:ns:yang:ietf-restconf", "ietf-restconf");

        final YangDataLeaf errorTypeLeaf = restconfYangDataNodeBuilder.newInstance("error-type").leaf(errorType.getName());
        final YangDataLeaf errorTagLeaf = restconfYangDataNodeBuilder.newInstance("error-tag").leaf(responseTag.getTagName());
        final YangDataLeaf errorPathLeaf = restconfYangDataNodeBuilder.newInstance("error-path").leaf(errorPath != null ? errorPath : "");
        final YangDataLeaf errorMessageLeaf = restconfYangDataNodeBuilder.newInstance("error-message")
                .leaf(errorMessage != null ? errorMessage : responseTag.getMessage());

        final YangDataSet yangDataSet = restconfYangDataNodeBuilder.dataSet(errorTypeLeaf, errorTagLeaf, errorPathLeaf, errorMessageLeaf);

        final YangDataList errorList = restconfYangDataNodeBuilder.newInstance("error").list();
        errorList.addDataNode(yangDataSet);

        final YangDataContainer errorsContainer = restconfYangDataNodeBuilder.newInstance("errors").container();
        errorsContainer.addDataNode(errorList);
        if (null != errorInfoMessage && errorInfoMessage.length() > 0) {
            final YangDataLeaf errorInfoMessageLeaf = restconfYangDataNodeBuilder.newInstance("message")
                    .leaf(errorInfoMessage);
            final YangDataContainer errorInfoContainer = restconfYangDataNodeBuilder.newInstance("error-info").container();
            errorInfoContainer.addDataNode(errorInfoMessageLeaf);
            errorsContainer.addDataNode(errorInfoContainer);
        }

        return new YangDataNodeBuilder().root(errorsContainer);
    }
}
