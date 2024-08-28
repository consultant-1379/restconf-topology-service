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

package com.ericsson.oss.services.restconf.topologyservice.ejb;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps;

/**
 * Parse and populate TCIM data in stubbed DPS.
 */
public class TcimFileDynamicPopulator {
    private RuntimeConfigurableDps runtimeDps;
    private Map<String, ManagedObject> managedObjectMap = new HashMap<>();
    private MoDto moDto;

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
                    moDto = new MoDto();
                    moDto.fdnValue = line.replaceAll("FDN : ", "").replaceAll("\"", "");
                    moDto.attributes = new LinkedHashMap<>();
                    do {
                        line = scanner.nextLine();
                        if (line.isEmpty()) {
                            break;
                        }
                        final String[] tokens = line.split(" : ");
                        moDto.attributes.put(tokens[0].replaceAll("\"", ""), tokens[1].replaceAll("<empty>", "").replaceAll("\"", ""));
                    } while (scanner.hasNextLine());
                    final ManagedObject parentMo = managedObjectMap.get(moDto.fdnValue);
                    ManagedObject mo = null;
                    if (parentMo != null) {
                        mo = runtimeDps.addManagedObject().withFdn(moDto.fdnValue).addAttributes(moDto.attributes).namespace("OSS_TCIM")
                                .version("1.0.0").parent(parentMo).build();
                    } else {
                        mo = runtimeDps.addManagedObject().withFdn(moDto.fdnValue).addAttributes(moDto.attributes).namespace("OSS_TCIM")
                                .version("1.0.0").build();
                    }
                    managedObjectMap.put(moDto.fdnValue, mo);
                    //System.out.println(mo.getFdn());
                }
            }
            scanner.close();
        } catch (final FileNotFoundException exception) {
            exception.printStackTrace();
        }
    }

    static class MoDto {
        public String fdnValue;
        public Map<String, Object> attributes;
    }
}
