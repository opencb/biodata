package org.opencb.biodata.models.core;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertEquals;

public class RegionTest {

    @Test
    public void testParse() {
        assertEquals("1", new Region("1").toString());
        assertEquals("chr1", new Region("chr1").toString());
        assertEquals("1:100-100", new Region("1:100").toString());
        assertEquals("1:100-100", new Region("1:100-100").toString());
        assertEquals("1:100-200", new Region("1:100-200").toString());
    }
}