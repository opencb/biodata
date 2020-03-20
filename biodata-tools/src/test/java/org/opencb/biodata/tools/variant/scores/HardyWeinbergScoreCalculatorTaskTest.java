package org.opencb.biodata.tools.variant.scores;

import htsjdk.tribble.util.popgen.HardyWeinbergCalculation;
import org.junit.Test;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.VariantScore;
import org.opencb.biodata.models.variant.stats.VariantStats;

import java.util.Collections;

import static org.junit.Assert.*;

/**
 * Created on 22/06/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class HardyWeinbergScoreCalculatorTaskTest {

    public static final String STUDY = "study";

    @Test
    public void testHW() throws Exception {

        HardyWeinbergScoreCalculatorTask task = new HardyWeinbergScoreCalculatorTask(STUDY, "ALL");

        Variant variant = new VariantBuilder("1:100:A:C")
                .setStudyId(STUDY)
                .build();

        variant.getStudy(STUDY).setStats(Collections.singletonList(new VariantStats()
                .setCohortId("ALL")
                .addGenotype(new Genotype("0/0"), 50)
                .addGenotype(new Genotype("0/1"), 20)
                .addGenotype(new Genotype("1/1"), 5)
        ));
        task.apply(variant);

        VariantScore score = variant.getStudy(STUDY).getScores().get(0);

        assertNotNull(score);
        assertEquals(HardyWeinbergScoreCalculatorTask.SCORE_ID, score.getId());

        // TODO: check that values are correct

    }
}