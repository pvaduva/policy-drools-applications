/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2017 Intel Corp. All rights reserved.
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

package org.onap.policy.vfc;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class VFCHealAdditionalParams implements Serializable {

	private static final long serialVersionUID = 2656694137285096191L;

	@SerializedName("action")
	private String action;

	@SerializedName("actionvminfo")
	private VFCHealActionVmInfo actionInfo;

	public VFCHealAdditionalParams() {
		// Default constructor for VFCHealAdditionalParams
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public VFCHealActionVmInfo getActionInfo() {
		return actionInfo;
	}

	public void setActionInfo(VFCHealActionVmInfo actionInfo) {
		this.actionInfo = actionInfo;
	}
}
