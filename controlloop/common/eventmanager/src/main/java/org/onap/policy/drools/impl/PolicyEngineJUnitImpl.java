/*-
 * ============LICENSE_START=======================================================
 * policy engine
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

package org.onap.policy.drools.impl;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import org.onap.policy.appc.Request;
import org.onap.policy.controlloop.ControlLoopNotification;
import org.onap.policy.controlloop.util.Serialization;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.onap.policy.drools.PolicyEngine;

public class PolicyEngineJUnitImpl implements PolicyEngine {

	private static final Logger logger = LoggerFactory.getLogger(PolicyEngineJUnitImpl.class);
	private Map<String, Map<String, Queue<Object>>> busMap = new HashMap<String, Map<String, Queue<Object>>>();

	@Override
	public boolean deliver(String busType, String topic, Object obj) {
		if (obj instanceof ControlLoopNotification) {
			ControlLoopNotification notification = (ControlLoopNotification) obj;
			//logger.debug("Notification: " + notification.notification + " " + (notification.message == null ? "" : notification.message) + " " + notification.history);
			logger.debug(Serialization.gsonPretty.toJson(notification));
		}
		if (obj instanceof Request) {
			Request request = (Request) obj;
			logger.debug("Request: {} subrequst {}", request.Action, request.CommonHeader.SubRequestID);
		}
		//
		// Does the bus exist?
		//
		if (busMap.containsKey(busType) == false) {
			logger.debug("creating new bus type {}", busType);
			//
			// Create the bus
			//
			busMap.put(busType, new HashMap<String, Queue<Object>>());
		}
		//
		// Get the bus
		//
		Map<String, Queue<Object>> topicMap = busMap.get(busType);
		//
		// Does the topic exist?
		//
		if (topicMap.containsKey(topic) == false) {
			logger.debug("creating new topic {}", topic);
			//
			// Create the topic
			//
			topicMap.put(topic, new LinkedList<Object>());
		}
		//
		// Get the topic queue
		//
		logger.debug("queueing");
		return topicMap.get(topic).add(obj);
	}
	
	public Object	subscribe(String busType, String topic) {
		//
		// Does the bus exist?
		//
		if (busMap.containsKey(busType)) {
			//
			// Get the bus
			//
			Map<String, Queue<Object>> topicMap = busMap.get(busType);
			//
			// Does the topic exist?
			//
			if (topicMap.containsKey(topic)) {
				logger.debug("The queue has {}", topicMap.get(topic).size());
				return topicMap.get(topic).poll();
			} else {
				logger.error("No topic exists {}", topic);
			}
		} else {
			logger.error("No bus exists {}", busType);
		}
		return null;
	}

}
