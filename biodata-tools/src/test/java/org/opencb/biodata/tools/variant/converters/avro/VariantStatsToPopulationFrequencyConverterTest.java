package org.opencb.biodata.tools.variant.converters.avro;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.variant.avro.PopulationFrequency;
import org.opencb.biodata.models.variant.avro.VariantStats;
import org.opencb.commons.datastore.core.ObjectMap;
import org.opencb.commons.datastore.core.QueryOptions;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class VariantStatsToPopulationFrequencyConverterTest {

    private VariantStatsToPopulationFrequencyConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new VariantStatsToPopulationFrequencyConverter();
    }

    @Test
    public void testConvert() {
        assertEquals(
                getExpected()
                        .setAltAlleleCount(123412341)
                        .setRefHomGenotypeCount(4)
                        .setAltAlleleFreq(0.2f)
                        .setRefAlleleFreq(0.3f)
                        .setHetGenotypeCount(10)
                        .setAltHomGenotypeCount(2)
                        .setRefAlleleCount(1234).build(),
                convert(builder()
                        .setAltAlleleCount(123412341)
                        .setRefAlleleCount(1234)
                        .setAltAlleleFreq(0.2f)
                        .setRefAlleleFreq(0.3f)
                        .setGenotypeCount(new HashMap<>(((Map) new ObjectMap()
                                .append("0/0", 1)
                                .append("0|0", 2)
                                .append("0", 1)
                                .append("0/1", 2)
                                .append("1/0", 3)
                                .append("1|0", 2)
                                .append("0|1", 3)
                                .append("1|1", 1)
                                .append("1/1", 1)
                                .append("1/2", 112341234) // Ignored
                                .append("1/*", 112341234) // Ignored
                        )))
                        .build()));
    }

    private VariantStats.Builder builder() {
        return VariantStats.newBuilder(new VariantStats())
                .setCohortId("pop")
                .setRefAlleleCount(0)
                .setAltAlleleCount(0)
                .setRefAlleleFreq(0f)
                .setAltAlleleFreq(0f)
                .setFilterCount(new HashMap<>())
                .setFilterFreq(new HashMap<>());
    }

    private PopulationFrequency convert(VariantStats stats) {
        return converter.convert("ST", "pop", new org.opencb.biodata.models.variant.stats.VariantStats(stats), "A", "C");
    }

    private PopulationFrequency.Builder getExpected() {
        PopulationFrequency pf = new PopulationFrequency("ST", "pop", "A", "C", 0f, 0f, 0, 0, null, null, null, null, null, null);
        return PopulationFrequency.newBuilder(pf);
    }


}