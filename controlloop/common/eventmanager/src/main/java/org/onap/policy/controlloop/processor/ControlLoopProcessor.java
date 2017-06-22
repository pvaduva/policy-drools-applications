/*-
 * ============LICENSE_START=======================================================
 * controlloop processor
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

package org.onap.policy.controlloop.processor;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import org.onap.policy.controlloop.ControlLoopException;
import org.onap.policy.controlloop.policy.ControlLoop;
import org.onap.policy.controlloop.policy.ControlLoopPolicy;
import org.onap.policy.controlloop.policy.FinalResult;
import org.onap.policy.controlloop.policy.Policy;
import org.onap.policy.controlloop.policy.PolicyResult;

public class ControlLoopProcessor {
	
	private final String yaml;
	private final ControlLoopPolicy policy;
	private String currentPolicy = null;
	
	public ControlLoopProcessor(String yaml) throws ControlLoopException {
		this.yaml = yaml;
		try {
			Yaml y = new Yaml(new Constructor(ControlLoopPolicy.class));
			Object obj = y.load(this.yaml);
			if (obj instanceof ControlLoopPolicy) {
				this.policy = (ControlLoopPolicy) obj;
				this.currentPolicy = this.policy.controlLoop.trigger_policy;
			} else {
				this.policy = null;
				throw new ControlLoopException("Unable to parse yaml into ControlLoopPolicy object");
			}
		} catch (Exception e) {
			//
			// Most likely this is a YAML Exception
			//
			throw new ControlLoopException(e);
		}
	}
	
	public ControlLoop getControlLoop() {
		return this.policy.controlLoop;
	}
	
	public FinalResult	checkIsCurrentPolicyFinal() {
		return FinalResult.toResult(this.currentPolicy);
	}
	
	public Policy	getCurrentPolicy() {
		for (Policy policy : this.policy.policies) {
			if (policy.id.equals(this.currentPolicy)) {
				return policy;
			}
		}
		return null;
	}
	
	public void	nextPolicyForResult(PolicyResult result) throws ControlLoopException {
		Policy policy = this.getCurrentPolicy();
		try {
			if (this.policy == null) {
				throw new ControlLoopException("There is no current policy to determine where to go to.");
			}
			switch (result) {
			case SUCCESS:
				this.currentPolicy = policy.success;
				break;
			case FAILURE:
				this.currentPolicy = policy.failure;
				break;
			case FAILURE_TIMEOUT:
				this.currentPolicy = policy.failure_timeout;
				break;
			case FAILURE_RETRIES:
				this.currentPolicy = policy.failure_retries;
				break;
			case FAILURE_EXCEPTION:
				this.currentPolicy = policy.failure_exception;
				break;
			case FAILURE_GUARD:
				this.currentPolicy = policy.failure_guard;
				break;
			default:
				throw new ControlLoopException("Bad policy result given: " + result);
			}
		} catch (ControlLoopException e) {
			this.currentPolicy = FinalResult.FINAL_FAILURE_EXCEPTION.toString();
			throw e;
		}
	}
	
}
