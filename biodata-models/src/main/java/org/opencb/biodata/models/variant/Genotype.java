/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.variant;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

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
    public static final String NA = "NA";

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
        this(genotype, ref, Arrays.asList(alt.split(",")));
    }

    public Genotype(String genotype, String ref, List<String> alternates) {
        this.reference = ref;
        this.alternates = alternates;
        this.count = 0;
        parseGenotype(genotype);
    }

    public Genotype(Genotype other) {
        this.reference = other.reference;
        this.alternates = other.alternates;
        this.count = other.count;
        this.allelesIdx = Arrays.copyOf(other.allelesIdx, other.allelesIdx.length);
        this.phased = other.phased;
        this.code = other.code;
    }

    public static boolean isHet(String gt) {
        if (gt.length() == 3 && (gt.charAt(1) == '/' || gt.charAt(1) == '|')) {
            return gt.charAt(0) != gt.charAt(2) && gt.charAt(0) != '.'&& gt.charAt(2) != '.';
        } else if (gt.length() == 1) {
            return false;
        }
        int[] alleles = new Genotype(gt).getAllelesIdx();
        int firstAllele = alleles[0];
        if (firstAllele < 0 || alleles.length == 1) {
            // Discard if first allele is missing, or if haploid
            return false;
        }
        for (int i = 1; i < alleles.length; i++) {
            int allele = alleles[i];
            if (allele == firstAllele || allele < 0) {
                return false;
            }
        }
        return true;
    }

    public static boolean isHom(String gt) {
        if (gt.length() == 3 && (gt.charAt(1) == '/' || gt.charAt(1) == '|')) {
            return gt.charAt(0) == gt.charAt(2) && gt.charAt(0) != '.';
        } else if (gt.length() == 1) {
            return gt.charAt(0) != '.';
        }
        int[] alleles = new Genotype(gt).getAllelesIdx();
        int firstAllele = alleles[0];
        if (firstAllele < 0) {
            // Discard if missing
            return false;
        }
        for (int i = 1; i < alleles.length; i++) {
            if (alleles[i] != firstAllele) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return true if the given genotype has the main allele.
     *
     * e.g. 0/1, 1/1, 1/2, ...
     *
     * @param gt the genotype to test
     * @return if has the main allele
     */
    public static boolean hasMainAlternate(String gt) {
        switch (gt) {
            case HOM_REF:
            case "0":
            case ".":
            case "./.":
                return false;
            case HET_REF:
            case HOM_VAR:
            case "1|1":
            case "0|1":
            case "1|0":
                return true;
            default:
                for (int allelesIdx : new Genotype(gt).getAllelesIdx()) {
                    if (allelesIdx == 1) {
                        return true;
                    }
                }
                return false;
        }
    }

    private void parseGenotype(String genotype) {
        switch (genotype) {
            case HOM_REF:
                this.code = AllelesCode.ALLELES_OK;
                this.allelesIdx = new int[]{0, 0};
                this.phased = false;
                break;
            case HET_REF:
                this.code = AllelesCode.ALLELES_OK;
                this.allelesIdx = new int[]{0, 1};
                this.phased = false;
                break;
            case HOM_VAR:
                this.code = AllelesCode.ALLELES_OK;
                this.allelesIdx = new int[]{1, 1};
                this.phased = false;
                break;
            default:
                parseOtherGenotype(genotype);
                break;
        }
    }

    private void parseOtherGenotype(String genotype) {
        /*CUSTOM PARSER*/
        ArrayList<String> alleles = new ArrayList<>(2);
        int lastIdx = 0;
        int allelesLength;
        for (int i = 0; i < genotype.length(); i++) {
            char c = genotype.charAt(i);
            if (c == '/' || c == '|') {
                alleles.add(genotype.substring(lastIdx, i));
                lastIdx = i + 1;
            }
        }
        if (lastIdx != genotype.length() + 1) {
            alleles.add(genotype.substring(lastIdx));
        }
        allelesLength = alleles.size();
        this.phased = isPhased(genotype);
        this.allelesIdx = new int[allelesLength];
        boolean missingAlleles = false;
        boolean allelesOk = false;
        boolean multipleAlternates = false;

        for (int i = 0, allelesSize = allelesLength; i < allelesSize; i++) {
            String allele = alleles.get(i);
//            String allele = alleles[i];

            if (allele.equals(".") || allele.equals("-1")) {
                missingAlleles = true;
                this.allelesIdx[i] = -1;
            } else {
                allelesOk = true;
                char ch;
                if (allele.length() == 1 && ((ch = allele.charAt(0)) >= '0' && ch <= '9')) {
                    this.allelesIdx[i] = ch - '0';
                } else if (StringUtils.isNumeric(allele)) { // Accepts genotypes with form 0/0, 0/1, and so on
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
                    multipleAlternates = true;
                }
            }
        }

        if (allelesOk && !missingAlleles) {
            if (multipleAlternates) {
                this.code = AllelesCode.MULTIPLE_ALTERNATES;
            } else {
                this.code = AllelesCode.ALLELES_OK;
            }
        } else if (!allelesOk && missingAlleles) {
            this.code = AllelesCode.ALLELES_MISSING;
        } else {
            this.code = AllelesCode.PARTIAL_ALLELES_MISSING;
        }

    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public String getAlternate() {
        return alternates == null || alternates.isEmpty() ? null : alternates.get(0);
    }

    public void setAlternate(String alternate) {
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

    public int getPloidy() {
        return allelesIdx.length;
    }

    public boolean isHaploid() {
        return getPloidy() == 1;
    }

    /**
     * @deprecated use {@link #setAlleleIdx}
     */
    @Deprecated
    public void updateAlleleIdx(int idx, int allele){
        setAlleleIdx(idx, allele);
    }

    public void setAlleleIdx(int idx, int allele){
        this.allelesIdx[idx] = allele;
    }

    public void normalizeAllelesIdx() {
        Arrays.sort(allelesIdx);
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

    public static boolean isPhased(String genotype) {
        return genotype.contains("|");
    }

    public void setPhased(boolean phased) {
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

    @Override
    public String toString() {
        StringBuilder value = new StringBuilder();
        appendAllele(value, this.allelesIdx[0]);
        char separator = isPhased() ? '|' : '/';
        for (int i = 1; i < allelesIdx.length; i++) {
            value.append(separator);
            appendAllele(value, this.allelesIdx[i]);
        }
        return value.toString();
    }

    private void appendAllele(StringBuilder sb, int allelesIdx) {
        if (allelesIdx < 0) {
            sb.append(".");
        } else if (allelesIdx <= 9) {
            sb.append(((char) (allelesIdx + '0')));
        } else {
            sb.append(allelesIdx);
        }
    }

    /**
     * @deprecated Use {@link #toString()}
     */
    @Deprecated
    public String toGenotypeString() {
        return toString();
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

    public static List<Genotype> parse(String genotype) {
        if (StringUtils.isBlank(genotype)) {
            return Collections.emptyList();
        }
        return Arrays.stream(genotype.split(",")).map(Genotype::new).collect(Collectors.toList());
    }

    public static int getPloidy(String gt) {
        if (gt.length() == 3 && (gt.charAt(1) == '/' || gt.charAt(1) == '|')) {
            return 2;
        } else if (gt.length() == 1) {
            return 1;
        } else {
            return new Genotype(gt).getPloidy();
        }
    }
}
