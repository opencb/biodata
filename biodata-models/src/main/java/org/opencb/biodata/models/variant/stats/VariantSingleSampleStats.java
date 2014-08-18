package org.opencb.biodata.models.variant.stats;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/29/13
 * Time: 10:21 AM
 * To change this template use File | Settings | File Templates.
 */
public class VariantSingleSampleStats {

    private String id;
    private int numMendelianErrors;
    private int numMissingGenotypes;
    private int numHomozygous;


    public VariantSingleSampleStats(String id) {
        this.id = id;
        this.numMendelianErrors = 0;
        this.numMissingGenotypes = 0;
        this.numHomozygous = 0;
    }

    public void incrementMendelianErrors() {
        this.numMendelianErrors++;
    }

    public void incrementMissingGenotypes() {
        this.numMissingGenotypes++;
    }

    public void incrementHomozygous() {
        this.numHomozygous++;
    }

    public String getId() {
        return id;
    }

    public int getNumMendelianErrors() {
        return numMendelianErrors;
    }

    public int getNumMissingGenotypes() {
        return numMissingGenotypes;
    }

    public int getNumHomozygous() {
        return numHomozygous;
    }

    public void incrementMendelianErrors(int mendelianErrors) {
        this.numMendelianErrors += mendelianErrors;
    }

    public void incrementMissingGenotypes(int missingGenotypes) {
        this.numMissingGenotypes += missingGenotypes;
    }

    public void incrementHomozygotesNumber(int homozygotesNumber) {
        this.numHomozygous += homozygotesNumber;

    }
}
