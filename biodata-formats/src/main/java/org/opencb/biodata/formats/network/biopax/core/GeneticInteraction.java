package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class GeneticInteraction extends Interaction {

    private List<String> interactionScore;
    private List<String> phenotype;

    public GeneticInteraction(List<String> availability, List<String> comment,
                              List<String> dataSource, List<String> evidence, List<String> name,
                              List<String> xref, List<String> interactionType,
                              List<String> participant, List<String> interactionScore,
                              List<String> phenotype) {
        super(availability, comment, dataSource, evidence, name, xref,
                interactionType, participant);
        this.interactionScore = interactionScore;
        this.phenotype = phenotype;
    }

    public List<String> getInteractionScore() {
        return interactionScore;
    }

    public void setInteractionScore(List<String> interactionScore) {
        this.interactionScore = interactionScore;
    }

    public List<String> getPhenotype() {
        return phenotype;
    }

    public void setPhenotype(List<String> phenotype) {
        this.phenotype = phenotype;
    }


}
