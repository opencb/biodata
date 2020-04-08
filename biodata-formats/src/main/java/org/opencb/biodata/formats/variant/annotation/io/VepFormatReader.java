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

package org.opencb.biodata.formats.variant.annotation.io;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.formats.variant.vcf4.io.VariantVcfReader;
//import org.opencb.biodata.models.variant.annotation.ConsequenceType;
//import org.opencb.biodata.models.variant.annotation.Score;
//import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.opencb.biodata.models.variant.annotation.ConsequenceTypeMappings;
import org.opencb.biodata.models.variant.annotation.ProteinSubstitutionScores;
import org.opencb.biodata.models.variant.avro.*;
//import org.opencb.biodata.models.variation.PopulationFrequency;
//import org.opencb.biodata.models.variant.annotation.ProteinVariantAnnotation;
import org.opencb.commons.io.DataReader;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * Created by fjlopez on 07/04/15.
 */
public class VepFormatReader implements DataReader<VariantAnnotation> {

    private BufferedReader reader;
    private Path path;
    private String filename;
    private String currentVariantString = "";
    private VariantAnnotation currentAnnotation = null;

    private static final String CNV_PATTERN = "CN(([0123456789]+)|V)";

    public VepFormatReader(String filename) { this.filename = filename; }

    @Override
    public boolean open() {

        try {
            this.path = Paths.get(this.filename);
            Files.exists(this.path);

            if (path.toFile().getName().endsWith(".gz")) {
                this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
            } else {
                this.reader = Files.newBufferedReader(path, Charset.defaultCharset());
            }

        } catch (IOException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }


        return true;
    }

    @Override
    public boolean pre() { return true; }

    @Override
    public boolean close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public boolean post() { return true; }

