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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.ejb.Lock;
import javax.ejb.LockType;
import javax.ejb.Startup;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.yangtools.parser.ByteArrayYangInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.ParserExecutionContext;
import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.FindingsManager;
import com.ericsson.oss.mediation.modeling.yangtools.parser.findings.ModifyableFindingSeverityCalculator;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.ConformanceType;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.StatementClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.eri.EricssonExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.ietf.IetfExtensionsClassSupplier;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.threegpp.ThreeGppExtensionsClassSupplier;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfYangModulesParserException;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.utils.LoggingUtility;
import com.ericsson.oss.services.restconf.topologyservice.yang.YangModuleNameProvider;

/**
 * Restconf yang modules parser using ENM yang-parser component.
 */
@Startup
@Singleton
@SuppressWarnings("java:S2629")
public class RestconfYangModulesParser {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfYangModulesParser.class);

    private YangDeviceModel yangDeviceModel;

    @PostConstruct
    public void initialize() {
        try {
            parseYangModels();
        } catch (final Exception exception) {
            LoggingUtility.logException(LOGGER, "parse yang models", exception);
        }
    }

    @Asynchronous
    @Lock(LockType.READ)
    @TotalTimeTaken(task = "parsing yang models")
    private void parseYangModels() throws RestconfYangModulesParserException {
        try {
            // Yang Parser default configuration
            final ModifyableFindingSeverityCalculator severityCalculator = new ModifyableFindingSeverityCalculator();
            final FindingsManager findingsManager = new FindingsManager(severityCalculator);
            final List<StatementClassSupplier> extensionCreators = Arrays.asList(new EricssonExtensionsClassSupplier(),
                    new IetfExtensionsClassSupplier(), new ThreeGppExtensionsClassSupplier());
            final ParserExecutionContext parserContext = new ParserExecutionContext(findingsManager, extensionCreators, Collections.emptyList());

            parserContext.setFailFast(false);
            parserContext.setCheckModulesAgainstYangLibrary(false);
            parserContext.setMergeSubmodulesIntoModules(true);
            parserContext.setResolveDerivedTypesAndGroupings(true);
            parserContext.setStopAfterInitialParse(false);
            parserContext.setIgnoreImportedProtocolAccessibleObjects(true);
            parserContext.setResolveAugmentsAndDeviations(true);

            final List<YangModelInput> yangModelFiles = getYangModelInputs();
            if (yangModelFiles.isEmpty()) {
                throw new RestconfYangModulesParserException("No yang modules found!");
            }
            yangDeviceModel = new YangDeviceModel("RESTCONF_YANG_MODELS");
            yangDeviceModel.parseIntoYangModels(parserContext, yangModelFiles);
        } catch (final Exception exception) {
            throw new RestconfYangModulesParserException(exception);
        }
    }

    /**
     * Get {@code YangDeviceModel} object.
     *
     * @return {@code YangDeviceModel} object.
     */
    public YangDeviceModel getYangDeviceModel() throws RestconfException {
        if (yangDeviceModel == null) {
            parseYangModels();
        }
        return yangDeviceModel;
    }

    private List<YangModelInput> getYangModelInputs() throws RestconfYangModulesParserException {
        final List<YangModelInput> yangModelFiles = new ArrayList<>();
        try {
            final ClassLoader loader = Thread.currentThread().getContextClassLoader();
            for (final String fileName : YangModuleNameProvider.getRestconfYangModuleImplementFileNames()) {
                yangModelFiles.addAll(getYangModelInputs(loader, fileName, ConformanceType.IMPLEMENT));
            }
            for (final String fileName : YangModuleNameProvider.getRestconfYangModuleImportFileNames()) {
                yangModelFiles.addAll(getYangModelInputs(loader, fileName, ConformanceType.IMPORT));
            }
        } catch (final Exception exception) {
            throw new RestconfYangModulesParserException(exception);
        }
        return yangModelFiles;
    }

    private List<YangModelInput> getYangModelInputs(final ClassLoader loader, final String fileName, final ConformanceType conformanceType) {
        final List<YangModelInput> yangModelFiles = new ArrayList<>();
        final InputStream resourceAsStream = loader.getResourceAsStream("restconf/" + fileName);
        if (resourceAsStream != null) {
            final String yangModuleContent = new BufferedReader(new InputStreamReader(resourceAsStream, StandardCharsets.UTF_8)).lines()
                    .collect(Collectors.joining("\n"));
            yangModelFiles.add(
                    new YangModelInput(new ByteArrayYangInput(yangModuleContent.getBytes(StandardCharsets.UTF_8), fileName), conformanceType));
        }
        return yangModelFiles;
    }
}
