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

package com.ericsson.oss.services.restconf.topologyservice.ejb.listener;

import java.util.Set;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ericsson.oss.itpf.datalayer.dps.notification.DpsNotificationConfiguration;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.AttributeChangeData;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsAttributeChangedEvent;
import com.ericsson.oss.itpf.datalayer.dps.notification.event.DpsDataChangedEvent;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Consumes;
import com.ericsson.oss.services.restconf.topologyservice.ejb.holder.TransportCimNormalizedNodesLocalCache;

/**
 * TransportCIM dps notification listener.
 */
public class TransportCimNotificationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransportCimNotificationListener.class);
    private static final String NORMALIZED_STATE_ATTRIBUTE = "normalization-state";
    private static final String NORMALIZED_STATE = "NORMALIZED";

    private static final String ENDPOINT = DpsNotificationConfiguration.DPS_EVENT_NOTIFICATION_CHANNEL_URI;
    private static final String TCIM_FILTER = "namespace = 'OSS_TCIM' AND type ='Node'";

    @Inject
    private TransportCimNormalizedNodesLocalCache transportCimNormalizedNodesLocalCache;

    public void onNodeUpdatedEvent(@Observes @Consumes(endpoint = ENDPOINT, filter = TCIM_FILTER) final DpsDataChangedEvent event) {
        if (event instanceof DpsAttributeChangedEvent) {
            final DpsAttributeChangedEvent dpsAttributeChangedEvent = (DpsAttributeChangedEvent) event;
            final Set<AttributeChangeData> changedAttributes = dpsAttributeChangedEvent.getChangedAttributes();
            LOGGER.debug("Processing onNodeUpdatedEvent CM AVC. event.getChangedAttributes {}, " + "event.getFdn() {} .", changedAttributes,
                    event.getEventType());
            changeAttributeManagement(changedAttributes, event.getFdn());
        }
    }

    private void changeAttributeManagement(final Set<AttributeChangeData> changedAttributes, final String fdn) {
        for (final AttributeChangeData changedAttribute : changedAttributes) {
            if (changedAttribute.getName().equals(NORMALIZED_STATE_ATTRIBUTE)) {
                final String attOldValue = (String) changedAttribute.getOldValue();
                final String attNewValue = (changedAttribute.getNewValue() == null) ? "NULL" : (String) changedAttribute.getNewValue();
                LOGGER.debug("OLD VALUE = {}, NEW VALUE {}", attOldValue, attNewValue);
                final String eventNodeName = fdn.split("Node=")[1];
                if (NORMALIZED_STATE.equals(changedAttribute.getNewValue())) {
                    transportCimNormalizedNodesLocalCache.addNormalizedNode(eventNodeName);
                } else {
                    transportCimNormalizedNodesLocalCache.removeNode(eventNodeName);
                }
            }
        }
    }
}
