package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.variant.vcf.*;
import org.apache.commons.collections4.CollectionUtils;
import org.opencb.biodata.formats.variant.vcf4.VcfUtils;
import org.opencb.biodata.models.metadata.Cohort;
import org.opencb.biodata.models.metadata.Individual;
import org.opencb.biodata.models.metadata.Sample;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.commons.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 24/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStudyMetadataToVCFHeaderConverter implements Converter<VariantStudyMetadata, VCFHeader> {

    @Override
    public VCFHeader convert(VariantStudyMetadata variantStudyMetadata) {
        return convert(variantStudyMetadata, Collections.emptyList());
    }

    public VCFHeader convert(VariantStudyMetadata variantStudyMetadata, List<String> annotations) {
        VariantFileHeader header = variantStudyMetadata.getAggregatedHeader();
        if (header == null) {
            if (variantStudyMetadata.getFiles() != null && variantStudyMetadata.getFiles().size() == 1) {
                header = variantStudyMetadata.getFiles().get(0).getHeader();
            }
        }
        VCFHeader vcfHeader = new VariantFileHeaderToVCFHeaderConverter().convert(header);

        List<String> samples = new ArrayList<>();
        for (Individual individual : variantStudyMetadata.getIndividuals()) {
            for (Sample sample : individual.getSamples()) {
                samples.add(sample.getId());
            }
        }
        vcfHeader.getGenotypeSamples().addAll(samples);

        vcfHeader.addMetaDataLine(new VCFFilterHeaderLine(VCFConstants.MISSING_VALUE_v4, "No FILTER info"));
        vcfHeader.addMetaDataLine(new VCFFilterHeaderLine(VCFConstants.PASSES_FILTERS_v4, "All filters passed"));

        for (Cohort cohort : variantStudyMetadata.getCohorts()) {
            String cohortName = cohort.getId();
            if (cohortName.equals(StudyEntry.DEFAULT_COHORT)) {
                vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VCFConstants.ALLELE_COUNT_KEY, VCFHeaderLineCount.A,
                        VCFHeaderLineType.Integer, "Total number of alternate alleles in called genotypes,"
                        + " for each ALT allele, in the same order as listed"));
                vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                        VCFHeaderLineType.Float, "Allele Frequency, for each ALT allele, calculated from AC and AN, in the range (0,1),"
                        + " in the same order as listed"));
                vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VCFConstants.ALLELE_NUMBER_KEY, 1,
                        VCFHeaderLineType.Integer, "Total number of alleles in called genotypes"));
            } else {
                vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(cohortName + "_" + VCFConstants.ALLELE_FREQUENCY_KEY, VCFHeaderLineCount.A,
                        VCFHeaderLineType.Float,
                        "Allele frequency in the " + cohortName + " cohort calculated from AC and AN, in the range (0,1),"
                                + " in the same order as listed"));
            }
        }

        // annotations
        if (CollectionUtils.isNotEmpty(annotations)) {
            if (annotations.size() == 1 && annotations.get(0).equalsIgnoreCase("all")) {
                annotations = VcfUtils.ANNOTATION_INFO_VALUES;
            }
//            vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VcfUtils.STATS_INFO_KEY, 1, VCFHeaderLineType.String, "Allele frequency "
//                    + " for cohorts (separated by |), e.g.: ALL:0.0564705|MXL:0.0886758"));
            vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VcfUtils.ANNOTATION_INFO_KEY, 1, VCFHeaderLineType.String, "Consequence annotations (separated "
                    + " by &) from CellBase. Format: " +   String.join("|", annotations)));
//            if (annotations.contains("populationFrequency")) {
                vcfHeader.addMetaDataLine(new VCFInfoHeaderLine(VcfUtils.POPFREQ_INFO_KEY, 1, VCFHeaderLineType.String, "Alternate allele frequencies "
                        + " for study and population (separated by |), e.g.: 1kG_phase3_IBS:0.06542056|1kG_phase3_CEU:0.08585858"));
//            }
        }

        return vcfHeader;
    }
}
