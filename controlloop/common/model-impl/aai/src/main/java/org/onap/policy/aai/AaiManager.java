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

import com.google.gson.JsonSyntaxException;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.onap.policy.aai.util.Serialization;
import org.onap.policy.rest.RESTManager;
import org.onap.policy.rest.RESTManager.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles communication towards and responses from A&AI for this module.
 */
public final class AaiManager {

    /** The Constant LINE_SEPARATOR. */
    private static final String LINE_SEPARATOR = System.lineSeparator();

    /** The Constant logger. */
    private static final Logger logger = LoggerFactory.getLogger(AaiManager.class);

    /** The Constant netLogger. */
    private static final Logger netLogger =
            LoggerFactory.getLogger(org.onap.policy.common.endpoints.event.comm.Topic.NETWORK_LOGGER);

    /** The rest manager. */
    // The REST manager used for processing REST calls for this AAI manager
    private final RESTManager restManager;

    /**
     * Constructor, create the AAI manager with the specified REST manager.
     *
     * @param restManager the rest manager to use for REST calls
     */
    public AaiManager(final RESTManager restManager) {
        this.restManager = restManager;
    }

    /**
     * Post a query to A&AI.
     *
     * @param url the A&AI URL
     * @param username the user name for authentication
     * @param password the password for authentication
     * @param request the request to issue towards A&AI
     * @param requestId the UUID of the request
     * @return the response from A&AI
     */
    public AaiNqResponse postQuery(String url, String username, String password, AaiNqRequest request, UUID requestId) {

        final Map<String, String> headers = createHeaders(requestId);

        url = url + "/aai/search/named-query";

        logger.debug("RESTManager.post before");
        String requestJson = Serialization.gsonPretty.toJson(request);
        netLogger.info("[OUT|{}|{}|]{}{}", "AAI", url, LINE_SEPARATOR, requestJson);
        Pair<Integer, String> httpDetails =
                restManager.post(url, username, password, headers, "application/json", requestJson);
        logger.debug("RESTManager.post after");

        if (httpDetails == null) {
            logger.info("AAI POST Null Response to {}", url);
            return null;
        }

        int httpResponseCode = httpDetails.first;

        logger.info(url);
        logger.info("{}", httpResponseCode);
        logger.info(httpDetails.second);

        if (httpDetails.second != null) {
            return composeResponse(httpDetails, url, AaiNqResponse.class);
        }
        return null;
    }

    /**
     * Perform a GET request for a particular virtual server towards A&AI.
     *
     * @param urlGet the A&AI URL
     * @param username the user name for authentication
     * @param password the password for authentication
     * @param requestId the UUID of the request
     * @param key the key of the virtual server
     * @return the response for the virtual server from A&AI
     */
    public AaiGetVserverResponse getQueryByVserverName(String urlGet, String username, String password, UUID requestId,
            String key) {
        return getQuery(urlGet, username, password, requestId, key, AaiGetVserverResponse.class);
    }

    /**
     * Perform a GET request for a particular VNF by VNF ID towards A&AI.
     *
     * @param urlGet the A&AI URL
     * @param username the user name for authentication
     * @param password the password for authentication
     * @param requestId the UUID of the request
     * @param key the ID of the VNF
     * @return the response for the virtual server from A&AI
     */
    public AaiGetVnfResponse getQueryByVnfId(String urlGet, String username, String password, UUID requestId,
            String key) {
        return getQuery(urlGet, username, password, requestId, key, AaiGetVnfResponse.class);
    }

    /**
     * Perform a GET request for a particular VNF by VNF name towards A&AI.
     *
     * @param urlGet the A&AI URL
     * @param username the user name for authentication
     * @param password the password for authentication
     * @param requestId the UUID of the request
     * @param key the name of the VNF
     * @return the response for the virtual server from A&AI
     */
    public AaiGetVnfResponse getQueryByVnfName(String urlGet, String username, String password, UUID requestId,
            String key) {
        return getQuery(urlGet, username, password, requestId, key, AaiGetVnfResponse.class);
    }

    /**
     * Perform a GET query for a particular entity towards A&AI.
     *
     * @param <T> the generic type for the response
     * @param urlGet the A&AI URL
     * @param username the user name for authentication
     * @param password the password for authentication
     * @param requestId the UUID of the request
     * @param key the name of the VNF
     * @param classOfT the class of the response to return
     * @return the response for the virtual server from A&AI
     */
    private <T> T getQuery(final String url, final String username, final String password, final UUID requestId,
            final String key, final Class<T> classOfResponse) {

        Map<String, String> headers = createHeaders(requestId);

        String urlGet = url + key;

        int attemptsLeft = 3;

        while (attemptsLeft-- > 0) {
            netLogger.info("[OUT|{}|{}|]", "AAI", urlGet);
            Pair<Integer, String> httpDetailsGet = restManager.get(urlGet, username, password, headers);
            if (httpDetailsGet == null) {
                logger.info("AAI GET Null Response to {}", urlGet);
                return null;
            }

            int httpResponseCode = httpDetailsGet.first;

            logger.info(urlGet);
            logger.info("{}", httpResponseCode);
            logger.info(httpDetailsGet.second);

            if (httpResponseCode == 200) {
                T responseGet = composeResponse(httpDetailsGet, urlGet, classOfResponse);
                if (responseGet != null) {
                    return responseGet;
                }
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

        }

        return null;
    }

    /**
     * Create the headers for the HTTP request.
     *
     * @param requestId the request ID to insert in the headers
     * @return the HTTP headers
     */
    private Map<String, String> createHeaders(final UUID requestId) {
        Map<String, String> headers = new HashMap<>();

        headers.put("X-FromAppId", "POLICY");
        headers.put("X-TransactionId", requestId.toString());
        headers.put("Accept", "application/json");

        return headers;
    }

    /**
     * This method uses Google's GSON to create a response object from a JSON string.
     *
     * @param <T> the generic type
     * @param httpDetails the HTTP response
     * @param url the URL from which the response came
     * @param classOfResponse The response class
     * @return an instance of the response class
     * @throws JsonSyntaxException on GSON errors instantiating the response
     */
    private <T> T composeResponse(final Pair<Integer, String> httpDetails, final String url,
            final Class<T> classOfResponse) {
        try {
            T response = Serialization.gsonPretty.fromJson(httpDetails.second, classOfResponse);
            netLogger.info("[IN|{}|{}|]{}{}", "AAI", url, LINE_SEPARATOR, httpDetails.second);
            return response;
        } catch (JsonSyntaxException e) {
            logger.error("postQuery threw: ", e);
            return null;
        }
    }
}
