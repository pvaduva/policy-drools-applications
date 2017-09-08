/*-
 * ============LICENSE_START=======================================================
 * controlloop
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

package org.onap.policy.controlloop;

public enum ControlLoopEventStatus {
	ONSET("ONSET"),
	ABATED("ABATED")
	;
	
	private String status;
	
	private ControlLoopEventStatus(String status) {
		this.status = status;
	}
	
	public String toString() {
		return this.status;
	}
	
	public static ControlLoopEventStatus toStatus(String status) {
		if (ONSET.status.equalsIgnoreCase(status)) {
			return ONSET;
		}
		if (ABATED.status.equalsIgnoreCase(status)) {
			return ABATED;
		}
		//
		// In case DCAE uses the old abatement
		//
		if (status.equalsIgnoreCase("abatement")) {
			return ABATED;
		}
		return null;
	}

}