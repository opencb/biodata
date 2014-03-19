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
    private int mendelianErrors;
    private int missingGenotypes;
    private int homozygotesNumber;


    public VariantSingleSampleStats(String id) {
        this.id = id;
        this.mendelianErrors = 0;
        this.missingGenotypes = 0;
        this.homozygotesNumber = 0;
    }

    public void incrementMendelianErrors() {
        this.mendelianErrors++;
    }

    public void incrementMissingGenotypes() {
        this.missingGenotypes++;
    }

    public void incrementHomozygotesNumber() {
        this.homozygotesNumber++;
    }

    public String getId() {
        return id;
    }

    public int getMendelianErrors() {
        return mendelianErrors;
    }

    public int getMissingGenotypes() {
        return missingGenotypes;
    }

    public int getHomozygotesNumber() {
        return homozygotesNumber;
    }

    public void incrementMendelianErrors(int mendelianErrors) {
        this.mendelianErrors += mendelianErrors;
    }

    public void incrementMissingGenotypes(int missingGenotypes) {
        this.missingGenotypes += missingGenotypes;
    }

    public void incrementHomozygotesNumber(int homozygotesNumber) {
        this.homozygotesNumber += homozygotesNumber;

    }
}
