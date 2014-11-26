package org.opencb.biodata.models.variant.annotation;

/**
 * Created by fjlopez on 19/11/14.
 */
public class Xref {

    private String id;
    private String src;

    public Xref(String id, String src) {
        this.id = id;
        this.src = src;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

}
