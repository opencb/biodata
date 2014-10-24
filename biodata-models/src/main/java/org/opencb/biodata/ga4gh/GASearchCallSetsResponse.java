package org.opencb.biodata.ga4gh;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GASearchCallSetsResponse {
    
    /**
     * The list of matching call sets.
     */
    private List<GACallSet> callSets;

    /**
     * The continuation token, which is used to page through large result sets. 
     * Provide this value in a subsequent request to return the next page of results. 
     * This field will be empty if there aren't any additional results.
     */
    private String nextPageToken;

    
    public GASearchCallSetsResponse() {
    }

    public GASearchCallSetsResponse(List<GACallSet> callSets, String nextPageToken) {
        this.callSets = callSets != null ? callSets : new ArrayList<GACallSet>();
        this.nextPageToken = nextPageToken;
    }

    public List<GACallSet> getCallSets() {
        return callSets;
    }

    public void setCallSets(List<GACallSet> callSets) {
        this.callSets = callSets;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    
}
