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
