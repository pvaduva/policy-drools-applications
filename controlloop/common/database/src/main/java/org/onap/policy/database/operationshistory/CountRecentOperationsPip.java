/*-
 * ============LICENSE_START=======================================================
 * Copyright (C) 2019 AT&T Intellectual Property. All rights reserved.
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
 *
 * SPDX-License-Identifier: Apache-2.0
 * ============LICENSE_END=========================================================
 */

package org.onap.policy.database.operationshistory;

import com.att.research.xacml.api.XACML3;
import com.att.research.xacml.api.pip.PIPException;
import com.att.research.xacml.api.pip.PIPFinder;
import com.att.research.xacml.api.pip.PIPRequest;
import com.att.research.xacml.api.pip.PIPResponse;
import com.att.research.xacml.std.pip.StdMutablePIPResponse;
import com.att.research.xacml.std.pip.StdPIPResponse;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import org.onap.policy.database.ToscaDictionary;
import org.onap.policy.database.std.StdOnapPip;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class CountRecentOperationsPip extends StdOnapPip {
    public static final String ISSUER_NAME = "count-recent-operations";
    private static Logger logger = LoggerFactory.getLogger(CountRecentOperationsPip.class);

    private static final Set<String> TIME_WINDOW_SCALES = Collections
                    .unmodifiableSet(new HashSet<>(Arrays.asList("minute", "hour", "day", "week", "month", "year")));

    public CountRecentOperationsPip() {
        super();
    }

    @Override
    public Collection<PIPRequest> attributesRequired() {
        return Arrays.asList(PIP_REQUEST_ACTOR, PIP_REQUEST_RECIPE, PIP_REQUEST_TARGET);
    }

    @Override
    public void configure(String id, Properties properties) throws PIPException {
        super.configure(id, properties, ISSUER_NAME);
    }

    /**
     * getAttributes.
     *
     * @param pipRequest the request
     * @param pipFinder the pip finder
     * @return PIPResponse
     */
    @Override
    public PIPResponse getAttributes(PIPRequest pipRequest, PIPFinder pipFinder) throws PIPException {
        logger.debug("getAttributes requesting attribute {} of type {} for issuer {}",
                pipRequest.getAttributeId(), pipRequest.getDataTypeId(), pipRequest.getIssuer());

        if (isRequestInvalid(pipRequest)) {
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }

        //
        // Parse out the issuer which denotes the time window
        // Eg: any-prefix:tw:10:minute
        //
        String[] s1 = pipRequest.getIssuer().split("tw:");
        String[] s2 = s1[1].split(":");
        int timeWindowVal = Integer.parseInt(s2[0]);
        String timeWindowScale = s2[1];
        //
        // Grab other attribute values
        //
        String actor = getActor(pipFinder);
        String operation = getRecipe(pipFinder);
        String target = getTarget(pipFinder);
        String timeWindow = timeWindowVal + " " + timeWindowScale;
        logger.info("Going to query DB about: actor {} operation {} target {} time window {}",
                actor, operation, target, timeWindow);
        //
        // Sanity check
        //
        if (actor == null || operation == null || target == null) {
            //
            // See if we have all the values
            //
            logger.error("missing attributes return empty");
            return StdPIPResponse.PIP_RESPONSE_EMPTY;
        }
        //
        // Ok do the database query
        //
        int operationCount = doDatabaseQuery(actor, operation, target, timeWindowVal, timeWindowScale);
        //
        // Create and return PipResponse
        //
        StdMutablePIPResponse pipResponse    = new StdMutablePIPResponse();
        this.addIntegerAttribute(pipResponse,
                XACML3.ID_ATTRIBUTE_CATEGORY_RESOURCE,
                ToscaDictionary.ID_RESOURCE_GUARD_OPERATIONCOUNT,
                operationCount,
                pipRequest);
        return new StdPIPResponse(pipResponse);
    }

    private int doDatabaseQuery(String actor, String operation, String target, int timeWindowVal,
            String timeWindowScale) {
        logger.info("Querying operations history for {} {} {} {} {}",
                actor, operation, target, timeWindowVal, timeWindowScale);
        if (em == null) {
            logger.error("No EntityManager available");
            return -1;
        }
        //
        // Compute the time window
        //
        if (! TIME_WINDOW_SCALES.contains(timeWindowScale.toLowerCase())) {
            //
            // Unsupported
            //
            logger.error("Unsupported time window scale value {}", timeWindowScale);
            //
            // Throw an exception instead?
            //
            return -1;
        }
        //
        // Do the query
        //
        Object result = null;
        try {
            //
            // Set up query --- operationshistory is magic, should fix sometime
            //
            String strQuery = "select count(*) as numops from operationshistory"
                + " where outcome<>'Failure_Guard'"
                + " and actor=?"
                + " and operation=?"
                + " and target=?"
                + " and endtime between"
                + " TIMESTAMPADD(?, ?, CURRENT_TIMESTAMP)"
                + " and CURRENT_TIMESTAMP";
            //
            // We are expecting a single result
            //
            result = em.createNativeQuery(strQuery)
                .setParameter(1, actor)
                .setParameter(2, operation)
                .setParameter(3, target)
                .setParameter(4, timeWindowScale)
                .setParameter(5, timeWindowVal * -1)
                .getSingleResult();
        } catch (RuntimeException e) {
            logger.error("Named query failed ", e);
        }
        //
        // Check our query results
        //
        if (result != null) {
            //
            // Success let's see what JPA returned to us
            //
            logger.info("operations query returned {}", result);
            //
            // Should get back a number
            //
            if (result instanceof Number) {
                return ((Number) result).intValue();
            }
            //
            // We shouldn't really get this result, but just
            // in case we'll do the dirty work of parsing the
            // string representation of the object.
            //
            return Integer.parseInt(result.toString());
        }
        //
        // We get here if we didn't get a result. Should
        // we propagate back an exception?
        //
        return -1;
    }

}
