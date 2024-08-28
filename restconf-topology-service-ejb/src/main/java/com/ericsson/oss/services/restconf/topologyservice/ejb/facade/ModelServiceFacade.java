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

import javax.inject.Inject;
import javax.inject.Singleton;

import com.ericsson.oss.itpf.modeling.modelservice.ModelService;

/**
 * Model Service facade.
 */
@Singleton
public class ModelServiceFacade {

    @Inject
    private ModelService modelService;
}
