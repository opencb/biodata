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
public class GASearchVariantSetsResponse {
    
    /**
     * The list of matching variant sets.
     */
    private List<GAVariantSet> variantSets;

    /**
     * The continuation token, which is used to page through large result sets. 
     * Provide this value in a subsequent request to return the next page of results. 
     * This field will be empty if there aren't any additional results.
     */
    private String nextPageToken;

    public GASearchVariantSetsResponse() {
    }

    public GASearchVariantSetsResponse(List<GAVariantSet> variantSets, String nextPageToken) {
        this.variantSets = variantSets != null ? variantSets : new ArrayList<GAVariantSet>();
        this.nextPageToken = nextPageToken;
    }

    public List<GAVariantSet> getVariantSets() {
        return variantSets;
    }

    public void setVariantSets(List<GAVariantSet> variantSets) {
        this.variantSets = variantSets;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

}
