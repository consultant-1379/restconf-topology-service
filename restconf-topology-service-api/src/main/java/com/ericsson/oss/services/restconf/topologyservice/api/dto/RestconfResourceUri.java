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

package com.ericsson.oss.services.restconf.topologyservice.api.dto;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Restconf HTTP request path DTO.
 */
public final class RestconfResourceUri {
    private final String resourceUri;
    private final List<Step> steps = new LinkedList<>();

    public RestconfResourceUri(final String resourceUri) {
        this.resourceUri = resourceUri;
    }

    public void addListInstanceStep(final String originalStep, final String[] groups) {
        addListInstanceStep(originalStep, groups[0], groups[1], groups[2]);
    }

    public void addListInstanceStep(final String originalStep, final String moduleName, final String identifier, final String keyValues) {
        final Step prevStep = getLastStep();
        final Step step = new Step(originalStep, Step.Type.LIST_INSTANCE, checkAndGetModuleName(moduleName), identifier, keyValues.split(","));
        step.setPrevStep(prevStep);
        if (prevStep != null) {
            prevStep.setNextStep(step);
        }
        steps.add(step);
    }

    public void addApiIdentifierStep(final String originalStep, final String[] groups) {
        addApiIdentifierStep(originalStep, groups[0], groups[1]);
    }

    public void addApiIdentifierStep(final String originalStep, final String moduleName, final String identifier) {
        final Step prevStep = getLastStep();
        final Step step = new Step(originalStep, Step.Type.API_IDENTIFIER, checkAndGetModuleName(moduleName), identifier, new String[0]);
        step.setPrevStep(prevStep);
        if (prevStep != null) {
            prevStep.setNextStep(step);
        }
        steps.add(step);
    }

    public List<Step> getSteps() {
        return steps; //NOSONAR
    }

    public Step getStep(final int stepIndex) {
        if (!steps.isEmpty()) {
            return steps.get(stepIndex);
        }
        return null;
    }

    public boolean isEmpty() {
        return steps.isEmpty();
    }

    public Step getLastStep() {
        if (!steps.isEmpty()) {
            final Optional<Step> step = steps.stream().skip(steps.size() - 1L).findFirst();
            return step.isPresent() ? step.get() : null;
        }
        return null;
    }

    private String checkAndGetModuleName(final String moduleName) {
        if (moduleName != null) {
            return moduleName;
        }
        final Step lastStep = getLastStep();
        if (lastStep != null) {
            return lastStep.getModuleName();
        }
        return "";
    }

    @Override
    public String toString() {
        return resourceUri;
    }

    /**
     * Resource URI step.
     */
    public static class Step {
        /**
         * Step types.
         */
        public enum Type {
            API_IDENTIFIER, LIST_INSTANCE, LEAF, LEAF_LIST
        }

        String originalStep;
        Type type;
        String moduleName;
        String identifier;
        String[] keyValues;
        Step prevStep;
        Step nextStep;

        public Step(final String originalStep, final Type type, final String moduleName, final String identifier, final String[] keyValues) {
            this.originalStep = originalStep;
            this.type = type;
            this.moduleName = moduleName;
            this.identifier = identifier;
            this.keyValues = keyValues == null ? new String[0] : keyValues;
            this.prevStep = null;
            this.nextStep = null;
        }

        public String getOriginalStep() {
            return originalStep;
        }

        public boolean isApiIdentifier() {
            return type == Step.Type.API_IDENTIFIER;
        }

        public boolean isListInstance() {
            return type == Step.Type.LIST_INSTANCE;
        }

        public boolean isLeaf() {
            return type == Step.Type.LEAF;
        }

        public boolean isLeafList() {
            return type == Step.Type.LEAF_LIST;
        }

        public void setType(final Type type) {
            this.type = type;
        }

        public String getModuleName() {
            return moduleName;
        }

        public String getIdentifier() {
            return identifier;
        }

        public String[] getKeyValues() {
            return keyValues;
        }

        public void setPrevStep(final Step prevStep) {
            this.prevStep = prevStep;
        }

        public void setNextStep(final Step nextStep) {
            this.nextStep = nextStep;
        }

        public Step getPrevStep() {
            return prevStep;
        }

        public Step getNextStep() {
            return nextStep;
        }

        public boolean hasNext() {
            return nextStep != null;
        }

        public String getQName() {
            return moduleName + ":" + identifier;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            final Step step = (Step) o;
            return Objects.equals(originalStep, step.originalStep) && type == step.type && Objects.equals(moduleName, step.moduleName)
                    && Objects.equals(identifier, step.identifier) && Arrays.equals(keyValues, step.keyValues);
        }

        @Override
        public int hashCode() {
            int result = Objects.hash(originalStep, type, moduleName, identifier);
            result = 31 * result + Arrays.hashCode(keyValues);
            return result;
        }

        @Override
        public String toString() {
            return "Step{" + "originalStep='" + originalStep + '\'' + ", type=" + type + ", moduleName='" + moduleName + '\'' + ", identifier='"
                    + identifier + '\'' + ", keyValues=" + Arrays.toString(keyValues) + '}';
        }
    }
}
