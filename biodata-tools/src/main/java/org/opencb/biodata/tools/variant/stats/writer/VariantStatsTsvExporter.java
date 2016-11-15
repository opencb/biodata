package org.opencb.biodata.tools.variant.stats.writer;

import htsjdk.variant.vcf.VCFConstants;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.variant.converters.avro.VariantStatsToPopulationFrequencyConverter;
import org.opencb.commons.io.DataWriter;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

/**
 * Exports the given variant stats into a TSV format.
 *
 *
 * For each cohort defines a set of 5 columns.
 * Each column will be prefixed with the name of the cohort.
 *  - {cohort}_AN    : Allele number, with the number of alleles in called genotypes in the cohort
 *  - {cohort}_AC    : Allele count, total number of alternate alleles in called genotypes
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
    private static final String MISSING_NUMBER = ".";
    private static final String MISSING_ALLELE = "-";
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.######");
    private PrintStream dataOutputStream;
    private final String study;
    private final boolean closeStream;
    private final List<String> cohorts;
    private final VariantStatsToPopulationFrequencyConverter converter;
    private int writtenVariants;

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
        dataOutputStream.print("#CHR\tPOS\tREF\tALT\t");
        for (Iterator<String> cohortIterator = cohorts.iterator(); cohortIterator.hasNext(); ) {
            String cohort = cohortIterator.next();
            dataOutputStream.print(cohort + "_" + VCFConstants.ALLELE_NUMBER_KEY + TAB
                    + cohort + "_" + VCFConstants.ALLELE_COUNT_KEY + TAB
                    + cohort + "_" + VCFConstants.ALLELE_FREQUENCY_KEY + TAB
                    + cohort + "_" + "HET" + TAB
                    + cohort + "_" + "HOM");
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
        for (Iterator<String> cohortIterator = cohorts.iterator(); cohortIterator.hasNext(); ) {
            String cohort = cohortIterator.next();
            VariantStats stats = studyEntry.getStats(cohort);
            if (stats == null) {
                dataOutputStream.print(".\t.\t.\t.\t.");
            } else {

                int an = stats.getAltAlleleCount() + stats.getRefAlleleCount();
                Integer ac = stats.getAltAlleleCount();
                Float af = stats.getAltAlleleFreq();

                if (an >= 0) {
                    dataOutputStream.print(an);
                } else {
                    dataOutputStream.print(MISSING_NUMBER);
                }
                dataOutputStream.print(TAB);
                if (ac >= 0) {
                    dataOutputStream.print(ac);
                } else {
                    dataOutputStream.print(MISSING_NUMBER);
                }
                dataOutputStream.print(TAB);
                if (af >= 0) {
                    dataOutputStream.print(DECIMAL_FORMAT.format(af));
                } else {
                    dataOutputStream.print(MISSING_NUMBER);
                }
                dataOutputStream.print(TAB);


                if (stats.getGenotypesFreq() != null && !stats.getGenotypesFreq().isEmpty()) {
                    PopulationFrequency frequency = converter.convert("", "", stats, "", "");
                    dataOutputStream.print(frequency.getHetGenotypeFreq() + TAB + frequency.getAltHomGenotypeFreq());
                } else {
                    dataOutputStream.print(".\t.");
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
