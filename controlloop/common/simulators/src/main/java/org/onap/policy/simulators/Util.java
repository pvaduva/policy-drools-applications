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

import java.io.IOException;

import org.onap.policy.common.endpoints.http.server.HttpServletServer;
import org.onap.policy.common.utils.network.NetworkUtil;

public class Util {
    public static final String AAISIM_SERVER_NAME = "aaiSim";
    public static final String SOSIM_SERVER_NAME = "soSim";
    public static final String VFCSIM_SERVER_NAME = "vfcSim";
    public static final String GUARDSIM_SERVER_NAME = "guardSim";
    public static final String SDNCSIM_SERVER_NAME = "sdncSim";

    public static final int AAISIM_SERVER_PORT = 6666;
    public static final int SOSIM_SERVER_PORT = 6667;
    public static final int VFCSIM_SERVER_PORT = 6668;
    public static final int GUARDSIM_SERVER_PORT = 6669;
    public static final int SDNCSIM_SERVER_PORT = 6670;
    
    private static final String CANNOT_CONNECT = "cannot connect to port ";
    private static final String LOCALHOST = "localhost";

    private Util() {
        // Prevent instantiation of thic class
    }

    /**
     * Build an A&AI simulator.
     * 
     * @return the simulator
     * @throws InterruptedException if a thread is interrupted
     * @throws IOException if an IO errror occurs
     */
    public static HttpServletServer buildAaiSim() throws InterruptedException, IOException {
        final HttpServletServer testServer =
                HttpServletServer.factory.build(AAISIM_SERVER_NAME, LOCALHOST, AAISIM_SERVER_PORT, "/", false, true);
        testServer.addServletClass("/*", AaiSimulatorJaxRs.class.getName());
        testServer.waitedStart(5000);
        if (!NetworkUtil.isTcpPortOpen(LOCALHOST, testServer.getPort(), 5, 10000L)) {
            throw new IllegalStateException(CANNOT_CONNECT + testServer.getPort());
        }
        return testServer;
    }

    /**
     * Build an SDNC simulator.
     *
     * @return the simulator
     * @throws InterruptedException if a thread is interrupted
     * @throws IOException if an IO errror occurs
     */
    public static HttpServletServer buildSdncSim() throws InterruptedException, IOException {
        final HttpServletServer testServer =
                HttpServletServer.factory.build(SDNCSIM_SERVER_NAME, LOCALHOST, SDNCSIM_SERVER_PORT, "/", false, true);
        testServer.addServletClass("/*", SdncSimulatorJaxRs.class.getName());
        testServer.waitedStart(5000);
        if (!NetworkUtil.isTcpPortOpen(LOCALHOST, testServer.getPort(), 5, 10000L)) {
            throw new IllegalStateException(CANNOT_CONNECT + testServer.getPort());
        }
        return testServer;
    }


    /**
     * Build an SO simulator.
     * 
     * @return the simulator
     * @throws InterruptedException if a thread is interrupted
     * @throws IOException if an IO errror occurs
     */
    public static HttpServletServer buildSoSim() throws InterruptedException, IOException {
        final HttpServletServer testServer =
                HttpServletServer.factory.build(SOSIM_SERVER_NAME, LOCALHOST, SOSIM_SERVER_PORT, "/", false, true);
        testServer.addServletClass("/*", SoSimulatorJaxRs.class.getName());
        testServer.waitedStart(5000);
        if (!NetworkUtil.isTcpPortOpen(LOCALHOST, testServer.getPort(), 5, 10000L)) {
            throw new IllegalStateException(CANNOT_CONNECT + testServer.getPort());
        }
        return testServer;
    }

    /**
     * Build a VFC simulator.
     * 
     * @return the simulator
     * @throws InterruptedException if a thread is interrupted
     * @throws IOException if an IO errror occurs
     */
    public static HttpServletServer buildVfcSim() throws InterruptedException, IOException {
        final HttpServletServer testServer =
                HttpServletServer.factory.build(VFCSIM_SERVER_NAME,LOCALHOST, VFCSIM_SERVER_PORT, "/", false, true);
        testServer.addServletClass("/*", VfcSimulatorJaxRs.class.getName());
        testServer.waitedStart(5000);
        if (!NetworkUtil.isTcpPortOpen(LOCALHOST, testServer.getPort(), 5, 10000L)) {
            throw new IllegalStateException(CANNOT_CONNECT + testServer.getPort());
        }
        return testServer;
    }

    /**
     * Build a guard simulator.
     * 
     * @return the simulator
     * @throws InterruptedException if a thread is interrupted
     * @throws IOException if an IO errror occurs
     */
    public static HttpServletServer buildGuardSim() throws InterruptedException, IOException {
        HttpServletServer testServer = HttpServletServer.factory.build(GUARDSIM_SERVER_NAME, LOCALHOST,
                GUARDSIM_SERVER_PORT, "/", false, true);
        testServer.addServletClass("/*", GuardSimulatorJaxRs.class.getName());
        testServer.waitedStart(5000);
        if (!NetworkUtil.isTcpPortOpen(LOCALHOST, testServer.getPort(), 5, 10000L)) {
            throw new IllegalStateException(CANNOT_CONNECT + testServer.getPort());
        }
        return testServer;
    }
}
