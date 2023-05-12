package org.opencb.biodata.models.pharma;

import org.opencb.biodata.models.core.Xref;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PharmaChemical {
    private String id;
    private String source;
    private String name;
    private List<String> genericNames;
    private List<String> tradeNames;
    private List<String> tradeMixtures;
    private List<String> types;
    private List<Xref> xrefs;
    private String smiles;
    private String inChI;
    private List<PharmaVariantAnnotation> variants;
    private List<PharmaGeneAnnotation> genes;

    private Map<String, Object> attributes;

    public PharmaChemical() {
        this.genericNames = new ArrayList<>();
        this.tradeNames = new ArrayList<>();
        this.tradeMixtures = new ArrayList<>();
        this.types = new ArrayList<>();
        this.xrefs = new ArrayList<>();
        this.variants = new ArrayList<>();
        this.genes = new ArrayList<>();
        this.attributes = new HashMap<>();
    }

    public PharmaChemical(String id, String source, String name, List<String> genericNames, List<String> tradeNames,
                          List<String> tradeMixtures, List<String> types, List<Xref> xrefs, String smiles, String inChI,
                          List<PharmaVariantAnnotation> variants, List<PharmaGeneAnnotation> genes, Map<String, Object> attributes) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.genericNames = genericNames;
        this.tradeNames = tradeNames;
        this.tradeMixtures = tradeMixtures;
        this.types = types;
        this.xrefs = xrefs;
        this.smiles = smiles;
        this.inChI = inChI;
        this.variants = variants;
        this.genes = genes;
        this.attributes = attributes;
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
        sb.append(", smiles='").append(smiles).append('\'');
        sb.append(", inChI='").append(inChI).append('\'');
        sb.append(", variants=").append(variants);
        sb.append(", genes=").append(genes);
        sb.append(", attributes=").append(attributes);
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

    public String getSmiles() {
        return smiles;
    }

    public PharmaChemical setSmiles(String smiles) {
        this.smiles = smiles;
        return this;
    }

    public String getInChI() {
        return inChI;
    }

    public PharmaChemical setInChI(String inChI) {
        this.inChI = inChI;
        return this;
    }

    public List<PharmaVariantAnnotation> getVariants() {
        return variants;
    }

    public PharmaChemical setVariants(List<PharmaVariantAnnotation> variants) {
        this.variants = variants;
        return this;
    }

    public List<PharmaGeneAnnotation> getGenes() {
        return genes;
    }

    public PharmaChemical setGenes(List<PharmaGeneAnnotation> genes) {
        this.genes = genes;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public PharmaChemical setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
