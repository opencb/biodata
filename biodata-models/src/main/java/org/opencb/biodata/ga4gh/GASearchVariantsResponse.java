package org.opencb.biodata.ga4gh;

import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GASearchVariantsResponse {

    /**
     * The list of matching variants. If the callSetId field on the returned calls is not present, the ordering of the 
     * call sets from a SearchCallSetsRequest over the parent GAVariantSet is guaranteed to match the ordering of the 
     * calls on each GAVariant. The number of results will also be the same.
     */
    private List<GAVariant> variants;

    /**
     * The continuation token, which is used to page through large result sets. Provide this value in a subsequent 
     * request to return the next page of results. This field will be empty if there aren't any additional results.
     */
    private String nextPageToken;

    public GASearchVariantsResponse() {
    }

    public GASearchVariantsResponse(List<GAVariant> variants, String nextPageToken) {
        this.variants = variants;
        this.nextPageToken = nextPageToken;
    }

    public List<GAVariant> getVariants() {
        return variants;
    }

    void setVariants(List<GAVariant> variants) {
        this.variants = variants;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

}
