/*-
 * ============LICENSE_START=======================================================
 * aai
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

package org.onap.policy.aai;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class AaiNqResponse implements Serializable {
    private static final long serialVersionUID = 8411407444051746101L;

    @SerializedName("inventory-response-item")
    private List<AaiNqInventoryResponseItem> inventoryResponseItems = new LinkedList<>();

    @SerializedName("requestError")
    private AaiNqRequestError requestError;

    public List<AaiNqInventoryResponseItem> getInventoryResponseItems() {
        return inventoryResponseItems;
    }

    public AaiNqRequestError getRequestError() {
        return requestError;
    }

    public void setRequestError(AaiNqRequestError requestError) {
        this.requestError = requestError;
    }

    public void setInventoryResponseItems(List<AaiNqInventoryResponseItem> inventoryResponseItems) {
        this.inventoryResponseItems = inventoryResponseItems;
    }
}
