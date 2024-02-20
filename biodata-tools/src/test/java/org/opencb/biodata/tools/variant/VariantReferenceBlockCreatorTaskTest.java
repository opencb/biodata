package org.opencb.biodata.tools.variant;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;

public class VariantReferenceBlockCreatorTaskTest {

    @Test
    public void testFillBlocks() throws Exception {

        assertEquals(
                asList("1:1-99:N:.", "1:100:A:C", "1:101-119:N:.", "1:120:A:C", "1:121-10000:N:."),
                apply(               "1:100:A:C",                  "1:120:A:C"));
        assertEquals(
                asList("1:1-99:N:.", "1:100:A:CTG", "1:100:A:C", "1:101-119:N:.", "1:120:A:C", "1:121-10000:N:."),
                apply(               "1:100:A:CTG", "1:100:A:C",                  "1:120:A:C"));
        assertEquals(
                asList("1:1-99:N:.", "1:100:ATG:C", "1:100:A:C", "1:103-119:N:.", "1:120:A:C", "1:121-10000:N:."),
                apply(               "1:100:ATG:C", "1:100:A:C",                  "1:120:A:C"));
        assertEquals(
                asList("1:1-99:N:.", "1:100:A:C", "1:100:ATG:C", "1:103-119:N:.", "1:120:A:C", "1:121-10000:N:."),
                apply(               "1:100:A:C", "1:100:ATG:C",                  "1:120:A:C"));

    }

    @Test
    public void testCreateBlock() throws Exception {
        VariantReferenceBlockCreatorTask task = new VariantReferenceBlockCreatorTask();

        task.init(new VariantBuilder("1:1:A:C")
                .setStudyId("myStudy")
                .setFileId("myFile")
                .setSampleDataKeys("GT", "DP")
                .addSample("s1", "1/0", "10")
                .addSample("s2", "0/0", "30")
                .addSample("s3", "0/1", "20")
                .build());
        Variant variant = task.createRefBlock("1", 100, 200);
        System.out.println("variant = " + variant.toJson());

        assertEquals(VariantType.NO_VARIATION, variant.getType());
        assertEquals("myStudy", variant.getStudies().get(0).getStudyId());
        assertEquals("myFile", variant.getStudies().get(0).getFileId());
        assertEquals(Arrays.asList("s1", "s2", "s3"), variant.getStudies().get(0).getOrderedSamplesName());
        assertEquals(Arrays.asList(
                new SampleEntry(null, null, Collections.singletonList("./.")),
                new SampleEntry(null, null, Collections.singletonList("./.")),
                new SampleEntry(null, null, Collections.singletonList("./."))),
                variant.getStudies().get(0).getSamples());

    }

    private List<String> apply(String ...variants) throws Exception {
        VariantReferenceBlockCreatorTask task = new VariantReferenceBlockCreatorTask(Collections.singletonMap("chr1", 10000));
        List<Variant> list = task.apply(Stream.of(variants).map(Variant::new).collect(Collectors.toList()));
        list.addAll(task.drain());
        return list.stream().map(Variant::toString).collect(Collectors.toList());
    }
}