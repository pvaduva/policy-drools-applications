/*-
 * ============LICENSE_START=======================================================
 * simulators
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

package org.onap.policy.simulators;

import org.onap.policy.drools.http.server.HttpServletServer;
import org.onap.policy.simulators.AaiSimulatorJaxRs;
import org.onap.policy.simulators.MsoSimulatorJaxRs;
import org.onap.policy.simulators.VfcSimulatorJaxRs;

public class Util {
	public static HttpServletServer buildAaiSim() throws InterruptedException {
		HttpServletServer testServer = HttpServletServer.factory.build("testServer", "localhost", 6666, "/", false, true);
		testServer.addServletClass("/*", AaiSimulatorJaxRs.class.getName());
		testServer.waitedStart(5000);
		return testServer;
	}
	
	public static HttpServletServer buildMsoSim() throws InterruptedException {
		HttpServletServer testServer = HttpServletServer.factory.build("testServer", "localhost", 6667, "/", false, true);
		testServer.addServletClass("/*", MsoSimulatorJaxRs.class.getName());
		testServer.waitedStart(5000);
		return testServer;
	}
	
	public static HttpServletServer buildVfcSim() throws InterruptedException {
		HttpServletServer testServer = HttpServletServer.factory.build("testServer", "localhost", 6668, "/", false, true);
		testServer.addServletClass("/*", VfcSimulatorJaxRs.class.getName());
		testServer.waitedStart(5000);
		return testServer;
	}
}