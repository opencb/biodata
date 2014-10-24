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
