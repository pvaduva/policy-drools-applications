/*-
 * ============LICENSE_START=======================================================
 * so
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

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.SerializedName;

public class SOAsyncRequestStatus implements Serializable {

    private static final long serialVersionUID = -3283942659786236032L;

    @SerializedName("correlator")
    private String correlator;

    @SerializedName("requestId")
    private String requestId;

    @SerializedName("instanceReferences")
    private SOInstanceReferences instanceReferences;

    @SerializedName("startTime")
    private LocalDateTime startTime;

    @SerializedName("finishTime")
    private LocalDateTime finishTime;

    @SerializedName("requestScope")
    private String requestScope;

    @SerializedName("requestType")
    private String requestType;

    @SerializedName("requestStatus")
    private SORequestStatus requestStatus;

    public SOAsyncRequestStatus() {
        // required by author
    }

    public String getCorrelator() {
        return correlator;
    }


    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public SOInstanceReferences getInstanceReferences() {
        return instanceReferences;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getRequestScope() {
        return requestScope;
    }

    public SORequestStatus getRequestStatus() {
        return requestStatus;
    }

    public String getRequestType() {
        return requestType;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setCorrelator(String correlator) {
        this.correlator = correlator;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
    }

    public void setInstanceReferences(SOInstanceReferences instanceReferences) {
        this.instanceReferences = instanceReferences;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setRequestScope(String requestScope) {
        this.requestScope = requestScope;
    }

    public void setRequestStatus(SORequestStatus requestStatus) {
        this.requestStatus = requestStatus;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

}
