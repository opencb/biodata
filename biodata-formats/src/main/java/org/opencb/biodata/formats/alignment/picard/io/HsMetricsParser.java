package org.opencb.biodata.formats.alignment.picard.io;

import org.opencb.biodata.formats.alignment.picard.HsMetrics;
import org.opencb.biodata.formats.sequence.fastqc.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class HsMetricsParser {

    public static HsMetrics parse(File file) throws IOException {
        HsMetrics hsMetrics = new HsMetrics();

        FileReader fr = new FileReader(file);
        BufferedReader br = new BufferedReader(fr);

        // Skip first line
        br.readLine();

        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith("## METRICS CLASS")) {
                parseMetrics(hsMetrics, br);
            }
            if (line.startsWith("## HISTOGRAM")) {
                parseHistogram(hsMetrics, br);
            }
        }
        fr.close();

        return hsMetrics;
    }

    private static void parseMetrics(HsMetrics hsMetrics, BufferedReader br) throws IOException {
        // Skip first line
        br.readLine();
        String line = br.readLine();
        String[] split = line.split("\t");
        int i = 0;
        hsMetrics.setBaitSet(split[i++]);
        hsMetrics.setBaitTerritory(Integer.parseInt(split[i++]));
        hsMetrics.setBaitDesignEfficiency(Double.parseDouble(split[i++]));
        hsMetrics.setOnBaitBases(Integer.parseInt(split[i++]));
        hsMetrics.setNearBaitBases(Integer.parseInt(split[i++]));
        hsMetrics.setOffBaitBases(Integer.parseInt(split[i++]));
        i++; //hsMetrics.setPctSelectedBases();
        i++; //hsMetrics.setPctOffBait();
        hsMetrics.setOnBaitVsSelected(Double.parseDouble(split[i++]));
        hsMetrics.setMeanBaitCoverage(Double.parseDouble(split[i++]));
        i++; //hsMetrics.setPctUsableBasesOnBait();
        i++; //hsMetrics.setPctUsableBasesOnTarget();
        hsMetrics.setFoldEnrichment(Double.parseDouble(split[i++]));
        hsMetrics.setHsLibrarySize(Integer.parseInt(split[i++]));
        hsMetrics.setHsPenalty(new double[]{Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++]),
                Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++])});
        hsMetrics.setTargetTerritory(Integer.parseInt(split[i++]));
        hsMetrics.setGenomeSize(Integer.parseInt(split[i++]));
        hsMetrics.setTotalReads(Integer.parseInt(split[i++]));
        hsMetrics.setPfReads(Integer.parseInt(split[i++]));
        hsMetrics.setPfBases(Integer.parseInt(split[i++]));
        hsMetrics.setPfUniqueReads(Integer.parseInt(split[i++]));
        hsMetrics.setPfUqReadsAligned(Integer.parseInt(split[i++]));
        hsMetrics.setPfBasesAligned(Integer.parseInt(split[i++]));
        hsMetrics.setPfUqBasesAligned(Integer.parseInt(split[i++]));
        hsMetrics.setOnTargetBases(Integer.parseInt(split[i++]));
        i++; //hsMetrics.setPctPfReads();
        i++; //hsMetrics.setPctPfUqReads();
        i++; //hsMetrics.setPctPfUqReadsAligned();
        hsMetrics.setMeanTargetCoverage(Double.parseDouble(split[i++]));
        hsMetrics.setMedianTargetCoverage(Double.parseDouble(split[i++]));
        hsMetrics.setMaxTargetCoverage(Double.parseDouble(split[i++]));
        hsMetrics.setMinTargetCoverage(Double.parseDouble(split[i++]));
        hsMetrics.setZeroCvgTargetsPct(Double.parseDouble(split[i++]));
        i++; //hsMetrics.setPctExcDupe();
        i++; //hsMetrics.setPctExcAdapter();
        i++; //hsMetrics.setPctExcMapq();
        i++; //hsMetrics.setPctExcBaseQ();
        i++; //hsMetrics.setPctExcOverlap();
        i++; //hsMetrics.setPctExcOffTarget();
        hsMetrics.setFold80BasePenalty(Double.parseDouble(split[i++]));
        hsMetrics.setPctTargetBases(new double[]{Double.parseDouble(split[i++]), Double.parseDouble(split[i++]),
                Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++]),
                Double.parseDouble(split[i++]), Double.parseDouble(split[i++]), Double.parseDouble(split[i++])});
        hsMetrics.setAtDropout(Double.parseDouble(split[i++]));
        hsMetrics.setGcDropout(Double.parseDouble(split[i++]));
        hsMetrics.setHetSnpSensitivity(Double.parseDouble(split[i++]));
        hsMetrics.setHetSnpQ(Double.parseDouble(split[i++]));
        i++; //hsMetrics.setSample();
        i++; //hsMetrics.setLibrary();
        i++; //hsMetrics.setReadGroup();
    }

