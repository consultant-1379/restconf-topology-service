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

package com.ericsson.oss.services.restconf.topologyservice.resources;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps;

/**
 * Parse and populate TCIM data in stubbed DPS.
 */
public class TcimFileDynamicPopulator {
    private final RuntimeConfigurableDps runtimeDps;
    private final Map<String, ManagedObject> managedObjectMap = new HashMap<>();

    public TcimFileDynamicPopulator(final RuntimeConfigurableDps runtimeDps) {
        this.runtimeDps = runtimeDps;
    }

    public void parseFile(final String filePath) {
        try {
            final File file = new File(filePath);
            final Scanner scanner = new Scanner(file);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (line.startsWith("FDN : ") && scanner.hasNextLine()) {
                    final MoDto moDto = new MoDto();
                    moDto.fdnValue = line.replaceAll("FDN : ", "").replaceAll("\"", "");
                    moDto.attributes = new LinkedHashMap<>();
                    do {
                        line = scanner.nextLine();
                        if (line.isEmpty()) {
                            break;
                        }
                        final String[] tokens = line.split(" : ");
                        String value = tokens[1].replaceAll("<empty>", "");
                        if (value.startsWith("[")) {
                            final String[] values = value.split("\", \"");
                            final List<String> listValues = new ArrayList<>();
                            for (final String item : values) {
                                listValues.add(item.replaceAll("\\[|\\]", "").replaceAll("\"", ""));
                            }
                            moDto.attributes.put(tokens[0].replaceAll("\"", ""), listValues);
                        } else {
                            value = value.replaceAll("\"", "");
                            moDto.attributes.put(tokens[0].replaceAll("\"", ""), value);
                        }
                    } while (scanner.hasNextLine());
                    final ManagedObject parentMo = managedObjectMap.get(moDto.fdnValue);
                    final ManagedObject mo;
                    if (parentMo != null) {
                        mo = runtimeDps.addManagedObject().withFdn(moDto.fdnValue).addAttributes(moDto.attributes).namespace("OSS_TCIM")
                                .version("1.0.0").parent(parentMo).build();
                    } else {
                        mo = runtimeDps.addManagedObject().withFdn(moDto.fdnValue).addAttributes(moDto.attributes).namespace("OSS_TCIM")
                                .version("1.0.0").build();
                    }
                    managedObjectMap.put(moDto.fdnValue, mo);
                    // System.out.println(mo.getFdn());
                }
            }
            scanner.close();
        } catch (final FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Managed Object DTO.
     */
    static class MoDto {
        public String fdnValue;
        public Map<String, Object> attributes;
    }
}