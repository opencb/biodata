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

package org.opencb.biodata.tools.variant.converter;

import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFFileReader;
import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.commons.utils.FileUtils;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;


/**
 * @author Pawan Pal & Kalyan
 *
 */
public class VariantContextToVariantConverter {

    private final String studyId;
    private final String fileId;

    public VariantContextToVariantConverter(){
        this("", "");
    }

    public VariantContextToVariantConverter(String studyId, String fileId) {
        this.studyId = studyId;
        this.fileId = fileId;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.avroidl.service.IVariantHtsjdkVCFReader#readVCFFile(java.lang.String,
     * java.lang.String)
     */
    public void readVCFFile(Path vcfFilePath, Path outputAvroFilePath) throws IOException {
        FileUtils.checkFile(vcfFilePath);
        FileUtils.checkDirectory(outputAvroFilePath.getParent(), true);

        List<Variant> variantList = new ArrayList<>();
        VCFFileReader vcfFileReader = new VCFFileReader(vcfFilePath.toFile(), false);
        Iterator<VariantContext> itr = vcfFileReader.iterator();
        while (itr.hasNext()) {
            VariantContext variantContext = itr.next();
            Variant variant = convert(variantContext);
            variantList.add(variant);
        }

        /*
         * method writeHtsjdkDataIntoAvro to write VCF data into AVRO format
         */
        writeHtsjdkDataIntoAvro(variantList, outputAvroFilePath.toAbsolutePath().toString());
    }

    public Variant convert(VariantContext variantContext) {
        return convert(variantContext, new Variant());
    }

