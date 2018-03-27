/*-
 * ============LICENSE_START=======================================================
 * so
 * ================================================================================
 * Copyright (C) 2018 Ericsson. All rights reserved.
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;

@Path("/SO")
public class TestSoDummyServer {

    private static int postMessagesReceived = 0;
    private static int putMessagesReceived = 0;
    private static int statMessagesReceived = 0;
    private static int getMessagesReceived = 0;

    private static Map<String, SOResponse> ongoingRequestMap = new ConcurrentHashMap<>();

    @GET
    @Path("/Stats")
    public Response serviceGetStats() {
        statMessagesReceived++;
        return Response.status(200).entity("{\"GET\": " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}").build();
    }

    @GET
    @Path("/OneStat/{statType}")
    public Response serviceGetStat(@PathParam("statType") final String statType) {
        statMessagesReceived++;
        return Response.status(200).entity("{\"TYPE\": " + statType + "}").build();
    }

    @POST
    @Path("/serviceInstances/v5")
    public Response servicePostRequest(final String jsonString) {
        postMessagesReceived++;

        if (jsonString == null) {
            return Response.status(400).build();
        }

        SORequest request = null;
        try {
            request = new Gson().fromJson(jsonString, SORequest.class);
        }
        catch (Exception e) {
            return Response.status(400).build();
        }

        if (request == null) {
            return Response.status(400).build();
        }

        if (request.getRequestType() == null) {
            return Response.status(400).build();
        }

        if ("ReturnBadJson".equals(request.getRequestType())) {
            return Response.status(200)
                    .entity("{\"GET\": , " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": , " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}")
                    .build();
        }

        SOResponse response = new SOResponse();
        response.setRequest(request);
        response.setRequestReferences(new SORequestReferences());
        response.getRequestReferences().setRequestId(request.getRequestId().toString());

        if ("ReturnCompleted".equals(request.getRequestType())) {
            response.getRequest().getRequestStatus().setRequestState("COMPLETE");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnFailed".equals(request.getRequestType())) {
            response.getRequest().getRequestStatus().setRequestState("FAILED");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnOnging202".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(202);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnOnging200".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }


        if ("ReturnBadAfterWait".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        return null;
    }

    @POST
    @Path("/serviceInstances/v5/{serviceInstanceId}/vnfs/{vnfInstanceId}/vfModules")
    public Response servicePostRequestVfModules(
            @PathParam("serviceInstanceId") final String serviceInstanceId,
            @PathParam("vnfInstanceId") final String vnfInstanceId,
            final String jsonString) {
        postMessagesReceived++;

        if (jsonString == null) {
            return Response.status(400).build();
        }

        SORequest request = null;
        try {
            request = new Gson().fromJson(jsonString, SORequest.class);
        }
        catch (Exception e) {
            return Response.status(400).build();
        }

        if (request == null) {
            return Response.status(400).build();
        }

        if (request.getRequestType() == null) {
            return Response.status(400).build();
        }

        if ("ReturnBadJson".equals(request.getRequestType())) {
            return Response.status(200)
                    .entity("{\"GET\": , " + getMessagesReceived + ",\"STAT\": " + statMessagesReceived + ",\"POST\": , " + postMessagesReceived + ",\"PUT\": " + putMessagesReceived + "}")
                    .build();
        }

        SOResponse response = new SOResponse();
        response.setRequest(request);
        response.setRequestReferences(new SORequestReferences());
        response.getRequestReferences().setRequestId(request.getRequestId().toString());

        if ("ReturnCompleted".equals(request.getRequestType())) {
            response.getRequest().getRequestStatus().setRequestState("COMPLETE");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnFailed".equals(request.getRequestType())) {
            response.getRequest().getRequestStatus().setRequestState("FAILED");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnOnging202".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(202);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        if ("ReturnOnging200".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }


        if ("ReturnBadAfterWait".equals(request.getRequestType())) {
            ongoingRequestMap.put(request.getRequestId().toString(), response);

            response.getRequest().getRequestStatus().setRequestState("ONGOING");
            response.setHttpResponseCode(200);
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        return null;
    }

    @GET
    @Path("/orchestrationRequests/v5/{nsInstanceId}")
    public Response soRequestStatus(@PathParam("nsInstanceId") final String nsInstanceId) {

        SOResponse response = ongoingRequestMap.get(nsInstanceId);

        int iterationsLeft = Integer.valueOf(response.getRequest().getRequestScope());
        if (--iterationsLeft > 0) {
            response.getRequest().setRequestScope(new Integer(iterationsLeft).toString());
            String responseString = new Gson().toJson(response, SOResponse.class);
            return Response.status(response.getHttpResponseCode())
                    .entity(responseString)
                    .build();
        }

        ongoingRequestMap.remove(nsInstanceId);

        if ("ReturnBadAfterWait".equals(response.getRequest().getRequestType())) {
            return Response.status(400).build();
        }

        response.getRequest().getRequestStatus().setRequestState("COMPLETE");
        response.getRequest().setRequestScope("0");
        response.setHttpResponseCode(200);
        String responseString = new Gson().toJson(response, SOResponse.class);
        return Response.status(response.getHttpResponseCode())
                .entity(responseString)
                .build();
    }
}