package org.opencb.biodata.tools.variant.filters;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantType;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

/**
 * Created on 12/10/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAvroFiltersTest {

    protected static final String STUDY_ID = "study";
    protected static final String FILE_ID = "file";
    private List<Variant> variants;

    @Before
    public void setUp() throws Exception {
        variants = Arrays.asList(
                Variant.newBuilder("1:10:A:T")
                        .setId("v1")
                        .setStudyId(STUDY_ID)
                        .setFormat("GT", "DP")
                        .addSample("s1", "0/1", "10")
                        .setFileId(FILE_ID)
                        .setFilter("PASS")
                        .setQuality(1234.0).build(),
                Variant.newBuilder("1:20:A:-")
                        .setId("v2")
                        .setStudyId(STUDY_ID)
                        .setFormat("GT", "DP", "DP2")
                        .addSample("s1", "0/0", "20", "20")
                        .setFileId(FILE_ID)
                        .setFilter("noPass")
                        .setQuality(321.0).build(),
                Variant.newBuilder("1:30:A:.")
                        .setId("v3")
                        .setStudyId(STUDY_ID)
                        .setFormat("GT", "DP", "DP2")
                        .addSample("s1", "1/1", "60", "60")
                        .setFileId(FILE_ID)
                        .setFilter("filter1;filter2")
                        .setQuality(1234.0).build()
                );
    }

    @Test
    public void test() {
        assertEquals(Arrays.asList("v1"), filter(new VariantAvroFilters().addTypeFilter(VariantType.SNV)));
        assertEquals(Arrays.asList("v2"), filter(new VariantAvroFilters().addTypeFilter(VariantType.INDEL)));
        assertEquals(Arrays.asList("v3"), filter(new VariantAvroFilters().addTypeFilter(VariantType.NO_VARIATION)));
        assertEquals(Arrays.asList("v1", "v2"), filter(new VariantAvroFilters().addSampleFormatFilter("GT", Arrays.asList("0/0", "0/1")::contains)));
        assertEquals(Arrays.asList("v2", "v3"), filter(new VariantAvroFilters().addSampleFormatFilter("DP", dp -> Double.valueOf(dp) > 15)));
        assertEquals(Arrays.asList("v2", "v3"), filter(new VariantAvroFilters().addFilter("FORMAT:DP>15")));
        assertEquals(Arrays.asList("v2"), filter(new VariantAvroFilters()
                .addSampleFormatFilter("GT", Arrays.asList("0/0", "0/1")::contains)
                .addFilter("FORMAT:DP>15")));
        assertEquals(Arrays.asList("v1"), filter(new VariantAvroFilters().addFilter("FILTER=PASS")));
        assertEquals(Arrays.asList("v1", "v2"), filter(new VariantAvroFilters().addFilter("FILTER=PASS,noPass")));
        assertEquals(Arrays.asList("v1", "v2"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,noPass")));
        assertEquals(Arrays.asList("v1", "v3"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,filter2")));
        assertEquals(Arrays.asList("v1", "v3"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,filter1")));
        assertEquals(Arrays.asList("v1", "v3"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,filter1,filter2")));
        assertEquals(Arrays.asList("v1"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,noPass;QUAL>500")));
        assertEquals(Arrays.asList("v2"), filter(new VariantAvroFilters().addFilter("FILE:FILTER=PASS,noPass;QUAL<500")));

        // Negated filter
        assertEquals(Arrays.asList("v2", "v3"), filter(new VariantAvroFilters().addFilter("FILTER=!PASS")));
        assertEquals(Arrays.asList("v2"), filter(new VariantAvroFilters().addFilter("FILTER=!PASS,!filter1")));

        // Missing value
        assertEquals(Arrays.asList("v2", "v3"), filter(new VariantAvroFilters().addFilter("FORMAT:DP2<100")));
        assertEquals(Arrays.asList("v2", "v3"), filter(new VariantAvroFilters().addFilter("FORMAT:DP2>10")));
        assertEquals(Arrays.asList("v1", "v2", "v3"), filter(new VariantAvroFilters().addFilter(true, true, "FORMAT:DP2<100")));

    }

    private List<String> filter(Predicate<Variant> variantAvroFilters) {
        return variants.stream().filter(variantAvroFilters).map(Variant::getId).collect(Collectors.toList());
    }
}