    /**
     *
     * @param variantContext
     * @param reuse an instance to reuse.
     * @return
     */
    public Variant convert(VariantContext variantContext, Variant reuse) {
        Variant variant = reuse;

        variant.setChromosome(variantContext.getContig());
        variant.setStart(variantContext.getStart());
        variant.setEnd(variantContext.getEnd());

        // Setting reference and alternate alleles
        variant.setReference(variantContext.getReference().getDisplayString());
        List<Allele> alternateAlleleList = variantContext.getAlternateAlleles();
        if (alternateAlleleList != null && !alternateAlleleList.isEmpty()) {
            variant.setAlternate(alternateAlleleList.get(0).toString());
        } else {
            variant.setAlternate("");
        }

        //Do not need to store dot ID. It means that this variant does not have any ID
        String[] idsArray = variantContext.getID().split(VCFConstants.ID_FIELD_SEPARATOR);
        List<String> ids = new ArrayList<>(idsArray.length);
        for (String id : idsArray) {
            if (!id.equals(".")) {
                ids.add(id);
            }
        }
        variant.setIds(ids);

        // TODO length needs to be properly calculated
        variant.setLength(variantContext.getEnd() - variantContext.getStart() + 1);

        variant.setType(getEnumFromString(VariantType.class, variantContext.getType().toString()));

        // set parameter for HGVS hard coded values for time being as these value will not be getting from HTSJDK currently.
        // TODO: Add HGVS information
        Map<String, List<String>> hgvsMap = new HashMap<>();
        variant.setHgvs(hgvsMap);


        // set variantSourceEntry fields
        List<VariantSourceEntry> studies = new ArrayList<>();
        VariantSourceEntry variantSourceEntry = new VariantSourceEntry();

        // For time being setting the hard coded values for FileId and Study ID
        variantSourceEntry.setStudyId(studyId);


        FileEntry fileEntry = new FileEntry();
        fileEntry.setFileId(fileId);
        fileEntry.setCall(variantContext.getStart()
                + ":" + variantContext.getReference()
                + ":" + StringUtils.join(variantContext.getAlternateAlleles(), ","));
        Map<String, String> attributes = new HashMap<>();
        for (String key : variantContext.getAttributes().keySet()) {
            attributes.put(key, variantContext.getAttributeAsString(key, ""));
        }
        fileEntry.setAttributes(attributes);
        variantSourceEntry.setFiles(Collections.singletonList(fileEntry));


        // We need to convert Allele object to String
        // We skip the first alternate allele since these are the secondaries
        List<String> secondaryAlternateList = new ArrayList<>();
        for (int i = 1; i < variantContext.getAlternateAlleles().size(); i++) {
            secondaryAlternateList.add(variantContext.getAlternateAlleles().get(i).toString());
        }
        variantSourceEntry.setSecondaryAlternates(secondaryAlternateList);


        // set variant format
        // FIXME: This code is not respecting the original format order
        List<String> formatFields = new ArrayList<>(10);
        if (variantContext.getGenotypes().size() > 1) {
            htsjdk.variant.variantcontext.Genotype gt = variantContext.getGenotypes().get(0);

            //GT Field is mandatory and MUST be the first one
            formatFields.add(VCFConstants.GENOTYPE_KEY);

            if (gt.hasGQ()) {
                formatFields.add(VCFConstants.GENOTYPE_QUALITY_KEY);
            }
            if (gt.hasAD()) {
                formatFields.add(VCFConstants.GENOTYPE_ALLELE_DEPTHS);
            }
            if (gt.hasDP()) {
                formatFields.add(VCFConstants.DEPTH_KEY);
            }
            if (gt.hasPL()) {
                formatFields.add(VCFConstants.GENOTYPE_PL_KEY);
            }

            for (String key : gt.getExtendedAttributes().keySet()) {
                formatFields.add(key);
            }
        }
        variantSourceEntry.setFormat(formatFields);


        // set sample data parameters Eg: GT:GQ:GQX:DP:DPF:AD 1/1:63:29:22:7:0,22
        List<List<String>> sampleDataList = new ArrayList<>(variantContext.getSampleNames().size());
        for (String sampleName : variantContext.getSampleNames()) {
            htsjdk.variant.variantcontext.Genotype genotype = variantContext.getGenotype(sampleName);
            List<String> sampleList = new ArrayList<>(formatFields.size());

            for (String formatField : formatFields) {
                final String value;
                switch (formatField) {
                    case VCFConstants.GENOTYPE_KEY:
                        //TODO: Change from specific allele genotype to codified genotype (A/C -> 0/1)
                        value = genotype.getGenotypeString();
                        break;
                    default:
                        Object attribute = genotype.getAnyAttribute(formatField);
                        if (attribute != null) {
                            if (attribute instanceof Collection) {
                                value = ((List<Object>) attribute).stream().map(Object::toString).collect(Collectors.joining(","));
                            } else {
                                value = attribute.toString();
                            }
                        } else {
                            //TODO: Can hts return null fields?
                            value = ".";
                            System.err.println("Null value");
                        }
                        break;
                }
                sampleList.add(value);
            }
            sampleDataList.add(sampleList);
        }
        variantSourceEntry.setSamplesData(sampleDataList);


        /*
         * set stats fields. Putting hard coded values for time
         * being as these value will not be getting from HTSJDK
         * currently.
         */
        Map<String, VariantStats> stats = new HashMap<>();
        //TODO: Call to the Variant Aggregated Stats Parser
//        stats.put(
//                "2",
//                setVariantStatsParams(
//                        setVariantHardyWeinbergStatsParams(),
//                        variantContext));
        variantSourceEntry.setStats(stats);

        studies.add(variantSourceEntry);
        variant.setStudies(studies);


        // set VariantAnnotation parameters
        // TODO: Read annotation from info column
//        variant.setAnnotation(setVariantAnnotationParams());


        return variant;
    }

