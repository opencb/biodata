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
