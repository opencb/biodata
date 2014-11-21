package org.opencb.biodata.ga4gh;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GACallSet {

    /**
     * The call set ID.
     */
    private String id;

    /**
     * The call set name.
     */
    private String name;

    /**
     * The sample this call set's data was generated from.
     */
    private String sampleId;

    /**
     * The IDs of the variant sets this call set has calls in.
     */
    private List<String> variantSetIds;

    /**
     * The date this call set was created in milliseconds from the epoch.
     */
    private long created;

    /**
     * The time at which this call set was last updated in milliseconds from the epoch.
     */
    private long updated;

    /**
     * A map of additional call set information.
     */
    private Map<String, List> info;

    
    public GACallSet(String id, String sampleId) {
        this(id, null, sampleId, null, 0, 0, null);
    }

    public GACallSet(String id, String name, String sampleId, List<String> variantSetIds, long created, long updated, Map<String, List> info) {
        this.id = id;
        this.name = name;
        this.sampleId = sampleId;
        this.variantSetIds = variantSetIds != null ? variantSetIds : new ArrayList<String>();
        this.created = created;
        this.updated = updated;
        this.info = info != null ? info : new HashMap<String, List>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSampleId() {
        return sampleId;
    }

    public void setSampleId(String sampleId) {
        this.sampleId = sampleId;
    }

    public List<String> getVariantSetIds() {
        return variantSetIds;
    }

    public void setVariantSetIds(List<String> variantSetIds) {
        this.variantSetIds = variantSetIds;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public Map<String, List> getInfo() {
        return info;
    }

    public void setInfo(Map<String, List> info) {
        this.info = info;
    }

    
}
