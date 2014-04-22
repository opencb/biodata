package org.opencb.biodata.models.feature;

import java.util.Arrays;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class Genotype {
    
    private String reference;
    private String alternate;
    private int[] allelesIdx;
    private boolean phased;
    
    private AllelesCode code;
    
    private int count;

    public Genotype(String genotype) {
        this(genotype, null, null);
    }
    
    public Genotype(String genotype, String ref, String alt) {
        this.reference = ref;
        this.alternate = alt;
        this.phased = genotype.contains("|");
        this.count = 0;
        parseGenotype(genotype);
    }

    private void parseGenotype(String genotype) {
        String[] alleles = genotype.split("/|\\|", -1);
        
        this.code = alleles.length > 1 ? AllelesCode.ALLELES_OK : AllelesCode.HAPLOID;
        this.allelesIdx = new int[alleles.length];
        
        for (int i = 0; i < alleles.length; i++) {
            String allele = alleles[i];
            if (allele.equals(".")) {
                this.code = AllelesCode.ALLELES_MISSING;
                this.allelesIdx[i] = -1;
            } else {
                if (StringUtils.isNumeric(allele)) { // Accepts genotypes with form 0/0, 0/1, and so on
                    this.allelesIdx[i] = Integer.parseInt(allele);
                } else { // Accepts genotypes with form A/A, A/T, and so on
                    if (allele.equalsIgnoreCase(reference)) {
                        this.allelesIdx[i] = 0;
                    } else if (allele.equalsIgnoreCase(alternate)) {
                        this.allelesIdx[i] = 1;
                    } else {
                        if (allele.isEmpty()) {
                            System.out.println("Empty allele: REF=" + reference + ",ALT=" + alternate);
                        }
                        this.allelesIdx[i] = 2; // TODO What happens with more than 2 alternate alleles? Difficult situation
                    }
                }
                
                if (allelesIdx[i] > 1) {
                    this.code = AllelesCode.MULTIPLE_ALTERNATES;
                }
            }
        }
    }

    public String getReference() {
        return reference;
    }

    public String getAlternate() {
        return alternate;
    }
    
    public int getAllele(int i) {
        return allelesIdx[i];
    }
    
    public boolean isAlleleRef(int i) {
        return allelesIdx[i] == 0;
    }
    
    public boolean isPhased() {
        return phased;
    }
    
    public AllelesCode getCode() {
        return code;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
    
    public void incrementCount(int count) {
        this.count += count;
    }

    public String getGenotype() {
        StringBuilder value = new StringBuilder();
        value.append(allelesIdx[0]);
        char separator = isPhased() ? '|' : '/';
        for (int i = 1; i < allelesIdx.length; i++) {
            value.append(separator);
            value.append(allelesIdx[i]);
        }
        return value.toString();
    }
    
    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        value.append(allelesIdx[0]);
        char separator = isPhased() ? '|' : '/';
        for (int i = 1; i < allelesIdx.length; i++) {
            value.append(separator);
            value.append(allelesIdx[i]);
        }
        value.append(" (REF=");
        value.append(reference);
        value.append(", ALT=");
        value.append(alternate);
        value.append(")");
        return value.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.reference);
        hash = 11 * hash + Objects.hashCode(this.alternate);
        hash = 11 * hash + Arrays.hashCode(this.allelesIdx);
        hash = 11 * hash + (this.phased ? 1 : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Genotype other = (Genotype) obj;
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (!Objects.equals(this.alternate, other.alternate)) {
            return false;
        }
        if (!Arrays.equals(this.allelesIdx, other.allelesIdx)) {
            return false;
        }
        if (this.phased != other.phased) {
            return false;
        }
        return true;
    }

}
