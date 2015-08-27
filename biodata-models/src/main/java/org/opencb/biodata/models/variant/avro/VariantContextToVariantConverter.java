package org.opencb.biodata.models.variant.avro;

import htsjdk.tribble.index.IndexFactory;
import htsjdk.variant.variantcontext.LazyGenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.avro.file.CodecFactory;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;

/**
 * @author Pawan Pal & Kalyan
 *
 */
public class VariantContextToVariantConverter {

	private static final long serialVersionUID = 1L;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.avroidl.service.IVariantHtsjdkVCFReader#readVCFFile(java.lang.String,
	 * java.lang.String)
	 */
	@SuppressWarnings("deprecation")
	public void readVCFFile(String vcfFilePath, String tbiFilePath,
			String outputAvroFilePath) throws FileNotFoundException {

		// This is the path for VCF and VCF Index
		String vcfPath = null;
		String tbiPath = null;
		// Load the tbi file to support the indexing
		IndexFactory.loadIndex(tbiPath);

		@SuppressWarnings("resource")
		VCFFileReader vcfFileReader = new VCFFileReader(new File(vcfPath));
		Iterator<VariantContext> itr = vcfFileReader.iterator();

		Variant varRec = new Variant();
		/*
		 * List for variant
		 */
		List<Variant> variantList = new ArrayList<Variant>();

		try {
			while (itr.hasNext()) {
				Variant variant = new Variant();
				VariantContext variantContext = itr.next();

				/*
				 * set reference parameter
				 */
				variant.setReference(variantContext.getReference().toString());

				/*
				 * set alternate parameter
				 */
				String[] alternateAllelArray = variantContext
						.getAlternateAlleles().toString().split(",");
				String alternate = null;
				if (alternateAllelArray.length >= 2) {
					alternate = getAlternateAllele(variantContext
							.getAlternateAlleles().toString());
				} else {
					alternate = variantContext
							.getAlternateAlleles()
							.toString()
							.substring(
									1,
									variantContext.getAlternateAlleles()
											.toString().length() - 1);
				}
				variant.setAlternate(alternate);

				/*
				 * set variant type parameter
				 */
				variant.setVariantType(getEnumFromString(
						org.opencb.biodata.models.variant.avro.VariantType.class, variantContext
								.getType().toString()));

				/*
				 * set chromosome, start and end type parameter
				 */
				variant.setChromosome(variantContext.getContig());
				variant.setStart(variantContext.getStart());
				variant.setStart(variantContext.getEnd());

				/*
				 * set id parameter
				 */
				Set<String> id = new HashSet<String>();
				String[] getId = variantContext.getID().split(",");
				if (getId.length > 1)
					for (String refId : getId) {
						id.add(refId);
					}

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
				// For time being setting the hard coded values for FiledID and
				// Study ID
				variantSourceEntry.setFileId("123");
				variantSourceEntry.setStudyId("23");

				/*
				 * set secondary alternate
				 */
				List<CharSequence> alternateAllelList = new ArrayList<CharSequence>();
				if (variantContext
						.getAlleles()
						.toString()
						.substring(
								1,
								variantContext.getAlleles().toString().length() - 1)
						.split(",").length >= 3) {
					alternateAllelList = getSecondaryAlternateAllele(variantContext
							.getAlleles().toString());
				} else {
					alternateAllelList.add("null");
				}
				variantSourceEntry.setSecondaryAlternates(alternateAllelList);

				/*
				 * set variant format
				 */
				String[] str = ((LazyGenotypesContext) variantContext
						.getGenotypes()).getUnparsedGenotypeData().toString()
						.split("\t");
				variantSourceEntry.setFormat(str[0]);

				/*
				 * set sample data parameters Eg: GT:GQ:GQX:DP:DPF:AD
				 * 1/1:63:29:22:7:0,22
				 */

				Map<CharSequence, Map<CharSequence, CharSequence>> sampledataMap = new HashMap<CharSequence, Map<CharSequence, CharSequence>>();
				Map<CharSequence, CharSequence> sampledata = new HashMap<CharSequence, CharSequence>();
				if (str[0].split(":").length == str[1].split(":").length) {
					sampledata = getSampleDataMap(str[0], str[1]);
				} else {
					// this case will never occur
					sampledata.put("error", "error");
				}

				sampledataMap.put(variantContext.getSampleNames().toString(),
						sampledata);
				variantSourceEntry.setSamplesData(sampledataMap);

				/*
				 * set default cohort
				 */
				variantSourceEntry.DEFAULT_COHORT = "50";

				/*
				 * set cohortStats fields Putting hard coded values for time
				 * being as these value will not be getting from HTSJDK
				 * currently.
				 */
				Map<CharSequence, VariantStats> cohortStats = new HashMap<CharSequence, VariantStats>();
				cohortStats.put(
						"2",
						setVariantStatsParam(
								setVariantHardyWeinbergStatsParam(),
								variantContext));
				variantSourceEntry.setCohortStats(cohortStats);

				/*
				 * set attribute fields Putting hard coded values for time being
				 * as these value will not be getting from HTSJDK currently.
				 */
				Map<CharSequence, CharSequence> attributeMap = new HashMap<CharSequence, CharSequence>();
				attributeMap.put("1", "ssss");
				variantSourceEntry.setAttributes(attributeMap);
				sourceEntry.put("11", variantSourceEntry);

				/*
				 * set parameter for HGVS Putting hard coded values for time
				 * being as these value will not be getting from HTSJDK
				 * currently.
				 */
				Map<CharSequence, List<CharSequence>> hgvsMap = new HashMap<CharSequence, List<CharSequence>>();
				List<CharSequence> hgvsList = new ArrayList<CharSequence>();
				hgvsList.add("HGVS");
				hgvsMap.put("11", hgvsList);
				variant.setHgvs(hgvsMap);
				variant.setSourceEntries(sourceEntry);

				/*
				 * set parameter for ids Putting hard coded values for time
				 * being as not sure about the significance of this
				 */

				List<CharSequence> ids = new ArrayList<CharSequence>();
				ids.add("123");
				variant.setIds(ids);

				/*
				 * Add final Variant bean to the VariantList
				 */
				variantList.add(variant);
			}

			/*
			 * call method writeHtsjdkDataIntoAvro to write VCF data into AVRO
			 * format
			 */
			writeHtsjdkDataIntoAvro(variantList, varRec, outputAvroFilePath);

		} catch (Exception e) {
			System.out.println("Error message" + e.getLocalizedMessage());
			e.printStackTrace();
		}
	}

	/**
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
	 * @param secondaryAllel
	 * @return secondaryAllelString
	 */
	public static String getAlternateAllele(String secondaryAllel) {
		System.out.println("inside getAlternateAllele :: " + secondaryAllel);

		StringBuffer secondaryAllelString = new StringBuffer();
		String secondaryAllelArrayTemp[] = secondaryAllel.trim()
				.substring(1, secondaryAllel.trim().length() - 1).split(",");
		if (secondaryAllelArrayTemp.length > 1) {
			for (int i = 0; i < secondaryAllelArrayTemp.length; i++) {
				if (i == 0) {
					continue;
				} else if (i == 1) {
					secondaryAllelString.append(secondaryAllelArrayTemp[i]
							.toString());
				}
			}
		}
		return secondaryAllelString.substring(0,
				secondaryAllelString.length() - 1);
	}

	/**
	 * @param secondaryAllel
	 * @return secondaryAllelArrayList
	 */
	public static List<CharSequence> getSecondaryAlternateAllele(
			String secondaryAllel) {

		
		CharSequence secondaryAllelArray[] = null;
		// CharSequence secondaryAllelElement = null;
		StringBuffer secondaryAllelString = new StringBuffer();
		CharSequence secondaryAllelArrayTemp[] = secondaryAllel.trim()
				.substring(1, secondaryAllel.trim().length() - 1).split(",");
		if (secondaryAllelArrayTemp.length >= 3) {
			for (int i = 2; i < secondaryAllelArrayTemp.length; i++) {
				if (i == 0 || i == 1) {
					secondaryAllelString.append("null,");
					break;
				} else {
					System.out.println(secondaryAllelArrayTemp[i].toString());
					secondaryAllelString.append(secondaryAllelArrayTemp[i]
							.toString());
					secondaryAllelString.append(",");
				}
			}
			secondaryAllelArray = secondaryAllelString.substring(0,
					secondaryAllelString.length() - 1).split(",");
		}
		return Arrays.asList(secondaryAllelArray);
	}

	/**
	 * @param variantHardyWeinbergStats
	 * @param variantContext
	 * @return variantStats
	 */
	private VariantStats setVariantStatsParam(
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
		variantStats.setQuality(8f);
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
	 * @return variantHardyWeinbergStats
	 */
	private VariantHardyWeinbergStats setVariantHardyWeinbergStatsParam() {

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
	 * @param vcfBean
	 * @param varRec
	 * @param outputAvroFilePath
	 * @throws FileNotFoundException
	 */
	public void writeHtsjdkDataIntoAvro(List<Variant> vcfBean, Variant varRec,
			String outputAvroFilePath) throws FileNotFoundException {

		if (outputAvroFilePath.isEmpty()) {
			throw new FileNotFoundException(
					"Output file path is empty or null...");
		}

		try {
			FileOutputStream outputStream = new FileOutputStream(
					outputAvroFilePath);
			DatumWriter<Variant> vcfDatumWriter = new SpecificDatumWriter<Variant>(
					Variant.class);
			DataFileWriter<Variant> vcfdataFileWriter = new DataFileWriter<Variant>(
					vcfDatumWriter);

			vcfdataFileWriter.setCodec(CodecFactory.snappyCodec());
			vcfdataFileWriter.create(varRec.getSchema(), outputStream);

			for (Variant variant : vcfBean) {
				varRec.setSVTHRESHOLD(variant.getSVTHRESHOLD());
				varRec.setVariantType(variant.getVariantType());
				varRec.setChromosome(variant.getChromosome().length() == 0 ? "NotApplicable"
						: variant.getChromosome());
				varRec.setStart(variant.getStart());
				varRec.setEnd(variant.getEnd());
				varRec.setLength(variant.getLength());
				varRec.setReference(variant.getReference().length() == 0 ? "NotApplicable"
						: variant.getReference());
				varRec.setAlternate(variant.getAlternate().length() == 0 ? "NotApplicable"
						: variant.getAlternate());
				varRec.setSourceEntries(variant.getSourceEntries());
				varRec.setAnnotation(variant.getChromosome().length() == 0 ? "NotApplicable"
						: variant.getChromosome());

				varRec.setIds(variant.getIds());
				varRec.setHgvs(variant.getHgvs());

				vcfdataFileWriter.append(varRec);

			}
			vcfdataFileWriter.flush();
			vcfdataFileWriter.close();
			outputStream.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param c
	 * @param string
	 * @return
	 */
	public static <VariantType extends Enum<VariantType>> VariantType getEnumFromString(
			Class<VariantType> c, String string) {
		if (c != null && string != null) {

			try {
				return Enum.valueOf(c, string.trim().toUpperCase());
			} catch (IllegalArgumentException e) {
			}
		}
		return null;
	}
}
