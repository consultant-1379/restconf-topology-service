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

package com.ericsson.oss.services.restconf.topologyservice.api.provider;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.sdk.resources.Resource;
import com.ericsson.oss.itpf.sdk.resources.Resources;

/**
 * Enm global properties provider.
 */
public final class EnmGlobalPropertiesProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnmGlobalPropertiesProvider.class);

    private static final String GLOBAL_PROPERTIES = "/ericsson/tor/data/global.properties";

    private static final String UI_PRES_SERVER = "UI_PRES_SERVER";

    private static Properties globalProperties;

    private EnmGlobalPropertiesProvider() {
    }

    static {
        try {
            globalProperties = new Properties();
            final Resource propertiesResource = Resources.getFileSystemResource(GLOBAL_PROPERTIES);
            if (propertiesResource != null) {
                globalProperties.load(propertiesResource.getInputStream());
            }
        } catch (final Exception exception) {
            LOGGER.error("Failed to load {} file. Exception: {}", GLOBAL_PROPERTIES, exception.getMessage());
        }
    }

    /**
     * Gets Enm UI server property.
     *
     * @return the property value
     */
    public static String getEnmHost() {
        if (globalProperties == null) {
            return "";
        }
        final String uiPresServer = globalProperties.getProperty(UI_PRES_SERVER);
        if (uiPresServer == null) {
            LOGGER.trace("{} property not found in {} file", UI_PRES_SERVER, GLOBAL_PROPERTIES);
            return "";
        }
        return "https://" + uiPresServer;
    }
}
