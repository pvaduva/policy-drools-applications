/*-
 * ============LICENSE_START=======================================================
 * aai
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

package org.onap.policy.aai.AAINQF199;

import java.io.Serializable;

import com.google.gson.annotations.SerializedName;

public class AAINQF199ServiceInstance implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8000944396593889586L;

	@SerializedName("service-instance-id")
	public String serviceInstanceID;
	
	@SerializedName("service-instance-name")
	public String serviceInstanceName;
	
	@SerializedName("persona-model-id")
	public String personaModelId;
	
	@SerializedName("persona-model-version")
	public String personaModelVersion;
	
	@SerializedName("service-instance-location-id")
	public String serviceInstanceLocationId;
	
	@SerializedName("resource-version")
	public String resourceVersion;
	
	public AAINQF199ServiceInstance() {
	}

}