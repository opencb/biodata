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
package org.opencb.biodata.tools.variant.simulator;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantType;

/**
 * Created by imedina on 08/10/15.
 * @author kalyan
 * @author pawan
 */
public class VariantSimulator {

    private Configuration configuration;
    private Random rand;
    private String referenceAllele = null;
    private String alternateAllele = null;

    public VariantSimulator() {
        this(new Configuration());
    }

    public VariantSimulator(Configuration configuration) {
        this.configuration = configuration;
        rand = new Random();
    }

    public Variant simulate() {
        return _create(10, configuration.getRegions());
    }

    /**
     * @param batchSize
     * @return variants
     */
    public List<Variant> simulate(int batchSize) {
        List<Variant> variants = new ArrayList<>(batchSize);
        Variant variant;
        for (int i=0; i < batchSize; i++) {
            variant = _create(10, configuration.getRegions());
            variants.add(variant);
        }
        return variants;
    }

    /**
     * @param batchSize
     * @param numSamples
     * @param regions
     * @return variants
     */
    public List<Variant> simulate(int batchSize, int numSamples, List<Region> regions) {
        batchSize = Math.max(batchSize, 1);
        numSamples = (numSamples <= 0) ? configuration.getNumSamples() : numSamples;
        regions = (regions == null || regions.isEmpty()) ? configuration.getRegions() : regions;

        List<Variant> variants = new ArrayList<>(batchSize);
        Variant variant;
        for (int i=0; i < batchSize; i++) {
            variant = _create(numSamples, regions);
            variants.add(variant);
        }
        return variants;
    }

