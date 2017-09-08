/*-
 * ============LICENSE_START=======================================================
 * guard
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

package org.onap.policy.guard;

import com.att.research.xacml.api.DataTypeException;
import com.att.research.xacml.api.pdp.PDPEngine;
import com.att.research.xacml.std.annotations.RequestParser;

import java.util.UUID;

import org.drools.core.WorkingMemory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CallGuardTask implements Runnable {
	
	private static final Logger logger = LoggerFactory.getLogger(CallGuardTask.class);
	WorkingMemory workingMemory;
	PDPEngine embeddedPdpEngine;
	String restfulPdpUrl;
	String clname;
	String actor;
	String recipe;
	String target;
	String requestId;
	
    public CallGuardTask(PDPEngine engine, String url, WorkingMemory wm, String cl, String act, String rec, String tar, String reqId) { 
    
    	embeddedPdpEngine = engine; 
    	restfulPdpUrl = url;
    	workingMemory = wm;
    	clname = cl;
    	actor = act;
    	recipe = rec;
    	requestId = reqId;
    	target = tar;
    }
    public void run() {
    	long startTime = System.nanoTime();
    	com.att.research.xacml.api.Request request = null;
    	
    	PolicyGuardXacmlRequestAttributes xacmlReq = new PolicyGuardXacmlRequestAttributes(clname, actor,  recipe, target, requestId);
    	
    	try {
    		request = RequestParser.parseRequest(xacmlReq);
		} catch (IllegalArgumentException | IllegalAccessException | DataTypeException e) {
			logger.error("CallGuardTask.run threw: {}", e);
		} 
    	
		
  		logger.debug("\n********** XACML REQUEST START ********");
		logger.debug("{}", request);
		logger.debug("********** XACML REQUEST END ********\n");
		
		com.att.research.xacml.api.Response xacmlResponse = PolicyGuardXacmlHelper.callPDP(embeddedPdpEngine, "", request, false);
		
		logger.debug("\n********** XACML RESPONSE START ********");
		logger.debug("{}", xacmlResponse);
		logger.debug("********** XACML RESPONSE END ********\n");
						
		PolicyGuardResponse guardResponse = PolicyGuardXacmlHelper.ParseXacmlPdpResponse(xacmlResponse);
		
		//
		//Create an artificial Guard response in case we didn't get a clear Permit or Deny
		//
		if(guardResponse.result.equals("Indeterminate")){
			guardResponse.operation = recipe;
			guardResponse.requestID = UUID.fromString(requestId);
		}
		
		long estimatedTime = System.nanoTime() - startTime;
		logger.debug("\n\n============ Guard inserted with decision {} !!! =========== time took: {} mili sec \n\n",
				guardResponse.result, (double)estimatedTime/1000/1000);
		workingMemory.insert(guardResponse);

    }

}