package org.opencb.biodata.tools.variant;

import htsjdk.samtools.SAMSequenceRecord;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFContigHeaderLine;
import htsjdk.variant.vcf.VCFHeader;
import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.commons.run.Task;

import java.util.*;

public class VariantReferenceBlockCreatorTask implements Task<Variant, Variant> {


    private String chromosome= null;
    private int position;
    private int end;
    private String studyId;
    private String fileId;
    private LinkedHashMap<String, Integer> samplesPosition;
    private List<List<String>> missingGtSamplesData;
    private Map<String, Integer> contigs;

    public VariantReferenceBlockCreatorTask() {
    }

    public VariantReferenceBlockCreatorTask(Map<String, Integer> contigs) {
        this.contigs = new HashMap<>(contigs);
        for (Map.Entry<String, Integer> entry : contigs.entrySet()) {
            this.contigs.put(Region.normalizeChromosome(entry.getKey()), entry.getValue());
        }
    }

    public VariantReferenceBlockCreatorTask(VariantFileHeader fileHeader) {
        this.contigs = new HashMap<>();
        for (VariantFileHeaderComplexLine line : fileHeader.getComplexLines()) {
            if (line.getKey().equals(VCFConstants.CONTIG_HEADER_KEY)) {
                String contig = line.getId();
                String length = line.getGenericFields().get("length");
                if (StringUtils.isNumeric(length)) {
                    contigs.put(contig, Integer.valueOf(length));
                    contigs.put(Region.normalizeChromosome(contig), Integer.valueOf(length));
                }
            }
        }
    }

    public VariantReferenceBlockCreatorTask(VCFHeader fileHeader) {
        this.contigs = new HashMap<>();
        for (VCFContigHeaderLine line : fileHeader.getContigLines()) {
            SAMSequenceRecord record = line.getSAMSequenceRecord();
            String contig = record.getSequenceName();
            int length = record.getSequenceLength();
            if (length > 0) {
                contigs.put(contig, length);
                contigs.put(Region.normalizeChromosome(contig), length);
            }
        }
    }

    @Override
    public void pre() throws Exception {
    }

    @Override
    public List<Variant> apply(List<Variant> list) throws Exception {
        List<Variant> fixedList = new ArrayList<>(((int) (list.size() * 1.2)));
        for (Variant variant : list) {
            if (chromosome == null) {
                init(variant);

                // Create first telomere ref block (if needed)
                fixedList.addAll(createContigFirstBlock());
            } else {
                if (!variant.getChromosome().equals(chromosome)) {
                    // Change chromosome
                    // Create first and last telomere ref block (if needed)
                    fixedList.addAll(createContigLastBlock());
                    init(variant);
                    fixedList.addAll(createContigFirstBlock());
                } else {
                    if (variant.getStart() != position) {
                        // Check if need to create a block

                        if ((end + 1) < variant.getStart()) {
                            // Create ref block
                            fixedList.add(createRefBlock(chromosome, end + 1, variant.getStart() - 1));
                        }

                        position = variant.getStart();
                        end = variant.getEnd();
                    } else {
                        // Update end
                        end = Math.max(variant.getEnd(), end);
                    }
                }
            }
            fixedList.add(variant);
        }
        return fixedList;
    }

    @Override
    public List<Variant> drain() throws Exception {
        return createContigLastBlock();
    }

    protected void init(Variant variant) {
        chromosome = variant.getChromosome();
        position = variant.getStart();
        end = variant.getEnd();
        if (!variant.getStudies().isEmpty()) {
            StudyEntry studyEntry = variant.getStudies().get(0);
            studyId = studyEntry.getStudyId();
            fileId = studyEntry.getFiles().get(0).getFileId();
            samplesPosition = studyEntry.getSamplesPosition();
            missingGtSamplesData = new ArrayList<>(samplesPosition.size());
            for (int i = 0; i < samplesPosition.size(); i++) {
                missingGtSamplesData.add(Collections.singletonList("./."));
            }
        }
    }

    protected List<Variant> createContigFirstBlock() {
        if (position <= 1) {
            return Collections.emptyList();
        } else {
            return Collections.singletonList(createRefBlock(chromosome, 1, position - 1));
        }
    }

    protected List<Variant> createContigLastBlock() {
        if (!contigs.containsKey(chromosome)) {
            return Collections.emptyList();
        } else {
            Integer length = contigs.get(chromosome);
            if (end >= length) {
                return Collections.emptyList();
            }
            return Collections.singletonList(createRefBlock(chromosome, end + 1, length));
        }
    }

    protected Variant createRefBlock(String chromosome, int start, int end) {
        VariantBuilder builder = new VariantBuilder(chromosome, start, end, "N", ".");
        if (studyId != null) {
            builder.setStudyId(studyId)
                    .setFileId(fileId)
                    .setSamplesPosition(samplesPosition)
                    .setFilter(VCFConstants.UNFILTERED)
                    .setFormat("GT")
                    .setSamplesData(missingGtSamplesData);
        }
        return builder.build();
    }

}
