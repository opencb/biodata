/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.models.feature;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.protobuf.VariantProto.Genotype.Builder;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Matthias Haimel &lt;mh719--git@cam.ac.uk&gt;
 */
public class Genotype {

    public static final String NOCALL = ".";
    public static final String HOM_REF = "0/0";
    public static final String HET_REF = "0/1";
    public static final String HOM_VAR = "1/1";

    private String reference;
    private List<String> alternates;
    private int[] allelesIdx;
    private boolean phased;
    
    private AllelesCode code;
    
    private int count;
    protected static final Pattern genotypePattern = Pattern.compile("/|\\|");

    Genotype() {
    }
    
    public Genotype(String genotype) {
        this(genotype, null, Collections.emptyList());
    }
    
    public Genotype(String genotype, String ref, String alt) {
        this(genotype, ref, Collections.singletonList(alt));
    }

    public Genotype(String genotype, String ref, List<String> alternates) {
        this.reference = ref;
        this.alternates = alternates;
        this.phased = genotype.contains("|");
        this.count = 0;
        parseGenotype(genotype);
    }

    public Genotype(org.opencb.biodata.models.variant.protobuf.VariantProto.Genotype gt){
        this.allelesIdx = ArrayUtils.toPrimitive(gt.getAllelesIdxList().toArray(new Integer[0]));
        this.reference = gt.getReference();
        this.alternates = new ArrayList<String>(gt.getAlternatesList());
        this.phased = gt.getPhased();
        this.code = AllelesCode.valueOf(gt.getCode().name());
    }

