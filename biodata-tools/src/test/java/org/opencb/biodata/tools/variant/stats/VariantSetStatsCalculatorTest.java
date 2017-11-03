package org.opencb.biodata.tools.variant.stats;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;
import org.opencb.biodata.models.variant.metadata.VariantFileHeader;
import org.opencb.biodata.models.variant.metadata.VariantFileHeaderComplexLine;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.VariantSetStats;
import org.opencb.commons.run.ParallelTaskRunner;

import java.util.*;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.opencb.commons.run.ParallelTaskRunner.Config;

/**
 * Created on 12/09/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantSetStatsCalculatorTest {

    protected static final String STUDY_ID = "Study";
    private VariantReader reader;
    private VariantStudyMetadata studyMetadata;

    @Before
    public void setUp() throws Exception {
        List<Variant> variants = new ArrayList<>();
        VariantFileHeader header = new VariantFileHeader();
        header.setComplexLines(Collections.singletonList(new VariantFileHeaderComplexLine("contig", "chr1", null, null, null, Collections.singletonMap("length", "1000"))));

        studyMetadata = new VariantStudyMetadata();
        studyMetadata.setId(STUDY_ID);
        studyMetadata.setFiles(Arrays.asList(
                getVariantFileMetadata(header, 1).getImpl(),
                getVariantFileMetadata(header, 2).getImpl(),
                getVariantFileMetadata(header, 3).getImpl()
        ));

        studyMetadata.setAggregatedHeader(header);

        variants.add(variant("chr1:1000:A:T", file("F1", "PASS", 80F), file("F2", "NoPass", 100F)));
        variants.add(variant("chr1:2000:A:G", file("F1", "NoPass", 100F), file("F2", "PASS", 100F)));
        variants.add(variant("chr1:3000:C:T", file("F1", "PASS", 90F), file("F2", "PASS", 100F)));
        variants.add(variant("chr1:4000:G:A", file("F1", "PASS", 100F), file("F2", "PASS", 100F)));

        reader = new VariantReader() {
            private Iterator<Variant> iterator = variants.iterator();

            @Override
            public List<String> getSampleNames() {
                return null;
            }

            @Override
            public VariantFileMetadata getVariantFileMetadata() {
                return null;
            }

            @Override
            public List<Variant> read(int i) {
                List<Variant> variants = new ArrayList<>(i);
                while (i > 0 && iterator.hasNext()) {
                    i--;
                    variants.add(iterator.next());
                }
                return variants;
            }
        };
    }

    protected VariantFileMetadata getVariantFileMetadata(VariantFileHeader header, int fileId) {
        VariantFileMetadata fileMetadata = new VariantFileMetadata("F" + fileId, "");
        fileMetadata.setSampleIds(Arrays.asList(buildSampleId(fileId, 1), buildSampleId(fileId, 2)));
        fileMetadata.setHeader(header);
        return fileMetadata;
    }

    private Variant variant(String var, FileEntry... files) {
        Variant variant = new Variant(var);
        StudyEntry studyEntry = new StudyEntry(STUDY_ID);
        studyEntry.setFiles(Arrays.asList(files));
        studyEntry.setFormatAsString("GT");
        for (FileEntry file : files) {
            Integer fileId = Integer.valueOf(file.getFileId().substring(1));
            for (Integer i = 0; i < fileId; i++) {
                studyEntry.addSampleData(buildSampleId(fileId, i), Collections.singletonList("0/0"));
            }
        }
        variant.addStudyEntry(studyEntry);

        ConsequenceType consequenceType = new ConsequenceType();
        consequenceType.setBiotype("lincRNA");
        consequenceType.setSequenceOntologyTerms(Collections.singletonList(new SequenceOntologyTerm("transcript_ablation", "SO:00001893")));
        VariantAnnotation variantAnnotation = new VariantAnnotation();
        variantAnnotation.setConsequenceTypes(Collections.singletonList(consequenceType));
        variant.setAnnotation(variantAnnotation);
        return variant;
    }

    private String buildSampleId(Integer fileId, Integer sampleIdx) {
        return "S" + (fileId * 100 + sampleIdx);
    }

    private FileEntry file(String fileId, String filter, float qual) {
        HashMap<String, String> attributes = new HashMap<>(2);
        attributes.put(StudyEntry.FILTER, filter);
        attributes.put(StudyEntry.QUAL, String.valueOf(qual));
        return new FileEntry(fileId, null, attributes);
    }

    @Test
    public void testStudyStats() throws Exception {
        VariantSetStatsCalculator statsTask = new VariantSetStatsCalculator(studyMetadata);
        VariantSetStats stats = calculateStats(statsTask);

        System.out.println("stats = " + stats);
    }

    @Test
    public void testFileStats() throws Exception {
        VariantSetStatsCalculator statsTask = new VariantSetStatsCalculator(STUDY_ID, new VariantFileMetadata(studyMetadata.getFiles().get(0)));
        VariantSetStats stats = calculateStats(statsTask);

        System.out.println("stats = " + stats);
        int[] quals = {90, 100, 80, 100};
        double mean = IntStream.of(quals).average().orElse(0);
        double stDev = 0;
        for (int qual : quals) {
            stDev += Math.pow(qual - mean, 2);
        }
        stDev = Math.sqrt(stDev / quals.length);

        assertEquals(4, stats.getNumVariants());
        assertEquals(3, stats.getNumPass());
        assertEquals(2, stats.getNumSamples());

        assertEquals(mean, stats.getMeanQuality(), 0.000001);
        assertEquals(stDev, stats.getStdDevQuality(), 0.000001);
        assertEquals(3, stats.getTiTvRatio(), 0.000001);

    }

    protected VariantSetStats calculateStats(VariantSetStatsCalculator statsTask) throws java.util.concurrent.ExecutionException {
        Config config = Config.builder().setNumTasks(1).build();

        ParallelTaskRunner<Variant, Variant> parallelTaskRunner = new ParallelTaskRunner<>(reader, statsTask, null, config);
        parallelTaskRunner.run();

        return statsTask.getStats();
    }
}
