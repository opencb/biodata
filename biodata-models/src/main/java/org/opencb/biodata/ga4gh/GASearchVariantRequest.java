/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.ga4gh;

import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GASearchVariantRequest {
    
    /**
     * Required. The IDs of the variant sets to search over.
     * 
     * TODO Not fully supported
     */
    private List<String> variantSetIds;
    
    /**
     * Only return variants which have exactly this name.
     * 
     * TODO Not fully supported
     */
    private String variantName;
    
    /**
     * Only return variant calls which belong to call sets with these IDs. 
     * Leaving this blank returns all variant calls.
     * 
     * TODO Not fully supported
     */
    private List<String> callSetIds;
    
    /**
     * Required. Only return variants on this reference (e.g. chr20 or X).
     */
    private String referenceName;
    
    /**
     * Required. The beginning of the window (0-based, inclusive) for which overlapping variants should be returned.
     *
     * NOTE: this field should be INT and not LONG, it stays as LONG for compatibility reasons
     */
    private long start;

    /**
     * Required. The end of the window (0-based, exclusive) for which overlapping variants should be returned.
     *
     * NOTE: this field should be INT and not LONG, it stays as LONG for compatibility reasons
     */
    private long end;
    
    /**
     * The continuation token, which is used to page through large result sets. To get the next page of results, 
     * set this parameter to the value of nextPageToken from the previous response.
    */
    private String pageToken;
    
    /**
     * The maximum number of variants to return in each response. If more variants match this request, the pageToken 
     * can be used to fetch the next page of responses.
     */
    private int pageSize;

    
    public GASearchVariantRequest() {
        this(null, null, 0, 0, null, 10);
    }

    public GASearchVariantRequest(List<String> variantSetIds, String referenceName, long start, long end, String pageToken, int pageSize) {
        this.variantSetIds = variantSetIds;
        this.referenceName = referenceName;
        this.start = start;
        this.end = end;
        this.pageToken = pageToken;
        this.pageSize = pageSize;
    }

    public boolean validate() {
//        if (variantSetIds == null || variantSetIds.isEmpty()) {
//            throw new IllegalArgumentException("Please provide at least one variant set ID");
//        }
        
        if (referenceName == null) {
            throw new IllegalArgumentException("Please provide a reference name");
        }
        
        if (start <= 0 || end <= 0) {
            throw new IllegalArgumentException("Please provide start and end positions greater than zero");
        }
        
        return true;
    }

    public List<String> getVariantSetIds() {
        return variantSetIds;
    }

    public void setVariantSetIds(List<String> variantSetIds) {
        this.variantSetIds = variantSetIds;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public List<String> getCallSetIds() {
        return callSetIds;
    }

    public void setCallSetIds(List<String> callSetIds) {
        this.callSetIds = callSetIds;
    }

    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    
    
}