    /**
     * @param numSamples
     * @param regions
     * @return variant
     */
    private Variant _create(int numSamples, List<Region> regions) {
        if (numSamples < 1) {
            throw new IllegalArgumentException();
        }
        if (regions == null || regions.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String id = null;
        String variantType = null;
        String strand = "+";
        List<StudyEntry> studyEntry = new ArrayList<>();
        id = genId();
        String[] refAlt = generateReferenceAlternate();
        referenceAllele = refAlt[0];
        alternateAllele = refAlt[1];
        variantType = getVariantType();

        //get number of sample
        studyEntry = getStudies(100);

        //TODO will be used in future
        /*Map<String, List<String>> hgvsMap = new HashMap<>();
        List<String> hgvsList = new ArrayList<>();*/

        Variant variant = new Variant();
        //set Chromosome, start and end in mapper
        variant.setReference(referenceAllele);
        variant.setAlternate(alternateAllele);
        variant.setId(id);
        variant.setStudies(studyEntry);        
        variant.setStrand(strand);
        variant.setType(VariantType.valueOf(variantType));
        variant.setAnnotation(null);
        variant.setHgvs(null);
        return variant;
    }

    /*public String genChromose(int min, int max) {
        int randomNum = rand.nextInt((max - min) + 1) + min;
        return Integer.toString(randomNum);
    }
    public int start() {
        int n = 100000 + rand.nextInt(900000);
        return n;
    }
    public int end() {
        int n = 100000 + rand.nextInt(900000);
        return n;
    }*/

    /**
     * @return id
     */
    public String genId() {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double randomDouble = rand.nextDouble();

        String idProbabilities = df.format(randomDouble);
        String id = null;
        int n = 0;
        if (idProbabilities.equals("0.1")){
            n = 100000 + rand.nextInt(900000);
            id = "rs" + Integer.toString(n);
        } else {
            id = "null";
        }        
        return id;
    }

    /*public String getReference(String alleles) {
        final int n = alleles.length();
        Character s = alleles.charAt((rand.nextInt(n)));
        return s.toString();
    }
    public String getAlternate(String alleles) {
        final int n = alleles.length();
        Character s = alleles.charAt((rand.nextInt(n)));
        return s.toString();
    }*/

    /**
     * @return
     */
    public String[] generateReferenceAlternate() {

        String[] refAltArray = new String[10];
        String[] refAlt = {"A", "C", "G", "T"};

        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double randomDouble = rand.nextDouble();

        String refAltProbabilities = df.format(randomDouble);
        String randomRef = (refAlt[rand.nextInt(refAlt.length)]);
        String randomAlt = (refAlt[rand.nextInt(refAlt.length)]);

        if (refAltProbabilities.equals("0.5")){
            refAltArray[0] = randomRef;
            refAltArray[1] = randomAlt;
        } else if (refAltProbabilities.equals("0.3")) {
            refAltArray[0] = randomRef + refAlt[rand.nextInt(refAlt.length)];
            refAltArray[1] = randomAlt + refAlt[rand.nextInt(refAlt.length)];
        } else if (refAltProbabilities.equals("0.2")) {
            refAltArray[0] = randomRef + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)]
                    + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)];
            refAltArray[1] = randomAlt + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)];
        } else if (refAltProbabilities.equals("0.1")) {
            refAltArray[0] = randomRef;
            refAltArray[1] = randomAlt + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)]
                    + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)] + refAlt[rand.nextInt(refAlt.length)];
        } else if (refAltProbabilities.equals("0.9")) {
            refAltArray[0] = "-";
            refAltArray[1] = randomAlt ;
        } else {
            refAltArray[0] = randomRef;
            refAltArray[1] = randomAlt;
        }
        return refAltArray;
    }

    /**
     * @return type
     */
    public String getVariantType() {
        List<String> variants = new LinkedList<String>();
        variants.add("SNP");
        variants.add("MNP");
        variants.add("MNV");
        variants.add("SNV");
        variants.add("INDEL");
        variants.add("SV");
        variants.add("CNV");
        variants.add("NO_VARIATION");
        variants.add("SYMBOLIC");
        variants.add("MIXED");

        String type = null;

        if (referenceAllele.length() == 1 && alternateAllele.length() > 1) {
            type = variants.get(9);            
        } else if (!referenceAllele.equals("-") && referenceAllele.length() == 1) {
            if (alternateAllele.length() == 1) {
                type = variants.get(3);
            }
        } else if (referenceAllele.length() > 1 && alternateAllele.length() > 1) {
            if (referenceAllele.length() == alternateAllele.length()) {
                type = variants.get(2);
            } else {
                type = variants.get(9);
            }
        } else if (referenceAllele.equals("-")) {
            type = variants.get(4);
        } else {
            type = variants.get(9);
        }
        return type;
    }

    /**
     * @param n
     * @return studyEntryList
     */
    public List<StudyEntry> getStudies(int n) {
        int studyID = 2;
        int fieldID = 3;

        List<StudyEntry> studyEntryList = new ArrayList<>();
        StudyEntry studyEntry = new StudyEntry();
        studyEntry.setStudyId(Integer.toString(studyID));
        studyEntry.setFileId(Integer.toString(fieldID));
        Map<String, String> attributes = genAttributes(); 
        studyEntry.setAttributes(attributes);
        studyEntry.setFormat(getFormat());
        List<List<String>> sampleList = new ArrayList<>(getFormat().size());
        for (int i = 0; i < n; i++) {
            sampleList.add(getRandomample());
        }
        studyEntry.setSamplesData(sampleList);
        studyEntryList.add(studyEntry);
        return studyEntryList;
    }

    /**
     * @return sample
     */
    private List<String> getRandomample() {
        List<String> sample = new ArrayList<>();
        int gqValue = rand.nextInt(100 - 0 + 100) + 0;
        int dpValue = rand.nextInt(100 - 0 + 100) + 0;
        int hqValue = rand.nextInt(100 - 0 + 100) + 0;

        // Nacho example
        int genotypeIndex = rand.nextInt(1000);
        String genotype = configuration.getGenotypeValues()[genotypeIndex];

        //sample.add(gtValue1 + "/" + gtValue2);
        sample.add(genotype);
        sample.add(Integer.toString(gqValue));
        sample.add(Integer.toString(dpValue));
        sample.add(Integer.toString(hqValue));
        return sample;
    }

    /**
     * @return formatFields formatFields
     */
    public List<String> getFormat() {
        List<String> formatFields = new ArrayList<>(10);
        formatFields.add("GT");
        formatFields.add("GQ");
        formatFields.add("DP");
        formatFields.add("HQ");
        return formatFields;
    }

    /**
     * @return attributeMap attributeMap
     */
    public Map<String, String> genAttributes() {

        Map<String, String> attributeMap = new HashMap<>(); 

        int acLength = alternateAllele.length();
        //int afLength = alternateAllele.length();
        int anLength = referenceAllele.length() + alternateAllele.length();
        //int dpLength = alternateAllele.length();

        String alleleACVal = String.valueOf(acLength);
        String alleleAFVal = String.valueOf(rand.nextInt(10 - 0 + 0) + 0);
        String alleleANVal = String.valueOf(anLength);
        String alleleDPVal = String.valueOf(rand.nextInt(200 - 100 + 0) + 100);

        attributeMap.put("AC", alleleACVal);
        attributeMap.put("AF", alleleAFVal);
        attributeMap.put("AN", alleleANVal);
        attributeMap.put("DP", alleleDPVal);

        return attributeMap;
    }

}