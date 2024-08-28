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

package com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.provider;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.mediation.modeling.yangtools.parser.YangDeviceModel;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.YangModelInput;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.AbstractStatement;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.StatementModuleAndName;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.CY;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YDefault;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YPath;
import com.ericsson.oss.mediation.modeling.yangtools.parser.model.statements.yang.YType;
import com.ericsson.oss.services.restconf.topologyservice.api.dto.RestconfResourceUri;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfException;
import com.ericsson.oss.services.restconf.topologyservice.api.exception.RestconfYangHierarchyValidationException;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.AbstractYangDataNode.YangDataNodeBuilder;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataContainer;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeaf;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataLeafList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataList;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataNode;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangDataRoot;
import com.ericsson.oss.services.restconf.topologyservice.api.yang.dto.YangModelSet;
import com.ericsson.oss.services.restconf.topologyservice.ejb.interceptor.binding.TotalTimeTaken;
import com.ericsson.oss.services.restconf.topologyservice.ejb.listener.RestconfConfigurationListener;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.data.validator.RestconfYangDataNodeValidator;
import com.ericsson.oss.services.restconf.topologyservice.ejb.yang.model.parser.RestconfYangModulesParser;

/**
 * Restconf yang data node provider.
 */
@ApplicationScoped
@SuppressWarnings("java:S2629")
public class RestconfYangDataNodeTemplateProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestconfYangDataNodeTemplateProvider.class);
    private static final Set<StatementModuleAndName> YANG_DATA_CONSTRUCTS = new HashSet<>(Arrays.asList(CY.STMT_CONTAINER, CY.STMT_LIST));
    private static final String SLASH_SYMBOL = "/";
    private static final String COLON_SYMBOL = ":";

    @Inject
    private RestconfYangModulesParser restconfYangModulesParser;

    @Inject
    private RestconfYangDataNodeValidator restconfYangDataNodeValidator;

    @Inject
    private RestconfConfigurationListener restconfConfigurationListener;

    @TotalTimeTaken(task = "Generate yang data node template")
    public void generateAndGetTemplateDataNode(final RestconfResourceUri restconfResourceUri, final YangDataNode yangDataRootNode)
            throws RestconfException {
        try {
            final YangDeviceModel yangDeviceModel = restconfYangModulesParser.getYangDeviceModel();
            final Iterator<RestconfResourceUri.Step> stepIterator = restconfResourceUri.getSteps().iterator();
            if (stepIterator.hasNext()) {
                final RestconfResourceUri.Step step = stepIterator.next();
                for (final YangModelInput yangModule : yangDeviceModel.getModuleRegistry().getAllValidYangModelInputs()) {
                    final AbstractStatement moduleOrSubmodule = yangModule.getYangModelRoot().getModuleOrSubmodule();
                    if (isEqualsModule(step, moduleOrSubmodule)) {
                        generateYangDataNodeTemplate(moduleOrSubmodule, step, yangDataRootNode);
                        return;
                    }
                }
                throw new RestconfYangHierarchyValidationException("Couldn't find root module [" + step.getQName() + "]");
            }
        } catch (final RestconfYangHierarchyValidationException exception) {
            throw exception;
        } catch (final Exception exception) {
            throw new RestconfYangHierarchyValidationException(exception);
        }
        throw new RestconfYangHierarchyValidationException("Couldn't generate yang data for resource uri " + restconfResourceUri);
    }

    private boolean isEqualsModule(final RestconfResourceUri.Step step, final AbstractStatement moduleOrSubmodule) {
        return step.getModuleName().equals(getModuleName(moduleOrSubmodule));
    }

    @SuppressWarnings("java:S3776")
    private void generateYangDataNodeTemplate(final AbstractStatement yangStatement, final RestconfResourceUri.Step step,
                                              final YangDataNode yangDataNode) throws RestconfYangHierarchyValidationException {
        final AbstractStatement yangChildDataConstructStatement = getChildDataConstructStatement(yangStatement, step);
        if (yangChildDataConstructStatement != null) {
            if (!isEqualsModule(step, yangChildDataConstructStatement)) {
                throw new RestconfYangHierarchyValidationException("Invalid yang module hierarchy at step: [" + step.getQName() + "]");
            }
            final YangDataNode yangChildDataNode = createAndGetYangDataNode(yangChildDataConstructStatement);
            if (yangChildDataNode instanceof YangDataLeaf) {
                step.setType(RestconfResourceUri.Step.Type.LEAF);
            } else if (yangChildDataNode instanceof YangDataLeafList) {
                step.setType(RestconfResourceUri.Step.Type.LEAF_LIST);
            }
            restconfYangDataNodeValidator.validateYangDataNode(step, yangChildDataNode);
            if (yangDataNode instanceof YangDataRoot) {
                yangDataNode.addDataNode(yangChildDataNode);
            } else {
                if (yangChildDataNode instanceof YangDataList) {
                    ((YangDataList) yangChildDataNode).setKeyValues(Arrays.asList(step.getKeyValues()));
                }
                ((AbstractYangDataNode) yangDataNode).getModelSet().addDataNode(yangChildDataNode);
            }
            if (step.hasNext()) {
                generateYangDataNodeTemplate(yangChildDataConstructStatement, step.getNextStep(), yangChildDataNode);
            } else {
                final AtomicInteger hierarchyLevel = new AtomicInteger(0);
                traverseAndPopulateAllChildYangDataNodes(yangChildDataConstructStatement, yangChildDataNode, hierarchyLevel);
            }
        } else {
            final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
            if (yangModelSet != null) {
                final Set<YangDataNode> dataNodes = ((AbstractYangDataNode) yangDataNode).getModelSet().getDataNodes();
                boolean foundLeaf = false;
                for (final YangDataNode childNode : dataNodes) {
                    if (childNode instanceof YangDataLeaf || childNode instanceof YangDataLeafList) {
                        final String qName = childNode.getModule() + ":" + childNode.getNodeName();
                        if (qName.equals(step.getQName())) {
                            foundLeaf = true;
                            if (step.getKeyValues().length != 0) {
                                throw new RestconfYangHierarchyValidationException("Unexpected values for a leaf at step: [" + step.getQName() + "]");
                            }
                            break;
                        }
                    }
                }
                if (!foundLeaf) {
                    throw new RestconfYangHierarchyValidationException("Invalid yang module hierarchy at step: [" + step.getQName() + "]");
                }
            } else {
                throw new RestconfYangHierarchyValidationException("Invalid yang module hierarchy at step: [" + step.getQName() + "]");
            }
        }
    }

    private void traverseAndPopulateAllChildYangDataNodes(final AbstractStatement yangStatement, final YangDataNode yangDataNode,
                                                          final AtomicInteger hierarchyLevel) {
        hierarchyLevel.incrementAndGet();
        for (final AbstractStatement yangChildStatement : yangStatement.getChildren(YANG_DATA_CONSTRUCTS)) {
            final YangDataNode yangChildDataNode = createAndGetYangDataNode(yangChildStatement);
            ((AbstractYangDataNode) yangDataNode).getModelSet().addDataNode(yangChildDataNode);
            traverseAndPopulateAllChildYangDataNodes(yangChildStatement, yangChildDataNode, hierarchyLevel);
        }
    }

    private AbstractStatement getChildDataConstructStatement(final AbstractStatement yangStatement, final RestconfResourceUri.Step step) {
        for (final AbstractStatement yangChildStatement : yangStatement.getChildren(YANG_DATA_CONSTRUCTS)) {
            if (getDomElementValue(yangChildStatement).equals(step.getIdentifier())) {
                return yangChildStatement;
            }
        }
        return null;
    }

    private YangDataNode createAndGetYangDataNode(final AbstractStatement yangStatement) {
        return yangStatement.is(CY.STMT_LIST) ? createAndGetListDataNode(yangStatement) : createAndGetContainerDataNode(yangStatement);
    }

    private YangDataNode createAndGetContainerDataNode(final AbstractStatement yangStatement) {
        final YangDataContainer yangDataContainer = createAndGetYangDataNodeBuilder(yangStatement).container();
        findAndAddLeafDataNodes(yangStatement, yangDataContainer);
        return yangDataContainer;
    }

    private YangDataNode createAndGetListDataNode(final AbstractStatement yangStatement) {
        final YangDataList yangDataList = createAndGetYangDataNodeBuilder(yangStatement).list();
        findAndAddLeafDataNodes(yangStatement, yangDataList);

        for (final AbstractStatement keyStatement : yangStatement.getChildren(CY.STMT_KEY)) {
            markKeyLeafDataNodes(yangDataList, keyStatement);
        }
        return yangDataList;
    }

    private void findAndAddLeafDataNodes(final AbstractStatement yangStatement, final YangDataNode yangDataNode) {
        final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
        for (final AbstractStatement leafStatement : yangStatement.getChildren(CY.STMT_LEAF)) {
            final YangDataNode leafDataNode = createAndGetLeafDataNode(leafStatement);
            yangModelSet.addDataNode(leafDataNode);
        }

        for (final AbstractStatement leafListStatement : yangStatement.getChildren(CY.STMT_LEAF_LIST)) {
            final YangDataNode leafListDataNode = createAndGetLeafListDataNode(leafListStatement);
            yangModelSet.addDataNode(leafListDataNode);
        }
    }

    private YangDataNode createAndGetLeafListDataNode(final AbstractStatement leafListStatement) {
        final YangDataLeafList yangDataLeafList = createAndGetYangDataNodeBuilder(leafListStatement).leafList();
        for (final AbstractStatement childStatement : leafListStatement.getChildStatements()) {
            if (childStatement instanceof YType) {
                yangDataLeafList.setType(getDomElementValue(childStatement));
            }
        }
        return yangDataLeafList;
    }

    private void markKeyLeafDataNodes(final YangDataNode yangDataNode, final AbstractStatement keyStatement) {
        final YangModelSet yangModelSet = ((AbstractYangDataNode) yangDataNode).getModelSet();
        final List<String> keys = getKeys(keyStatement);
        for (final YangDataNode dataNode : yangModelSet.getDataNodes()) {
            if (dataNode instanceof YangDataLeaf && keys.contains(dataNode.getNodeName())) {
                ((YangDataLeaf) dataNode).setKey(true);
            }
        }
    }

    private YangDataNode createAndGetLeafDataNode(final AbstractStatement leafStatement) {
        final YangDataLeaf yangDataLeaf = createAndGetYangDataNodeBuilder(leafStatement).leaf();
        for (final AbstractStatement childStatement : leafStatement.getChildStatements()) {
            if (childStatement instanceof YType) {
                yangDataLeaf.setType(getDomElementValue(childStatement));
                for (final AbstractStatement typeChildStatement : childStatement.getChildStatements()) {
                    if (typeChildStatement instanceof YPath) {
                        yangDataLeaf.setPath(replacePrefixWithModuleNames(typeChildStatement, getDomElementValue(typeChildStatement)));
                    }
                }
            } else if (childStatement instanceof YDefault) {
                yangDataLeaf.setDefaultValue(getDomElementValue(childStatement));
            }
        }
        return yangDataLeaf;
    }

    private YangDataNodeBuilder createAndGetYangDataNodeBuilder(final AbstractStatement yangStatement) {
        return new YangDataNodeBuilder(getNamespace(yangStatement), getModuleName(yangStatement), getDomElementValue(yangStatement));
    }

    private String getModuleName(final AbstractStatement yangStatement) {
        final String[] tokens = yangStatement.getEffectiveNamespace().split(COLON_SYMBOL);
        return tokens[tokens.length - 1];
    }

    private String getDomElementValue(final AbstractStatement yangStatement) {
        return yangStatement.getDomElement().getValue();
    }

    private String getNamespace(final AbstractStatement yangStatement) {
        return yangStatement.getEffectiveNamespace();
    }

    private List<String> getKeys(final AbstractStatement yangStatement) {
        return Arrays.asList(getDomElementValue(yangStatement).split(" "));
    }

    @SuppressWarnings("java:S3776")
    private String replacePrefixWithModuleNames(final AbstractStatement yangStatement, final String path) {
        StringBuilder slashTokenBuilder = new StringBuilder();
        final List<String> pathElements = new ArrayList<>();
        boolean predicate = false;
        for (final char token : path.toCharArray()) {
            if (token == '/' && !predicate) {
                pathElements.add(slashTokenBuilder.toString());
                slashTokenBuilder = new StringBuilder();
            } else if (token == '[') {
                predicate = true;
            } else if (token == ']') {
                predicate = false;
            } else {
                if (!predicate) {
                    slashTokenBuilder.append(token);
                }
            }
        }
        pathElements.add(slashTokenBuilder.toString());

        final StringBuilder stringBuilder = new StringBuilder();
        for (final String pathElement : pathElements) {
            LOGGER.trace("Check and update path element: {}", pathElement);
            if ("..".equals(pathElement)) {
                stringBuilder.append(pathElement).append(SLASH_SYMBOL);
            } else if (pathElement.contains(COLON_SYMBOL)) {
                final String[] colonTokens = pathElement.split(COLON_SYMBOL);
                final String moduleName = yangStatement.getPrefixResolver().getModuleForPrefix(colonTokens[0]).getModuleName();
                stringBuilder.append(moduleName).append(COLON_SYMBOL).append(colonTokens[1]).append(SLASH_SYMBOL);
            } else if (!pathElement.isEmpty()) {
                final String moduleName = getModuleName(yangStatement);
                if (moduleName != null && !moduleName.isEmpty()) {
                    stringBuilder.append(moduleName).append(COLON_SYMBOL).append(pathElement).append(SLASH_SYMBOL);
                } else {
                    stringBuilder.append(pathElement).append(SLASH_SYMBOL);
                }
            }
        }
        LOGGER.trace("Final xpath generated is {}", stringBuilder);
        return stringBuilder.substring(0, stringBuilder.toString().length() - 1);
    }
}