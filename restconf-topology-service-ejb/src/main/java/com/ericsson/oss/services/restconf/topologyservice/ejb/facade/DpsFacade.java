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

package com.ericsson.oss.services.restconf.topologyservice.ejb.facade;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.context.ApplicationScoped;

import com.ericsson.oss.itpf.datalayer.dps.DataBucket;
import com.ericsson.oss.itpf.datalayer.dps.DataPersistenceService;
import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.query.Query;
import com.ericsson.oss.itpf.datalayer.dps.query.QueryBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.Restriction;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeContainmentRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.TypeRestrictionBuilder;
import com.ericsson.oss.itpf.datalayer.dps.query.graph.QueryPathRestrictionBuilder;
import com.ericsson.oss.itpf.sdk.core.annotation.EServiceRef;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.DependsOnDatabase;

/**
 * DPS facade.
 */
@ApplicationScoped
@DependsOnDatabase
@TransactionManagement(TransactionManagementType.CONTAINER)
public class DpsFacade {

    @EServiceRef
    private DataPersistenceService dataPersistenceService;

    private DataBucket getReadOnlyLiveBucket() {
        dataPersistenceService.setWriteAccess(false);
        return dataPersistenceService.getLiveBucket();
    }

    /**
     * Get {@code ManagedObject} objects by moType and baseFdn.
     *
     * @param namespace namespace.
     * @param type      mo type.
     * @param baseFdn   parent or base fdn.
     * @return Iterator of {@code ManagedObject} objects
     */
    public Iterator<ManagedObject> getMoByType(final String namespace, final String type, final String baseFdn) {
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Query<TypeContainmentRestrictionBuilder> query = queryBuilder.createTypeQuery(namespace, type, baseFdn);
        return getReadOnlyLiveBucket().getQueryExecutor().execute(query);
    }

    /**
     * Get {@code ManagedObject} objects by moType, baseFdn and specific attributes.
     *
     * @param namespace             namespace.
     * @param type                  mo type.
     * @param baseFdn               parent or base fdn.
     * @param restrictionAttributes specific attributes.
     * @return Iterator of {@code ManagedObject} objects
     */
    public Iterator<ManagedObject> getMoByType(final String namespace, final String type, final String baseFdn,
                                               final Map<String, String> restrictionAttributes) {
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Restriction restriction = getRestriction(restrictionAttributes);
        if (baseFdn == null) {
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(namespace, type);
            if (restriction != null) {
                typeQuery.setRestriction(restriction);
            }
            return getReadOnlyLiveBucket().getQueryExecutor().execute(typeQuery);
        } else {
            final Query<TypeContainmentRestrictionBuilder> containmentQuery = queryBuilder.createTypeQuery(namespace, type, baseFdn);
            if (restriction != null) {
                containmentQuery.setRestriction(restriction);
            }
            return getReadOnlyLiveBucket().getQueryExecutor().execute(containmentQuery);
        }
    }

    /**
     * Get {@code Count} objects by moType, baseFdn and specific attributes.
     *
     * @param namespace             namespace.
     * @param type                  mo type.
     * @param baseFdn               parent or base fdn.
     * @param restrictionAttributes specific attributes.
     * @return Iterator of {@code ManagedObject} objects
     */
    public Long getMoByTypeCount(final String namespace, final String type, final String baseFdn,
                                               final Map<String, String> restrictionAttributes) {
        final QueryBuilder queryBuilder = dataPersistenceService.getQueryBuilder();
        final Restriction restriction = getRestriction(restrictionAttributes);
        if (baseFdn == null) {
            final Query<TypeRestrictionBuilder> typeQuery = queryBuilder.createTypeQuery(namespace, type);
            if (restriction != null) {
                typeQuery.setRestriction(restriction);
            }
            return getReadOnlyLiveBucket().getQueryExecutor().executeCount(typeQuery);
        } else {
            final Query<TypeContainmentRestrictionBuilder> containmentQuery = queryBuilder.createTypeQuery(namespace, type, baseFdn);
            if (restriction != null) {
                containmentQuery.setRestriction(restriction);
            }
            return getReadOnlyLiveBucket().getQueryExecutor().executeCount(containmentQuery);
        }
    }

    /**
     * Get attribute value from {@code ManagedObject} object
     *
     * @param fdn       fdn.
     * @param attribute attribute.
     * @return value or null.
     */
    public Object getValueFromFdn(final String fdn, final String attribute) {
        final ManagedObject managedObject = getReadOnlyLiveBucket().findMoByFdn(fdn);
        if (managedObject != null) {
            return managedObject.getAttribute(attribute);
        }
        return null;
    }

    private Restriction getRestriction(final Map<String, String> restrictionAttributes) {
        if (restrictionAttributes != null && !restrictionAttributes.isEmpty()) {
            final QueryPathRestrictionBuilder pathRestrictionBuilder = getReadOnlyLiveBucket().getQueryPathExecutor().getRestrictionBuilder();
            final List<Restriction> restrictions = new ArrayList<>();
            for (final Map.Entry<String, String> entrySet : restrictionAttributes.entrySet()) {
                restrictions.add(pathRestrictionBuilder.equalTo(entrySet.getKey(), entrySet.getValue()));
            }
            return pathRestrictionBuilder.allOf(restrictions.toArray(restrictions.toArray(new Restriction[0])));
        }
        return null;
    }
}