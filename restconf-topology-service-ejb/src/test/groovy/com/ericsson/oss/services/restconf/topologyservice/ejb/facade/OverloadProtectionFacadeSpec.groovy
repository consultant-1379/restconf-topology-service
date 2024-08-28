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

package com.ericsson.oss.services.restconf.topologyservice.ejb.facade

import static com.ericsson.oss.services.overload.protection.configuration.ConfigFileLocator.CONFIG_FILE_NAME

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.overload.protection.configuration.ConfigFileLocator
import com.ericsson.oss.services.overload.protection.exceptions.NotEnoughCapacityException
import com.ericsson.oss.services.overload.protection.service.api.LoadCounterService
import com.ericsson.oss.services.overload.protection.service.impl.LoadCounterServiceImpl
import spock.lang.Unroll

class OverloadProtectionFacadeSpec extends CdiSpecification {

    @ObjectUnderTest
    OverloadProtectionFacade overloadProtectionFacade

    @SpyImplementation
    ConfigFileLocator fileLocator

    @ImplementationClasses
    Class[] implementationClasses = (Class[]) [LoadCounterServiceImpl.class]

    def setup() {
        fileLocator.getConfigFile() >> new File(this.class.getResource("/$CONFIG_FILE_NAME").toURI())
    }

    @Unroll
    def "Test single capacity request"() {
        given: "Get LoadCounterService"
            LoadCounterService loadCounterService = overloadProtectionFacade.getLoadCounterService()
        when: "Request capacity"
            String capacityId = overloadProtectionFacade.requestCapacity("application", "read", 1)
        then:
            capacityId != null
            2 == loadCounterService.getTotalCapacity()
            1 == loadCounterService.getAvailableCapacity()
            1 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }

    @Unroll
    def "Test multiple capacity requests"() {
        given: "Get LoadCounterService"
            LoadCounterService loadCounterService = overloadProtectionFacade.getLoadCounterService()
        when: "Request first capacity"
            String capacityId = overloadProtectionFacade.requestCapacity("application", "read", 1)
            overloadProtectionFacade.releaseCapacity(capacityId)
        and: "Request second capacity"
            capacityId = overloadProtectionFacade.requestCapacity("application", "write", 1)
            overloadProtectionFacade.releaseCapacity(capacityId)
        then:
            2 == loadCounterService.getTotalCapacity()
            2 == loadCounterService.getAvailableCapacity()
            0 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }

    @Unroll
    def "Test NotEnoughCapacityException upon parallel capacity request"() {
        given: "Get LoadCounterService"
            LoadCounterService loadCounterService = overloadProtectionFacade.getLoadCounterService()
        when: "Request first capacity"
            overloadProtectionFacade.requestCapacity("application", "read", 1)
        and: "Request second capacity"
            overloadProtectionFacade.requestCapacity("application", "write", 1)
        then:
            def exception = thrown(NotEnoughCapacityException)
            exception.message == "There's no capacity available for this request on the server. Please try again later."
            2 == loadCounterService.getTotalCapacity()
            1 == loadCounterService.getAvailableCapacity()
            1 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }
}
