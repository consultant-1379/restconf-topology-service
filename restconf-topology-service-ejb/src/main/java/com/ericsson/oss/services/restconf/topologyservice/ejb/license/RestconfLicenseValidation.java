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

package com.ericsson.oss.services.restconf.topologyservice.ejb.license;

import com.ericsson.oss.itpf.sdk.licensing.LicensingService;
import com.ericsson.oss.itpf.sdk.licensing.Permission;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.LicenseValidationException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;

import javax.inject.Inject;

/**
 * Restconf License Validation.
 */
public class RestconfLicenseValidation {

    private static final String RESTCONF_NBI_5MHZSC = "FAT1023443";
    private static final String RESTCONF_NBI_CELL_CARRIER = "FAT1023603";
    private static final String RESTCONF_NBI_ONOFFSCOPE_GSM_TRX = "FAT1023653";
    private static final String RESTCONF_NBI_ONOFFSCOPE_RADIO = "FAT1023988";
    private static final String RESTCONF_NBI_ONOFFSCOPE_CORE = "FAT1023989";
    private static final String RESTCONF_NBI_ONOFFSCOPE_TRANSPORT = "FAT1023990";

    @Inject
    private LicensingService licensingService;

    /**
     * Validates licenses.
     *
     * @throws RestconfException if all licenses validation fails.
     */
    public void validateLicense() throws RestconfException {
        switch (getDynamicFileFormatLicensePermission()) {
            case ALLOWED:
                return;
            case DENIED_NO_VALID_LICENSE:
                throw new LicenseValidationException(
                        "License validation failed! Please check server logs for more details.");
            default:
                break;
        }
    }

    private Permission getDynamicFileFormatLicensePermission() {
        if (licensingService.validatePermission(RESTCONF_NBI_5MHZSC) == Permission.ALLOWED
                || licensingService.validatePermission(RESTCONF_NBI_ONOFFSCOPE_GSM_TRX) == Permission.ALLOWED
                || licensingService.validatePermission(RESTCONF_NBI_CELL_CARRIER) == Permission.ALLOWED
                || licensingService.validatePermission(RESTCONF_NBI_ONOFFSCOPE_RADIO) == Permission.ALLOWED
                || licensingService.validatePermission(RESTCONF_NBI_ONOFFSCOPE_CORE) == Permission.ALLOWED
                || licensingService.validatePermission(RESTCONF_NBI_ONOFFSCOPE_TRANSPORT) == Permission.ALLOWED) {
            return Permission.ALLOWED;
        }
        return Permission.DENIED_NO_VALID_LICENSE;
    }
}