/*-
 * ============LICENSE_START=======================================================
 * AppcServiceProviderTest
 * ================================================================================
 * Copyright (C) 2017-2018 AT&T Intellectual Property. All rights reserved.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.Instant;
import java.util.HashMap;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.appc.Request;
import org.onap.policy.appc.Response;
import org.onap.policy.appc.ResponseCode;
import org.onap.policy.appc.util.Serialization;
import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.controlloop.ControlLoopEventStatus;
import org.onap.policy.controlloop.ControlLoopOperation;
import org.onap.policy.controlloop.ControlLoopTargetType;
import org.onap.policy.controlloop.VirtualControlLoopEvent;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.controlloop.policy.Target;
import org.onap.policy.controlloop.policy.TargetType;
import org.onap.policy.drools.system.PolicyEngine;
import org.onap.policy.simulators.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppcServiceProviderTest {

    private static final Logger logger = LoggerFactory.getLogger(AppcServiceProviderTest.class);

    private static final VirtualControlLoopEvent onsetEvent;
    private static final ControlLoopOperation operation;
    private static final Policy policy;

    static {
        /*
         * Construct an onset with an AAI subtag containing generic-vnf.vnf-id and a target type of
         * VM.
         */
        onsetEvent = new VirtualControlLoopEvent();
        onsetEvent.setClosedLoopControlName("closedLoopControlName-Test");
        onsetEvent.setRequestId(UUID.randomUUID());
        onsetEvent.setClosedLoopEventClient("tca.instance00001");
        onsetEvent.setTargetType(ControlLoopTargetType.VNF);
        onsetEvent.setTarget("generic-vnf.vnf-name");
        onsetEvent.setFrom("DCAE");
        onsetEvent.setClosedLoopAlarmStart(Instant.now());
        onsetEvent.setAai(new HashMap<>());
        onsetEvent.getAai().put("generic-vnf.vnf-name", "fw0001vm001fw001");
        onsetEvent.setClosedLoopEventStatus(ControlLoopEventStatus.ONSET);

        /* Construct an operation with an APPC actor and ModifyConfig operation. */
        operation = new ControlLoopOperation();
        operation.setActor("APPC");
        operation.setOperation("ModifyConfig");
        operation.setTarget("VNF");
        operation.setEnd(Instant.now());
        operation.setSubRequestId("1");

        /* Construct a policy specifying to modify configuration. */
        policy = new Policy();
        policy.setName("Modify Packet Generation Config");
        policy.setDescription("Upon getting the trigger event, modify packet gen config");
        policy.setActor("APPC");
        policy.setTarget(new Target(TargetType.VNF));
        policy.getTarget().setResourceID("Eace933104d443b496b8.nodes.heat.vpg");
        policy.setRecipe("ModifyConfig");
        policy.setPayload(null);
        policy.setRetry(2);
        policy.setTimeout(300);

        /* Set environment properties */
        PolicyEngine.manager.setEnvironmentProperty("aai.url", "http://localhost:6666");
        PolicyEngine.manager.setEnvironmentProperty("aai.username", "AAI");
        PolicyEngine.manager.setEnvironmentProperty("aai.password", "AAI");

    }

    /**
     * Set up before test class.
     */
    @BeforeClass
    public static void setUpSimulator() {
        try {
            Util.buildAaiSim();
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * Tear down after test class.
     */
    @AfterClass
    public static void tearDownSimulator() {
        HttpServletServer.factory.destroy();
    }

    @Test
    public void constructModifyConfigRequestTest() {

        Request appcRequest;
        appcRequest = APPCActorServiceProvider.constructRequest(onsetEvent, operation, policy, "vnf01");

        /* The service provider must return a non null APPC request */
        assertNotNull(appcRequest);

        /* A common header is required and cannot be null */
        assertNotNull(appcRequest.getCommonHeader());
        assertEquals(appcRequest.getCommonHeader().getRequestId(), onsetEvent.getRequestId());

        /* An action is required and cannot be null */
        assertNotNull(appcRequest.getAction());
        assertEquals("ModifyConfig", appcRequest.getAction());

        /* A payload is required and cannot be null */
        assertNotNull(appcRequest.getPayload());
        assertTrue(appcRequest.getPayload().containsKey("generic-vnf.vnf-id"));
        assertNotNull(appcRequest.getPayload().get("generic-vnf.vnf-id"));
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

        Response appcResponse = new Response(appcRequest);
        appcResponse.getStatus().setCode(ResponseCode.SUCCESS.getValue());
        appcResponse.getStatus().setDescription("AppC success");
        /* Print out request as json to make sure serialization works */
        String jsonResponse = Serialization.gsonPretty.toJson(appcResponse);
        logger.debug("JSON Output: \n" + jsonResponse);
    }

    @Test
    public void testMethods() {
        APPCActorServiceProvider sp = new APPCActorServiceProvider();

        assertEquals("APPC", sp.actor());
        assertEquals(4, sp.recipes().size());
        assertEquals("VM", sp.recipeTargets("Restart").get(0));
        assertEquals(0, sp.recipePayloads("Restart").size());
    }
}
