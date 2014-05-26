package org.opencb.biodata.models.variant.CADD;

import java.util.List;

/**
 * Created by antonior on 5/22/14.
 */
public class Cadd {

    /***
     Alternate Allele
     ***/
    private String allele;

    /***
     Reference Allele
     ***/
    private String reference;

    /***
     Chromosome
     ***/
    private String chr;

    /***
     Variant position
     ***/
    private int pos;

    /***
     Maximum ENCODE expression value
     ***/
    private float EncExp;

    /***
     Maximum ENCODE H3K27 acetylation level
     ***/
    private float EncH3K27Ac;

    /***
     Maximum ENCODE H3K4 methylation level
     ***/
    private float EncH3K4Me1;

    /***
     Maximum ENCODE H3K4 trimethylation level
     ***/
    private float EncH3K4Me3;


    /***
     Maximum of ENCODE Nucelosome position track score
     ***/
    private float EncNucleo;

    /***
     ENCODE open chromatin code
     ***/
    private int EncOCC;

    /***
     ENCODE combined p-Value (PHRED-scale) of Faire, Dnase,polII, CTCF, Myc evidence for open chromatin
     ***/
    private float EncOCCombPVal;

    /***
     p-Value (PHRED-scale) of Dnase evidence for open chromatin
     ***/
    private float EncOCDNasePVal;

    /***
     p-Value (PHRED-scale) of Faire evidence for open chromatin
     ***/
    private float EncOCFairePVal;

    /***
     p-Value (PHRED-scale) of polII evidence for open chromatin
     ***/
    private float EncOCpolIIPVal;


    /***
     p-Value (PHRED-scale) of CTCF evidence for open chromatin
     ***/
    private float EncOCctcfPVal;


    /***
     p-Value (PHRED-scale) of Myc evidence for open chromatin
     ***/
    private float EncOCmycPVal;


    /***
     Peak signal for Dnase evidence of open chromatin
     ***/
    private float EncOCDNaseSig;


    /***
     Peak signal for Faire evidence of open chromatin
     ***/
    private float EncOCFaireSig;

    /***
     Peak signal for polII evidence of open chromatin
     ***/
    private float EncOCpolIISig;


    /***
     Peak signal for CTCF evidence of open chromatin
     ***/
    private float EncOCctcfSig;

    /***
     Peak signal for Myc evidence of open chromatin
     ***/
    private float EncOCmycSig;


    /***
     List of pvalues, phred and genomicFeature
     ***/
    private List <CaddValues> valuesCadd;

    public Cadd(String allele, String reference, String chr, int pos, float encExp, float encH3K27Ac, float encH3K4Me1, float encH3K4Me3, float encNucleo, int encOCC, float encOCCombPVal, float encOCDNasePVal, float encOCFairePVal, float encOCpolIIPVal, float encOCctcfPVal, float encOCmycPVal, float encOCDNaseSig, float encOCFaireSig, float encOCpolIISig, float encOCctcfSig, float encOCmycSig, List<CaddValues> valuesCadd) {
        this.allele = allele;
        this.reference = reference;
        this.chr = chr;
        this.pos = pos;
        this.EncExp = encExp;
        this.EncH3K27Ac = encH3K27Ac;
        this.EncH3K4Me1 = encH3K4Me1;
        this.EncH3K4Me3 = encH3K4Me3;
        this.EncNucleo = encNucleo;
        this.EncOCC = encOCC;
        this.EncOCCombPVal = encOCCombPVal;
        this.EncOCDNasePVal = encOCDNasePVal;
        this.EncOCFairePVal = encOCFairePVal;
        this.EncOCpolIIPVal = encOCpolIIPVal;
        this.EncOCctcfPVal = encOCctcfPVal;
        this.EncOCmycPVal = encOCmycPVal;
        this.EncOCDNaseSig = encOCDNaseSig;
        this.EncOCFaireSig = encOCFaireSig;
        this.EncOCpolIISig = encOCpolIISig;
        this.EncOCctcfSig = encOCctcfSig;
        this.EncOCmycSig = encOCmycSig;
        this.valuesCadd = valuesCadd;
    }

