package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class Interaction extends Entity {

    private List<String> interactionType;
    private List<String> participant;

    public Interaction(List<String> availability, List<String> comment,
                       List<String> dataSource, List<String> evidence, List<String> name,
                       List<String> xref, List<String> interactionType,
                       List<String> participant) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.interactionType = interactionType;
        this.participant = participant;
    }

    public List<String> getPathwayComponent() {
        return interactionType;
    }

    public void setPathwayComponent(List<String> pathwayComponent) {
        this.interactionType = pathwayComponent;
    }

    public List<String> getPathwayOrder() {
        return participant;
    }

    public void setPathwayOrder(List<String> pathwayOrder) {
        this.participant = pathwayOrder;
    }


}
