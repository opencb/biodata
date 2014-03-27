package org.opencb.biodata.models.variant.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.opencb.biodata.models.feature.AllelesCode;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.ArchivedVariantFile;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class ArchivedVariantFileStats {

    private final String fileId;
    private List<String> sampleNames;
    private VariantGlobalStats fileStats;
    private Map<String, VariantSingleSampleStats> samplesStats;

    
    public ArchivedVariantFileStats(String fileId) {
        this.fileId = fileId;
        this.sampleNames = new ArrayList<>();
        fileStats = new VariantGlobalStats();
        samplesStats = new LinkedHashMap<>();
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(List<String> sampleNames) {
        this.sampleNames = sampleNames;
        fileStats.setSamplesCount(sampleNames.size());
    }

    public VariantGlobalStats getFileStats() {
        return fileStats;
    }

    public void setFileStats(VariantGlobalStats fileStats) {
        this.fileStats = fileStats;
    }

    public void updateFileStats(List<Variant> variants) {
        for (Variant v : variants) {
            ArchivedVariantFile file = v.getFile(fileId);
            if (file == null) {
                // The variant is not contained in this file
                continue;
            }
            
            fileStats.addVariant();
            
            VariantStats stats = file.getStats();
            if (stats.isIndel()) {
                fileStats.addIndel();
            }
            if (stats.isSNP()) {
                fileStats.addSNP();
            }
            if (stats.isPass()) {
                fileStats.addPass();
            }
            
            if (stats.getNumAlleles() > 2) {
                fileStats.addMultiallelic();
            } else if (stats.getNumAlleles() > 1) {
                fileStats.addBiallelic();
            }
            
            fileStats.addTransitions(stats.getTransitionsCount());
            fileStats.addTransversions(stats.getTransversionsCount());
            fileStats.addAccumQuality(stats.getQual());
        }
    }
        
    public Map<String, VariantSingleSampleStats> getSamplesStats() {
        return samplesStats;
    }

    public VariantSingleSampleStats getSampleStats(String sampleName) {
        return samplesStats.get(sampleName);
    }
    
    public void setSamplesStats(Map<String, VariantSingleSampleStats> variantSampleStats) {
        this.samplesStats = variantSampleStats;
    }
    
    public void updateSampleStats(List<Variant> variants, Pedigree pedigree) {
        for (Variant v : variants) {
            ArchivedVariantFile file = v.getFile(fileId);
            if (file == null) {
                // The variant is not contained in this file
                continue;
            }
            
            for (Map.Entry<String, Map<String, String>> sample : file.getSamplesData().entrySet()) {
                String sampleName = sample.getKey();
                VariantSingleSampleStats sampleStats = samplesStats.get(sampleName);
                if (sampleStats == null) {
                    sampleStats = new VariantSingleSampleStats(sampleName);
                    samplesStats.put(sampleName, sampleStats);
                }
                
                Genotype g = new Genotype(sample.getValue().get("GT"));
                
                // Count missing genotypes (one or both alleles missing)
                if (g.getCode() != AllelesCode.ALLELES_OK) { 
                    sampleStats.incrementMissingGenotypes();
                }
                
//                // TODO Check mendelian errors
//                if (pedigree != null) {
//                    Individual ind = pedigree.getIndividual(sampleName);
//                    if (g.getCode() == AllelesCode.ALLELES_OK && isMendelianError(ind, g, record)) {
//                        sampleStats.incrementMendelianErrors();
//                    }
//                }
                
                // Count homozygotes
                if (g.getAllele1().equals(g.getAllele2())) {
                    sampleStats.incrementHomozygous();
                }
            }
        }
    }
    
}
