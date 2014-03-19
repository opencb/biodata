package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class Control extends Interaction {

    private String controlType;
    private List<String> controlled;
    private List<String> controller;

    public Control(List<String> availability, List<String> comment,
                   List<String> dataSource, List<String> evidence, List<String> name,
                   List<String> xref, List<String> interactionType,
                   List<String> participant, String controlType,
                   List<String> controlled, List<String> controller) {
        super(availability, comment, dataSource, evidence, name, xref,
                interactionType, participant);
        this.controlType = controlType;
        this.controlled = controlled;
        this.controller = controller;
    }

    public String getControlType() {
        return controlType;
    }

    public void setControlType(String controlType) {
        this.controlType = controlType;
    }

    public List<String> getControlled() {
        return controlled;
    }

    public void setControlled(List<String> controlled) {
        this.controlled = controlled;
    }

    public List<String> getController() {
        return controller;
    }

    public void setController(List<String> controller) {
        this.controller = controller;
    }
}
