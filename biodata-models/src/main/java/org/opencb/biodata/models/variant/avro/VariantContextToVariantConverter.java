package org.opencb.biodata.models.variant.avro;


import htsjdk.variant.variantcontext.*;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;


/**
 * @author Pawan Pal & Kalyan
 *
 */
public class VariantContextToVariantConverter {

	private final String studyId;
	private final String fileId;


	@Deprecated
	public VariantContextToVariantConverter(){
		this.studyId = "1000g";
		this.fileId = "chr22";
	}

	public VariantContextToVariantConverter(String studyId,String fileId) {
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
	public void readVCFFile(String vcfFilePath,
			String outputAvroFilePath) throws IOException {

		/*
		 * VCF input file path
		 */
		String vcfPath = vcfFilePath;		

		/*
		 * Avro output file path 
		 */
		String outputAvroPath = outputAvroFilePath;

		/*
		 * Create VCFFileReader object 
		 */
		@SuppressWarnings("resource")
		VCFFileReader vcfFileReader = new VCFFileReader(new File(vcfPath), false);
		Iterator<VariantContext> itr = vcfFileReader.iterator();
		/*
		 * List for variant
		 */
		List<Variant> variantList = new ArrayList<Variant>();

		while (itr.hasNext()) {
			VariantContext variantContext = itr.next();
			Variant variant = convert(variantContext);
			variantList.add(variant);
		}
		/*
		 * method writeHtsjdkDataIntoAvro to write VCF data into AVRO format
		 */
		writeHtsjdkDataIntoAvro(variantList, outputAvroPath);
	}

	public Variant convert(VariantContext variantContext) {
		return convert(variantContext, new Variant());
	}

	/**
	 *
	 * @param variantContext
	 * @param reuse 			an instance to reuse.
	 * @return
	 */
	public Variant convert(VariantContext variantContext, Variant reuse) {
		Variant variant = reuse;

		/*
		 * set reference parameter
		 */
		variant.setReference(variantContext.getReference().getDisplayString());
		/*
		 * set alternate parameter
		 */
		String alternateAllelString = variantContext.getAlternateAlleles().toString().substring(1, variantContext.getAlternateAlleles().toString().length() - 1);
		String[] alternateAllelArray = alternateAllelString.split(",");
		String alternate = null;
		if (alternateAllelArray.length >= 2) {
			alternate = getAlternateAllele(alternateAllelString);
		} else {
			alternate = alternateAllelString;
		}
		variant.setAlternate(alternate);
		/*
		 * set variant type parameter
		 */
		variant.setVariantType(getEnumFromString(org.opencb.biodata.models.variant.avro.VariantType.class, variantContext
				.getType().toString()));
		/*
		 * set chromosome, start and end type parameter
		 */
		variant.setChromosome(variantContext.getContig());
		variant.setStart(variantContext.getStart());
		variant.setEnd(variantContext.getEnd());
		/*
		 * set id parameter
		 */
		String[] idsArray = variantContext.getID().split(",");
		List<CharSequence> ids = new ArrayList<>(idsArray.length);
		for (String id : idsArray) {
			//Do not need to store dot ID. It means that this variant does not have any ID
			if (!id.equals(".")) {
				ids.add(id);
			}
		}
		variant.setIds(ids);
		/*
		 * set length parameter
		 */
		variant.setLength(variantContext.getStart()
				- variantContext.getEnd() == 0 ? 1 : variantContext
				.getEnd() - variantContext.getStart() + 1);
		/*
		 * set variantSourceEntry fields
		 */
		Map<CharSequence, VariantSourceEntry> sourceEntry = new HashMap<CharSequence, VariantSourceEntry>();
		VariantSourceEntry variantSourceEntry = new VariantSourceEntry();
		// For time being setting the hard coded values for FileId and
		// Study ID
		variantSourceEntry.setFileId(fileId);
		variantSourceEntry.setStudyId(studyId);
		/*
		 * set secondary alternate
		 */
		List<CharSequence> secondaryAlternateAlleleList = new ArrayList<>();
		if (alternateAllelString.split(",").length >= 2) {
			secondaryAlternateAlleleList = getSecondaryAlternateAllele(alternateAllelString);

		}
		variantSourceEntry.setSecondaryAlternates(secondaryAlternateAlleleList);
		/*
		 * set variant format
		 */
		//FIXME
		String[] str = ((LazyGenotypesContext) variantContext
				.getGenotypes()).getUnparsedGenotypeData().toString()
				.split("\t", 2);
		variantSourceEntry.setFormat(str[0]);
		/*
		 * set sample data parameters Eg: GT:GQ:GQX:DP:DPF:AD
		 * 1/1:63:29:22:7:0,22
		 */
		Map<CharSequence, Map<CharSequence, CharSequence>> sampledataMap = new HashMap<CharSequence, Map<CharSequence, CharSequence>>();
		Map<CharSequence, CharSequence> sampledata = new HashMap<CharSequence, CharSequence>();
//		if (str[0].split(":").length == str[1].split(":").length) {
//			sampledata = getSampleDataMap(str[0], str[1]);
//		} else {
//			// this case will never occur
//			sampledata.put("error", "error"); //TODO: Throw exception
//		}
//		sampledataMap.put(variantContext.getSampleNames().toString(),
//				sampledata);
		for (String sampleName : variantContext.getSampleNames()) {
			htsjdk.variant.variantcontext.Genotype genotype = variantContext.getGenotype(sampleName);
			HashMap<String, String> sampleData = new HashMap<>();

			//TODO: Add rest of fields. Read from FORMAT column
			sampleData.put("GT", genotype.getGenotypeString());
			sampledataMap.put(sampleName, sampledata);
		}


		variantSourceEntry.setSamplesData(sampledataMap);
		/*
		 * set default cohort
		 */

		/*
		 * set cohortStats fields. Putting hard coded values for time
		 * being as these value will not be getting from HTSJDK
		 * currently.
		 */
		Map<CharSequence, VariantStats> cohortStats = new HashMap<CharSequence, VariantStats>();
		//TODO: Call to the Variant Aggregated Stats Parser
//		cohortStats.put(
//				"2",
//				setVariantStatsParams(
//						setVariantHardyWeinbergStatsParams(),
//						variantContext));
		variantSourceEntry.setCohortStats(cohortStats);
		/*
		 * set attribute fields. Putting hard coded values for time being
		 * as these value will not be getting from HTSJDK currently.
		 */
		Map<CharSequence, CharSequence> attributeMapNew = new HashMap<CharSequence, CharSequence>();
		Map<String, Object> attributeMap = variantContext.getAttributes();
		for (Map.Entry<String, Object> attr : attributeMap.entrySet()) {
			attributeMapNew.put(attr.getKey(), attr.getValue().toString());
		}
		variantSourceEntry.setAttributes(attributeMapNew);
		sourceEntry.put(studyId + "_" + fileId, variantSourceEntry);

		variant.setSourceEntries(sourceEntry);

		/*
		 * set parameter for HGVS Putting hard coded values for time
		 * being as these value will not be getting from HTSJDK
		 * currently.
		 */
		Map<CharSequence, List<CharSequence>> hgvsMap = new HashMap<CharSequence, List<CharSequence>>();
		//TODO: Add HGVS information
		List<CharSequence> hgvsList = new ArrayList<CharSequence>();
//		hgvsList.add("HGVS");
//		hgvsMap.put("11", hgvsList);
		variant.setHgvs(hgvsMap);

		/*
		 * set VariantAnnotation parameters
		 */
		//TODO: Read annotation from info column
//		variant.setAnnotation(setVaraintAnnotationParams());
		return variant;


	}
	/**
	 * method to set Consequence Type Parameters
	 * @return consequenceTypeList
	 */
	public List<ConsequenceType> setConsequenceTypeParams(){

		List<ConsequenceType> consequenceTypeList = new ArrayList<>();
		ConsequenceType consequenceType = new ConsequenceType();

		consequenceType.setAaChange(null);
		consequenceType.setAaPosition(null);
		consequenceType.setBiotype(null);
		consequenceType.setCDnaPosition(null);
		consequenceType.setCdsPosition(null);
		consequenceType.setCodon(null);
		consequenceType.setEnsemblGeneId(null);
		consequenceType.setEnsemblTranscriptId(null);
		/*
		 * set ExpressionValues list type parameter
		 */
		List<ExpressionValue> expressionValueList = new ArrayList<>();
		ExpressionValue expressionValue = new ExpressionValue();
		expressionValue.setExpression(getEnumFromString(
				org.opencb.biodata.models.variant.avro.Expression.class, "UP"));
		/*expressionValue.setExperimentalFactor(null);
		expressionValue.setExperimentId(null);
		expressionValue.setExpression(null);
		expressionValue.setFactorValue(null);
		expressionValue.setPvalue(null);
		expressionValue.setTechnologyPlatform(null);*/
		expressionValueList.add(expressionValue);
		consequenceType.setExpressionValues(expressionValueList);

		consequenceType.setFunctionalDescription(null);
		consequenceType.setGeneName(null);		
		/*
		 * set ProteinSubstitutionScores list type parameter
		 */
		List<Score> proteinSubstitutionScoreList = new ArrayList<>();
		Score score = new Score();
		score.setDescription(null);
		score.setScore(null);
		score.setSource(null);
		proteinSubstitutionScoreList.add(score);
		consequenceType.setProteinSubstitutionScores(proteinSubstitutionScoreList);		
		/*
		 * set SoTerms list type parameter
		 */
		List<ConsequenceTypeEntry> consequenceTypeEntryList = new ArrayList<>();
		ConsequenceTypeEntry consequenceTypeEntry = new ConsequenceTypeEntry();
		consequenceTypeEntry.setSoAccession(null);
		consequenceTypeEntry.setSoName(null);
		consequenceTypeEntryList.add(consequenceTypeEntry);
		consequenceType.setSoTerms(consequenceTypeEntryList);

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
		populationFrequency.setPop(null);
		populationFrequency.setRefAllele(null);
		populationFrequency.setRefAlleleFreq(null);
		populationFrequency.setRefHomGenotypeFreq(null);
		populationFrequency.setStudy(null);
		populationFrequency.setSuperPop(null);

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
		Map<CharSequence, CharSequence> additionalAttributesMap = new HashMap();
		//additionalAttributesMap.put(null, null);
		variantAnnotation.setAdditionalAttributes(additionalAttributesMap);		
		/*
		 * set AlternateAllele parameter
		 */
		variantAnnotation.setAlternate(null);
		/*
		 * set CaddScore list type parameter
		 */
		List<CaddScore> caddScoreList = new ArrayList<>();
		CaddScore caddScore = new CaddScore();
		/*caddScore.setCScore(null);
		caddScore.setRawScore(null);
		caddScore.setTranscriptId(null);*/
//		caddScoreList.add(caddScore);
//		variantAnnotation.setCaddScore(caddScoreList);
		/*
		 * set Chromosome parameter
		 */
		variantAnnotation.setChromosome(null);		
		/*
		 * set Clinical map type parameter
		 */
		Map<CharSequence, CharSequence> clinicalMap = new HashMap<>();
		//clinicalMap.put(null, null);
		variantAnnotation.setVariantTraitAssociation(clinicalMap);
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
		score.setSource(null);	*/
		conservationScoreList.add(score);
		variantAnnotation.setConservation(conservationScoreList);

		variantAnnotation.setEnd(0);		
		/*
		 * set GeneDrugInteraction map of list type parameter
		 */
		Map<CharSequence, List<CharSequence>> geneDrugInteractionMap = new HashMap<>();
		List<CharSequence> geneDrugInteractionList = new ArrayList<>();
		//geneDrugInteractionList.add("AAA");
		//geneDrugInteractionMap.put("000", geneDrugInteractionList);		
		variantAnnotation.setGeneDrugInteraction(geneDrugInteractionMap);		
		/*
		 * set Hgvs list type parameter
		 */
		List<CharSequence> hgvsList = new ArrayList<>();
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
	public static Map<CharSequence, CharSequence> getSampleDataMap(
			String keyString, String valueString) {

		String keyArray[] = keyString.split(":");
		String valueArray[] = valueString.split(":");

		Map<CharSequence, CharSequence> sampleDataMap = new HashMap<CharSequence, CharSequence>();
		for (int i = 0; i < keyArray.length; i++) {
			sampleDataMap.put(keyArray[i], valueArray[i]);
		}
		return sampleDataMap;
	}

	/**
	 * method to get alternate allele
	 * @return alternateAlleleString
	 */
	public static String getAlternateAllele(String alternateAllele) {
		//System.out.print("insdie method " + alternateAllele);


		StringBuffer secondaryAllelString = new StringBuffer();
		String secondaryAllelArrayTemp[] = alternateAllele.trim().split(",");
		if (secondaryAllelArrayTemp.length > 1) {
			for (int i = 0; i < secondaryAllelArrayTemp.length; i++) {
				if (i == 0) {
					secondaryAllelString.append(secondaryAllelArrayTemp[i]
							.toString().trim());
					break;

				}
			}
		}
		//System.out.println(secondaryAllelString.toString());
		return secondaryAllelString.toString().trim();
	}

	/**
	 * method to get secondary allele
	 * @param secondaryAllele
	 * @return secondaryAllelArrayList
	 */
	public static List<CharSequence> getSecondaryAlternateAllele(
			String secondaryAllele) {

		CharSequence secondaryAllelArray[] = null;
		StringBuffer secondaryAlleleString = new StringBuffer();
		CharSequence secondaryAlleleArrayTemp[] = secondaryAllele.trim().split(",");
		if (secondaryAlleleArrayTemp.length >= 2) {
			for (int i = 1; i < secondaryAlleleArrayTemp.length; i++) {
				secondaryAlleleString.append(secondaryAlleleArrayTemp[i]
						.toString().trim());
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
				org.opencb.biodata.models.variant.avro.VariantType.class, variantContext.getType()
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
	public void writeHtsjdkDataIntoAvro(List<Variant> vcfBean,
			String outputAvroFilePath) throws IOException {

		if (outputAvroFilePath.isEmpty()) {
			throw new FileNotFoundException(
					"Output file path is empty or null...");
		}

		FileOutputStream outputStream = new FileOutputStream(
				outputAvroFilePath);
		DatumWriter<Variant> vcfDatumWriter = new SpecificDatumWriter<Variant>(
				Variant.class);
		DataFileWriter<Variant> vcfdataFileWriter = new DataFileWriter<Variant>(
				vcfDatumWriter);

		Variant variant =  new Variant();
		vcfdataFileWriter.setCodec(CodecFactory.snappyCodec());
		vcfdataFileWriter.create(variant.getSchema(), outputStream);

		for (Variant variantAvro : vcfBean) {
			vcfdataFileWriter.append(variantAvro);
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
			avroToVariantContextBean.setChrom(readAvro.getChromosome().toString());
			/*
			 * Set position
			 */
			int pos= readAvro.getStart();
			int position = 0;
			if ((readAvro.getLength()) == 1){
				position = pos;
				avroToVariantContextBean.setPos(position);
			}else {
				position = pos-1;
				avroToVariantContextBean.setPos(position);
			}				
			/*
			 * Set reference 
			 */
			avroToVariantContextBean.setRef(readAvro.getReference().toString());

			//get parameters from variant source entry
			List<CharSequence> secondaryAltList = new ArrayList<CharSequence>();
			String secondaryAlt = "";
			String format = "";
			String filter = "";
			String quality = "";
			String sample = "";
			String info = "";
			for(Map.Entry<CharSequence, VariantSourceEntry> srcEntry: readAvro.getSourceEntries().entrySet()){
				//get secondary alternate
				secondaryAltList = srcEntry.getValue().getSecondaryAlternates();
				for(CharSequence secAlt : secondaryAltList){
					if(secAlt.toString().equals("null")){
						secondaryAlt = "";
					}else{
						secondaryAlt = secAlt.toString();
					}
				}
				//get format
				format = srcEntry.getValue().getFormat().toString();
				//get filter				
				for(Entry<CharSequence, VariantStats> qual : srcEntry.getValue().getCohortStats().entrySet()){
					if(qual.getValue().getPassedFilters().toString().equals("true")){
						filter = "Pass";
					}else{
						filter = "Fail";
					}
				}
				//get quality
				for(Entry<CharSequence, VariantStats> qual : srcEntry.getValue().getCohortStats().entrySet()){
					quality = qual.getValue().getQuality().toString();
				}
				//get sample
				for(Entry<CharSequence, Map<CharSequence, CharSequence>> smpl : srcEntry.getValue().getSamplesData().entrySet()){
					sample = smpl.getValue().toString();
				}
				//get attributes
				Map<CharSequence, CharSequence> attributeMap = srcEntry.getValue().getAttributes();
				for(Map.Entry<CharSequence, CharSequence> attribute : attributeMap.entrySet()){
					info += attribute.getKey()+"="+attribute.getValue()+";";
					info=info.substring(0, info.length()-1);
				}
			}			
			/*
			 * Set alternate
			 */
			String alternate = readAvro.getAlternate().toString()+","+ secondaryAlt;
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
			List<CharSequence> idList = readAvro.getIds();
			String ids = "";
			for(CharSequence idStr : idList){
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
	public static <VariantType extends Enum<VariantType>> VariantType getEnumFromString(
			Class<VariantType> variantType, String string) {
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