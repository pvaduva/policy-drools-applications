/*-
 * ============LICENSE_START=======================================================
 * SOActorServiceProvider
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

package org.onap.policy.controlloop.actor.so;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.core.WorkingMemory;
import org.onap.policy.aai.AAINQInstanceFilters;
import org.onap.policy.aai.AAINQInventoryResponseItem;
import org.onap.policy.aai.AAIManager;
import org.onap.policy.aai.AAINQNamedQuery;
import org.onap.policy.aai.AAINQQueryParameters;
import org.onap.policy.aai.AAINQRequest;
import org.onap.policy.aai.AAINQResponse;
import org.onap.policy.aai.AAINQResponseWrapper;
import org.onap.policy.controlloop.ControlLoopNotificationType;
import org.onap.policy.controlloop.ControlLoopOperation;
import org.onap.policy.controlloop.VirtualControlLoopEvent;
import org.onap.policy.controlloop.VirtualControlLoopNotification;
import org.onap.policy.controlloop.actorServiceProvider.spi.Actor;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.drools.system.PolicyEngine;
import org.onap.policy.so.SOCloudConfiguration;
import org.onap.policy.so.SOManager;
import org.onap.policy.so.SOModelInfo;
import org.onap.policy.so.SORelatedInstance;
import org.onap.policy.so.SORelatedInstanceListElement;
import org.onap.policy.so.SORequest;
import org.onap.policy.so.SORequestDetails;
import org.onap.policy.so.SORequestInfo;
import org.onap.policy.so.SORequestParameters;
import org.onap.policy.so.util.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SOActorServiceProvider implements Actor {
	
	private static final Logger logger = LoggerFactory.getLogger(SOActorServiceProvider.class);

	private static String vnfItemVnfId;

	private String vnfItemVnfType;

	private String vnfItemModelInvariantId;

	private String vnfItemModelVersionId;

	private String vnfItemModelName;

	private String vnfItemModelVersion;

	private String vnfItemModelNameVersionId;

	private static String serviceItemServiceInstanceId;

	private String serviceItemModelInvariantId;

	private String serviceItemModelName;

	private String serviceItemModelType;

	private String serviceItemModelVersion;

	private String serviceItemModelNameVersionId;

	private String vfModuleItemVfModuleName;

	private String vfModuleItemModelInvariantId;

	private String vfModuleItemModelVersionId;

	private String vfModuleItemModelName;

	private String vfModuleItemModelNameVersionId;

	private String tenantItemTenantId;

	private String cloudRegionItemCloudRegionId;

	private static final ImmutableList<String> recipes = ImmutableList.of(
			"VF Module Create");
	private static final ImmutableMap<String, List<String>> targets = new ImmutableMap.Builder<String, List<String>>()
			.put("VF Module Create", ImmutableList.of("VFC"))
			.build();
	
	@Override
	public String actor() {
		return "SO";
	}

	@Override
	public List<String> recipes() {
		return ImmutableList.copyOf(recipes);
	}

	@Override
	public List<String> recipeTargets(String recipe) {
		return ImmutableList.copyOf(targets.getOrDefault(recipe, Collections.emptyList()));
	}

	@Override
	public List<String> recipePayloads(String recipe) {
		return Collections.emptyList();
	}
	
	/**
	 * SOActorServiceProvider Constructor
	 * 
	 */
	public SOActorServiceProvider() {
		
	}
	
	/**
	 * Constructs and sends an AAI vserver Named Query
	 * 
	 * @param eventRequestID
	 * @returns the response to the AAI Named Query
	 */
	private AAINQResponseWrapper AaiNamedQueryRequest(VirtualControlLoopEvent onset) {
		
		// create AAI named-query request with UUID started with ""
		AAINQRequest aainqrequest = new AAINQRequest();
		AAINQQueryParameters aainqqueryparam = new AAINQQueryParameters();
		AAINQNamedQuery aainqnamedquery = new AAINQNamedQuery();
		AAINQInstanceFilters aainqinstancefilter = new AAINQInstanceFilters();

		// queryParameters
		aainqnamedquery.namedQueryUUID = UUID.fromString("4ff56a54-9e3f-46b7-a337-07a1d3c6b469"); // UUID.fromString($params.getAaiNamedQueryUUID()) TO DO: AaiNamedQueryUUID 
		aainqqueryparam.namedQuery = aainqnamedquery;
		aainqrequest.queryParameters = aainqqueryparam;
		//
		// instanceFilters
		//
		Map<String, Map<String, String>> aainqinstancefiltermap = new HashMap<>();
		Map<String, String> aainqinstancefiltermapitem = new HashMap<>();
		aainqinstancefiltermapitem.put("vserver-name", onset.AAI.get("vserver.vserver-name")); // TO DO: get vserver.vname from dcae onset.AAI.get("vserver.vserver-name")
		aainqinstancefiltermap.put("vserver", aainqinstancefiltermapitem);
		aainqinstancefilter.instanceFilter.add(aainqinstancefiltermap);
		aainqrequest.instanceFilters = aainqinstancefilter;
		//
		// print aainqrequest for debug
		//
  		logger.debug("AAI Request sent:");
  		logger.debug(Serialization.gsonPretty.toJson(aainqrequest));
		//
		// Create AAINQRequestWrapper
		//
//		AAINQRequestWrapper aainqRequestWrapper = new AAINQRequestWrapper(onset.requestID, aainqrequest);
		//
		// insert aainqrequest into memory
		//
//		insert(aainqRequestWrapper);
		
  		/*
         * Obtain A&AI credentials from properties.environment file
         * TODO: What if these are null?
         */
        String aaiUrl = PolicyEngine.manager.getEnvironmentProperty("aai.url");
        String aaiUsername = PolicyEngine.manager.getEnvironmentProperty("aai.username");
        String aaiPassword = PolicyEngine.manager.getEnvironmentProperty("aai.password");
		
		//***** send the request *****\\
		AAINQResponse aainqresponse = AAIManager.postQuery(aaiUrl, aaiUsername, aaiPassword,
				aainqrequest, onset.requestID);

		// Check AAI response
		if (aainqresponse == null) {
			System.err.println("Failed to get AAI response");
			
			// Fail and retract everything
			return null;
		} else {
			// Create AAINQResponseWrapper
			AAINQResponseWrapper aainqResponseWrapper = new AAINQResponseWrapper(onset.requestID, aainqresponse);

			// insert aainqResponseWrapper to memory -- Is this needed?
//			insert(aainqResponseWrapper);
			
			// 
			extractSOFieldsFromNamedQuery(aainqResponseWrapper, onset);
			return aainqResponseWrapper;
		}
	}

	/**
	 * Extract the required fields from the named query response
	 * @param namedQueryResponseWrapper
	 * @param onset
	 */
	private void extractSOFieldsFromNamedQuery(AAINQResponseWrapper namedQueryResponseWrapper, VirtualControlLoopEvent onset) {
		
		try {
			// vnfItem
			setVnfItemVnfId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.vnfID);
			setVnfItemVnfType(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.vnfType);
			setVnfItemVnfType(vnfItemVnfType.substring(vnfItemVnfType.lastIndexOf("/")+1));
			setVnfItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.modelInvariantId);
			setVnfItemModelVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).genericVNF.modelVersionId);
			setVnfItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(0).propertyValue);
			setVnfItemModelVersion(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(2).propertyValue);
			setVnfItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(4).propertyValue);			

			// serviceItem
			setServiceItemServiceInstanceId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.serviceInstanceID);
			setServiceItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.modelInvariantId);
			setServiceItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(0).propertyValue);
			setServiceItemModelType(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(1).propertyValue);
			setServiceItemModelVersion(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).serviceInstance.modelVersionId);
			setServiceItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).extraProperties.extraProperty.get(4).propertyValue);
			
			// Find the index for base vf module and non-base vf module
			int baseIndex = -1;
			int nonBaseIndex = -1;
			List<AAINQInventoryResponseItem> inventoryItems = namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems;
			for (AAINQInventoryResponseItem m : inventoryItems) {
				if (m.vfModule != null && m.vfModule.isBaseVfModule == true) {
					baseIndex = inventoryItems.indexOf(m);
				} else if (m.vfModule != null && m.vfModule.isBaseVfModule == false && m.vfModule.orchestrationStatus == null) {
					nonBaseIndex = inventoryItems.indexOf(m);
				}
				//
				if (baseIndex != -1 && nonBaseIndex != -1) {
					break;
				}
			}
			
			// Report the error if either base vf module or non-base vf module is not found
			if (baseIndex == -1 || nonBaseIndex == -1) {
				logger.error("Either base or non-base vf module is not found from AAI response.");
				return;
			}
			
			// This comes from the base module
			setVfModuleItemVfModuleName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(baseIndex).vfModule.vfModuleName);
			setVfModuleItemVfModuleName(vfModuleItemVfModuleName.replace("Vfmodule", "vDNS"));

			// vfModuleItem - NOT the base module
			setVfModuleItemModelInvariantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(nonBaseIndex).vfModule.modelInvariantId);
			setVfModuleItemModelVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(nonBaseIndex).vfModule.modelVersionId);
			setVfModuleItemModelName(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(nonBaseIndex).extraProperties.extraProperty.get(0).propertyValue);
			setVfModuleItemModelNameVersionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(0).items.inventoryResponseItems.get(nonBaseIndex).extraProperties.extraProperty.get(4).propertyValue);
			
			// tenantItem
			setTenantItemTenantId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(1).tenant.tenantId);

			// cloudRegionItem
			setCloudRegionItemCloudRegionId(namedQueryResponseWrapper.aainqresponse.inventoryResponseItems.get(0).items.inventoryResponseItems.get(1).items.inventoryResponseItems.get(0).cloudRegion.cloudRegionId);
		
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			VirtualControlLoopNotification notification = new VirtualControlLoopNotification(onset);
			notification.notification = ControlLoopNotificationType.REJECTED;
			notification.message = "Exception occurred " + e.getMessage();
			notification.policyName = onset.policyName;
			notification.policyScope = onset.policyScope;
			notification.policyVersion = onset.policyVersion;
			//
			try {
				logger.debug(Serialization.gsonPretty.toJson(notification));
			} catch (Exception e1) {
				logger.error("Can't deliver notification: " + notification);
				logger.error(e1.getMessage(), e1);
			}
			//
			notification.notification = ControlLoopNotificationType.FINAL_FAILURE;
			notification.message = "Invalid named-query response from AAI";
            //
            try {
            	logger.debug(Serialization.gsonPretty.toJson(notification));
            } catch (Exception e1) {
            	logger.error("Can't deliver notification: " + notification);
            	logger.error(e1.getMessage(), e1);
            }	
			// Retract everything
			return;
		}

		// Extracted fields should not be null
		if (checkExtractedFields() == false) {
			System.err.println("some fields are missing from AAI response.");
			return;
		}
	}
	
	/**
	 * Checks whether extracted fields from AAI Named Query are null or not
	 * @return false if some extracted fields are missing, true otherwise
	 */
	private boolean checkExtractedFields() {
		
		if ((getVnfItemVnfId() == null) || (getVnfItemVnfType() == null) ||
			    (getVnfItemModelInvariantId() == null) || (getVnfItemModelName() == null) ||
			    (getVnfItemModelVersion() == null) || (getVnfItemModelNameVersionId() == null) ||
			    (getServiceItemServiceInstanceId() == null) || (getServiceItemModelName() == null) ||
			    (getServiceItemModelType() == null) || (getServiceItemModelVersion() == null) ||
			    (getServiceItemModelNameVersionId() == null) || (getVfModuleItemVfModuleName() == null) ||
			    (getVfModuleItemModelInvariantId() == null) || (getVfModuleItemModelVersionId() == null) ||
			    (getVfModuleItemModelName() == null) || (getVfModuleItemModelNameVersionId() == null) ||
			    (getTenantItemTenantId() == null) || (getCloudRegionItemCloudRegionId() == null)) {
				return false;
			}
		return true;
	}
	
	/**
	 * Construct SO Request
	 * 
	 * @param onset
	 * @param operation
	 * @param policy
	 * @return SORequest
	 */
	public SORequest constructRequest(VirtualControlLoopEvent onset, ControlLoopOperation operation, Policy policy) {

		if (policy.getActor().equals("SO") && policy.getRecipe().equals("VF Module Create")) {
			// perform named query request and handle response
			AaiNamedQueryRequest(onset);
		} else {
			// for future extension
			return null;
		};
          
		// check if the fields extracted from named query response are 
		// not null so we can proceed with SO request
		if (checkExtractedFields() == false) {
			
			System.err.println("AAI response is missing some required fields. Cannot proceed with SO Request construction.");
			return null;
			
		} else {

			// Construct SO Request
			SORequest request = new SORequest();
			request.requestId = onset.requestID;
			request.requestDetails = new SORequestDetails();
			request.requestDetails.modelInfo = new SOModelInfo();
			request.requestDetails.cloudConfiguration = new SOCloudConfiguration();
			request.requestDetails.requestInfo = new SORequestInfo();
			request.requestDetails.requestParameters = new SORequestParameters();
			request.requestDetails.requestParameters.userParams = null;
			//
			// cloudConfiguration
			//
			request.requestDetails.cloudConfiguration.lcpCloudRegionId = getCloudRegionItemCloudRegionId();
			request.requestDetails.cloudConfiguration.tenantId = getTenantItemTenantId();
			//
			// modelInfo
			//
			request.requestDetails.modelInfo.modelType = "vfModule";
			request.requestDetails.modelInfo.modelInvariantId = getVfModuleItemModelInvariantId();
			request.requestDetails.modelInfo.modelNameVersionId = getVfModuleItemModelNameVersionId();
			request.requestDetails.modelInfo.modelName = getVfModuleItemModelName();
			request.requestDetails.modelInfo.modelVersion = getVfModuleItemModelVersionId();
			//
			// requestInfo
			//
			request.requestDetails.requestInfo.instanceName = getVfModuleItemVfModuleName();
			request.requestDetails.requestInfo.source = "POLICY";
			request.requestDetails.requestInfo.suppressRollback = false;
			//
			// relatedInstanceList
			//
			SORelatedInstanceListElement relatedInstanceListElement1 = new SORelatedInstanceListElement();
			SORelatedInstanceListElement relatedInstanceListElement2 = new SORelatedInstanceListElement();
			relatedInstanceListElement1.relatedInstance = new SORelatedInstance();
			relatedInstanceListElement2.relatedInstance = new SORelatedInstance();
			//
			relatedInstanceListElement1.relatedInstance.instanceId = getServiceItemServiceInstanceId();
			relatedInstanceListElement1.relatedInstance.modelInfo = new SOModelInfo();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelType = "service";
			relatedInstanceListElement1.relatedInstance.modelInfo.modelInvariantId = getServiceItemModelInvariantId();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelNameVersionId = getServiceItemModelNameVersionId();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelName = getServiceItemModelName();
			relatedInstanceListElement1.relatedInstance.modelInfo.modelVersion = getServiceItemModelVersion();
			//
			relatedInstanceListElement2.relatedInstance.instanceId = getVnfItemVnfId();
			relatedInstanceListElement2.relatedInstance.modelInfo = new SOModelInfo();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelType = "vnf";
			relatedInstanceListElement2.relatedInstance.modelInfo.modelInvariantId = getVnfItemModelInvariantId();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelNameVersionId = getVnfItemModelNameVersionId();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelName = getVnfItemModelName();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelVersion = getVnfItemModelVersion();
			relatedInstanceListElement2.relatedInstance.modelInfo.modelCustomizationName = getVnfItemVnfType();
			//	
			request.requestDetails.relatedInstanceList.add(relatedInstanceListElement1);
			request.requestDetails.relatedInstanceList.add(relatedInstanceListElement2);
			//
			// print SO request for debug
			//
			logger.debug("SO request sent:");
			logger.debug(Serialization.gsonPretty.toJson(request));
	
			return request;
		}
	}
	
	/**
	 * This method is needed to get the serviceInstanceId and vnfInstanceId which is used
	 * in the asyncSORestCall 
	 * 
	 * @param wm
	 * @param request
	 */
	public static void sendRequest(WorkingMemory wm, Object request) {
		SOManager Mm = new SOManager();
		Mm.asyncSORestCall(wm, getServiceItemServiceInstanceId(), getVnfItemVnfId(), (SORequest)request);
	}
		
	/**
	 * @return the vnfItemVnfId
	 */
	public static String getVnfItemVnfId() {
		return vnfItemVnfId;
	}

	/**
	 * @param vnfItemVnfId the vnfItemVnfId to set
	 */
	private static void setVnfItemVnfId(String vnfItemVnfId) {
		SOActorServiceProvider.vnfItemVnfId = vnfItemVnfId;
	}

	/**
	 * @return the vnfItemVnfType
	 */
	public String getVnfItemVnfType() {
		return this.vnfItemVnfType;
	}

	/**
	 * @param vnfItemVnfType the vnfItemVnfType to set
	 */
	private void setVnfItemVnfType(String vnfItemVnfType) {
		this.vnfItemVnfType = vnfItemVnfType;
	}

	/**
	 * @return the vnfItemModelInvariantId
	 */
	public String getVnfItemModelInvariantId() {
		return this.vnfItemModelInvariantId;
	}

	/**
	 * @param vnfItemModelInvariantId the vnfItemModelInvariantId to set
	 */
	private void setVnfItemModelInvariantId(String vnfItemModelInvariantId) {
		this.vnfItemModelInvariantId = vnfItemModelInvariantId;
	}

	/**
	 * @return the vnfItemModelVersionId
	 */
	public String getVnfItemModelVersionId() {
		return this.vnfItemModelVersionId;
	}

	/**
	 * @param vnfItemModelVersionId the vnfItemModelVersionId to set
	 */
	private void setVnfItemModelVersionId(String vnfItemModelVersionId) {
		this.vnfItemModelVersionId = vnfItemModelVersionId;
	}

	/**
	 * @return the vnfItemModelName
	 */
	public String getVnfItemModelName() {
		return this.vnfItemModelName;
	}

	/**
	 * @param vnfItemModelName the vnfItemModelName to set
	 */
	private void setVnfItemModelName(String vnfItemModelName) {
		this.vnfItemModelName = vnfItemModelName;
	}

	/**
	 * @return the vnfItemModelVersion
	 */
	public String getVnfItemModelVersion() {
		return this.vnfItemModelVersion;
	}

	/**
	 * @param vnfItemModelVersion the vnfItemModelVersion to set
	 */
	private void setVnfItemModelVersion(String vnfItemModelVersion) {
		this.vnfItemModelVersion = vnfItemModelVersion;
	}

	/**
	 * @return the vnfItemModelNameVersionId
	 */
	public String getVnfItemModelNameVersionId() {
		return this.vnfItemModelNameVersionId;
	}

	/**
	 * @param vnfItemModelNameVersionId the vnfItemModelNameVersionId to set
	 */
	private void setVnfItemModelNameVersionId(String vnfItemModelNameVersionId) {
		this.vnfItemModelNameVersionId = vnfItemModelNameVersionId;
	}

	/**
	 * @return the serviceItemServiceInstanceId
	 */
	public static String getServiceItemServiceInstanceId() {
		return serviceItemServiceInstanceId;
	}

	/**
	 * @param serviceItemServiceInstanceId the serviceItemServiceInstanceId to set
	 */
	private static void setServiceItemServiceInstanceId(
			String serviceItemServiceInstanceId) {
		SOActorServiceProvider.serviceItemServiceInstanceId = serviceItemServiceInstanceId;
	}

	/**
	 * @return the serviceItemModelInvariantId
	 */
	public String getServiceItemModelInvariantId() {
		return this.serviceItemModelInvariantId;
	}

	/**
	 * @param serviceItemModeInvariantlId the serviceItemModelInvariantId to set
	 */
	private void setServiceItemModelInvariantId(String serviceItemModelInvariantId) {
		this.serviceItemModelInvariantId = serviceItemModelInvariantId;
	}

	/**
	 * @return the serviceItemModelName
	 */
	public String getServiceItemModelName() {
		return this.serviceItemModelName;
	}

	/**
	 * @param serviceItemModelName the serviceItemModelName to set
	 */
	private void setServiceItemModelName(String serviceItemModelName) {
		this.serviceItemModelName = serviceItemModelName;
	}

	/**
	 * @return the serviceItemModelType
	 */
	public String getServiceItemModelType() {
		return this.serviceItemModelType;
	}

	/**
	 * @param serviceItemModelType the serviceItemModelType to set
	 */
	private void setServiceItemModelType(String serviceItemModelType) {
		this.serviceItemModelType = serviceItemModelType;
	}

	/**
	 * @return the serviceItemModelVersion
	 */
	public String getServiceItemModelVersion() {
		return this.serviceItemModelVersion;
	}

	/**
	 * @param serviceItemModelVersion the serviceItemModelVersion to set
	 */
	private void setServiceItemModelVersion(String serviceItemModelVersion) {
		this.serviceItemModelVersion = serviceItemModelVersion;
	}

	/**
	 * @return the serviceItemModelNameVersionId
	 */
	public String getServiceItemModelNameVersionId() {
		return this.serviceItemModelNameVersionId;
	}

	/**
	 * @param serviceItemModelNameVersionId the serviceItemModelNameVersionId to set
	 */
	private void setServiceItemModelNameVersionId(
			String serviceItemModelNameVersionId) {
		this.serviceItemModelNameVersionId = serviceItemModelNameVersionId;
	}

	/**
	 * @return the vfModuleItemVfModuleName
	 */
	public String getVfModuleItemVfModuleName() {
		return this.vfModuleItemVfModuleName;
	}

	/**
	 * @param vfModuleItemVfModuleName the vfModuleItemVfModuleName to set
	 */
	private void setVfModuleItemVfModuleName(String vfModuleItemVfModuleName) {
		this.vfModuleItemVfModuleName = vfModuleItemVfModuleName;
	}

	/**
	 * @return the vfModuleItemModelInvariantId
	 */
	public String getVfModuleItemModelInvariantId() {
		return this.vfModuleItemModelInvariantId;
	}

	/**
	 * @param vfModuleItemModelInvariantId the vfModuleItemModelInvariantId to set
	 */
	private void setVfModuleItemModelInvariantId(String vfModuleItemModelInvariantId) {
		this.vfModuleItemModelInvariantId = vfModuleItemModelInvariantId;
	}

	/**
	 * @return the vfModuleItemModelVersionId
	 */
	public String getVfModuleItemModelVersionId() {
		return this.vfModuleItemModelVersionId;
	}

	/**
	 * @param vfModuleItemModelVersionId the vfModuleItemModelVersionId to set
	 */
	private void setVfModuleItemModelVersionId(
			String vfModuleItemModelVersionId) {
		this.vfModuleItemModelVersionId = vfModuleItemModelVersionId;
	}

	/**
	 * @return the vfModuleItemModelName
	 */
	public String getVfModuleItemModelName() {
		return this.vfModuleItemModelName;
	}

	/**
	 * @param vfModuleItemModelName the vfModuleItemModelName to set
	 */
	private void setVfModuleItemModelName(String vfModuleItemModelName) {
		this.vfModuleItemModelName = vfModuleItemModelName;
	}

	/**
	 * @return the vfModuleItemModelNameVersionId
	 */
	public String getVfModuleItemModelNameVersionId() {
		return this.vfModuleItemModelNameVersionId;
	}

	/**
	 * @param vfModuleItemModelNameVersionId the vfModuleItemModelNameVersionId to set
	 */
	private void setVfModuleItemModelNameVersionId(
			String vfModuleItemModelNameVersionId) {
		this.vfModuleItemModelNameVersionId = vfModuleItemModelNameVersionId;
	}

	/**
	 * @return the tenantItemTenantId
	 */
	public String getTenantItemTenantId() {
		return this.tenantItemTenantId;
	}

	/**
	 * @param tenantItemTenantId the tenantItemTenantId to set
	 */
	private void setTenantItemTenantId(String tenantItemTenantId) {
		this.tenantItemTenantId = tenantItemTenantId;
	}

	/**
	 * @return the cloudRegionItemCloudRegionId
	 */
	public String getCloudRegionItemCloudRegionId() {
		return this.cloudRegionItemCloudRegionId;
	}

	/**
	 * @param cloudRegionItemCloudRegionId the cloudRegionItemCloudRegionId to set
	 */
	private void setCloudRegionItemCloudRegionId(
			String cloudRegionItemCloudRegionId) {
		this.cloudRegionItemCloudRegionId = cloudRegionItemCloudRegionId;
	}

}