    @Override
    public List<VariantAnnotation> read() {
        try {
            String line;
            Boolean noNewVariantFound = true;
            VariantAnnotation variantAnnotationToReturn = null;

            while ((line = reader.readLine()) != null && (line.trim().equals("") || line.startsWith("#"))) ;

            while ((line != null) && noNewVariantFound) {
                ConsequenceType consequenceType = new ConsequenceType();
                String[] lineFields = line.split("\t");
                // strings representing the current and the read are compared
                if (!currentVariantString.equals(lineFields[0] + ":" + lineFields[1] + ":" + lineFields[2])) {
                    noNewVariantFound = (currentAnnotation==null);  // currentAnnotation==null only in the first iteration.
                    variantAnnotationToReturn = currentAnnotation;
                    Map<String,String> variantMap = parseVariant(lineFields[0], lineFields[1], lineFields[2]);  // coordinates and alternative are only parsed onceca
                    currentAnnotation = new VariantAnnotation();
                    currentAnnotation.setChromosome(variantMap.get("chromosome"));
                    currentAnnotation.setStart(Integer.valueOf(variantMap.get("start")));
                    currentAnnotation.setEnd(Integer.valueOf(variantMap.get("end")));
                    currentAnnotation.setReference(variantMap.get("reference"));
                    currentAnnotation.setAlternate(variantMap.get("alternative"));

                    /**
                     * Set rs id
                     */
                    if (!lineFields[12].isEmpty() && !lineFields[12].equals("-")) {
                        currentAnnotation.setId(lineFields[12]);
                    }

                    /**
                     * Initialize list of consequence types
                     */
                    if(currentAnnotation.getConsequenceTypes()==null) {
                        currentAnnotation.setConsequenceTypes(new ArrayList<ConsequenceType>());
                    }

                    /**
                     * Save the string representing coordinates and
                     */
                    currentVariantString = lineFields[0] + ":" + lineFields[1] + ":" + lineFields[2];

                    /**
                     * parses extra column and populates fields as required. Some lines do not have extra field and end with a \t: the split function above does not return that field
                     * true parameter indicates the function to also parse frequencies
                     */
                    if(lineFields.length>13) {
                        parseExtraField(consequenceType, lineFields[13], true);
                    }

                    /**
                     * Another line must be read if this is the first variant in the file
                     */
                    if(noNewVariantFound) {
                        line = reader.readLine();
                    }
                } else {
                    /**
                     * Some lines do not have extra field and end with a \t: the split function above does not return that field
                     * false indicates the function to skip frequency attributes (were already parsed the first time this variant was seen)
                     */
                    if(lineFields.length>13) {
                        parseExtraField(consequenceType, lineFields[13], false);
                    }
                    line = reader.readLine();
                }
                // Remaining fields only of interest if the feature is a transcript
                if(lineFields[5].toLowerCase().equals("transcript")) {
                    parseRemainingFields(consequenceType, lineFields);
                // Otherwise just set SO terms
                } else {
                    consequenceType.setSequenceOntologyTerms(
                            getSequenceOntologyTerms(Arrays.asList(lineFields[6].split(","))));   // fill so terms
//                    consequenceType.setSoTermsFromSoNames(Arrays.asList(lineFields[6].split(",")));   // fill so terms
                }
                currentAnnotation.getConsequenceTypes().add(consequenceType);
            }

            /**
             * End of file.
             */
            if(line==null) {
                // Last variant was read, no more to read. Return last read variant and leave currentAnnotation=null so that if read() is called again, null will be returned
                if(currentAnnotation!=null) {
                    variantAnnotationToReturn = currentAnnotation;
                    currentAnnotation = null;
                } else {
                    return null;
                }
            }
            return Collections.singletonList(variantAnnotationToReturn);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    protected List<SequenceOntologyTerm> getSequenceOntologyTerms(List<String> SoNames) {
        List<SequenceOntologyTerm> sequenceOntologyTerms = new ArrayList<>(SoNames.size());
        for (String name : SoNames) {
            sequenceOntologyTerms.add(newSequenceOntologyTerm(name));
        }
        return sequenceOntologyTerms;
    }

    private SequenceOntologyTerm newSequenceOntologyTerm(String name) {
        return new SequenceOntologyTerm(ConsequenceTypeMappings.getSoAccessionString(name), name);
    }

    private void parseRemainingFields(ConsequenceType consequenceType, String[] lineFields) {
        consequenceType.setEnsemblGeneId(lineFields[3]);    // fill Ensembl gene id
        consequenceType.setEnsemblTranscriptId(lineFields[4]);  // fill Ensembl transcript id
        ProteinVariantAnnotation proteinVariantAnnotation = new ProteinVariantAnnotation();
        if(!lineFields[7].equals("-")) {
            consequenceType.setCdnaPosition(parseStringInterval(lineFields[7]));    // fill cdna position
        }
        if(!lineFields[8].equals("-")) {
            consequenceType.setCdsPosition(parseStringInterval(lineFields[8]));  // fill cds position
        }
        if(!lineFields[9].equals("-")) {
            proteinVariantAnnotation.setPosition(parseStringInterval(lineFields[9]));    // fill aa position
        }
        if(!lineFields[10].equals("-")) {
            String parts[] = lineFields[10].split("/");
            if(parts.length == 2) {
                proteinVariantAnnotation.setReference(parts[0]);  // fill aa change
                proteinVariantAnnotation.setAlternate(parts[1]);  // fill aa change
            }
        }
        consequenceType.setProteinVariantAnnotation(proteinVariantAnnotation);
        consequenceType.setCodon(lineFields[11]); // fill codon change
        if(!lineFields[6].equals("") && !lineFields.equals("-")) {  // VEP may leave this field empty
            consequenceType.setSequenceOntologyTerms(
                    getSequenceOntologyTerms(Arrays.asList(lineFields[6].split(","))));   // fill so terms
//            consequenceType.setSoTermsFromSoNames(Arrays.asList(lineFields[6].split(",")));    // fill so terms
        }
    }

    private Integer parseStringInterval(String stringInterval) {
        String[] parts = stringInterval.split("-");
        if(!parts[0].equals("?")) {
            return Integer.valueOf(parts[0]);
        } else if(parts.length>1 && !parts[1].equals("?"))  {
            return Integer.valueOf(parts[1]);
        } else {
            return null;
        }
    }

    private void parseExtraField(ConsequenceType consequenceType, String extraField, Boolean parseFrequencies) {

        for (String field : extraField.split(";")) {
            String[] keyValue = field.split("=");

            switch (keyValue[0].toLowerCase()) {
                case "aa_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "ESP_6500",
                                "African_American"));
                    }
                    break;
                case "afr_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "1000GENOMES",
                                "phase_1_AFR"));
                    }
                    break;
                case "amr_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "1000GENOMES",
                                "phase_1_AMR"));
                    }
                    break;
                case "asn_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "1000GENOMES",
                                "phase_1_ASN"));
                    }
                    break;
                case "biotype":
                    consequenceType.setBiotype(keyValue[1]);
                    break;
                case "ea_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "ESP_6500",
                                "European_American"));
                    }
                    break;
                case "eur_maf":
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "1000GENOMES",
                                "phase_1_EUR"));
                    }
                    break;
                case "gmaf": // Format is GMAF=G:0.2640  or  GMAF=T:0.1221,-:0.0905
                    if(parseFrequencies) {
                        if(currentAnnotation.getPopulationFrequencies()==null) {
                            currentAnnotation.setPopulationFrequencies(new ArrayList<PopulationFrequency>());
                        }
                        currentAnnotation.getPopulationFrequencies().add(parsePopulationFrequency(keyValue[1], "1000GENOMES",
                                "phase_1_ALL"));
                    }
                    break;
                case "hgvsc":
                    if(currentAnnotation.getHgvs()==null) {
                        currentAnnotation.setHgvs(new ArrayList<String>());
                    }
                    currentAnnotation.getHgvs().add(keyValue[1]);
                    break;
                case "hgvsp":
                    if(currentAnnotation.getHgvs()==null) {
                        currentAnnotation.setHgvs(new ArrayList<String>());
                    }
                    currentAnnotation.getHgvs().add(keyValue[1]);
                    break;
                case "polyphen": // Format is PolyPhen=possibly_damaging(0.859)
                    addProteinSubstitutionScore(consequenceType, parseProteinSubstitutionScore("Polyphen", keyValue[1]));
                    break;
                case "sift": // Format is SIFT=tolerated(0.07)
                    addProteinSubstitutionScore(consequenceType, parseProteinSubstitutionScore("Sift", keyValue[1]));
                    break;
                case "strand":
                    consequenceType.setStrand(keyValue[1].equals("1")?"+":"-");
                    break;
                case "symbol":
                    consequenceType.setGeneName(keyValue[1]);
                    break;
                default:
                    // ALLELE_NUM, FREQS, IND, ZYG
                    break;
            }
        }
    }

    private void addProteinSubstitutionScore(ConsequenceType consequenceType, Score score) {
        ProteinVariantAnnotation proteinVariantAnnotation = consequenceType.getProteinVariantAnnotation();
        List<Score> proteinSubstitionScores;
        if(proteinVariantAnnotation==null) {
            proteinVariantAnnotation = new ProteinVariantAnnotation();
            consequenceType.setProteinVariantAnnotation(proteinVariantAnnotation);
            proteinSubstitionScores = new ArrayList<>();
            proteinVariantAnnotation.setSubstitutionScores(proteinSubstitionScores);
        } else {
            proteinSubstitionScores = proteinVariantAnnotation.getSubstitutionScores();
        }
        proteinSubstitionScores.add(score);
    }

    private Score parseProteinSubstitutionScore(String predictorName, String scoreString) {
        String[] scoreFields = scoreString.split("[\\(\\)]");
        return new Score(Double.valueOf(scoreFields[1]), predictorName, scoreFields[0]);
    }

    private PopulationFrequency parsePopulationFrequency(String frequencyStrings, String study, String population) {
        PopulationFrequency populationFrequency = new PopulationFrequency();
        populationFrequency.setStudy(study);
        populationFrequency.setPopulation(population);
//        populationFrequency.setSuperPopulation(population);
        populationFrequency.setRefAllele(currentAnnotation.getReference());
        populationFrequency.setAltAllele(currentAnnotation.getAlternate());
        for(String frequencyString : frequencyStrings.split(",")) {
            String[] parts = frequencyString.split(":");
            if (parts[0].equals(currentAnnotation.getAlternate())) {
                populationFrequency.setAltAlleleFreq(Float.valueOf(parts[1]));
            } else {
                populationFrequency.setRefAlleleFreq(Float.valueOf(parts[1]));
            }
        }

        return populationFrequency;
    }

    private Map<String,String> parseVariant(String variantIdString, String coordinatesString, String alternate) {
        Map<String, String> parsedVariant = new HashMap<>(5);
        String[] variantLocationFields;
        try {
            variantLocationFields = coordinatesString.split("[:-]");
            parsedVariant.put("end", (variantLocationFields.length > 2) ? variantLocationFields[2] : variantLocationFields[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unexpected format for column 2: "+coordinatesString);
        }

        // Some VEP examples:
        // 1_718787_-/T    1:718786-718787 T    ...
        // 1_718787_T/-    1:718787        -    ...
        // 1_718788_T/A    1:718788        A    ...
        // rs559462325     21:9411239      A    ...
        // rs557738790     21:26192724-26192725    AAGAAAATTAATTTCTGTTGTCTGAAGTTG   ...
        String[] variantIdFields = variantIdString.split("[\\/]");
        if (variantIdFields.length < 2) {
            parseVariantFromOtherFields(parsedVariant, variantLocationFields, alternate);
        } else {
            parseVariantFromIdField(parsedVariant, variantIdFields);
            parsedVariant.put("alternative", alternate);
        }

        return parsedVariant;
    }

    private void parseVariantFromOtherFields(Map<String, String> parsedVariant, String[] variantLocationFields, String alternate) {
        parsedVariant.put("chromosome", variantLocationFields[0]);
        parsedVariant.put("alternative", alternate);
        // Deletion
        if (alternate.equals("-")) {
            parsedVariant.put("start", variantLocationFields[1]);
            // Reference sequence does not appear in VEP file - fill with Ns
            parsedVariant.put("reference", StringUtils.repeat("N", Integer.valueOf(parsedVariant.get("end"))
                    - Integer.valueOf(parsedVariant.get("start")) + 1));
            parsedVariant.put("alternative", alternate);
        // CNV
        } else if (alternate.matches(CNV_PATTERN)) {
            parsedVariant.put("start", variantLocationFields[1]);
            parsedVariant.put("reference", "N");
            parsedVariant.put("alternative", "<" + alternate + ">");
        // Insertion
        } else if (variantLocationFields.length > 2 || alternate.length() > 1) {
            parsedVariant.put("start", variantLocationFields[variantLocationFields.length == 3 ? 2 : 1]);
            parsedVariant.put("reference", "-");
            parsedVariant.put("alternative", alternate);
        // SNV. Reference nucleotide does not appear in VEP file - fill with N
        } else {
            parsedVariant.put("start", variantLocationFields[1]);
            parsedVariant.put("reference", "N");
            parsedVariant.put("alternative", alternate);
        }
    }

    private void parseVariantFromIdField(Map<String, String> parsedVariant, String[] variantIdFields) {
        try {
            // Some VEP examples:
            // 1_718787_-/T    1:718786-718787 T    ...
            // 1_718787_T/-    1:718787        -    ...
            // 1_718788_T/A    1:718788        A    ...
            String[] leftVariantFields = variantIdFields[0].split("_");

            // Chr id containing _
            if(leftVariantFields.length>3) {
                parsedVariant.put("chromosome",
                        String.join("_", (String[]) ArrayUtils.subarray(leftVariantFields, 0, leftVariantFields.length - 2)));
            } else {
                parsedVariant.put("chromosome", leftVariantFields[0]);
            }
            parsedVariant.put("start", leftVariantFields[leftVariantFields.length-2]);
            parsedVariant.put("reference", leftVariantFields[leftVariantFields.length-1]);
//            parsedVariant.put("alternative", variantIdFields[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new IllegalArgumentException("Unexpected variant format for column 1: "+variantIdFields.toString());
        }
    }

    @Override
    public List<VariantAnnotation> read(int batchSize) {
        List<VariantAnnotation> batch = new ArrayList<>(batchSize);
        List<VariantAnnotation> readRecords;
        int i = 0;
        while ((i < batchSize) && (readRecords = this.read()) != null) {
                batch.add(readRecords.get(0));
                i++;
        }
        return batch;
    }

}

