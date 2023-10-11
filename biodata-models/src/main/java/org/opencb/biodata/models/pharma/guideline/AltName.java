package org.opencb.biodata.models.pharma.guideline;

import java.util.List;

public class AltName {
    private List<String> synonym;
    private List<String> symbol;

    public List<String> getSynonym() {
        return synonym;
    }

    public AltName setSynonym(List<String> synonym) {
        this.synonym = synonym;
        return this;
    }

    public List<String> getSymbol() {
        return symbol;
    }

    public AltName setSymbol(List<String> symbol) {
        this.symbol = symbol;
        return this;
    }
}
