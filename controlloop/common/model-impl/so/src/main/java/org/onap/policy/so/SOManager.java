/*-
 * ============LICENSE_START=======================================================
 * mso
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

package org.onap.policy.so;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.onap.policy.so.util.Serialization;
import org.onap.policy.drools.system.PolicyEngine;
import org.onap.policy.rest.RESTManager;
import org.onap.policy.rest.RESTManager.Pair;
import org.drools.core.WorkingMemory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

public final class SOManager {

	private static final Logger logger = LoggerFactory.getLogger(SOManager.class);
	private static final Logger netLogger = LoggerFactory.getLogger(org.onap.policy.drools.event.comm.Topic.NETWORK_LOGGER);
	private static ExecutorService executors = Executors.newCachedThreadPool();
		
	public static SOResponse createModuleInstance(String url, String urlBase, String username, String password, SORequest request) {
		
		//
		// Call REST
		//
		Map<String, String> headers = new HashMap<String, String>();
		//headers.put("X-FromAppId", "POLICY");
		//headers.put("X-TransactionId", requestID.toString());
		headers.put("Accept", "application/json");
		
		//
		// 201 - CREATED - you are done just return 
		//
		String requestJson = Serialization.gsonPretty.toJson(request);
		netLogger.info("[OUT|{}|{}|]{}{}", "SO", url, System.lineSeparator(), requestJson);
		Pair<Integer, String> httpDetails = new RESTManager().post(url, username, password, headers, "application/json", requestJson);
		
		if (httpDetails == null) {
			return null;
		}
		
		if (httpDetails.a == 202) {
			try {
				SOResponse response = Serialization.gsonPretty.fromJson(httpDetails.b, SOResponse.class);
				
				String body = Serialization.gsonPretty.toJson(response);
				logger.debug("***** Response to post:");
				logger.debug(body);
				
				String requestId = response.requestReferences.requestId;
				int attemptsLeft = 20;
				
				//String getUrl = "/orchestrationRequests/v2/"+requestId;
				String urlGet = urlBase + "/orchestrationRequests/v2/"+requestId;
				SOResponse responseGet = null;
				
				while(attemptsLeft-- > 0){
					
					Pair<Integer, String> httpDetailsGet = new RESTManager().get(urlGet, username, password, headers);
					responseGet = Serialization.gsonPretty.fromJson(httpDetailsGet.b, SOResponse.class);
					netLogger.info("[IN|{}|{}|]{}{}", "SO", urlGet, System.lineSeparator(), httpDetailsGet.b);
                    
					body = Serialization.gsonPretty.toJson(responseGet);
					logger.debug("***** Response to get:");
					logger.debug(body);
					
					if(httpDetailsGet.a == 200){
						if(responseGet.request.requestStatus.requestState.equalsIgnoreCase("COMPLETE") || 
								responseGet.request.requestStatus.requestState.equalsIgnoreCase("FAILED")){
							logger.debug("***** ########  VF Module Creation "+responseGet.request.requestStatus.requestState);
							return responseGet;
						}
					}
					Thread.sleep(20000);
				}

				if (responseGet != null
				 && responseGet.request != null
				 &&	responseGet.request.requestStatus != null
				 && responseGet.request.requestStatus.requestState != null) {
					logger.warn("***** ########  VF Module Creation timeout. Status: ( {})", responseGet.request.requestStatus.requestState);
				}

				return responseGet;
			} catch (JsonSyntaxException e) {
				logger.error("Failed to deserialize into SOResponse: ", e);
			} catch (InterruptedException e) {
				logger.error("Interrupted exception: ", e);
				Thread.currentThread().interrupt();
			}
		}
		
		
		
		
		return null;
	}

	/**
	 * 
	 * @param wm
	 * @param url
	 * @param urlBase
	 * @param username
	 * @param password
	 * @param request
	 * 
	 * This method makes an asynchronous Rest call to MSO and inserts the response into the Drools working memory
	 */
	  public void asyncSORestCall(String requestID, WorkingMemory wm, String serviceInstanceId, String vnfInstanceId, SORequest request) {
		  executors.submit(new Runnable()
		  	{
			  @Override
			  	public void run()
			  {
				  try {
					  String serverRoot = PolicyEngine.manager.getEnvironmentProperty("so.url");
					  String username = PolicyEngine.manager.getEnvironmentProperty("so.username");
					  String password = PolicyEngine.manager.getEnvironmentProperty("so.password");
					  
					  String url = serverRoot + "/serviceInstances/v5/" + serviceInstanceId + "/vnfs/" + vnfInstanceId + "/vfModules";
					  
					  String auth = username + ":" + password;
					  
					  Map<String, String> headers = new HashMap<String, String>();
					  byte[] encodedBytes = Base64.getEncoder().encode(auth.getBytes());
					  headers.put("Accept", "application/json");
					  headers.put("Authorization", "Basic " + new String(encodedBytes));
					  
					  Gson gsonPretty = new GsonBuilder().disableHtmlEscaping()
							  .setPrettyPrinting()
							  .create();
					  
					  String soJson = gsonPretty.toJson(request);
					  
					  SOResponse so = new SOResponse();
					  netLogger.info("[OUT|{}|{}|]{}{}", "SO", url, System.lineSeparator(), soJson);
					  Pair<Integer, String> httpResponse = new RESTManager().post(url, "policy", "policy", headers, "application/json", soJson);
					  
					  if (httpResponse != null ) {
						  if (httpResponse.b != null && httpResponse.a != null) {
							  netLogger.info("[IN|{}|{}|]{}{}", url, "SO", System.lineSeparator(), httpResponse.b);
							  
							  Gson gson = new Gson();
							  so = gson.fromJson(httpResponse.b, SOResponse.class);
							  so.httpResponseCode = httpResponse.a;
						  } else {
							  logger.error("SO Response status/code is null.");
							  so.httpResponseCode = 999;
						  }
						  
					  } else {
						  logger.error("SO Response returned null.");
						  so.httpResponseCode = 999;
					  }
					  
					  SOResponseWrapper SoWrapper = new SOResponseWrapper(so, requestID);
					  wm.insert(SoWrapper);
					  logger.info("SOResponse inserted " + gsonPretty.toJson(SoWrapper));
				  } catch (Exception e) {
					  logger.error("Error while performing asyncSORestCall: "+ e.getMessage(),e);
					  
					  // create dummy SO object to trigger cleanup
					  SOResponse so = new SOResponse();
					  so.httpResponseCode = 999;
					  wm.insert(so);
				  }
			  }
		  	});
	  }

}
