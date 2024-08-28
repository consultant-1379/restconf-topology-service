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

import com.ericsson.oss.services.overload.protection.exceptions.ConfigurationFileNotFoundException;

import java.io.File;

/**
 * Class responsible to discover and read the config file.
 */
public class ConfigFileLocator {

    public static final String CONFIG_FILE_LOCATION_PROPERTY = "op.config.file.dir";

    public static final String CONFIG_FILE_NAME = "overload-protection-config.json";

    /**
     * Returns the config file as a File instance.
     * @return config file instance
     * @throws ConfigurationFileNotFoundException when the config file is not found.
     */
    public File getConfigFile() {
        // if the system property is not available use the jboss/standalone/configuration folder
        final String fileLocation = System.getProperty(CONFIG_FILE_LOCATION_PROPERTY, System.getProperty("jboss.server.config.dir")) +
            File.separator + CONFIG_FILE_NAME;

        final File configFile = new File(fileLocation);

        if (!configFile.exists() || !configFile.canRead()) {
            throw new ConfigurationFileNotFoundException(fileLocation);
        }

        return configFile;
    }

}
