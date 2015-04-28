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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GASearchVariantSetsRequest {
    
    /**
     * The IDs of the variant sets to search over.
     */
    private List<String> datasetIds;
    
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

    
    public GASearchVariantSetsRequest() {
        this(null, null, 10);
    }

    public GASearchVariantSetsRequest(List<String> datasetIds, String pageToken, int pageSize) {
        this.datasetIds = datasetIds != null ? datasetIds : new ArrayList<String>();
        this.pageToken = pageToken;
        this.pageSize = pageSize;
    }

    public List<String> getDatasetIds() {
        return datasetIds;
    }

    public void setDatasetIds(List<String> datasetIds) {
        this.datasetIds = datasetIds;
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
