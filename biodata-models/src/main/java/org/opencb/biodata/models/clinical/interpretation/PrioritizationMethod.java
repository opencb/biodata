package org.opencb.biodata.models.clinical.interpretation;

import java.util.List;
import java.util.Map;

public class PrioritizationMethod {

    private String name;
    private Map<String, Object> filters;
    private List<DiseasePanel> panels;
    private List<Software> dependencies;

    public PrioritizationMethod() {
    }

    public PrioritizationMethod(String name, Map<String, Object> filters, List<DiseasePanel> panels, List<Software> dependencies) {
        this.name = name;
        this.filters = filters;
        this.panels = panels;
        this.dependencies = dependencies;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PrioritizationMethod{");
        sb.append("name='").append(name).append('\'');
        sb.append(", filters=").append(filters);
        sb.append(", panels=").append(panels);
        sb.append(", dependencies=").append(dependencies);
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public PrioritizationMethod setName(String name) {
        this.name = name;
        return this;
    }

    public Map<String, Object> getFilters() {
        return filters;
    }

    public PrioritizationMethod setFilters(Map<String, Object> filters) {
        this.filters = filters;
        return this;
    }

    public List<DiseasePanel> getPanels() {
        return panels;
    }

    public PrioritizationMethod setPanels(List<DiseasePanel> panels) {
        this.panels = panels;
        return this;
    }

    public List<Software> getDependencies() {
        return dependencies;
    }

    public PrioritizationMethod setDependencies(List<Software> dependencies) {
        this.dependencies = dependencies;
        return this;
    }
}
