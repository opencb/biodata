package org.opencb.biodata.ga4gh;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantSet {
    
    /**
     * The variant set ID.
     */
    private String id;
    
    /**
     * The ID of the dataset this variant set belongs to.
     */
    private String datasetId;

    /**
     * The metadata associated with this variant set. This is equivalent to the 
     * VCF header information not already presented in first class fields.
     */
    private List<GAVariantSetMetadata> metadata;

    public GAVariantSet() {
    }

    public GAVariantSet(String id, String datasetId, List<GAVariantSetMetadata> metadata) {
        this.id = id;
        this.datasetId = datasetId;
        this.metadata = metadata != null ? metadata : new ArrayList<GAVariantSetMetadata>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDatasetId() {
        return datasetId;
    }

    public void setDatasetId(String datasetId) {
        this.datasetId = datasetId;
    }

    public List<GAVariantSetMetadata> getMetadata() {
        return metadata;
    }

    public void setMetadata(List<GAVariantSetMetadata> metadata) {
        this.metadata = metadata;
    }
   
    
}
