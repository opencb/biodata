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
