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

package org.onap.policy.aai;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.google.gson.annotations.SerializedName;

public class AAIGETVserverResponse implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6247505944905898870L;
 
	@SerializedName("vserver-id")
	public String vserverID;
	
	@SerializedName("vserver-name")
	public String vserverName;
	
	@SerializedName("vserver-name2")
	public String vserverName2;
	
	@SerializedName("vserver-selflink")
	public String vserverSelflink;
	
	@SerializedName("in-maint")
	public String inMaint;
	
	@SerializedName("is-closed-loop-disabled")
	public String isClosedLoopDisabled;
	
	@SerializedName("resource-version")
	public String resourceVersion; 
	
	@SerializedName("model-invariant-id")
	public String modelInvariantId;
	
	public RelationshipList relationshipList;

	public AAIGETVserverResponse() {
	}

}