    /**
     * method to set Consequence Type Parameters
     * @return consequenceTypeList
     */
    public List<ConsequenceType> setConsequenceTypeParams(){

        List<ConsequenceType> consequenceTypeList = new ArrayList<>();
        ConsequenceType consequenceType = new ConsequenceType();
        consequenceType.setGeneName(null);
        consequenceType.setEnsemblGeneId(null);
        consequenceType.setEnsemblTranscriptId(null);
        consequenceType.setStrand(null);
        consequenceType.setBiotype(null);
        consequenceType.setCDnaPosition(null);
        consequenceType.setCdsPosition(null);
        consequenceType.setCodon(null);

        /*
         * set ExpressionValues list type parameter
         */
//        List<ExpressionValue> expressionValueList = new ArrayList<>();
//        ExpressionValue expressionValue = new ExpressionValue();
//        expressionValue.setExpression(getEnumFromString(org.opencb.biodata.models.variant.avro.ExpressionCall.class, "UP"));
        /*expressionValue.setExperimentalFactor(null);
        expressionValue.setExperimentId(null);
        expressionValue.setExpression(null);
        expressionValue.setFactorValue(null);
        expressionValue.setPvalue(null);
        expressionValue.setTechnologyPlatform(null);*/
//        expressionValueList.add(expressionValue);
//        consequenceType.setExpressionValues(expressionValueList);

        /*
         * set ProteinSubstitutionScores list type parameter
         */
//        List<Score> proteinSubstitutionScoreList = new ArrayList<>();
//        Score score = new Score(null, null, null);
//        proteinSubstitutionScoreList.add(score);


        ProteinVariantAnnotation proteinVariantAnnotation = new ProteinVariantAnnotation();
        proteinVariantAnnotation.setSubstitutionScores(Arrays.asList());
        consequenceType.setProteinVariantAnnotation(proteinVariantAnnotation);

        /*
         * set SoTerms list type parameter
         */
        List<SequenceOntologyTerm> sequenceOntologyTerms = new ArrayList<>();
        SequenceOntologyTerm sequenceOntologyTerm = new SequenceOntologyTerm();
        sequenceOntologyTerm.setAccession(null);
        sequenceOntologyTerm.setName(null);
        sequenceOntologyTerms.add(sequenceOntologyTerm);
        consequenceType.setSequenceOntologyTerms(sequenceOntologyTerms);

        consequenceType.setStrand(null);
        /*
         * Add consequenceType final bean to list
         */
        consequenceTypeList.add(consequenceType);
        return consequenceTypeList;
    }

    /**
     * method to set Population Frequency Parameters
     * @return populationFrequencyList
     */
    public List<PopulationFrequency> setPopulationFrequencyParams(){

        List<PopulationFrequency> populationFrequencyList = new ArrayList<>();
        PopulationFrequency populationFrequency = new PopulationFrequency();
        populationFrequency.setAltAllele(null);
        populationFrequency.setAltAlleleFreq(null);
        populationFrequency.setAltHomGenotypeFreq(null);
        populationFrequency.setHetGenotypeFreq(null);
        populationFrequency.setPopulation(null);
        populationFrequency.setRefAllele(null);
        populationFrequency.setRefAlleleFreq(null);
        populationFrequency.setRefHomGenotypeFreq(null);
        populationFrequency.setStudy(null);
        populationFrequency.setSuperPopulation(null);

        populationFrequencyList.add(populationFrequency);
        return populationFrequencyList;
    }


    /**
     * method to set Varaint Annotation Parameters
     * @return variantAnnotation
     */
    public VariantAnnotation setVaraintAnnotationParams(){
        VariantAnnotation variantAnnotation = new VariantAnnotation();
        /*
         * set AdditionalAttributes map type parameter
         */
        Map<String, String> additionalAttributesMap = new HashMap();
        //additionalAttributesMap.put(null, null);
        variantAnnotation.setAdditionalAttributes(additionalAttributesMap);
        /*
         * set AlternateAllele parameter
         */
        variantAnnotation.setAlternate(null);
        /*
         * set CaddScore list type parameter
         */
//        List<CaddScore> caddScoreList = new ArrayList<>();
//        CaddScore caddScore = new CaddScore();
        /*caddScore.setCScore(null);
        caddScore.setRawScore(null);
        caddScore.setTranscriptId(null);*/
//        caddScoreList.add(caddScore);
//        variantAnnotation.setCaddScore(caddScoreList);
        /*
         * set Chromosome parameter
         */
        variantAnnotation.setChromosome(null);

        /*
         * set Clinical map type parameter
         */
        variantAnnotation.setVariantTraitAssociation(new VariantTraitAssociation(Arrays.asList(), Arrays.asList(),Arrays.asList()));

        /*
         * set ConsequenceTypes list type parameter
         */
        variantAnnotation.setConsequenceTypes(setConsequenceTypeParams());
        /*
         * set ConservationScores list type parameter
         */
        List<Score> conservationScoreList = new ArrayList<>();
        Score score = new Score();
        /*score.setDescription(null);
        score.setScore(null);
        score.setSource(null);    */
        conservationScoreList.add(score);
        variantAnnotation.setConservation(conservationScoreList);

        variantAnnotation.setEnd(0);
        /*
         * set GeneDrugInteraction map of list type parameter
         */
//        Map<String, List<String>> geneDrugInteractionMap = new HashMap<>();
        List<GeneDrugInteraction> geneDrugInteractionList = new ArrayList<>();
//        List<String> geneDrugInteractionList = new ArrayList<>();
        //geneDrugInteractionList.add("AAA");
        //geneDrugInteractionMap.put("000", geneDrugInteractionList);
        variantAnnotation.setGeneDrugInteraction(geneDrugInteractionList);

        /*
         * set Hgvs list type parameter
         */
        List<String> hgvsList = new ArrayList<>();
        //hgvsList.add(null);
        variantAnnotation.setHgvs(hgvsList);

        variantAnnotation.setId(null);
        /*
         * set PopulationFrequencies list type parameter
         */
        variantAnnotation.setPopulationFrequencies(setPopulationFrequencyParams());

        variantAnnotation.setReference(null);
        variantAnnotation.setStart(0);
        /*
         * set Xref list type parameter
         */
        List<Xref> xrefsList = new ArrayList<>();
        Xref xref = new Xref();
        /*xref.setId(null);
        xref.setSrc(null);*/
        xrefsList.add(xref);
        variantAnnotation.setXrefs(xrefsList);
        /*
         * return variantAnnotation bean
         */
        return variantAnnotation;
    }

