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

package org.opencb.biodata.tools.variant.stats.writer;

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.converters.avro.VariantStatsToPopulationFrequencyConverter;
import org.opencb.biodata.tools.variant.stats.VariantStatsCalculator;
import org.opencb.commons.io.DataWriter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Exports the given variant stats into a TSV format.
 *
 *
 * For each cohort defines a set of 5 columns.
 * Each column will be prefixed with the name of the cohort.
 *  - {cohort}_AN    : Allele number, with the number of alleles in called genotypeCounters in the cohort
 *  - {cohort}_AC    : Allele count, total number of alternate alleles in called genotypeCounters
 *  - {cohort}_AF    : Allele frequency in the cohort calculated from AC and AN, in the range (0,1)
 *  - {cohort}_HET   : Heterozygous genotype frequency
 *  - {cohort}_HOM   : Homozygous alternate genotype frequency
 *
 * Created on 01/06/16.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsTsvExporter implements DataWriter<Variant> {

    private static final String TAB = "\t";
    private static final String MISSING_VALUE = ".";
    private static final String MISSING_ALLELE = "-";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.######");
    public static final String RF = "RF";
    public static final String AF = VCFConstants.ALLELE_FREQUENCY_KEY;
    public static final String RC = "RC";
    public static final String AC = VCFConstants.ALLELE_COUNT_KEY;
    public static final String AN = VCFConstants.ALLELE_NUMBER_KEY;
    public static final String MISS_AC = "MISS_AC";

    public static final String HOM_REF_F= "HOM_REF_F";
    public static final String HOM_REF_C= "HOM_REF_C";
    public static final String HET_F = "HET_F";
    public static final String HET_C = "HET_C";
    public static final String HOM_ALT_F = "HOM_ALT_F";
    public static final String HOM_ALT_C = "HOM_ALT_C";
    public static final String MISS_GT = "MISS_GT";
    private PrintStream dataOutputStream;
    private final String study;
    private final boolean closeStream;
    private final List<String> cohorts;
    private final VariantStatsToPopulationFrequencyConverter converter;
    private int writtenVariants;

    private static LinkedHashMap<String, Function<VariantStats, Number>> STATS_COLUMNS;

    static {
        STATS_COLUMNS = new LinkedHashMap<>();

        STATS_COLUMNS.put(RF, VariantStats::getRefAlleleFreq);
        STATS_COLUMNS.put(AF, VariantStats::getAltAlleleFreq);
        STATS_COLUMNS.put(RC, VariantStats::getRefAlleleCount);
        STATS_COLUMNS.put(AC, VariantStats::getAltAlleleCount);
        STATS_COLUMNS.put(AN, VariantStats::getAlleleCount);
        STATS_COLUMNS.put(MISS_AC, VariantStats::getMissingAlleleCount);

        Genotype homRefGt = new Genotype("0/0");
        Genotype hetGt = new Genotype("0/1");
        Genotype homAltGt = new Genotype("1/1");

        STATS_COLUMNS.put(HOM_REF_F, variantStats -> variantStats.getGenotypeFreq().get(homRefGt));
        STATS_COLUMNS.put(HOM_REF_C, variantStats -> variantStats.getGenotypeCount().get(homRefGt));
        STATS_COLUMNS.put(HET_F, variantStats -> variantStats.getGenotypeFreq().get(hetGt));
        STATS_COLUMNS.put(HET_C, variantStats -> variantStats.getGenotypeCount().get(hetGt));
        STATS_COLUMNS.put(HOM_ALT_F, variantStats -> variantStats.getGenotypeFreq().get(homAltGt));
        STATS_COLUMNS.put(HOM_ALT_C, variantStats -> variantStats.getGenotypeCount().get(homAltGt));
        STATS_COLUMNS.put(MISS_GT, VariantStats::getMissingGenotypeCount);
    }

    /**
     * Constructor.
     *
     * @param os        OutputStream. Won't be closed at the end.
     * @param study     Study to export.
     * @param cohorts   List of cohorts to export.
     */
    public VariantStatsTsvExporter(OutputStream os, String study, List<String> cohorts) {
        this.dataOutputStream = new PrintStream(os);
        this.study = study;
        this.closeStream = false;
        this.cohorts = cohorts;
        converter = new VariantStatsToPopulationFrequencyConverter();
    }

    @Override
    public boolean open() {
        return true;
    }

    @Override
    public boolean close() {
        if (closeStream) {
            dataOutputStream.close();
        }
        return true;
    }

    @Override
    public boolean pre() {
        dataOutputStream.print("#CHR\tPOS\tREF\tALT\tID\tGENE\t");

        Iterator<String> cohortIterator = cohorts.iterator();
        while (cohortIterator.hasNext()) {
            String cohort = cohortIterator.next();

            Iterator<String> columnIterator = STATS_COLUMNS.keySet().iterator();
            while (columnIterator.hasNext()) {
                String key = columnIterator.next();
                dataOutputStream.print(cohort + "_" + key);
                if (columnIterator.hasNext()) {
                    dataOutputStream.print(TAB);
                }
            }
            if (cohortIterator.hasNext()) {
                dataOutputStream.print(TAB);
            } else {
                dataOutputStream.print("\n");
            }
        }
        writtenVariants = 0;
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(List<Variant> batch) {
        for (Variant variant : batch) {
            write(variant);
        }
        return true;
    }

    /**
     * Exports a variant.
     *
     * @param variant Variant to print
     * @return        True by default.
     */
    @Override
    public boolean write(Variant variant) {
        StudyEntry studyEntry = variant.getStudy(study);
        if (studyEntry == null) {
            return true;
//            List<String> studies = variant.getStudies().stream().map(StudyEntry::getStudyId).collect(Collectors.toList());
//            throw new IllegalArgumentException("Can not export more than one study at the same time. Found: " + studies);
        }

        dataOutputStream.print(variant.getChromosome());
        dataOutputStream.print(TAB);
        dataOutputStream.print(variant.getStart());
        dataOutputStream.print(TAB);
        if (variant.getReference().isEmpty()) {
            dataOutputStream.print(MISSING_ALLELE);
        } else {
            dataOutputStream.print(variant.getReference());
        }
        dataOutputStream.print(TAB);
        if (variant.getAlternate().isEmpty()) {
            dataOutputStream.print(MISSING_ALLELE);
        } else {
            dataOutputStream.print(variant.getAlternate());
        }
        dataOutputStream.print(TAB);
        if (variant.getAnnotation() != null) {
            if (StringUtils.isNotEmpty(variant.getAnnotation().getId())) {
                dataOutputStream.print(variant.getAnnotation().getId());
            } else {
                dataOutputStream.print(MISSING_VALUE);
            }
            dataOutputStream.print(TAB);

            String genes;
            if (variant.getAnnotation().getConsequenceTypes() != null) {
                genes = String.join(",", variant.getAnnotation().getConsequenceTypes()
                        .stream()
                        .map(ConsequenceType::getGeneName)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet()));
                if (genes.isEmpty()) {
                    genes = MISSING_VALUE;
                }
            } else {
                genes = MISSING_VALUE;
            }
            dataOutputStream.print(genes);
            dataOutputStream.print(TAB);
        } else {
            dataOutputStream.print(MISSING_VALUE + TAB + MISSING_VALUE + TAB);
        }
        for (Iterator<String> cohortIterator = cohorts.iterator(); cohortIterator.hasNext(); ) {
            String cohort = cohortIterator.next();
            VariantStats stats = studyEntry.getStats(cohort);
            if (stats == null) {
                for (int i = 0; i < STATS_COLUMNS.size() - 1; i++) {
                    dataOutputStream.print(".\t");
                }
                dataOutputStream.print(".");
            } else {
                if (stats.getGenotypeCount().keySet().stream().anyMatch(Genotype::isPhased)) {
                    // Remove phase of genotypes
                    stats = VariantStatsCalculator.calculate(variant, stats.getGenotypeCount(), false);
                }

                Iterator<Function<VariantStats, Number>> iterator = STATS_COLUMNS.values().iterator();
                while (iterator.hasNext()) {
                    Function<VariantStats, Number> column = iterator.next();
                    Number number = column.apply(stats);
                    if (number instanceof Float || number instanceof Double) {
                        float f = number.floatValue();
                        if (f >= 0) {
                            dataOutputStream.print(DECIMAL_FORMAT.format(f));
                        } else {
                            dataOutputStream.print(MISSING_VALUE);
                        }
                    } else if (number != null) {
                        int i = number.intValue();
                        if (i >= 0) {
                            dataOutputStream.print(i);
                        } else {
                            dataOutputStream.print(MISSING_VALUE);
                        }
                    } else {
                        dataOutputStream.print(MISSING_VALUE);
                    }
                    if (iterator.hasNext()) {
                        dataOutputStream.print(TAB);
                    }
                }
            }
            if (cohortIterator.hasNext()) {
                dataOutputStream.print(TAB);
            } else {
                dataOutputStream.print("\n");
            }
        }
        writtenVariants++;

        return true;
    }

    public int getWrittenVariants() {
        return writtenVariants;
    }
}
