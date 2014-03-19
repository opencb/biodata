package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class Pathway extends Entity {

    private String organism;
    private List<String> pathwayComponent;
    private List<String> pathwayOrder;

    public Pathway(List<String> availability, List<String> comment,
                   List<String> dataSource, List<String> evidence, List<String> name,
                   List<String> xref, String organism, List<String> pathwayComponent,
                   List<String> pathwayOrder) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.organism = organism;
        this.pathwayComponent = pathwayComponent;
        this.pathwayOrder = pathwayOrder;
    }

    public String getOrganism() {
        return organism;
    }

    public void setOrganism(String organism) {
        this.organism = organism;
    }

    public List<String> getPathwayComponent() {
        return pathwayComponent;
    }

    public void setPathwayComponent(List<String> pathwayComponent) {
        this.pathwayComponent = pathwayComponent;
    }

    public List<String> getPathwayOrder() {
        return pathwayOrder;
    }

    public void setPathwayOrder(List<String> pathwayOrder) {
        this.pathwayOrder = pathwayOrder;
    }


}