    /**
     * get sample data
     * @param keyString
     * @param valueString
     * @return
     */
    public static Map<String, String> getSampleDataMap(
            String keyString, String valueString) {

        String keyArray[] = keyString.split(":");
        String valueArray[] = valueString.split(":");

        Map<String, String> sampleDataMap = new HashMap<>();
        for (int i = 0; i < keyArray.length; i++) {
            sampleDataMap.put(keyArray[i], valueArray[i]);
        }
        return sampleDataMap;
    }


    /**
     * method to get secondary allele
     * @param secondaryAllele
     * @return secondaryAllelArrayList
     */
    public static List<String> getSecondaryAlternateAllele(String secondaryAllele) {

        String secondaryAllelArray[] = null;
        StringBuilder secondaryAlleleString = new StringBuilder();
        String secondaryAlleleArrayTemp[] = secondaryAllele.trim().split(",");
        if (secondaryAlleleArrayTemp.length >= 2) {
            for (int i = 1; i < secondaryAlleleArrayTemp.length; i++) {
                secondaryAlleleString.append(secondaryAlleleArrayTemp[i].trim());
                secondaryAlleleString.append(",");
            }
        }
        secondaryAllelArray = secondaryAlleleString.substring(0,
                secondaryAlleleString.length() - 1).split(",");

        return Arrays.asList(secondaryAllelArray);
    }

    /**
     * method to set Variant Stats Parameters
     * @param variantHardyWeinbergStats
     * @param variantContext
     * @return variantStats
     */
    private VariantStats setVariantStatsParams(
            VariantHardyWeinbergStats variantHardyWeinbergStats,
            VariantContext variantContext) {

        VariantStats variantStats = new VariantStats();
        variantStats.setAltAllele("aa");
        variantStats.setAltAlleleCount(1);
        variantStats.setAltAlleleFreq(2.1f);
        variantStats.setCasesPercentDominant(3.1f);
        variantStats.setCasesPercentRecessive(5.1f);
        variantStats.setControlsPercentDominant(1.0f);
        variantStats.setControlsPercentRecessive(3.1f);
        variantStats.setMaf(4f);
        variantStats.setMafAllele("ss");
        variantStats.setMendelianErrors(4);
        variantStats.setMgf(3f);
        variantStats.setMgfGenotype("AA");
        variantStats.setMissingAlleles(3);
        variantStats.setMissingGenotypes(3);
        variantStats.setNumSamples(4);
        variantStats.setPassedFilters(true);
        variantStats.setQuality((float) variantContext.getPhredScaledQual());
        variantStats.setRefAllele("SS");
        variantStats.setRefAlleleCount(4);
        variantStats.setRefAlleleFreq(2f);
        variantStats.setHw(variantHardyWeinbergStats);
        variantStats.setVariantType(getEnumFromString(
                VariantType.class, variantContext.getType()
                        .toString()));

        return variantStats;
    }

