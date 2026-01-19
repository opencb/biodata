package org.opencb.biodata.models.clinical.interpretation;

import java.util.Map;

public class ClinicalVariantFilter {

    private Map<String, Object> query;
    private String opencgaVersion;
    private String cellbaseVersion;

    public ClinicalVariantFilter() {
    }

    public ClinicalVariantFilter(Map<String, Object> query, String opencgaVersion, String cellbaseVersion) {
        this.query = query;
        this.opencgaVersion = opencgaVersion;
        this.cellbaseVersion = cellbaseVersion;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ClinicalVariantFilter{");
        sb.append("query=").append(query);
        sb.append(", opencgaVersion='").append(opencgaVersion).append('\'');
        sb.append(", cellbaseVersion='").append(cellbaseVersion).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public Map<String, Object> getQuery() {
        return query;
    }

    public ClinicalVariantFilter setQuery(Map<String, Object> query) {
        this.query = query;
        return this;
    }

    public String getOpencgaVersion() {
        return opencgaVersion;
    }

    public ClinicalVariantFilter setOpencgaVersion(String opencgaVersion) {
        this.opencgaVersion = opencgaVersion;
        return this;
    }

    public String getCellbaseVersion() {
        return cellbaseVersion;
    }

    public ClinicalVariantFilter setCellbaseVersion(String cellbaseVersion) {
        this.cellbaseVersion = cellbaseVersion;
        return this;
    }
}
