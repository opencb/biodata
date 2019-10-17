package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.VCFConstants;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.Converter;
import org.opencb.biodata.tools.variant.stats.VariantStatsCalculator;

import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Converts a set of variant stats into TSV lines.
 *
 * For each cohort defines a set of 5 columns.
 * Each column will be prefixed with the name of the cohort.
 *  - {cohort}_AN    : Allele number, with the number of alleles in called genotypeCounters in the cohort
 *  - {cohort}_AC    : Allele count, total number of alternate alleles in called genotypeCounters
 *  - {cohort}_AF    : Allele frequency in the cohort calculated from AC and AN, in the range (0,1)
 *  - {cohort}_HET   : Heterozygous genotype frequency
 *  - {cohort}_HOM   : Homozygous alternate genotype frequency
 *
 */
public class VariantStatsToTsvConverter implements Converter<Variant, String> {

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
    private final String study;
    private final List<String> cohorts;
    private final StringBuilder sb;

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

    public VariantStatsToTsvConverter(String study, List<String> cohorts) {
        this.study = study;
        this.cohorts = cohorts;
        sb = new StringBuilder();
    }

    @Override
    public String convert(Variant variant) {
        StudyEntry studyEntry = variant.getStudy(study);
        if (studyEntry == null) {
            return null;
        }

        return convert(variant, studyEntry.getStats(), variant.getAnnotation());
    }

    public String createHeader() {
        sb.setLength(0);
        sb.append("#CHR\tPOS\tREF\tALT\tID\tGENE\t");

        Iterator<String> cohortIterator = cohorts.iterator();
        while (cohortIterator.hasNext()) {
            String cohort = cohortIterator.next();

            Iterator<String> columnIterator = STATS_COLUMNS.keySet().iterator();
            while (columnIterator.hasNext()) {
                String key = columnIterator.next();
                sb.append(cohort).append("_").append(key);
                if (columnIterator.hasNext()) {
                    sb.append(TAB);
                }
            }
            if (cohortIterator.hasNext()) {
                sb.append(TAB);
            }
        }
        return sb.toString();
    }

    public String convert(Variant variant, Map<String, VariantStats> statsMap, VariantAnnotation annotation) {
        sb.setLength(0);

        sb.append(variant.getChromosome());
        sb.append(TAB);
        sb.append(variant.getStart());
        sb.append(TAB);
        if (variant.getReference().isEmpty()) {
            sb.append(MISSING_ALLELE);
        } else {
            sb.append(variant.getReference());
        }
        sb.append(TAB);
        if (variant.getAlternate().isEmpty()) {
            sb.append(MISSING_ALLELE);
        } else {
            sb.append(variant.getAlternate());
        }
        sb.append(TAB);
        if (annotation != null) {
            if (StringUtils.isNotEmpty(annotation.getId())) {
                sb.append(annotation.getId());
            } else {
                sb.append(MISSING_VALUE);
            }
            sb.append(TAB);

            String genes;
            if (annotation.getConsequenceTypes() != null) {
                genes = String.join(",", annotation.getConsequenceTypes()
                        .stream()
                        .map(ConsequenceType::getGeneName)
                        .filter(StringUtils::isNotEmpty)
                        .collect(Collectors.toSet()));
                if (genes.isEmpty()) {
                    genes = MISSING_VALUE;
                }
            } else {
                genes = MISSING_VALUE;
            }
            sb.append(genes);
            sb.append(TAB);
        } else {
            sb.append(MISSING_VALUE + TAB + MISSING_VALUE + TAB);
        }
        for (Iterator<String> cohortIterator = cohorts.iterator(); cohortIterator.hasNext(); ) {
            String cohort = cohortIterator.next();
            VariantStats stats = statsMap.get(cohort);
            if (stats == null) {
                for (int i = 0; i < STATS_COLUMNS.size() - 1; i++) {
                    sb.append(".\t");
                }
                sb.append(".");
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
                            sb.append(DECIMAL_FORMAT.format(f));
                        } else {
                            sb.append(MISSING_VALUE);
                        }
                    } else if (number != null) {
                        int i = number.intValue();
                        if (i >= 0) {
                            sb.append(i);
                        } else {
                            sb.append(MISSING_VALUE);
                        }
                    } else {
                        sb.append(MISSING_VALUE);
                    }
                    if (iterator.hasNext()) {
                        sb.append(TAB);
                    }
                }
            }
            if (cohortIterator.hasNext()) {
                sb.append(TAB);
            }
        }
        return sb.toString();
    }
}
