/*-
 * ============LICENSE_START=======================================================
 * eventmanager
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
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

package org.onap.policy.controlloop;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.onap.policy.controlloop.impl.ControlLoopPublisherJUnitImpl;

public class ControlLoopPublisherTest {
    @Test
    public void testControlLoopPublisher() throws ControlLoopException {
        ControlLoopPublisher publisher =
                new ControlLoopPublisher.Factory().buildLogger(ControlLoopPublisherJUnitImpl.class.getName());
        assertNotNull(publisher);

        assertThatThrownBy(() -> publisher.publish(Double.valueOf(3)))
            .hasMessage("publish() method is not implemented on "
                            + "org.onap.policy.controlloop.impl.ControlLoopPublisherJUnitImpl");

        assertThatThrownBy(() -> new ControlLoopPublisher.Factory().buildLogger("java.lang.String"))
            .hasMessage("Cannot load class java.lang.String as a control loop publisher");
    }
}
