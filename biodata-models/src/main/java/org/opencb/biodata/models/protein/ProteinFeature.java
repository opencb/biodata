package org.opencb.biodata.models.protein;

/**
 * Created by fjlopez on 18/09/15.
 */
public class ProteinFeature {
    private String id;
    private int start;
    private int end;
    private String type;
    private String description;
    private String ref;

    public ProteinFeature() {
    }

    public ProteinFeature(String id, int start, int end, String type, String description, String ref) {
        this.id = id;
        this.start = start;
        this.end = end;
        this.type = type;
        this.description = description;
        this.ref = ref;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRef() {
        return ref;
    }

    public void setRef(String ref) {
        this.ref = ref;
    }
}
