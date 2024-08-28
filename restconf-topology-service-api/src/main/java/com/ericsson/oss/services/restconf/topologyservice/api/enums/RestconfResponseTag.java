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

package com.ericsson.oss.services.restconf.topologyservice.api.enums;

/**
 * Restconf success/error response tag enumeration.
 * <br/>
 * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc8040#section-7">RFC 8040, section-7</a>
 * <br/>
 * Reference: <a href="https://datatracker.ietf.org/doc/html/rfc7231#section-6.5.4">RFC 7231, section-6.5.4</a>
 */
public enum RestconfResponseTag {
    IN_USE_409("in-use", 409, "Requested resource is in use"), INVALID_VALUE_400("invalid-value", 400,
            "Invalid value in the request"), INVALID_VALUE_404("invalid-value", 404, "Requested resource not found"), INVALID_VALUE_406(
            "invalid-value", 406, "Requested resource not acceptable"), REQUEST_TOO_BIG_413("too-big", 413,
            "Request message is too large to process"), RESPONSE_TOO_BIG_400("too-big", 400, "Response message is too large"), MISSING_ATTRIBUTE_400(
            "missing-attribute", 400, "Attribute is missing on the element node"), BAD_ATTRIBUTE_400("bad-attribute", 400,
            "Attribute on the element node is incorrect"), UNKNOWN_ATTRIBUTE_400("unknown-attribute", 400,
            "Attribute on the element node is incorrect"), BAD_ELEMENT_400("bad-element", 400,
            "Value of the element node is incorrect"), UNKNOWN_ELEMENT_400("unknown-element", 400,
            "Element cannot be identified"), UNKNOWN_NAMESPACE_400("unknown-namespace", 400, "Namespace cannot be identified"), ACCESS_DENIED_401(
            "access-denied", 401, "Access is denied; Unauthorized"), ACCESS_DENIED_403("access-denied", 403,
            "Access is denied; Forbidden"), LOCK_DENIED_409("lock-denied", 409, "Configuration is locked"), RESOURCE_DENIED_409("resource-denied",
            409, "Request cannot be processed due to insufficient resources"), ROLLBACK_FAILED_500("rollback-failed", 500,
            "Rollback failed"), DATA_EXISTS_409("data-exists", 409, "Data already exists; cannot create new resource"), DATA_MISSING_409(
            "data-missing", 409, "Data is missing; not available or deleted"), OPERATION_NOT_SUPPORTED_405("operation-not-supported", 405,
            "Operation is not supported; Invalid HTTP method"), OPERATION_NOT_SUPPORTED_501("operation-not-supported", 501,
            "Operation is not supported; Not implemented"), OPERATION_FAILED_412("operation-failed", 412,
            "An error occurred during operation execution; Precondition failed"), OPERATION_FAILED_500("operation-failed", 500,
            "An error occurred during operation execution"), PARTIAL_OPERATION_500("partial-operation", 500,
            "Some operations succeeded or failed"), MALFORMED_MESSAGE_400("malformed-message", 400, "Message is malformed"), OK_200("success", 200,
            "Success"), CREATED_201("created", 201, "Resource created"), NO_CONTENT_204("no-content", 204, "No content");

    private final String tagName;
    private final int statusCode;
    private final String message;

    RestconfResponseTag(final String tagName, final int statusCode, final String message) {
        this.tagName = tagName;
        this.statusCode = statusCode;
        this.message = message;
    }

    public String getTagName() {
        return tagName;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }
}