    private void parseGenotype(String genotype) {
        String[] alleles = genotypePattern.split(genotype, -1);
        
        this.code = alleles.length > 1 ? AllelesCode.ALLELES_OK : AllelesCode.HAPLOID;
        this.allelesIdx = new int[alleles.length];
        
        for (int i = 0; i < alleles.length; i++) {
            String allele = alleles[i];
            
            if (allele.equals(".") || allele.equals("-1")) {
                this.code = AllelesCode.ALLELES_MISSING;
                this.allelesIdx[i] = -1;
            } else {
                if (StringUtils.isNumeric(allele)) { // Accepts genotypes with form 0/0, 0/1, and so on
                    this.allelesIdx[i] = Integer.parseInt(allele);
                    
                } else { // Accepts genotypes with form A/A, A/T, and so on
                    if (allele.equalsIgnoreCase(reference)) {
                        this.allelesIdx[i] = 0;
                    } else {
                        if (allele.isEmpty()) {
                            throw new IllegalArgumentException("Unable to parse genotype \'" + genotype + "\'. Empty allele: REF=" + reference + ",ALT=" + alternates.stream().collect(Collectors.joining(",")));
                        }
                        int alleleIdx = 1;
                        for (String alternate : alternates) {
                            if (allele.equalsIgnoreCase(alternate)) {
                                this.allelesIdx[i] = alleleIdx;
                                break;
                            }
                            alleleIdx++;
                        }
                        if (alleleIdx > alternates.size()) {
                            throw new IllegalArgumentException("Unable to parse genotype \'" + genotype + "'. Unknown allele \"" + allele + "\". REF=" + reference + ",ALT=" + alternates.stream().collect(Collectors.joining(",")));
                        }
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

    void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternates == null || alternates.isEmpty() ? null : alternates.get(0);
    }

    void setAlternate(String alternate) {
        this.alternates = Collections.singletonList(alternate);
    }

    public List<String> getAlternates() {
        return alternates;
    }

    public Genotype setAlternates(List<String> alternates) {
        this.alternates = alternates;
        return this;
    }

    public int getAllele(int i) {
        return allelesIdx[i];
    }
    
    public int[] getAllelesIdx() {
        return allelesIdx;
    }

    public void updateAlleleIdx(int idx, int allele){
        this.allelesIdx[idx] = allele;
    }

    public int[] getNormalizedAllelesIdx() {
        int[] sortedAlleles = Arrays.copyOf(allelesIdx, allelesIdx.length);
        Arrays.sort(sortedAlleles);
        return sortedAlleles;
    }
    
    void setAllelesIdx(int[] allelesIdx) {
        this.allelesIdx = allelesIdx;
    }

    public boolean isAlleleRef(int i) {
        return allelesIdx[i] == 0;
    }

    public boolean isAllelesRefs(){
        int len = allelesIdx.length;
        for(int i = 0; i < len; ++i){
            if(! isAlleleRef(i))
                return false;
        }
        return true;
    }

    public boolean isPhased() {
        return phased;
    }

    void setPhased(boolean phased) {
        this.phased = phased;
    }

    public AllelesCode getCode() {
        return code;
    }

    void setCode(AllelesCode code) {
        this.code = code;
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

    public String getGenotypeInfo() {
        StringBuilder value = new StringBuilder(toString());
        value.append(" (REF=").append(reference);
        value.append(", ALT=").append(alternates.stream().collect(Collectors.joining(",")));
        value.append(")");
        return value.toString();
    }
    
    /**
     * Each allele is encoded as the ith-power of 10, being i the index where it is placed. Then its value 
     * (0,1,2...) is multiplied by that power.
     * 
     * Two genotypes with the same alleles but different phase will have different sign. Phased genotypes
     * have positive encoding, whereas unphased ones have negative encoding.
     * 
     * For instance, genotype 1/0 would be -10, 1|0 would be 10 and 2/1 would be -21.
     * 
     * @return A numerical encoding of the genotype
     */
    public int encode() {
        // TODO Support missing genotypes
        int encoding = 0;
        for (int i = 0; i < allelesIdx.length; i++) {
            encoding += Math.pow(10, allelesIdx.length - i - 1) * allelesIdx[i]; 
        }
        
        return isPhased() ? encoding : encoding * (-1);
    }
    
    public static Genotype decode(int encoding) {
        // TODO Support missing genotypes
        boolean unphased = encoding < 0;
        if (unphased) {
            encoding = Math.abs(encoding);
        }
        
        // TODO What to do with haploids?
        StringBuilder builder = new StringBuilder(String.format("%02d", encoding));
        for (int i = 0; i < builder.length() - 1; i += 2) {
            builder.insert(i + 1, unphased ? "/" : "|");
        }
        
        return new Genotype(builder.toString());
    }

    public String toGenotypeString() {
        StringBuilder value = new StringBuilder();
        value.append(allelesIdx[0] >= 0 ? allelesIdx[0] : ".");
        char separator = isPhased() ? '|' : '/';
        for (int i = 1; i < allelesIdx.length; i++) {
            value.append(separator);
            value.append(allelesIdx[i] >= 0 ? allelesIdx[i] : ".");
        }
        return value.toString();
    }

    @Override
    public String toString() {
        return toGenotypeString();
    }

    @Override
    public int hashCode() {
        int result = reference != null ? reference.hashCode() : 0;
        result = 31 * result + (alternates != null ? alternates.hashCode() : 0);
        result = 31 * result + (allelesIdx != null ? Arrays.hashCode(allelesIdx) : 0);
        result = 31 * result + (phased ? 1 : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + count;
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Genotype)) return false;

        Genotype genotype = (Genotype) o;

        if (phased != genotype.phased) return false;
        if (count != genotype.count) return false;
        if (reference != null ? !reference.equals(genotype.reference) : genotype.reference != null) return false;
        if (alternates != null ? !alternates.equals(genotype.alternates) : genotype.alternates != null) return false;
        if (!Arrays.equals(allelesIdx, genotype.allelesIdx)) return false;
        return code == genotype.code;
    }

    public org.opencb.biodata.models.variant.protobuf.VariantProto.Genotype toProtobuf(){
        Builder pb = org.opencb.biodata.models.variant.protobuf.VariantProto.Genotype.newBuilder();
        pb.addAllAllelesIdx(Arrays.asList(ArrayUtils.toObject(this.allelesIdx)));
        pb.addAllAlternates(this.alternates);
        pb.setPhased(this.phased);
        pb.setCode(org.opencb.biodata.models.variant.protobuf.VariantProto.AllelesCode.valueOf(this.code.name()));
        return pb.build();
    }
}
