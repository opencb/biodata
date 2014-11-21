package org.opencb.biodata.ga4gh;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by imedina on 27/08/14.
 */
public class GAVariant {
    /**
     * The variant ID
     */
    private String id;

    /**
     * The ID of the variant set this variant belongs to
     */
    private String variantSetId;

    /**
     * Names for the variant, for example a RefSNP ID
     */
    private String[] names;

    /**
     * The date this variant was created in milliseconds from the epoch
     */
    private long created;

    /**
     * The time at which this variant was last updated in milliseconds from the epoch
     */
    private long updated;

    /**
     * The reference on which this variant occurs. (e.g. chr20 or X)
     */
    private String referenceName;

    /**
     * The start position at which this variant occurs (0-based). This corresponds to the first base of the string of reference bases
     *
     * NOTE: this field should be INT and not LONG, it stays as LONG for compatibility reasons
     */
    private long start;

    /**
     * The end position (exclusive), resulting in [start, end) closed-open interval. This is typically calculated by start + referenceBases.length
     *
     * NOTE: this field should be INT and not LONG, it stays as LONG for compatibility reasons
     */
    private long end;

    /**
     * The reference bases for this variant. They start at the given position
     */
    private String referenceBases;

    /**
     * The bases that appear instead of the reference bases
     */
    private String[] alternateBases;

    /**
     * A map of additional variant information. In JSON, this looks like: info: {key1: value1, key2: value2}
     *
     * NOTE: GAKeyValue is useless as this could be replaced by a Map<String, String>
     */
    private Map<String, List> info;

    /**
     * The variant calls for this particular variant. Each one represents the determination of genotype with respect to this variant
     */
    private GACall[] calls;


    public GAVariant(String id) {
        this.id = id;
        this.names = new String[0];
        this.alternateBases = new String[0];
        this.info = new HashMap<>();
        this.calls = new GACall[0];
    }

    public GAVariant(String id, String variantSetId, String[] names, long created, long updated, String referenceName,
                     long start, long end, String referenceBases, String[] alternateBases, Map<String, List> info, GACall[] calls) {
        this.id = id;
        this.variantSetId = variantSetId;
        this.names = names != null ? names : new String[0];
        this.created = created;
        this.updated = updated;
        this.referenceName = referenceName;
        this.start = start;
        this.end = end;
        this.referenceBases = referenceBases;
        this.alternateBases = alternateBases != null ? alternateBases : new String[0];
        this.info = info != null ? info : new HashMap<String, List>();
        this.calls = calls != null ? calls : new GACall[0];
    }

    @Override
    public String toString() {
        return "GAVariant{" + "id=" + id + 
                ", variantSetId=" + variantSetId + 
                ", names=" + names + 
                ", created=" + created + 
                ", updated=" + updated + 
                ", referenceName=" + referenceName + 
                ", start=" + start + 
                ", end=" + end + 
                ", referenceBases=" + referenceBases + 
                ", alternateBases=" + alternateBases + 
                ", info=" + info + 
                ", calls=" + calls + '}';
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getVariantSetId() {
        return variantSetId;
    }

    public void setVariantSetId(String variantSetId) {
        this.variantSetId = variantSetId;
    }


    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
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


    public String getReferenceName() {
        return referenceName;
    }

    public void setReferenceName(String referenceName) {
        this.referenceName = referenceName;
    }


    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }


    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }


    public String getReferenceBases() {
        return referenceBases;
    }

    public void setReferenceBases(String referenceBases) {
        this.referenceBases = referenceBases;
    }


    public String[] getAlternateBases() {
        return alternateBases;
    }

    public void setAlternateBases(String[] alternateBases) {
        this.alternateBases = alternateBases;
    }

    public Map<String, List> getInfo() {
        return info;
    }

    public void setInfo(Map<String, List> info) {
        this.info = info;
    }

    public GACall[] getCalls() {
        return calls;
    }

    public void setCalls(GACall[] calls) {
        this.calls = calls;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(this.variantSetId);
        hash = 59 * hash + Objects.hashCode(this.referenceName);
        hash = 59 * hash + (int) (this.start ^ (this.start >>> 32));
        hash = 59 * hash + (int) (this.end ^ (this.end >>> 32));
        hash = 59 * hash + Objects.hashCode(this.referenceBases);
        hash = 59 * hash + Arrays.deepHashCode(this.alternateBases);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GAVariant other = (GAVariant) obj;
        if (!Objects.equals(this.variantSetId, other.variantSetId)) {
            return false;
        }
        if (!Objects.equals(this.referenceName, other.referenceName)) {
            return false;
        }
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (!Objects.equals(this.referenceBases, other.referenceBases)) {
            return false;
        }
        if (!Arrays.deepEquals(this.alternateBases, other.alternateBases)) {
            return false;
        }
        return true;
    }

    
    
}
