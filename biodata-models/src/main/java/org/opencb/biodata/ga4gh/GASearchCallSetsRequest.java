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
public class GASearchCallSetsRequest {
    
    /**
     * If specified, restricts the query to call sets within the given variant sets.
     */
    private List<String> variantSetIds;

    /**
     * Only return call sets for which a substring of the name matches this string.
     */
    private String name;

    /**
     * Specifies the maximum number of results to return in a single page. If 
     * unspecified, a system default will be used.
     */
    private int pageSize;

    /**
     * The continuation token, which is used to page through large result sets. 
     * To get the next page of results, set this parameter to the value of 
     * nextPageToken from the previous response.
     */
    private String pageToken;

    
    public GASearchCallSetsRequest() {
        this(null, null, 10, null);
    }

    public GASearchCallSetsRequest(List<String> variantSetIds, String name, int pageSize, String pageToken) {
        this.variantSetIds = variantSetIds;
        this.name = name;
        this.pageSize = pageSize;
        this.pageToken = pageToken;
    }

    public List<String> getVariantSetIds() {
        return variantSetIds;
    }

    public void setVariantSetIds(List<String> variantSetIds) {
        this.variantSetIds = variantSetIds;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getPageToken() {
        return pageToken;
    }

    public void setPageToken(String pageToken) {
        this.pageToken = pageToken;
    }

    
}
