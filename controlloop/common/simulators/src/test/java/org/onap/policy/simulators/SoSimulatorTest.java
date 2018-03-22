/*-
 * ============LICENSE_START=======================================================
 * simulators
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

package org.onap.policy.simulators;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.UUID;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.onap.policy.drools.http.server.HttpServletServer;
import org.onap.policy.drools.utils.LoggerUtil;
import org.onap.policy.rest.RESTManager;
import org.onap.policy.rest.RESTManager.Pair;
import org.onap.policy.so.SOCloudConfiguration;
import org.onap.policy.so.SOModelInfo;
import org.onap.policy.so.SORelatedInstance;
import org.onap.policy.so.SORelatedInstanceListElement;
import org.onap.policy.so.SORequest;
import org.onap.policy.so.SORequestDetails;
import org.onap.policy.so.SORequestInfo;
import org.onap.policy.so.SORequestParameters;
import org.onap.policy.so.SOResponse;
import org.onap.policy.so.util.Serialization;

public class SoSimulatorTest {

    /**
     * Set up test class.
     */
    @BeforeClass
    public static void setUpSimulator() {
        LoggerUtil.setLevel("ROOT", "INFO");
        LoggerUtil.setLevel("org.eclipse.jetty", "WARN");
        try {
            Util.buildSoSim();
        } catch (final Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownSimulator() {
        HttpServletServer.factory.destroy();
    }

    /**
     * Create dummy SO request for TestResponse() junit.
     */
    private SORequest createTestRequest() {

        // Construct SO Request
        final SORequest request = new SORequest();
        request.setRequestId(UUID.randomUUID());
        request.setRequestDetails(new SORequestDetails());
        request.getRequestDetails().setModelInfo(new SOModelInfo());
        request.getRequestDetails().setCloudConfiguration(new SOCloudConfiguration());
        request.getRequestDetails().setRequestInfo(new SORequestInfo());
        request.getRequestDetails().setRequestParameters(new SORequestParameters());
        request.getRequestDetails().getRequestParameters().setUserParams(null);
        //
        // cloudConfiguration
        //
        request.getRequestDetails().getCloudConfiguration().setLcpCloudRegionId("DFW");
        request.getRequestDetails().getCloudConfiguration().setTenantId("1015548");
        //
        // modelInfo
        //
        request.getRequestDetails().getModelInfo().setModelType("vfModule");
        request.getRequestDetails().getModelInfo().setModelInvariantId("f32568ec-2f1c-458a-864b-0593d53d141a");
        request.getRequestDetails().getModelInfo().setModelVersionId("69615025-879d-4f0d-afe3-b7d1a7eeed1f");
        request.getRequestDetails().getModelInfo().setModelName("C15ce9e1E9144c8fB8bb..dnsscaling..module-1");
        request.getRequestDetails().getModelInfo().setModelVersion("1.0");
        //
        // requestInfo
        //
        request.getRequestDetails().getRequestInfo()
                .setInstanceName("vDNS_Ete_Named90e1ab3-dcd5-4877-9edb-eadfc84e32c8");
        request.getRequestDetails().getRequestInfo().setSource("POLICY");
        request.getRequestDetails().getRequestInfo().setSuppressRollback(false);
        request.getRequestDetails().getRequestInfo().setRequestorId("policy");
        //
        // relatedInstanceList
        //
        final SORelatedInstanceListElement relatedInstanceListElement1 = new SORelatedInstanceListElement();
        final SORelatedInstanceListElement relatedInstanceListElement2 = new SORelatedInstanceListElement();
        relatedInstanceListElement1.setRelatedInstance(new SORelatedInstance());
        relatedInstanceListElement2.setRelatedInstance(new SORelatedInstance());
        //
        relatedInstanceListElement1.getRelatedInstance().setInstanceId("cf8426a6-0b53-4e3d-bfa6-4b2f4d5913a5");
        relatedInstanceListElement1.getRelatedInstance().setModelInfo(new SOModelInfo());
        relatedInstanceListElement1.getRelatedInstance().getModelInfo().setModelType("service");
        relatedInstanceListElement1.getRelatedInstance().getModelInfo()
                .setModelInvariantId("4fcbc1c0-7793-46d8-8aa1-fa1c2ed9ec7b");
        relatedInstanceListElement1.getRelatedInstance().getModelInfo()
                .setModelVersionId("5c996219-b2e2-4c76-9b43-7e8672a33c1d");
        relatedInstanceListElement1.getRelatedInstance().getModelInfo().setModelName("8330e932-2a23-4943-8606");
        relatedInstanceListElement1.getRelatedInstance().getModelInfo().setModelVersion("1.0");
        //
        relatedInstanceListElement2.getRelatedInstance().setInstanceId("594e2fe0-48b8-41ff-82e2-3d4bab69b192");
        relatedInstanceListElement2.getRelatedInstance().setModelInfo(new SOModelInfo());
        relatedInstanceListElement2.getRelatedInstance().getModelInfo().setModelType("vnf");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                .setModelInvariantId("033a32ed-aa65-4764-a736-36f2942f1aa0");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                .setModelVersionId("d4d072dc-4e21-4a03-9524-628985819a8e");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo().setModelName("c15ce9e1-e914-4c8f-b8bb");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo().setModelVersion("1");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                .setModelCustomizationName("c15ce9e1-e914-4c8f-b8bb 1");
        //
        request.getRequestDetails().getRelatedInstanceList().add(relatedInstanceListElement1);
        request.getRequestDetails().getRelatedInstanceList().add(relatedInstanceListElement2);

        return request;
    }

    @Test
    public void testResponse() {
        final String request = Serialization.gsonPretty.toJson(this.createTestRequest());
        final Pair<Integer, String> httpDetails =
                new RESTManager().post("http://localhost:6667/serviceInstances/v5/12345/vnfs/12345/vfModules",
                        "username", "password", new HashMap<>(), "application/json", request);
        assertNotNull(httpDetails);
        final SOResponse response = Serialization.gsonPretty.fromJson(httpDetails.b, SOResponse.class);
        assertNotNull(response);
    }
}
