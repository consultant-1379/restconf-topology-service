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

package com.ericsson.oss.services.restconf.topologyservice.rest.interceptor

import static com.ericsson.oss.services.overload.protection.configuration.ConfigFileLocator.CONFIG_FILE_NAME
import static com.ericsson.oss.services.restconf.topologyservice.api.constants.RestconfRestConstants.APPLICATION_YANG_JSON

import javax.interceptor.InvocationContext

import org.jboss.resteasy.mock.MockHttpRequest

import com.ericsson.cds.cdi.support.rule.ImplementationClasses
import com.ericsson.cds.cdi.support.rule.MockedImplementation
import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.rule.SpyImplementation
import com.ericsson.oss.itpf.sdk.licensing.LicensingService
import com.ericsson.oss.itpf.sdk.licensing.Permission
import com.ericsson.oss.services.overload.protection.configuration.ConfigFileLocator
import com.ericsson.oss.services.overload.protection.service.api.LoadCounterService
import com.ericsson.oss.services.overload.protection.service.impl.LoadCounterServiceImpl
import com.ericsson.oss.services.restconf.topologyservice.api.exception.LicenseValidationException
import com.ericsson.oss.services.restconf.topologyservice.ejb.facade.OverloadProtectionFacade
import com.ericsson.oss.services.restconf.topologyservice.rest.resources.RestconfTopologyServiceDataResource
import com.ericsson.oss.services.restconf.topologyservice.rest.utils.RequestProvider
import com.ericsson.oss.services.restconf.topologyservice.test.common.BaseRestconfTopologyServiceResourceSpec
import spock.lang.Ignore

class RestconfRequestValidationInterceptorSpec extends BaseRestconfTopologyServiceResourceSpec {

    @ObjectUnderTest
    RestconfRequestValidationInterceptor restconfRequestValidationInterceptor

    @SpyImplementation
    RestconfTopologyServiceDataResource restconfTopologyServiceDataResource

    @MockedImplementation
    LicensingService licensingService

    @SpyImplementation
    ConfigFileLocator fileLocator

    @SpyImplementation
    OverloadProtectionFacade overloadProtectionFacade

    @SpyImplementation
    RequestProvider requestProvider

    @ImplementationClasses
    Class[] implementationClasses = (Class[]) [LoadCounterServiceImpl.class]

    LoadCounterService loadCounterService

    def setup() {
        dispatcher.getRegistry().addSingletonResource(restconfTopologyServiceDataResource)
        fileLocator.getConfigFile() >> new File(this.class.getResource("/$CONFIG_FILE_NAME").toURI())
        loadCounterService = overloadProtectionFacade.getLoadCounterService()
    }

    def "Test onIntercept with all successful licenses validation"() {
        given: "Create request object"
            final MockHttpRequest request = MockHttpRequest.get("/data/ietf-yang-library:modules-state")
            request.accept(APPLICATION_YANG_JSON)
            request.header("X-Tor-userId", "administrator")
        and: "Inject HttpRequest context"
            requestProvider.setHttpRequest(request)
        and: "Define interceptor context"
            def context = [proceed: { return "i was invoked!" }] as InvocationContext
        and: "Mock LicensingService methods"
            licensingService.validatePermission(_ as String) >> Permission.ALLOWED
        when: "Execute onIntercept"
            def result = restconfRequestValidationInterceptor.onIntercept(context)
        then: "Verify context proceed method is executed"
            result == "i was invoked!"
        and: "Verify capacity allocation"
            3 == loadCounterService.getTotalCapacity()
            3 == loadCounterService.getAvailableCapacity()
            0 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }

