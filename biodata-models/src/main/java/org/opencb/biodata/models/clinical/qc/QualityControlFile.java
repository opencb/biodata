package org.opencb.biodata.models.clinical.qc;

import java.util.Map;

public class QualityControlFile {

    private String file;
    private String type;
    private String subtype;
    private Map<String, String> query;

    public QualityControlFile() {
    }

    public QualityControlFile(String file, String type, String subtype, Map<String, String> query) {
        this.file = file;
        this.type = type;
        this.subtype = subtype;
        this.query = query;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("QualityControlFile{");
        sb.append("file='").append(file).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", subtype='").append(subtype).append('\'');
        sb.append(", query=").append(query);
        sb.append('}');
        return sb.toString();
    }

    public String getFile() {
        return file;
    }

    public QualityControlFile setFile(String file) {
        this.file = file;
        return this;
    }

    public String getType() {
        return type;
    }

    public QualityControlFile setType(String type) {
        this.type = type;
        return this;
    }

    public String getSubtype() {
        return subtype;
    }

    public QualityControlFile setSubtype(String subtype) {
        this.subtype = subtype;
        return this;
    }

    public Map<String, String> getQuery() {
        return query;
    }

    public QualityControlFile setQuery(Map<String, String> query) {
        this.query = query;
        return this;
    }
}