    /**
     * method to set VariantHardyWeinberg Stats Parameters
     * @return variantHardyWeinbergStats
     */
    private VariantHardyWeinbergStats setVariantHardyWeinbergStatsParams() {
        VariantHardyWeinbergStats variantHardyWeinbergStats = new VariantHardyWeinbergStats();
        variantHardyWeinbergStats.setChi2(1f);
        variantHardyWeinbergStats.setEAa00(2f);
        variantHardyWeinbergStats.setEAa10(3f);
        variantHardyWeinbergStats.setEAA11(4f);
        variantHardyWeinbergStats.setN(1);
        variantHardyWeinbergStats.setNAa00(2);
        variantHardyWeinbergStats.setNAa10(3);
        variantHardyWeinbergStats.setNAA11(4);
        variantHardyWeinbergStats.setP(1f);
        variantHardyWeinbergStats.setQ(2f);
        return variantHardyWeinbergStats;
    }

    /**
     * method which will write data into Avro format
     * @param vcfBean
     * @param outputAvroFilePath
     * @throws FileNotFoundException
     */
    public void writeHtsjdkDataIntoAvro(List<Variant> vcfBean, String outputAvroFilePath) throws IOException {

        if (outputAvroFilePath.isEmpty()) {
            throw new FileNotFoundException("Output file path is empty or null...");
        }

        FileOutputStream outputStream = new FileOutputStream(outputAvroFilePath);
        DatumWriter<VariantAvro> vcfDatumWriter = new SpecificDatumWriter<>(VariantAvro.class);
        DataFileWriter<VariantAvro> vcfdataFileWriter = new DataFileWriter<>(vcfDatumWriter);

        Variant variant =  new Variant();
        vcfdataFileWriter.setCodec(CodecFactory.snappyCodec());
        vcfdataFileWriter.create(variant.getImpl().getSchema(), outputStream);

        for (Variant variantAvro : vcfBean) {
            vcfdataFileWriter.append(variantAvro.getImpl());
        }
        vcfdataFileWriter.flush();
        vcfdataFileWriter.close();
        outputStream.close();
    }


