package org.opencb.biodata.models.variant.stats;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created on 18/08/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsTest {


    @Test
    public void transitionTest() {
        assertTrue(VariantStats.isTransition("A", "G"));
        assertTrue(VariantStats.isTransition("C", "T"));
        assertTrue(VariantStats.isTransition("G", "A"));
        assertTrue(VariantStats.isTransition("T", "C"));

        assertFalse(VariantStats.isTransition("A", "N"));
        assertFalse(VariantStats.isTransition("C", "N"));
        assertFalse(VariantStats.isTransition("G", "N"));
        assertFalse(VariantStats.isTransition("T", "N"));
        assertFalse(VariantStats.isTransition("A", ""));
        assertFalse(VariantStats.isTransition("C", ""));
        assertFalse(VariantStats.isTransition("G", ""));
        assertFalse(VariantStats.isTransition("T", ""));
        assertFalse(VariantStats.isTransition("", "A"));
        assertFalse(VariantStats.isTransition("", "C"));
        assertFalse(VariantStats.isTransition("", "G"));
        assertFalse(VariantStats.isTransition("", "T"));
    }

    @Test
    public void transversionTest() {
        assertFalse(VariantStats.isTransversion("A", "G"));
        assertFalse(VariantStats.isTransversion("C", "T"));
        assertFalse(VariantStats.isTransversion("G", "A"));
        assertFalse(VariantStats.isTransversion("T", "C"));

        assertTrue(VariantStats.isTransversion("A", "C"));
        assertTrue(VariantStats.isTransversion("A", "T"));
        assertTrue(VariantStats.isTransversion("C", "G"));
        assertTrue(VariantStats.isTransversion("C", "A"));
        assertTrue(VariantStats.isTransversion("G", "T"));
        assertTrue(VariantStats.isTransversion("G", "C"));
        assertTrue(VariantStats.isTransversion("T", "G"));
        assertTrue(VariantStats.isTransversion("T", "A"));


        assertFalse(VariantStats.isTransversion("A", "N"));
        assertFalse(VariantStats.isTransversion("C", "N"));
        assertFalse(VariantStats.isTransversion("G", "N"));
        assertFalse(VariantStats.isTransversion("T", "N"));
        assertFalse(VariantStats.isTransversion("A", ""));
        assertFalse(VariantStats.isTransversion("C", ""));
        assertFalse(VariantStats.isTransversion("G", ""));
        assertFalse(VariantStats.isTransversion("T", ""));
        assertFalse(VariantStats.isTransversion("", "A"));
        assertFalse(VariantStats.isTransversion("", "C"));
        assertFalse(VariantStats.isTransversion("", "G"));
        assertFalse(VariantStats.isTransversion("", "T"));
    }

}