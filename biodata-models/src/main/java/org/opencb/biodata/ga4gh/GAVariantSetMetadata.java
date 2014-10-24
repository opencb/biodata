package org.opencb.biodata.ga4gh;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class GAVariantSetMetadata {
    
    /**
     * The top-level key.
     */
    private String key;

    /**
     * The value field for simple metadata.
     */
    private String value;

    /**
     * User-provided ID field, not enforced by this API. Two or more pieces of 
     * structured metadata with identical id and key fields are considered equivalent.
     */
    private String id;

    /**
     * The type of data.
     */
    private String type;

    /**
     * The number of values that can be included in a field described by this metadata.
     */
    private String number;

    /**
     * A textual description of this metadata.
     */
    private String description;

    /**
     * Remaining structured metadata key-value pairs.
     */
    private Map<String, String> info;

    public GAVariantSetMetadata() {
        this(null, null, null, null, null, null, null);
    }

    public GAVariantSetMetadata(String key, String value, String id, String type, String number, String description, Map<String, String> info) {
        this.key = key;
        this.value = value;
        this.id = id;
        this.type = type != null ? type : "UNKNOWN_TYPE";
        this.number = number;
        this.description = description;
        this.info = info != null ? info : new HashMap<String, String>();
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Map<String, String> getInfo() {
        return info;
    }

    public void setInfo(Map<String, String> info) {
        this.info = info;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.key);
        hash = 29 * hash + Objects.hashCode(this.id);
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
        final GAVariantSetMetadata other = (GAVariantSetMetadata) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        return true;
    }

}