//    BAIT_SET	BAIT_TERRITORY	BAIT_DESIGN_EFFICIENCY	ON_BAIT_BASES	NEAR_BAIT_BASES	OFF_BAIT_BASES	PCT_SELECTED_BASES	PCT_OFF_BAIT	ON_BAIT_VS_SELECTED	MEAN_BAIT_COVERAGE	PCT_USABLE_BASES_ON_BAIT	PCT_USABLE_BASES_ON_TARGET	FOLD_ENRICHMENT	HS_LIBRARY_SIZE	HS_PENALTY_10X	HS_PENALTY_20X	HS_PENALTY_30X	HS_PENALTY_40X	HS_PENALTY_50X	HS_PENALTY_100X	TARGET_TERRITORY	GENOME_SIZE	TOTAL_READS	PF_READS	PF_BASES	PF_UNIQUE_READS	PF_UQ_READS_ALIGNED	PF_BASES_ALIGNED	PF_UQ_BASES_ALIGNED	ON_TARGET_BASES	PCT_PF_READS	PCT_PF_UQ_READS	PCT_PF_UQ_READS_ALIGNED	MEAN_TARGET_COVERAGE	MEDIAN_TARGET_COVERAGE	MAX_TARGET_COVERAGE	MIN_TARGET_COVERAGE	ZERO_CVG_TARGETS_PCT	PCT_EXC_DUPE	PCT_EXC_ADAPTER	PCT_EXC_MAPQ	PCT_EXC_BASEQ	PCT_EXC_OVERLAP	PCT_EXC_OFF_TARGET	FOLD_80_BASE_PENALTY	PCT_TARGET_BASES_1X	PCT_TARGET_BASES_2X	PCT_TARGET_BASES_10X	PCT_TARGET_BASES_20X	PCT_TARGET_BASES_30X	PCT_TARGET_BASES_40X	PCT_TARGET_BASES_50X	PCT_TARGET_BASES_100X	AT_DROPOUT	GC_DROPOUT	HET_SNP_SENSITIVITY	HET_SNP_Q	SAMPLE	LIBRARY	READ_GROUP
//    test	      7679	         1	                     313356	          14984	        19149259	     0.016857	          0.983143	    0.954364	          40.806876	          0.015575                    	0.011865	            132.04269	     3543	         89.646178	     95.157058	     101.753416	      109.936238	120.156417    	-1	             7679	63025520	133237	133237	20118787	115885	115292	19477599	16929949	238710	1	0.869766	0.994883	31.086079	32	491	0	0.130799	0.000007	0.000797	0.080077	0.01631	0.759757	1.195618	1	0.99987	0.988149	0.952468	0.6299	0.076182	0	0	0	0	0.997414	26

    private static void parseHistogram(HsMetrics hsMetrics, BufferedReader br) throws IOException {
        // Skip first line
        br.readLine();
        String line;
        int i = 0;
        int[] highQualityCoverageCount = new int[201];
        int[] unfilteredBaseQCount = new int[201];
        while ((line = br.readLine()) != null) {
            String[] split = line.split("\t");
            highQualityCoverageCount[i] = Integer.parseInt(split[1]);
            unfilteredBaseQCount[i] = Integer.parseInt(split[2]);
            i++;
            if (i >= hsMetrics.getHighQualityCoverageCount().length) {
                break;
            }
        }

        hsMetrics.setHighQualityCoverageCount(highQualityCoverageCount);
        hsMetrics.setUnfilteredBaseQCount(unfilteredBaseQCount);
    }
}
