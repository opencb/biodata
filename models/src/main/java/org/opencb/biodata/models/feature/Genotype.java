package org.opencb.biodata.models.feature;

import java.util.Objects;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 8/26/13
 * Time: 6:09 PM
 * To change this template use File | Settings | File Templates.
 */
public class Genotype {
    private Integer allele1;
    private Integer allele2;
    private AllelesCode code;
    private Integer count;


    public Genotype(String genotype) {
        parseGenotype(genotype);

    }

    public Genotype(String genotype, String ref, String alt) {
        String[] alleles = genotype.split("/|\\|");
        StringBuilder newGenotype = new StringBuilder();
        if (alleles[0].equals(".")) {
            newGenotype.append(".");
        } else if (alleles[0].equals(ref)) {
            newGenotype.append("0");
        } else {
            String[] altAlleles = alt.split(",");
            String a = null;
            for (int i = 0; i < altAlleles.length; i++) {
                if (alleles[0].equals(altAlleles[i])) {
                    a = String.valueOf(i + 1);
                }
            }
            if (a != null) {
                newGenotype.append(a);
            } else {
                newGenotype.append(".");
            }
        }

        newGenotype.append("/");

        if (alleles[1].equals(".")) {
            newGenotype.append(".");
        } else if (alleles[1].equals(ref)) {
            newGenotype.append("0");
        } else {
            String[] altAlleles = alt.split(",");
            String a = null;
            for (int i = 0; i < altAlleles.length; i++) {
                if (alleles[1].equals(altAlleles[i])) {
                    a = String.valueOf(i + 1);
                }
            }
            if (a != null) {
                newGenotype.append(a);
            } else {
                newGenotype.append(".");
            }
        }

        parseGenotype(newGenotype.toString());

    }

    private void parseGenotype(String genotype) {
        this.code = null;
        this.count = 0;
        if (genotype.length() < 3) {
            this.allele1 = null;
            this.allele2 = null;
            this.code = AllelesCode.ALL_ALLELES_MISSING;
        } else {
            String[] auxAlleles = genotype.split("/|\\|");
            if (auxAlleles[0].equals(".")) {
                this.allele1 = null;
                this.code = AllelesCode.FIRST_ALLELE_MISSING;
            } else {
                this.allele1 = Integer.valueOf(auxAlleles[0]);
            }

            if (auxAlleles.length == 1) { // Haploid
                this.allele2 = null;
                this.code = AllelesCode.HAPLOID;

            } else {
                if (auxAlleles[1].equals(".")) {
                    this.allele2 = null;
                    this.code = (this.code == AllelesCode.FIRST_ALLELE_MISSING) ? AllelesCode.ALL_ALLELES_MISSING : AllelesCode.SECOND_ALLELE_MISSING;
                } else {
                    this.allele2 = Integer.valueOf(auxAlleles[1]);
                }
            }

        }
        if (this.code == null) {
            this.code = AllelesCode.ALLELES_OK;

        }
    }

    public Integer getAllele1() {
        return allele1;
    }

    public void setAllele1(Integer allele1) {
        this.allele1 = allele1;
        if (allele1 == null) {
            this.setCode(AllelesCode.FIRST_ALLELE_MISSING);
        }
        if (allele2 == null) {
            this.setCode(AllelesCode.ALL_ALLELES_MISSING);
        }
    }

    public Integer getAllele2() {
        return allele2;
    }

    public void setAllele2(Integer allele2) {
        this.allele2 = allele2;
        if (allele2 == null) {
            this.setCode(AllelesCode.FIRST_ALLELE_MISSING);
        }
        if (allele1 == null) {
            this.setCode(AllelesCode.ALL_ALLELES_MISSING);
        }
    }

    public AllelesCode getCode() {
        return code;
    }

    public void setCode(AllelesCode code) {
        this.code = code;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public boolean isAllele1Ref() {
        return allele1 == 0;
    }

    public boolean isAllele2Ref() {
        return allele2 == 0;
    }

    public String getGenotype() {
        StringBuilder sb = new StringBuilder(6);
        if (allele1 != null) {
            sb.append(allele1);
        } else {
            sb.append(".");
        }
        sb.append("/");

        if (allele2 != null) {
            sb.append(allele2);
        } else {
            sb.append(".");
        }
        return sb.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(6);
        if (allele1 != null) {
            sb.append(allele1);
        } else {
            sb.append(".");
        }
        sb.append("/");

        if (allele2 != null) {
            sb.append(allele2);
        } else {
            sb.append(".");
        }
        sb.append(":");
        sb.append(count);
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Genotype) {
            Genotype g = (Genotype) obj;
            return Objects.equals(this.getAllele1(), g.getAllele1()) &&
                    Objects.equals(this.getAllele2(), g.getAllele2());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.allele1);
        hash = 47 * hash + Objects.hashCode(this.allele2);
        return hash;
    }

}
