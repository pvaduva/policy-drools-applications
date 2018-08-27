/*
 * ============LICENSE_START=======================================================
 * SOActorServiceProvider
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

package org.onap.policy.controlloop.actor.so;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.drools.core.WorkingMemory;
import org.onap.policy.aai.AaiNqExtraProperty;
import org.onap.policy.aai.AaiNqInventoryResponseItem;
import org.onap.policy.aai.AaiNqResponseWrapper;
import org.onap.policy.controlloop.ControlLoopOperation;
import org.onap.policy.controlloop.VirtualControlLoopEvent;
import org.onap.policy.controlloop.actorserviceprovider.spi.Actor;
import org.onap.policy.controlloop.policy.Policy;
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
import com.google.gson.reflect.TypeToken;

public class SOActorServiceProvider implements Actor {
    private static final Logger logger = LoggerFactory.getLogger(SOActorServiceProvider.class);

    // Strings for SO Actor
    private static final String SO_ACTOR = "SO";

    // Strings for targets
    private static final String TARGET_VFC = "VFC";

    // Strings for recipes
    private static final String RECIPE_VF_MODULE_CREATE = "VF Module Create";

    private static final ImmutableList<String> recipes = ImmutableList.of(RECIPE_VF_MODULE_CREATE);
    private static final ImmutableMap<String, List<String>> targets = new ImmutableMap.Builder<String, List<String>>()
                    .put(RECIPE_VF_MODULE_CREATE, ImmutableList.of(TARGET_VFC)).build();

    // name of request parameters within policy payload
    public static final String REQ_PARAM_NM = "requestParameters";

    // name of configuration parameters within policy payload
    public static final String CONFIG_PARAM_NM = "configurationParameters";

    // used to decode configuration parameters via gson
    public static Type CONFIG_TYPE = new TypeToken<List<Map<String, String>>>() {}.getType();

    // Static variables required to hold the IDs of the last service item and VNF item.
    // Note that in
    // a multithreaded deployment this WILL break
    private static String lastVNFItemVnfId;
    private static String lastServiceItemServiceInstanceId;

    @Override
    public String actor() {
        return SO_ACTOR;
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
     * Constructs a SO request conforming to the lcm API. The actual request is
     * constructed and then placed in a wrapper object used to send through DMAAP.
     *
     * @param onset the event that is reporting the alert for policy to perform an action
     * @param operation the control loop operation specifying the actor, operation,
     *        target, etc.
     * @param policy the policy the was specified from the yaml generated by CLAMP or
     *        through the Policy GUI/API
     * @param aaiResponseWrapper wrapper for AAI vserver named-query response
     * @return a SO request conforming to the lcm API using the DMAAP wrapper
     */
    public SORequest constructRequest(VirtualControlLoopEvent onset, ControlLoopOperation operation, Policy policy,
                    AaiNqResponseWrapper aaiResponseWrapper) {
        String modelNamePropertyKey = "model-ver.model-name";
        String modelVersionPropertyKey = "model-ver.model-version";
        String modelVersionIdPropertyKey = "model-ver.model-version-id";


        if (!SO_ACTOR.equals(policy.getActor()) || !RECIPE_VF_MODULE_CREATE.equals(policy.getRecipe())) {
            // for future extension
            return null;
        }

        // Perform named query request and handle response
        if (aaiResponseWrapper == null) {
            return null;
        }

        AaiNqInventoryResponseItem vnfItem;
        AaiNqInventoryResponseItem vnfServiceItem;
        AaiNqInventoryResponseItem tenantItem;

        // Extract the items we're interested in from the response
        try {
            vnfItem = aaiResponseWrapper.getAaiNqResponse().getInventoryResponseItems().get(0).getItems()
                            .getInventoryResponseItems().get(0);
        } catch (Exception e) {
            logger.error("VNF Item not found in AAI response {}", Serialization.gsonPretty.toJson(aaiResponseWrapper),
                            e);
            return null;
        }

        try {
            vnfServiceItem = vnfItem.getItems().getInventoryResponseItems().get(0);
        } catch (Exception e) {
            logger.error("VNF Service Item not found in AAI response {}",
                            Serialization.gsonPretty.toJson(aaiResponseWrapper), e);
            return null;
        }

        try {
            tenantItem = aaiResponseWrapper.getAaiNqResponse().getInventoryResponseItems().get(0).getItems()
                            .getInventoryResponseItems().get(1);
        } catch (Exception e) {
            logger.error("Tenant Item not found in AAI response {}",
                            Serialization.gsonPretty.toJson(aaiResponseWrapper), e);
            return null;
        }

        // Find the index for base vf module and non-base vf module
        AaiNqInventoryResponseItem baseItem = findVfModule(aaiResponseWrapper, true);
        AaiNqInventoryResponseItem vfModuleItem = findVfModule(aaiResponseWrapper, false);

        // Report the error if either base vf module or non-base vf module is not found
        if (baseItem == null || vfModuleItem == null) {
            logger.error("Either base or non-base vf module is not found from AAI response.");
            return null;
        }


        // Construct SO Request
        SORequest request = new SORequest();
        //
        // Do NOT send So the requestId, they do not support this field
        //
        request.setRequestDetails(new SORequestDetails());
        request.getRequestDetails().setModelInfo(new SOModelInfo());
        request.getRequestDetails().setCloudConfiguration(new SOCloudConfiguration());
        request.getRequestDetails().setRequestInfo(new SORequestInfo());
        request.getRequestDetails().setRequestParameters(new SORequestParameters());
        request.getRequestDetails().getRequestParameters().setUserParams(null);

        //
        // cloudConfiguration
        //
        request.getRequestDetails().getCloudConfiguration().setTenantId(tenantItem.getTenant().getTenantId());
        request.getRequestDetails().getCloudConfiguration().setLcpCloudRegionId(
                        tenantItem.getItems().getInventoryResponseItems().get(0).getCloudRegion().getCloudRegionId());

        //
        // modelInfo
        //
        request.getRequestDetails().getModelInfo().setModelType("vfModule");
        request.getRequestDetails().getModelInfo()
                        .setModelInvariantId(vfModuleItem.getVfModule().getModelInvariantId());
        request.getRequestDetails().getModelInfo().setModelVersionId(vfModuleItem.getVfModule().getModelVersionId());
        request.getRequestDetails().getModelInfo()
                        .setModelCustomizationId(vfModuleItem.getVfModule().getModelCustomizationId());

        for (AaiNqExtraProperty prop : vfModuleItem.getExtraProperties().getExtraProperty()) {
            if (prop.getPropertyName().equals(modelNamePropertyKey)) {
                request.getRequestDetails().getModelInfo().setModelName(prop.getPropertyValue());
            } else if (prop.getPropertyName().equals(modelVersionPropertyKey)) {
                request.getRequestDetails().getModelInfo().setModelVersion(prop.getPropertyValue());
            }
        }

        //
        // requestInfo
        //
        String vfModuleName = aaiResponseWrapper.genVfModuleName();
        request.getRequestDetails().getRequestInfo().setInstanceName(vfModuleName);
        request.getRequestDetails().getRequestInfo().setSource("POLICY");
        request.getRequestDetails().getRequestInfo().setSuppressRollback(false);
        request.getRequestDetails().getRequestInfo().setRequestorId("policy");

        //
        // relatedInstanceList
        //
        SORelatedInstanceListElement relatedInstanceListElement0 = new SORelatedInstanceListElement();
        SORelatedInstanceListElement relatedInstanceListElement1 = new SORelatedInstanceListElement();
        SORelatedInstanceListElement relatedInstanceListElement2 = new SORelatedInstanceListElement();
        relatedInstanceListElement1.setRelatedInstance(new SORelatedInstance());
        relatedInstanceListElement2.setRelatedInstance(new SORelatedInstance());

        // Volume Group Item
        relatedInstanceListElement0.setRelatedInstance(new SORelatedInstance());
        relatedInstanceListElement0.getRelatedInstance().setInstanceId(UUID.randomUUID().toString());
        relatedInstanceListElement0.getRelatedInstance().setInstanceName(vfModuleName + "_vol");
        relatedInstanceListElement0.getRelatedInstance().setModelInfo(new SOModelInfo());
        relatedInstanceListElement0.getRelatedInstance().getModelInfo().setModelType("volumeGroup");

        // Service Item
        relatedInstanceListElement1.getRelatedInstance()
                        .setInstanceId(vnfServiceItem.getServiceInstance().getServiceInstanceId());
        relatedInstanceListElement1.getRelatedInstance().setModelInfo(new SOModelInfo());
        relatedInstanceListElement1.getRelatedInstance().getModelInfo().setModelType("service");
        relatedInstanceListElement1.getRelatedInstance().getModelInfo()
                        .setModelInvariantId(vnfServiceItem.getServiceInstance().getModelInvariantId());
        for (AaiNqExtraProperty prop : vnfServiceItem.getExtraProperties().getExtraProperty()) {
            if (prop.getPropertyName().equals(modelNamePropertyKey)) {
                relatedInstanceListElement1.getRelatedInstance().getModelInfo().setModelName(prop.getPropertyValue());
            } else if (prop.getPropertyName().equals(modelVersionPropertyKey)) {
                relatedInstanceListElement1.getRelatedInstance().getModelInfo()
                                .setModelVersion(prop.getPropertyValue());
            } else if (prop.getPropertyName().equals(modelVersionIdPropertyKey)) {
                relatedInstanceListElement1.getRelatedInstance().getModelInfo()
                                .setModelVersionId(prop.getPropertyValue());
            }
        }

        // VNF Item
        relatedInstanceListElement2.getRelatedInstance().setInstanceId(vnfItem.getGenericVnf().getVnfId());
        relatedInstanceListElement2.getRelatedInstance().setModelInfo(new SOModelInfo());
        relatedInstanceListElement2.getRelatedInstance().getModelInfo().setModelType("vnf");
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                        .setModelInvariantId(vnfItem.getGenericVnf().getModelInvariantId());
        for (AaiNqExtraProperty prop : vnfItem.getExtraProperties().getExtraProperty()) {
            if (prop.getPropertyName().equals(modelNamePropertyKey)) {
                relatedInstanceListElement2.getRelatedInstance().getModelInfo().setModelName(prop.getPropertyValue());
            } else if (prop.getPropertyName().equals(modelVersionPropertyKey)) {
                relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                                .setModelVersion(prop.getPropertyValue());
            } else if (prop.getPropertyName().equals(modelVersionIdPropertyKey)) {
                relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                                .setModelVersionId(prop.getPropertyValue());
            }
        }
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                        .setModelCustomizationName(vnfItem.getGenericVnf().getVnfType()
                                        .substring(vnfItem.getGenericVnf().getVnfType().lastIndexOf('/') + 1));
        relatedInstanceListElement2.getRelatedInstance().getModelInfo()
                        .setModelCustomizationId(vnfItem.getGenericVnf().getModelCustomizationId());

        // Insert the Service Item and VNF Item
        request.getRequestDetails().getRelatedInstanceList().add(relatedInstanceListElement0);
        request.getRequestDetails().getRelatedInstanceList().add(relatedInstanceListElement1);
        request.getRequestDetails().getRelatedInstanceList().add(relatedInstanceListElement2);

        // Request Parameters
        request.getRequestDetails().setRequestParameters(buildRequestParameters(policy));

        // Configuration Parameters
        request.getRequestDetails().setConfigurationParameters(buildConfigurationParameters(policy));

        // Save the instance IDs for the VNF and service to static fields
        preserveInstanceIds(vnfItem.getGenericVnf().getVnfId(),
                        vnfServiceItem.getServiceInstance().getServiceInstanceId());

        if (logger.isDebugEnabled()) {
            logger.debug("SO request sent: {}", Serialization.gsonPretty.toJson(request));
        }

        return request;
    }

    /**
     * This method is needed to get the serviceInstanceId and vnfInstanceId which is used
     * in the asyncSORestCall.
     *
     * @param requestId the request Id
     * @param wm the working memory
     * @param request the request
     */
    public static void sendRequest(String requestId, WorkingMemory wm, Object request) {
        SOManager soManager = new SOManager();
        soManager.asyncSORestCall(requestId, wm, lastServiceItemServiceInstanceId, lastVNFItemVnfId,
                        (SORequest) request);
    }

    /**
     * Find the base or non base VF module item in an AAI response.
     *
     * @param aaiResponseWrapper the AAI response containing the VF modules
     * @param baseFlag true if we are searching for the base, false if we are searching
     *        for the non base
     * @return the base or non base VF module item or null if the module was not found
     */
    private AaiNqInventoryResponseItem findVfModule(AaiNqResponseWrapper aaiResponseWrapper, boolean baseFlag) {
        List<AaiNqInventoryResponseItem> lst = aaiResponseWrapper.getVfModuleItems(baseFlag);
        return (lst == null || lst.isEmpty() ? null : lst.get(0));
    }

    /**
     * Builds the request parameters from the policy payload.
     *
     * @param policy the policy
     * @return the request parameters, or {@code null} if the payload is {@code null}
     */
    private SORequestParameters buildRequestParameters(Policy policy) {
        if (policy.getPayload() == null) {
            return null;
        }

        String json = policy.getPayload().get(REQ_PARAM_NM);
        if (json == null) {
            return null;
        }

        return Serialization.gsonPretty.fromJson(json, SORequestParameters.class);
    }

    /**
     * Builds the configuration parameters from the policy payload.
     *
     * @param policy the policy
     * @return the configuration parameters, or {@code null} if the payload is
     *         {@code null}
     */
    private List<Map<String, String>> buildConfigurationParameters(Policy policy) {
        if (policy.getPayload() == null) {
            return null;
        }

        String json = policy.getPayload().get(CONFIG_PARAM_NM);
        if (json == null) {
            return null;
        }

        return Serialization.gsonPretty.fromJson(json, CONFIG_TYPE);
    }

    /**
     * This method is called to remember the last service instance ID and VNF Item VNF ID.
     * Note these fields are static, beware for multithreaded deployments
     *
     * @param vnfInstanceId update the last VNF instance ID to this value
     * @param serviceInstanceId update the last service instance ID to this value
     */
    private static void preserveInstanceIds(final String vnfInstanceId, final String serviceInstanceId) {
        lastVNFItemVnfId = vnfInstanceId;
        lastServiceItemServiceInstanceId = serviceInstanceId;
    }
}
