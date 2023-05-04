package org.opencb.biodata.models.core;

import java.util.ArrayList;
import java.util.List;

public class PharmaChemical {
    private String id;
    private String source;
    private String name;
    private List<String> genericNames;
    private List<String> tradeNames;
    private List<String> tradeMixtures;
    private List<String> types;
    private List<Xref> xrefs;

    public PharmaChemical() {
        genericNames = new ArrayList<>();
        tradeNames = new ArrayList<>();
        tradeMixtures = new ArrayList<>();
        types = new ArrayList<>();
        xrefs = new ArrayList<>();
    }

    public PharmaChemical(String id, String source, String name, List<String> genericNames, List<String> tradeNames,
                          List<String> tradeMixtures, List<String> types, List<Xref> xrefs) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.genericNames = genericNames;
        this.tradeNames = tradeNames;
        this.tradeMixtures = tradeMixtures;
        this.types = types;
        this.xrefs = xrefs;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PharmaChemical{");
        sb.append("id='").append(id).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", genericNames=").append(genericNames);
        sb.append(", tradeNames=").append(tradeNames);
        sb.append(", tradeMixtures=").append(tradeMixtures);
        sb.append(", types=").append(types);
        sb.append(", xrefs=").append(xrefs);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PharmaChemical setId(String id) {
        this.id = id;
        return this;
    }

    public String getSource() {
        return source;
    }

    public PharmaChemical setSource(String source) {
        this.source = source;
        return this;
    }

    public String getName() {
        return name;
    }

    public PharmaChemical setName(String name) {
        this.name = name;
        return this;
    }

    public List<String> getGenericNames() {
        return genericNames;
    }

    public PharmaChemical setGenericNames(List<String> genericNames) {
        this.genericNames = genericNames;
        return this;
    }

    public List<String> getTradeNames() {
        return tradeNames;
    }

    public PharmaChemical setTradeNames(List<String> tradeNames) {
        this.tradeNames = tradeNames;
        return this;
    }

    public List<String> getTradeMixtures() {
        return tradeMixtures;
    }

    public PharmaChemical setTradeMixtures(List<String> tradeMixtures) {
        this.tradeMixtures = tradeMixtures;
        return this;
    }

    public List<String> getTypes() {
        return types;
    }

    public PharmaChemical setTypes(List<String> types) {
        this.types = types;
        return this;
    }

    public List<Xref> getXrefs() {
        return xrefs;
    }

    public PharmaChemical setXrefs(List<Xref> xrefs) {
        this.xrefs = xrefs;
        return this;
    }
}
