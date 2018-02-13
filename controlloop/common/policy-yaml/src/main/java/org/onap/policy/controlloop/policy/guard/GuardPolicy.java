/*-
 * ============LICENSE_START=======================================================
 * policy-yaml
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

package org.onap.policy.controlloop.policy.guard;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class GuardPolicy {

    private String id = UUID.randomUUID().toString();
    private String name;
    private String description;
    private MatchParameters matchParameters;
    private LinkedList<Constraint> limitConstraints;
    
    public GuardPolicy() {
        //Do Nothing Empty Constructor. 
    }
    
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public MatchParameters getMatch_parameters() {
        return matchParameters;
    }

    public void setMatch_parameters(MatchParameters matchParameters) {
        this.matchParameters = matchParameters;
    }

    public LinkedList<Constraint> getLimit_constraints() {
        return  limitConstraints;
    }

    public void setLimit_constraints(LinkedList<Constraint> limitConstraints) {
        this.limitConstraints = limitConstraints;
    }

    public GuardPolicy(String id) {
        this.id = id;
    }
    
    public GuardPolicy(String name, MatchParameters matchParameters) {
        this.name = name;
        this.matchParameters = matchParameters;
    }
    
    public GuardPolicy(String id, String name, String description, MatchParameters matchParameters) {
        this(name, matchParameters);
        this.id = id;
        this.description = description;
    }
    
    public GuardPolicy(String name, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, matchParameters);
        if (limitConstraints != null) {
            this.limitConstraints = (LinkedList<Constraint>) limitConstraints;
        }
    }
    
    public GuardPolicy(String name, String description, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, matchParameters, limitConstraints);
        this.description = description;
    }
    
    public GuardPolicy(String id, String name, String description, MatchParameters matchParameters, List<Constraint> limitConstraints) {
        this(name, description, matchParameters, limitConstraints);
        this.id = id;
    }
    
    public GuardPolicy(GuardPolicy policy) {
        this.id = policy.id;
        this.name = policy.name;
        this.description = policy.description;
        this.matchParameters = new MatchParameters(policy.matchParameters);
        if (policy.limitConstraints != null) {
            this.limitConstraints = policy.limitConstraints;
        }
    }
    
    public boolean isValid() {
        return (id==null || name ==null)? false : true;
    }
    
    @Override
    public String toString() {
        return "Policy [id=" + id + ", name=" + name + ", description=" + description + ", match_parameters=" 
                +matchParameters + ", limitConstraints=" + limitConstraints + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((description == null) ? 0 : description.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((limitConstraints == null) ? 0 : limitConstraints.hashCode());
        result = prime * result + ((matchParameters == null) ? 0 : matchParameters.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        GuardPolicy other = (GuardPolicy) obj;
        return equalsMayBeNull(description, other.description)
        		&& equalsMayBeNull(id, other.id)
        		&& equalsMayBeNull(name, other.name)
        		&& equalsMayBeNull(limitConstraints, other.limitConstraints)
        		&& equalsMayBeNull(matchParameters, other.matchParameters);
    }
    
    private boolean equalsMayBeNull(final Object obj1, final Object obj2){
    	if ( obj1 == null ) {
            return obj2 == null;
        }
    	return obj1.equals(obj2);
    }        
}
