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

package com.ericsson.oss.services.restconf.topologyservice.api.parser

import com.ericsson.cds.cdi.support.rule.ObjectUnderTest
import com.ericsson.cds.cdi.support.spock.CdiSpecification
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri
import com.ericsson.oss.services.restconf.topologyservice.api.exception.ResourceUriParserException
import spock.lang.Unroll

class RestconfResourceUriParserSpec extends CdiSpecification {

    @ObjectUnderTest
    RestconfResourceUriParser restconfResourceUriParser

    @Unroll
    def "Test single api-path: #resourceUri"() {
        when:
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        then:
            !restconfResourceUri.isEmpty()
            assert restconfResourceUri.getStep(0) == createStep(resourceUri, type, moduleName, identifier, keyValues)
        where:
            resourceUri                                                         || type || moduleName || identifier || keyValues
            "name"                                                              || "AI" || null       || "name"     || []
            "module:name"                                                       || "AI" || "module"   || "name"     || []
            "name=value1"                                                       || "LI" || null       || "name"     || ["value1"]
            "module:name=value1"                                                || "LI" || "module"   || "name"     || ["value1"]
            "module:name=value1%252F2"                                          || "LI" || "module"   || "name"     || ["value1/2"]
            "module:name=value1,value2"                                         || "LI" || "module"   || "name"     || ["value1", "value2"]
            "module:name=value1,value2,value3?depth=1"                          || "LI" || "module"   || "name"     || ["value1", "value2", "value3"]
            "module:name=value1,,value3?depth=1"                                || "LI" || "module"   || "name"     || ["value1", "", "value3"]
            "module:name=value1,value2,value3?depth=1&with-defaults=report-all" || "LI" || "module"   || "name"     || ["value1", "value2", "value3"]
    }

    @Unroll
    def "Test single api-path which ends with slash: #resourceUri"() {
        when:
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        then:
            !restconfResourceUri.isEmpty()
            assert restconfResourceUri.getStep(0) == createStep("module:name=value1,value2,value3", type, moduleName, identifier, keyValues)
        where:
            resourceUri                         || type || moduleName || identifier || keyValues
            "module:name=value1,value2,value3/" || "LI" || "module"   || "name"     || ["value1", "value2", "value3"]
    }

    @Unroll
    def "Test multiple api-path: #resourceUri"() {
        when:
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        then:
            !restconfResourceUri.isEmpty()
            restconfResourceUri.getSteps().size() == 2
            int index = 0
            for (RestconfResourceUri.Step step : restconfResourceUri.getSteps()) {
                assert step == createStep(resourceUri.split("/")[index], type[index], moduleNames[index], identifiers[index], keyValues[index])
                index++
            }
        where:
            resourceUri                                                          || type         || moduleNames            || identifiers        || keyValues
            "name1/name2"                                                        || ["AI", "AI"] || [null, null]           || ["name1", "name2"] || []
            "module1:name1/module2:name2"                                        || ["AI", "AI"] || ["module1", "module2"] || ["name1", "name2"] || []
            "name1=value1/name2=test1"                                           || ["LI", "LI"] || [null, null]           || ["name1", "name2"] || [["value1"], ["test1"]]
            "module1:name1=value1/module2:name2=test1"                           || ["LI", "LI"] || ["module1", "module2"] || ["name1", "name2"] || [["value1"], ["test1"]]
            "module1:name1=value1,value2/module2:name2=test1,test2"              || ["LI", "LI"] || ["module1", "module2"] || ["name1", "name2"] || [["value1", "value2"], ["test1", "test2"]]
            "module1:name1=value1,value2,value3/module2:name2=test1,test2,test3" || ["LI", "LI"] || ["module1", "module2"] || ["name1", "name2"] || [["value1", "value2", "value3"], ["test1", "test2", "test3"]]
    }

    @Unroll
    def "Test complex api-path: #resourceUri"() {
        when:
            RestconfResourceUri restconfResourceUri = restconfResourceUriParser.parse(resourceUri)
        then:
            !restconfResourceUri.isEmpty()
            restconfResourceUri.getSteps().size() == 2
            int index = 0
            for (RestconfResourceUri.Step step : restconfResourceUri.getSteps()) {
                assert step == createStep(resourceUri.split("/")[index], type[index], moduleNames[index], identifiers[index], keyValues[index])
                index++
            }
        where:
            resourceUri                                     || type         || moduleNames            || identifiers        || keyValues
            "name1/module1:name2"                           || ["AI", "AI"] || [null, "module1"]      || ["name1", "name2"] || []
            "module1:name1/name2"                           || ["AI", "AI"] || ["module1", "module1"] || ["name1", "name2"] || []
            "name1=value1/name2=test1,test2"                || ["LI", "LI"] || [null, null]           || ["name1", "name2"] || [["value1"], ["test1", "test2"]]
            "name1=value1,value2/name2=test1"               || ["LI", "LI"] || [null, null]           || ["name1", "name2"] || [["value1", "value2"], ["test1"]]
            "module1:name1=value1/name2=test1"              || ["LI", "LI"] || ["module1", "module1"] || ["name1", "name2"] || [["value1"], ["test1"]]
            "name1=value1,value2/module2:name2=test1,test2" || ["LI", "LI"] || [null, "module2"]      || ["name1", "name2"] || [["value1", "value2"], ["test1", "test2"]]
            "name1/name2=test1,test2"                       || ["AI", "LI"] || [null, null]           || ["name1", "name2"] || [[], ["test1", "test2"]]
            "name1=value1,value2/name2"                     || ["LI", "AI"] || [null, null]           || ["name1", "name2"] || [["value1", "value2"], []]
            "module1:name1/name2=test1,test2"               || ["AI", "LI"] || ["module1", "module1"] || ["name1", "name2"] || [[], ["test1", "test2"]]
            "name1=value1,value2/module2:name2"             || ["LI", "AI"] || [null, "module2"]      || ["name1", "name2"] || [["value1", "value2"], []]
    }

    @Unroll
    def "Test invalid api-path #resourceUri"() {
        when:
            restconfResourceUriParser.parse(resourceUri)
        then:
            thrown(ResourceUriParserException)
        where:
            resourceUri           | _
            ""                    | _
            "module:"             | _
            "name="               | _
            "-module:name"        | _
            "module:name=value1," | _
            "@name"               | _
            ":name"               | _
            "module::name"        | _
    }

    def createStep(final String originalStep, String type, moduleName, identifier, keyValues) {
        RestconfResourceUri.Step step = new RestconfResourceUri.Step(
                doubleDecode(originalStep.split("\\?")[0]),
                (type == "AI") ? RestconfResourceUri.Step.Type.API_IDENTIFIER : RestconfResourceUri.Step.Type.LIST_INSTANCE,
                moduleName == null ? "" : moduleName,
                identifier,
                keyValues as String[])
        return step
    }

    def doubleDecode(token) {
        return URLDecoder.decode(URLDecoder.decode(token, "UTF-8"), "UTF-8");
    }
}