    def "Test onIntercept with one successful license validation"() {
        given: "Create request object"
            final MockHttpRequest request = MockHttpRequest.get("/data/ietf-yang-library:modules-state")
            request.accept(APPLICATION_YANG_JSON)
            request.header("X-Tor-userId", "administrator")
        and: "Inject HttpRequest context"
            requestProvider.setHttpRequest(request)
        and: "Define interceptor context"
            def context = [proceed: { return "i was invoked!" }] as InvocationContext
        and: "Mock LicensingService methods"
            licensingService.validatePermission("FAT1023443") >> Permission.ALLOWED
            licensingService.validatePermission("FAT1023603") >> Permission.DENIED_NO_VALID_LICENSE
            licensingService.validatePermission("FAT1023653") >> Permission.DENIED_INSUFFICIENT_CAPACITY
            licensingService.validatePermission("FAT1023988") >> Permission.ALLOWED
            licensingService.validatePermission("FAT1023989") >> Permission.ALLOWED
            licensingService.validatePermission("FAT1023990") >> Permission.ALLOWED
        when: "Execute onIntercept"
            def result = restconfRequestValidationInterceptor.onIntercept(context)
        then: "Verify context proceed method is executed"
            result == "i was invoked!"
        and: "Verify capacity allocation"
            3 == loadCounterService.getTotalCapacity()
            3 == loadCounterService.getAvailableCapacity()
            0 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }

    def "Test onIntercept with all denied license validation"() {
        given: "Create request object"
            final MockHttpRequest request = MockHttpRequest.get("/data/ietf-yang-library:modules-state")
            request.accept(APPLICATION_YANG_JSON)
            request.header("X-Tor-userId", "administrator")
        and: "Inject HttpRequest context"
            requestProvider.setHttpRequest(request)
        and: "Define interceptor context"
            def context = [proceed: { return "i was invoked!" }] as InvocationContext
        and: "Mock LicensingService methods"
            licensingService.validatePermission(_ as String) >> Permission.DENIED_NO_VALID_LICENSE
        when: "Execute onIntercept"
            restconfRequestValidationInterceptor.onIntercept(context)
        then: "Verify context proceed method is not executed"
            def exception = thrown(LicenseValidationException)
            exception.message == "License validation failed! Please check server logs for more details."
        and: "Verify capacity allocation"
            3 == loadCounterService.getTotalCapacity()
            3 == loadCounterService.getAvailableCapacity()
            0 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }

    @Ignore
    def "Test onIntercept with parallel requests which cannot allocate capacity"() {
        given: "Create first request object"
            final MockHttpRequest firstRequest = MockHttpRequest.get("/data/ietf-yang-library:modules-state")
            firstRequest.accept(APPLICATION_YANG_JSON)
            firstRequest.header("X-Tor-userId", "first_user")
        and: "Create second request object"
            final MockHttpRequest secondRequest = MockHttpRequest.get("/data/ietf-yang-library:modules-state")
            secondRequest.accept(APPLICATION_YANG_JSON)
            secondRequest.header("X-Tor-userId", "second_user")
        and: "Inject HttpRequest context"
            requestProvider.setHttpRequest(firstRequest)
        and: "Define interceptor context for first request which will sleep for 1 minute"
            def firstRequestContext = [proceed: { Thread.sleep(60000); return "i was invoked after 1 minute!" }] as InvocationContext
        and: "Define interceptor context for second request for parallel processing"
            def secondRequestContext = [proceed: { return "i was invoked in parallel!" }] as InvocationContext
        and: "Mock LicensingService methods"
            licensingService.validatePermission(_ as String) >> Permission.ALLOWED
        when: "Execute both contexts onIntercept in a thread"
            Thread.start {
                def firstResult = restconfRequestValidationInterceptor.onIntercept(firstRequestContext)
                def secondResult = restconfRequestValidationInterceptor.onIntercept(secondRequestContext)
            }
        then: "Verify second context proceed method is not executed"
            3 == loadCounterService.getTotalCapacity()
            3 == loadCounterService.getAvailableCapacity()
            0 == loadCounterService.getUsedCapacity()
            loadCounterService.getUsedCapacity() == loadCounterService.getRunningRequests().size()
    }
}
