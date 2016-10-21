package org.opencb.biodata.tools.sequence.fasta;

import org.junit.BeforeClass;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

/**
 * Created by imedina on 21/10/16.
 */
public class SamtoolsFastaIndexTest {

    @BeforeClass
    public void setUp() throws Exception {

    }

    @Test
    public void index() throws Exception {
        SamtoolsFastaIndex samtoolsFastaIndex = new SamtoolsFastaIndex();
        System.out.println(getClass().getResource("/homo_sapiens_grch37_small.fa.gz").toURI().toString().replace("file:", ""));
        samtoolsFastaIndex.index(Paths.get(getClass().getResource("/homo_sapiens_grch37_small.fa.gz").toURI().toString().replace("file:", "")));
    }

    @Test
    public void query() throws Exception {
        SamtoolsFastaIndex samtoolsFastaIndex = new SamtoolsFastaIndex(getClass().getResource("/homo_sapiens_grch37_small.fa.gz").toURI().toString());
        long l = System.currentTimeMillis();
        System.out.println(samtoolsFastaIndex.query("21", 1000000, 1000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000000, 10000010));
        System.out.println(samtoolsFastaIndex.query("21", 10000100, 10000200));
        long l1 = System.currentTimeMillis();
        System.out.println(l1 - l);

        assertEquals("", "", "");
    }

}