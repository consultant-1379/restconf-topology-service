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

import com.ericsson.oss.services.overload.protection.json.JsonParser;

import javax.ejb.Singleton;
import javax.inject.Inject;

/**
 * Singleton EJB used to read the configuration file.
 */
@Singleton
public class ConfigurationService {

    @Inject
    private ConfigFileLocator configFileLocator;

    private ConfigDTO config;

    /**
     * Total capacity supported by the server
     * @return an integer value representing the total server capacity
     */
    public ConfigDTO getConfiguration() {
        if (config == null) {
            config = new JsonParser().parseToObject(configFileLocator.getConfigFile(), ConfigDTO.class);
        }
        return config;
    }

}
