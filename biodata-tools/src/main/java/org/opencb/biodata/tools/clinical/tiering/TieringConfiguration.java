package org.opencb.biodata.tools.clinical.tiering;

import java.util.List;
import java.util.Map;

public class TieringConfiguration {

    private List<String> panels;
    private Map<String, Object> queries;
    private Map<String, Map<String, Object>> tiers;

    public TieringConfiguration() {
    }

    public TieringConfiguration(List<String> panels, Map<String, Object> queries, Map<String, Map<String, Object>> tiers) {
        this.panels = panels;
        this.queries = queries;
        this.tiers = tiers;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("TieringConfiguration{");
        sb.append("panels=").append(panels);
        sb.append(", queries=").append(queries);
        sb.append(", tiers=").append(tiers);
        sb.append('}');
        return sb.toString();
    }

    public List<String> getPanels() {
        return panels;
    }

    public TieringConfiguration setPanels(List<String> panels) {
        this.panels = panels;
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
