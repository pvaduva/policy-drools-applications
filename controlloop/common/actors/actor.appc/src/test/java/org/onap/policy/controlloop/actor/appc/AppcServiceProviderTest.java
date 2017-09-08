/*-
 * ============LICENSE_START=======================================================
 * AppcServiceProviderTest
 * ================================================================================
 * Copyright (C) 2017 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.controlloop.actor.appc;

import static org.junit.Assert.*;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.junit.Test;
import org.onap.policy.appc.Request;
import org.onap.policy.appc.util.Serialization;
import org.onap.policy.controlloop.ControlLoopEventStatus;
import org.onap.policy.controlloop.ControlLoopOperation;
import org.onap.policy.controlloop.ControlLoopTargetType;
import org.onap.policy.controlloop.VirtualControlLoopEvent;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.controlloop.policy.Target;
import org.onap.policy.controlloop.policy.TargetType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppcServiceProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(AppcServiceProviderTest.class);
    
    private static VirtualControlLoopEvent onsetEvent;
    private static ControlLoopOperation operation;
    private static Policy policy;

    static {
        /* 
         * Construct an onset with an AAI subtag containing
         * generic-vnf.vnf-id and a target type of VM.
         */
        onsetEvent = new VirtualControlLoopEvent();
        onsetEvent.closedLoopControlName = "closedLoopControlName-Test";
        onsetEvent.requestID = UUID.randomUUID();
        onsetEvent.closedLoopEventClient = "tca.instance00001";
        onsetEvent.target_type = ControlLoopTargetType.VF;
        onsetEvent.target = "generic-vnf.vnf-id";
        onsetEvent.from = "DCAE";
        onsetEvent.closedLoopAlarmStart = Instant.now();
        onsetEvent.AAI = new HashMap<>();
        onsetEvent.AAI.put("generic-vnf.vnf-id", "fw0001vm001fw001");
        onsetEvent.closedLoopEventStatus = ControlLoopEventStatus.ONSET;

        /* Construct an operation with an APPC actor and ModifyConfig operation. */
        operation = new ControlLoopOperation();
        operation.actor = "APPC";
        operation.operation = "ModifyConfig";
        operation.target = "VM";
        operation.end = Instant.now();
        operation.subRequestId = "1";

        /* Construct a policy specifying to modify configuration. */
        policy = new Policy();
        policy.setName("Modify Packet Generation Config");
        policy.setDescription("Upon getting the trigger event, modify packet gen config");
        policy.setActor("APPC");
        policy.setTarget(new Target(TargetType.VM));
        policy.setRecipe("ModifyConfig");
        policy.setPayload(null);
        policy.setRetry(2);
        policy.setTimeout(300);

    }

    @Test
    public void constructModifyConfigRequestTest() {
        
        Request appcRequest = APPCActorServiceProvider.constructRequest(onsetEvent, operation, policy);
        
        /* The service provider must return a non null APPC request */
        assertNotNull(appcRequest);

        /* A common header is required and cannot be null */
        assertNotNull(appcRequest.getCommonHeader());
        assertEquals(appcRequest.getCommonHeader().RequestID, onsetEvent.requestID);

        /* An action is required and cannot be null */
        assertNotNull(appcRequest.Action);
        assertEquals(appcRequest.Action, "ModifyConfig");

        /* A payload is required and cannot be null */
        assertNotNull(appcRequest.getPayload());
        assertTrue(appcRequest.getPayload().containsKey("generic-vnf.vnf-id"));
        assertTrue(appcRequest.getPayload().containsKey("pg-streams"));

        logger.debug("APPC Request: \n" + appcRequest.toString());
        
        /* Print out request as json to make sure serialization works */
        String jsonRequest = Serialization.gsonPretty.toJson(appcRequest);
        logger.debug("JSON Output: \n" + jsonRequest);
        
        /* The JSON string must contain the following fields */
        assertTrue(jsonRequest.contains("CommonHeader"));
        assertTrue(jsonRequest.contains("Action"));
        assertTrue(jsonRequest.contains("ModifyConfig"));
        assertTrue(jsonRequest.contains("Payload"));
        assertTrue(jsonRequest.contains("generic-vnf.vnf-id"));
        assertTrue(jsonRequest.contains("pg-streams"));
    }

}