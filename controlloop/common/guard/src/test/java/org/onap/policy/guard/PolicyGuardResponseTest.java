/*-
 * ============LICENSE_START=======================================================
 * guard
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
 * ================================================================================
 * Modifications Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.guard;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.UUID;

import org.junit.Test;

public class PolicyGuardResponseTest {

    private static final String GET_BACK_HOME = "GetBackHome";
    private static final String BACK_HOME = "BackHome";

    @Test
    public void policyGuardResponseTest() {
        UUID requestId = UUID.randomUUID();

        assertNotNull(new PolicyGuardResponse(null, null, null));

        PolicyGuardResponse response = new PolicyGuardResponse(BACK_HOME, requestId, GET_BACK_HOME);

        response.setRequestId(requestId);
        assertEquals(requestId, response.getRequestId());

        response.setResult(BACK_HOME);
        assertEquals(BACK_HOME, response.getResult());

        response.setOperation(GET_BACK_HOME);
        assertEquals(GET_BACK_HOME, response.getOperation());

        assertEquals("PolicyGuardResponse [requestId=", response.toString().substring(0, 31));
    }
}
