package org.opencb.biodata.tools.feature;

import org.junit.Test;
import org.opencb.biodata.models.core.Region;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManagerTest {

    @Test
    public void query() throws Exception {
        // this reads a file from src/test/resources folder
        Path inputPath = Paths.get(getClass().getResource("/wigVarStepExampleSmallChr21.bw").toURI());

        BigWigManager bigWigManager = new BigWigManager(inputPath);
        List<Float> chr21 = bigWigManager.query(new Region("chr21", 9411190, 9411291));
        bigWigManager.close();

        assertEquals(20, chr21.size());
    }

}