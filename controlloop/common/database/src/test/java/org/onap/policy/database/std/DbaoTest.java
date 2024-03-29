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

package org.onap.policy.database.std;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import org.junit.Test;
import org.onap.policy.database.operationshistory.Dbao;

public class DbaoTest {

    @Test
    public void test() {
        Dbao dao = new Dbao();

        dao.setActor("my-actor");
        dao.setClosedLoopName("cl-name");
        Date endDate = new Date();
        dao.setEndtime(endDate);
        dao.setId(100L);
        dao.setMessage("my-message");
        dao.setOperation("my-operation");
        dao.setOutcome("my-outcome");
        dao.setRequestId("my-request");
        Date startDate = new Date(endDate.getTime() - 1);
        dao.setStarttime(startDate);
        dao.setSubrequestId("my-sub");
        dao.setTarget("my-target");

        assertEquals("my-actor", dao.getActor());
        assertEquals("cl-name", dao.getClosedLoopName());
        assertEquals(endDate, dao.getEndtime());
        assertEquals(100L, dao.getId().longValue());
        assertEquals("my-message", dao.getMessage());
        assertEquals("my-operation", dao.getOperation());
        assertEquals("my-outcome", dao.getOutcome());
        assertEquals("my-request", dao.getRequestId());
        assertEquals(startDate, dao.getStarttime());
        assertEquals("my-sub", dao.getSubrequestId());
        assertEquals("my-target", dao.getTarget());

        assertTrue(dao.toString().startsWith("Dbao"));

        int hc = dao.hashCode();
        dao.setId(101L);
        assertTrue(hc != dao.hashCode());

        assertTrue(dao.equals(dao));
        assertFalse(dao.equals(new Dbao()));
    }
}
