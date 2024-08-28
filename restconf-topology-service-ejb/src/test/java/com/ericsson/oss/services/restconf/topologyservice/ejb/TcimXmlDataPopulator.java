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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import com.ericsson.oss.itpf.datalayer.dps.persistence.ManagedObject;
import com.ericsson.oss.itpf.datalayer.dps.stub.RuntimeConfigurableDps;

/**
 * Parse and populate TCIM data in stubbed DPS.
 */
public class TcimXmlDataPopulator extends DefaultHandler {

    private StringBuilder currentValue = new StringBuilder();
    private RuntimeConfigurableDps runtimeDps;
    private Stack<ManagedObject> managedObjectStack = new Stack<>();
    private MoDto moDto;

    public TcimXmlDataPopulator(final RuntimeConfigurableDps runtimeDps) {
        this.runtimeDps = runtimeDps;
    }

    @Override
    public void startDocument() {}

    @Override
    public void endDocument() {}

    @Override
    public void startElement(final String uri, final String localName, final String qName, final Attributes attributes) {
        currentValue.setLength(0);
        if (qName.endsWith(":VsDataContainer")) {
            moDto = new MoDto();
            moDto.fdnValue = attributes.getValue("id");
        }

        if (moDto != null && qName.endsWith(":" + moDto.elementType)) {
            moDto.attributes = new HashMap<>();
        }
    }

    @Override
    public void endElement(final String uri, final String localName, final String qName) {
        if (moDto != null) {
            if (qName.endsWith(":vsDataType")) {
                final String value = currentValue.toString();
                moDto.elementType = value;
                moDto.moType = value.replaceAll("vsData", "");
            }

            if (moDto.attributes != null) {
                final String value = currentValue.toString();
                if (!qName.contains(":vsData")) {
                    moDto.attributes.put(qName.replaceAll("es:", ""), value);
                }
            }

            if (qName.endsWith(":" + moDto.elementType)) {
                String fdn = "";
                ManagedObject parentMo = null;
                if (!managedObjectStack.isEmpty()) {
                    parentMo = managedObjectStack.peek();
                    final String parentFdn = parentMo.getFdn();
                    if (parentFdn != null && !parentFdn.isEmpty()) {
                        fdn = parentFdn + ",";
                    }
                }
                //System.out.println(moDto.attributes);
                fdn += moDto.moType + "=" + moDto.fdnValue;
                final ManagedObject mo = runtimeDps.addManagedObject().withFdn(fdn).addAttributes(moDto.attributes).namespace("OSS_TCIM")
                        .version("1.0.0").parent(parentMo).build();
                managedObjectStack.push(mo);
                //System.out.println(mo.getFdn());
            }

            if (qName.endsWith(":VsDataContainer")) {
                if (!managedObjectStack.isEmpty()) {
                    managedObjectStack.pop();
                }
            }
        }
    }

    @Override
    public void characters(final char[] ch, final int start, final int length) {
        currentValue.append(ch, start, length);
    }

    static class MoDto {
        public String fdnValue;
        public String moType;
        public String elementType;
        public Map<String, Object> attributes;
    }
}
