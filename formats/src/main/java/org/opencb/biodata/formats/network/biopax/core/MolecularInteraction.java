package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class MolecularInteraction extends Interaction {

    public MolecularInteraction(List<String> availability, List<String> comment,
                                List<String> dataSource, List<String> evidence, List<String> name,
                                List<String> xref, List<String> interactionType,
                                List<String> participant, List<String> interactionScore,
                                List<String> phenotype) {
        super(availability, comment, dataSource, evidence, name, xref,
                interactionType, participant);
    }

}