    /**
     * Method to convert avro data to variantContext
     * @return
     * @throws IOException
     */
    public void convertToVariantContext(String avroFilePath, String contextFilePath) throws IOException{
        /*
         * Read avro file and set value to the bean
         */
        DatumReader<Variant> variantDatumReader = new SpecificDatumReader<Variant>(Variant.class);
        DataFileReader<Variant> variantDataFileReader = new DataFileReader<Variant>(new File(avroFilePath), variantDatumReader);
        Variant readAvro = null;

        File file = new File(contextFilePath);
        FileWriter fw = new FileWriter(file.getAbsoluteFile());
        BufferedWriter bw = new BufferedWriter(fw);

        while (variantDataFileReader.hasNext()) {
            AvroToVariantContextBean avroToVariantContextBean = new AvroToVariantContextBean();
            readAvro = variantDataFileReader.next(readAvro);

            /*
             * Set chromosome
             */
            avroToVariantContextBean.setChrom(readAvro.getChromosome());
            /*
             * Set position
             */
            int pos = readAvro.getStart().intValue();
            int position = 0;
            if ((readAvro.getLength()) == 1) {
                position = pos;
                avroToVariantContextBean.setPos(position);
            } else {
                position = pos - 1;
                avroToVariantContextBean.setPos(position);
            }
            /*
             * Set reference
             */
            avroToVariantContextBean.setRef(readAvro.getReference());

            //get parameters from variant source entry
            List<String> secondaryAltList = new ArrayList<>();
            String secondaryAlt = "";
            String format = "";
            String filter = "";
            String quality = "";
            String sample = "";
            String info = "";
//            for(Map.Entry<String, VariantSourceEntry> srcEntry: readAvro.getStudies().entrySet()){
            for(VariantSourceEntry srcEntry: readAvro.getStudies()){
                //get secondary alternate
//                secondaryAltList = srcEntry.getValue().getSecondaryAlternates();
                secondaryAltList = srcEntry.getSecondaryAlternates();
                for(String secAlt : secondaryAltList){
                    if(secAlt.equals("null")){
                        secondaryAlt = "";
                    }else{
                        secondaryAlt = secAlt;
                    }
                }
                //get format
//                format = srcEntry.getValue().getFormat().toString();
                format = srcEntry.getFormat().stream().collect(Collectors.joining(VCFConstants.FORMAT_FIELD_SEPARATOR));
                //get filter
//                for(Entry<String, VariantStats> qual : srcEntry.getValue().getCohortStats().entrySet()){
                for(Entry<String, VariantStats> qual : srcEntry.getStats().entrySet()){
                    if(qual.getValue().getPassedFilters().toString().equals("true")){
                        filter = "Pass";
                    } else {
                        filter = "Fail";
                    }
                }
                //get quality
//                for(Entry<String, VariantStats> qual : srcEntry.getValue().getCohortStats().entrySet()){
                for(Entry<String, VariantStats> qual : srcEntry.getStats().entrySet()){
                    quality = qual.getValue().getQuality().toString();
                }
                // TODO this code needs to be rethink
                //get sample
//                for(Entry<String, Map<String, String>> smpl : srcEntry.getValue().getSamplesDataAsMap().entrySet()){
//                    sample = smpl.getValue().toString();
//                }
                //get attributes
//                Map<String, String> attributeMap = srcEntry.getValue().getAttributes();
                Map<String, String> attributeMap = srcEntry.getAttributes();
                for(Map.Entry<String, String> attribute : attributeMap.entrySet()){
                    info += attribute.getKey()+"="+attribute.getValue()+";";
                    info=info.substring(0, info.length()-1);
                }
            }
            /*
             * Set alternate
             */
            String alternate = readAvro.getAlternate() + "," + secondaryAlt;
            alternate=alternate.substring(0, alternate.length()-1);
            avroToVariantContextBean.setAlt(alternate);
            /*
             * set format
             */
            avroToVariantContextBean.setFormat(format);
            /*
             * set filter
             */
            avroToVariantContextBean.setFilter(filter);
            /*
             * set quality
             */
            avroToVariantContextBean.setQual(quality);
            /*
             * set sample
             */
            avroToVariantContextBean.setSample1(sample);
            /*
             * set id
             */
            List<String> idList = readAvro.getIds();
            String ids = "";
            for(String idStr : idList){
                ids=ids+idStr+",";
            }
            ids=ids.substring(0, ids.length()-1);
            avroToVariantContextBean.setId(ids);
            /*
             * set info
             */
            avroToVariantContextBean.setInfo(info);

            /*System.out.println(avroToVariantContextBean.getChrom() +"  "+ avroToVariantContextBean.getPos() +"  "+ avroToVariantContextBean.getId()
                    +"  "+ avroToVariantContextBean.getRef() +"  "+ avroToVariantContextBean.getAlt() +"  "+ avroToVariantContextBean.getQual()
                     +"  "+ avroToVariantContextBean.getFilter() +"  "+ avroToVariantContextBean.getInfo()
                     +" "+ avroToVariantContextBean.getFormat() +"  "+ avroToVariantContextBean.getSample1()
                     );*/

            bw.write(avroToVariantContextBean.getChrom() +"\t"+ avroToVariantContextBean.getPos() +"\t"+ avroToVariantContextBean.getId()
                    +"\t"+ avroToVariantContextBean.getRef() +"\t"+ avroToVariantContextBean.getAlt() +"\t"+ avroToVariantContextBean.getQual()
                    +"\t"+ avroToVariantContextBean.getFilter() +"\t"+ avroToVariantContextBean.getInfo()
                    +"\t"+ avroToVariantContextBean.getFormat() +"\t"+ avroToVariantContextBean.getSample1());
            bw.write("\n");
        }
        bw.close();
        variantDataFileReader.close();
    }


    /**
     * @param variantType
     * @param string
     * @return
     */
    public static <E extends Enum<E>> E getEnumFromString(Class<E> variantType, String string) {
        if (variantType != null && string != null) {
            try {
                return Enum.valueOf(variantType, string.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Unknown variantType " + string);
            }
        }
        return null;
    }
}