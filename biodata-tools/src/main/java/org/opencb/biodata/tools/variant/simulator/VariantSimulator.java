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

import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSourceEntry;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * Created by imedina on 08/10/15.
 */
public class VariantSimulator {

    private Configuration configuration;

    private Random rand;

    private Region start;
    private Region end;

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

    public List<Variant> simulate(int batchSize) {
        List<Variant> variants = new ArrayList<>(batchSize);
        Variant variant;
        for (int i=0; i < batchSize; i++) {
            variant = _create(10, configuration.getRegions());
            variants.add(variant);
        }
        return variants;
    }

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




    private Variant _create(int numSamples, List<Region> regions) {
        if (numSamples < 1) {
            throw new IllegalArgumentException();
        }

        if (regions == null || regions.isEmpty()) {
            throw new IllegalArgumentException();
        }

        String chromosome = null;
        int start = 0;
        int end = 0;
        String ID = null;
        String referenceAllele=null;
        String alternateAllele=null;

        String variantType = null;
        String strand = null;
        List<VariantSourceEntry> variantSourceEntry =new ArrayList<>();

        chromosome = genChromose(0 , 23);
        start = start();
        end = end();
        if (end < start) {
            end = start + rand.nextInt(9);
        }
        ID = genID();
        referenceAllele = getReference("BACD");
        alternateAllele = getAlternate("BACD");
        variantType = getVariantType();
        strand = genID();
        variantSourceEntry = getStudies(100);
        Variant variant = new Variant();

        return variant;
    }

    public String genChromose(int min, int max) {
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
    }
    public String genID() {
        int n = 100000 + rand.nextInt(900000);
        return "rs" + Integer.toString(n);
    }

    public String getReference(String alleles) {
        final int n = alleles.length();
        Character s = alleles.charAt((rand.nextInt(n)));
        return s.toString();

    }
    public String getAlternate(String alleles) {
        final int n = alleles.length();
        Character s = alleles.charAt((rand.nextInt(n)));
        return s.toString();

    }

    public String getVariantType() {
        List<String> variants = new LinkedList<>();
        variants.add("SNP");
        variants.add("MNP");
        variants.add("SNV");
        variants.add("INDEL");
        variants.add("SV");
        variants.add("CNV");
        variants.add("NO_VARIATION");
        variants.add("SYMBOLIC");
        variants.add("MIXED");
        int randomNumber = rand.nextInt(variants.size());
        return variants.get(randomNumber);
    }

    public List<VariantSourceEntry> getStudies(int n) {
        int studyID = 2;
        int fieldID = 3;
        Variant variant = new Variant();
        List<VariantSourceEntry> variantSourceEntryList=new ArrayList<>();
        VariantSourceEntry variantSourceEntry=new VariantSourceEntry();
        variantSourceEntry.setStudyId(Integer.toString(studyID));
        variantSourceEntry.setFileId(Integer.toString(fieldID));
        variantSourceEntry.setFormat(getFormat());
        List<List<String>> sampleList = new ArrayList<>(getFormat().size());
        for (int i=0; i < n; i++) {
            sampleList.add(getRandomample());
        }
        variantSourceEntry.setSamplesData(sampleList);
        variantSourceEntryList.add(variantSourceEntry);
        return variantSourceEntryList;
    }

    private List<String> getRandomample() {
        List<String> sample=new ArrayList<>();
        int gtValue1 = rand.nextInt(1 - 0 + 1) + 0;
        int gtValue2 = rand.nextInt(1 - 0 + 1) + 0;
        int gqValue = rand.nextInt(100 - 0 + 100) + 0;
        int dpValue = rand.nextInt(100 - 0 + 100) + 0;
        int hqValue = rand.nextInt(100 - 0 + 100) + 0;

        // Nacho example
        int genotypeIndex = rand.nextInt(1000);
        String genotype = configuration.getGenotypeValues()[genotypeIndex];


        sample.add(gtValue1 + "/" + gtValue2);
        sample.add(Integer.toString(gqValue));
        sample.add(Integer.toString(dpValue));
        sample.add(Integer.toString(hqValue));
        return sample;
    }
    public List<String> getFormat() {
        List<String> formatFields = new ArrayList<>(10);
        formatFields.add("GT");
        formatFields.add("GQ");
        formatFields.add("DP");
        formatFields.add("HQ");
        return formatFields;
    }

}
