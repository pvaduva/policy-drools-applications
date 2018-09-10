/*-
 * ============LICENSE_START=======================================================
 * sdnr
 * ================================================================================
 * Copyright (C) 2018 Wipro Limited Intellectual Property. All rights reserved.
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

package org.onap.policy.sdnr;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PciCommonHeader implements Serializable {

    private static final long serialVersionUID = 5435363539127062114L;

    @SerializedName(value = "TimeStamp")
    private Instant timeStamp = Instant.now();

    @SerializedName(value = "APIVer")
    private String apiVer = "1.0";

    @SerializedName(value = "RequestID")
    private UUID requestId;

    @SerializedName(value = "SubRequestID")
    private String subRequestId;

    @SerializedName(value = "RequestTrack")
    private Map<String, String> requestTrack = new HashMap<>();

    @SerializedName(value = "Flags")
    private Map<String, String> flags = new HashMap<>();

    public PciCommonHeader() {

    }

    /**
     * Used to copy a pci common header.
     * 
     * @param commonHeader a header that is defined by the Pci api guide that contains information
     *        about the request (requestId, flags, etc.)
     */
    public PciCommonHeader(PciCommonHeader commonHeader) {
        this.timeStamp = commonHeader.timeStamp;
        this.requestId = commonHeader.requestId;
        this.subRequestId = commonHeader.subRequestId;
        if (commonHeader.requestTrack != null) {
            this.requestTrack.putAll(commonHeader.requestTrack);
        }
        if (commonHeader.flags != null) {
            this.flags.putAll(commonHeader.flags);
        }
    }

    /**
     * Get the timestamp.
     * 
     * @return the timeStamp
     */
    public Instant getTimeStamp() {
        return timeStamp;
    }

    /**
     * Set the timestamp.
     * 
     * @param timeStamp
     *            the timeStamp to set
     */
    public void setTimeStamp(Instant timeStamp) {
        this.timeStamp = timeStamp;
    }

    /**
     * Get the API version.
     * 
     * @return the apiVer
     */
    public String getApiVer() {
        return apiVer;
    }

    /**
     * Set the API version.
     * 
     * @param apiVer
     *            the apiVer to set
     */
    public void setApiVer(String apiVer) {
        this.apiVer = apiVer;
    }

    /**
     * Get the request Id.
     * 
     * @return the requestId
     */
    public UUID getRequestId() {
        return requestId;
    }

    /**
     * Set the request Id.
     * 
     * @param requestId
     *            the requestId to set
     */
    public void setRequestId(UUID requestId) {
        this.requestId = requestId;
    }

    /**
     * Get the sub request Id.
     * 
     * @return the subRequestId
     */
    public String getSubRequestId() {
        return subRequestId;
    }

    /**
     * Set the sub request Id.
     * 
     * @param subRequestId
     *            the subRequestId to set
     */
    public void setSubRequestId(String subRequestId) {
        this.subRequestId = subRequestId;
    }
    /**
     * Set the request track.
     * 
     * @param requestTrack
     *            the requestTrack to set
     */
    public void setRequestTrack(Map<String, String> requestTrack) {
        this.requestTrack = requestTrack;
    }
    /**
     * Get the request track.
     * 
     * @return the requestTrack
     */
    public Map<String, String> getRequestTrack() {
        return requestTrack;
    }

    /**
     * Get the flags.
     * 
     * @return the flags
     */
    public Map<String, String> getFlags() {
        return flags;
    }

    /**
     * Set the flags.
     * 
     * @param flags
     *            the flags to set
     */
    public void setFlags(Map<String, String> flags) {
        this.flags = flags;
    }

    @Override
    public String toString() {
        return "CommonHeader [timeStamp=" + timeStamp + ", apiVer=" + apiVer 
                   + ", requestId=" + requestId + ", subRequestId=" + subRequestId + ", requestTrack=" + requestTrack
                   + ", flags=" + flags + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((apiVer == null) ? 0 : apiVer.hashCode());
        result = prime * result + ((flags == null) ? 0 : flags.hashCode());
        result = prime * result + ((requestTrack == null) ? 0 : requestTrack.hashCode());
        result = prime * result + ((requestId == null) ? 0 : requestId.hashCode());
        result = prime * result + ((subRequestId == null) ? 0 : subRequestId.hashCode());
        result = prime * result + ((timeStamp == null) ? 0 : timeStamp.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        PciCommonHeader other = (PciCommonHeader) obj;
        if (apiVer == null) {
            if (other.apiVer != null) {
                return false;
            }
        } else if (!apiVer.equals(other.apiVer)) {
            return false;
        }
        if (flags == null) {
            if (other.flags != null) {
                return false;
            }
        } else if (!flags.equals(other.flags)) {
            return false;
        }
        if (requestTrack == null) {
            if (other.requestTrack != null) {
                return false;
            }
        } else if (!requestTrack.equals(other.requestTrack)) {
            return false;
        }
        if (requestId == null) {
            if (other.requestId != null) {
                return false;
            }
        } else if (!requestId.equals(other.requestId)) {
            return false;
        }
        if (subRequestId == null) {
            if (other.subRequestId != null) {
                return false;
            }
        } else if (!subRequestId.equals(other.subRequestId)) {
            return false;
        }
        if (timeStamp == null) {
            if (other.timeStamp != null) {
                return false;
            }
        } else if (!timeStamp.equals(other.timeStamp)) {
            return false;
        }
        return true;
    }

}