    public Cadd() { }


    public String getAllele() {
        return allele;
    }

    public void setAllele(String allele) {
        this.allele = allele;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getChr() {
        return chr;
    }

    public void setChr(String chr) {
        this.chr = chr;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public float getEncExp() {
        return EncExp;
    }

    public void setEncExp(float encExp) {
        EncExp = encExp;
    }

    public float getEncH3K27Ac() {
        return EncH3K27Ac;
    }

    public void setEncH3K27Ac(float encH3K27Ac) {
        EncH3K27Ac = encH3K27Ac;
    }

    public float getEncH3K4Me1() {
        return EncH3K4Me1;
    }

    public void setEncH3K4Me1(float encH3K4Me1) {
        EncH3K4Me1 = encH3K4Me1;
    }

    public float getEncH3K4Me3() {
        return EncH3K4Me3;
    }

    public void setEncH3K4Me3(float encH3K4Me3) {
        EncH3K4Me3 = encH3K4Me3;
    }

    public float getEncNucleo() {
        return EncNucleo;
    }

    public void setEncNucleo(float encNucleo) {
        EncNucleo = encNucleo;
    }

    public int getEncOCC() {
        return EncOCC;
    }

    public void setEncOCC(int encOCC) {
        EncOCC = encOCC;
    }

    public float getEncOCCombPVal() {
        return EncOCCombPVal;
    }

    public void setEncOCCombPVal(float encOCCombPVal) {
        EncOCCombPVal = encOCCombPVal;
    }

    public float getEncOCDNasePVal() {
        return EncOCDNasePVal;
    }

    public void setEncOCDNasePVal(float encOCDNasePVal) {
        EncOCDNasePVal = encOCDNasePVal;
    }

    public float getEncOCFairePVal() {
        return EncOCFairePVal;
    }

    public void setEncOCFairePVal(float encOCFairePVal) {
        EncOCFairePVal = encOCFairePVal;
    }

    public float getEncOCpolIIPVal() {
        return EncOCpolIIPVal;
    }

    public void setEncOCpolIIPVal(float encOCpolIIPVal) {
        EncOCpolIIPVal = encOCpolIIPVal;
    }

    public float getEncOCctcfPVal() {
        return EncOCctcfPVal;
    }

    public void setEncOCctcfPVal(float encOCctcfPVal) {
        EncOCctcfPVal = encOCctcfPVal;
    }

    public float getEncOCmycPVal() {
        return EncOCmycPVal;
    }

    public void setEncOCmycPVal(float encOCmycPVal) {
        EncOCmycPVal = encOCmycPVal;
    }

    public float getEncOCDNaseSig() {
        return EncOCDNaseSig;
    }

    public void setEncOCDNaseSig(float encOCDNaseSig) {
        EncOCDNaseSig = encOCDNaseSig;
    }

    public float getEncOCFaireSig() {
        return EncOCFaireSig;
    }

    public void setEncOCFaireSig(float encOCFaireSig) {
        EncOCFaireSig = encOCFaireSig;
    }

    public float getEncOCpolIISig() {
        return EncOCpolIISig;
    }

    public void setEncOCpolIISig(float encOCpolIISig) {
        EncOCpolIISig = encOCpolIISig;
    }

    public float getEncOCctcfSig() {
        return EncOCctcfSig;
    }

    public void setEncOCctcfSig(float encOCctcfSig) {
        EncOCctcfSig = encOCctcfSig;
    }

    public float getEncOCmycSig() {
        return EncOCmycSig;
    }

    public void setEncOCmycSig(float encOCmycSig) {
        EncOCmycSig = encOCmycSig;
    }

    public List<CaddValues> getValuesCadd() {
        return valuesCadd;
    }

    public void setValuesCadd(List<CaddValues> valuesCadd) {
        this.valuesCadd = valuesCadd;
    }
}
