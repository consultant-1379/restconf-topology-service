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

package com.ericsson.oss.services.restconf.topologyservice.ejb;

import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfConstants.RBAC_ACTION_READ;
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfConstants.RBAC_RESOURCE_RESTCONF_NBI;

import java.io.InputStream;
import javax.ejb.Stateless;
import javax.inject.Inject;

import com.ericsson.oss.itpf.sdk.security.accesscontrol.EPredefinedRole;
import com.ericsson.oss.itpf.sdk.security.accesscontrol.annotation.Authorize;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNone;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.provider.operation.RestconfDataOperation;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator.RestconfTopLevelResourceDataGenerator;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator.RestconfYangDataGenerator;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator.RestconfYangLibraryVersionDataGenerator;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.generator.RestconfYangSchemaGenerator;

/**
 * Restconf topology service.
 */
@Stateless
@SuppressWarnings("java:S1874")
public class RestconfTopologyServiceBean {

    @Inject
    private RestconfYangDataGenerator restconfYangDataGenerator;

    @Inject
    private RestconfTopLevelResourceDataGenerator restconfTopLevelResourceDataGenerator;

    @Inject
    private RestconfYangLibraryVersionDataGenerator restconfYangLibraryVersionDataGenerator;

    @Inject
    private RestconfYangSchemaGenerator yangSchemaGenerator;

    /**
     * Get restconf top level data.
     *
     * @return {@code YangDataNode} object or none.
     * @throws RestconfException when failed
     */
    @Authorize(resource = RBAC_RESOURCE_RESTCONF_NBI, action = RBAC_ACTION_READ, role = { EPredefinedRole.OPERATOR, EPredefinedRole.ADMINISTRATOR })
    public YangDataNode getRestconfTopLevelResource() throws RestconfException {
        return restconfTopLevelResourceDataGenerator.generate();
    }

    /**
     * Get data based on requested resource uri.
     *
     * @param restconfResourceUri {@code restconfResourceUri} object.
     * @return {@code YangDataNode} object or none.
     * @throws RestconfException when failed
     */
    @Authorize(resource = RBAC_RESOURCE_RESTCONF_NBI, action = RBAC_ACTION_READ, role = { EPredefinedRole.OPERATOR, EPredefinedRole.ADMINISTRATOR })
    public YangDataNode getRestconfDataResource(final RestconfResourceUri restconfResourceUri) throws RestconfException {
        return restconfYangDataGenerator.generate(restconfResourceUri, RestconfDataOperation.Type.READ);
    }

    /**
     * Execute operation on data based on requested resource uri.
     *
     * @param restconfResourceUri {@code restconfResourceUri} object.
     * @return {@code YangDataNode} object or none.
     * @throws RestconfException when failed
     */
    @Authorize(resource = RBAC_RESOURCE_RESTCONF_NBI, action = RBAC_ACTION_READ, role = { EPredefinedRole.OPERATOR, EPredefinedRole.ADMINISTRATOR })
    public YangDataNode getRestconfOperationsResource(final RestconfResourceUri restconfResourceUri) throws RestconfException {
        return YangDataNone.none();
    }

    /**
     * Get restconf yang library version.
     *
     * @return {@code YangDataNode} object or none.
     * @throws RestconfException when failed
     */
    @Authorize(resource = RBAC_RESOURCE_RESTCONF_NBI, action = RBAC_ACTION_READ, role = { EPredefinedRole.OPERATOR, EPredefinedRole.ADMINISTRATOR })
    public YangDataNode getRestconfYangLibraryVersionResource() throws RestconfException {
        return restconfYangLibraryVersionDataGenerator.generate();
    }

    /**
     * Get yang module schema.
     * @param moduleName module name.
     * @param revision revision.
     * @return Input stream.
     * @throws RestconfException when failed to create input stream or module not found.
     */
    @Authorize(resource = RBAC_RESOURCE_RESTCONF_NBI, action = RBAC_ACTION_READ, role = { EPredefinedRole.OPERATOR, EPredefinedRole.ADMINISTRATOR })
    public InputStream getRestconfYangSchemaResource(final String moduleName, final String revision) throws RestconfException {
        return yangSchemaGenerator.generate(moduleName, revision);
    }
}
