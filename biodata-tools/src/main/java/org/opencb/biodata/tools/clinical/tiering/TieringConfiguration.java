package org.opencb.biodata.tools.clinical.tiering;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TieringConfiguration {
    private String penetrance;
    private Map<String, Object> queries;
    private Map<String, Map<String, Object>> tiers;

    public TieringConfiguration() {
    }

    public TieringConfiguration(String penetrance, Map<String, Object> queries, Map<String, Map<String, Object>> tiers) {
        this.penetrance = penetrance;
        this.queries = queries;
        this.tiers = tiers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TieringConfiguration{");
        sb.append("penetrance='").append(penetrance).append('\'');
        sb.append(", queries=").append(queries);
        sb.append(", tiers=").append(tiers);
        sb.append('}');
        return sb.toString();
    }

    public String getPenetrance() {
        return penetrance;
    }

    public TieringConfiguration setPenetrance(String penetrance) {
        this.penetrance = penetrance;
        return this;
    }

    public Map<String, Object> getQueries() {
        return queries;
    }

    public TieringConfiguration setQueries(Map<String, Object> queries) {
        this.queries = queries;
        return this;
    }

    public Map<String, Map<String, Object>> getTiers() {
        return tiers;
    }

    public TieringConfiguration setTiers(Map<String, Map<String, Object>> tiers) {
        this.tiers = tiers;
        return this;
    }
}
