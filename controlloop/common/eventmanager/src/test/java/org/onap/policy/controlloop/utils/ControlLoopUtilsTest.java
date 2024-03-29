/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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

package org.onap.policy.controlloop.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import org.junit.Test;
import org.onap.policy.common.utils.coder.StandardCoder;
import org.onap.policy.controlloop.params.ControlLoopParams;
import org.onap.policy.models.tosca.authorative.concepts.ToscaPolicy;

public class ControlLoopUtilsTest {

    @Test
    public void toControlLoopParams() throws Exception {
        String policy =
            new String(Files.readAllBytes(Paths.get("src/test/resources/tosca-policy-operational-restart.json")));

        ToscaPolicy toscaPolicy = new StandardCoder().decode(policy, ToscaPolicy.class);
        ControlLoopParams params = ControlLoopUtils.toControlLoopParams(toscaPolicy);
        assertNotNull(params);
        assertNotNull(params.getClosedLoopControlName());
        assertEquals(toscaPolicy.getProperties().get("content"), params.getControlLoopYaml());
        assertEquals(toscaPolicy.getName(), params.getPolicyName());
        assertEquals(toscaPolicy.getVersion(), params.getPolicyVersion());
        assertEquals(toscaPolicy.getType() + ":" + toscaPolicy.getVersion(), params.getPolicyScope());

        assertNull(ControlLoopUtils.toControlLoopParams(null));

        Map<String, Object> properties = toscaPolicy.getProperties();
        toscaPolicy.setProperties(null);
        assertNull(ControlLoopUtils.toControlLoopParams(toscaPolicy));
        toscaPolicy.setProperties(properties);
    }
}