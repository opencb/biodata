package org.opencb.biodata.models.pharma.guideline;

public class Allele {
    private float id;
    private String allele;
    private String _label;
    private Term function;
    private BasicObject haplotype;
    private float version;

    public float getId() {
        return id;
    }

    public Allele setId(float id) {
        this.id = id;
        return this;
    }

    public String getAllele() {
        return allele;
    }

    public Allele setAllele(String allele) {
        this.allele = allele;
        return this;
    }

    public String get_label() {
        return _label;
    }

    public Allele set_label(String _label) {
        this._label = _label;
        return this;
    }

    public Term getFunction() {
        return function;
    }

    public Allele setFunction(Term function) {
        this.function = function;
        return this;
    }

    public BasicObject getHaplotype() {
        return haplotype;
    }

    public Allele setHaplotype(BasicObject haplotype) {
        this.haplotype = haplotype;
        return this;
    }

    public float getVersion() {
        return version;
    }

    public Allele setVersion(float version) {
        this.version = version;
        return this;
    }
}
