package org.opencb.biodata.formats.network.biopax.core;

import java.util.List;

public class Gene extends Entity {

    private String organims;

    public Gene(List<String> availability, List<String> comment,
                List<String> dataSource, List<String> evidence, List<String> name,
                List<String> xref, String organims) {
        super(availability, comment, dataSource, evidence, name, xref);
        this.organims = organims;
    }

    public String getOrganims() {
        return organims;
    }

    public void setOrganims(String organims) {
        this.organims = organims;
    }
}
