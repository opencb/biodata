package org.opencb.biodata.ga4gh;

import java.util.Arrays;

/**
 * Created by imedina on 27/08/14.
 */
public class GACall {

    /**
     * The ID of the call set this variant call belongs to. If this field is not present, the ordering of the call sets
     * from a SearchCallSetsRequest over this GAVariantSet is guaranteed to match the ordering of the calls on this
     * GAVariant. The number of results will also be the same
     */
    private String callSetId;

    /**
     * The name of the call set this variant call belongs to. If this field is not present, the ordering of the
     * call sets from a SearchCallSetsRequest over this GAVariantSet is guaranteed to match the ordering of the calls
     * on this GAVariant. The number of results will also be the same
     */
    private String callSetName;

    /**
     * The genotype of this variant call. Each value represents either the value of the referenceBases field or
     * is a 1-based index into alternateBases. If a variant had a referenceBases field of "T", an alternateBases
     * value of ["A", "C"], and the genotype was [2, 1], that would mean the call represented the heterozygous
     * value "CA" for this variant. If the genotype was instead [0, 1] the represented value would be "TA".
     * Ordering of the genotype values is important if the phaseset field is present
     *
     * NOTE: Arrays and Lists should be plural names
     */
    private int[] genotype;

    /**
     * If this field is present, this variant call's genotype ordering implies the phase of the bases and is consistent
     * with any other variant calls on the same contig which have the same phaseset value
     *
     * NOTE: should not this name be phaseSet?
     */
    private String phaseset;


    /**
     * The genotype likelihoods for this variant call. Each array entry represents how likely a specific genotype is
     * for this call. The value ordering is defined by the GL tag in the VCF spec
     *
     * NOTE: float should be enough for storing the likelihoods. Anyway this field is not very useful since each caller
     * use a different metric.
     */
    private double[] genotypeLikelihood;

    /**
     * A map of additional variant call information. In JSON, this looks like: info: {key1: value1, key2: value2}
     */
    private GAKeyValue[] info;

    public GACall() {

    }

    public GACall(String callSetId, String callSetName, int[] genotype, String phaseset, double[] genotypeLikelihood, GAKeyValue[] info) {
        this.callSetId = callSetId;
        this.callSetName = callSetName;
        this.genotype = genotype;
        this.phaseset = phaseset;
        this.genotypeLikelihood = genotypeLikelihood;
        this.info = info;
    }

    @Override
    public String toString() {
        return "GACall{" +
                "callSetId='" + callSetId + '\'' +
                ", callSetName='" + callSetName + '\'' +
                ", genotype=" + Arrays.toString(genotype) +
                ", phaseset='" + phaseset + '\'' +
                ", genotypeLikelihood=" + Arrays.toString(genotypeLikelihood) +
                ", info=" + Arrays.toString(info) +
                '}';
    }


    public String getCallSetId() {
        return callSetId;
    }

    public void setCallSetId(String callSetId) {
        this.callSetId = callSetId;
    }


    public String getCallSetName() {
        return callSetName;
    }

    public void setCallSetName(String callSetName) {
        this.callSetName = callSetName;
    }


    public int[] getGenotype() {
        return genotype;
    }

    public void setGenotype(int[] genotype) {
        this.genotype = genotype;
    }


    public String getPhaseset() {
        return phaseset;
    }

    public void setPhaseset(String phaseset) {
        this.phaseset = phaseset;
    }


    public double[] getGenotypeLikelihood() {
        return genotypeLikelihood;
    }

    public void setGenotypeLikelihood(double[] genotypeLikelihood) {
        this.genotypeLikelihood = genotypeLikelihood;
    }


    public GAKeyValue[] getInfo() {
        return info;
    }

    public void setInfo(GAKeyValue[] info) {
        this.info = info;
    }
}
