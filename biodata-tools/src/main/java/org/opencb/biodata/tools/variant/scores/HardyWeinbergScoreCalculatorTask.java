package org.opencb.biodata.tools.variant.scores;

import org.opencb.biodata.models.variant.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantScore;
import org.opencb.biodata.models.variant.stats.VariantHardyWeinbergStats;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.commons.run.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 22/06/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class HardyWeinbergScoreCalculatorTask implements Task<Variant, Variant> {

    public static final String SCORE_ID = "hw";
    private final String studyId;
    private final String cohortName;

    private final static Genotype HOM_REF = new Genotype("0/0");
    private final static Genotype HET = new Genotype("0/1");
    private final static Genotype HOM_ALT = new Genotype("1/1");
    private final static Genotype HOM_REF_PHASED = new Genotype("0|0");
    private final static Genotype HET_PHASED = new Genotype("0|1");
    private final static Genotype HOM_ALT_PHASED = new Genotype("1|1");

    public HardyWeinbergScoreCalculatorTask(String studyId, String cohortName) {
        this.studyId = studyId;
        this.cohortName = cohortName;
    }

    @Override
    public List<Variant> apply(List<Variant> list) throws Exception {
        List<Variant> result = new ArrayList<>(list.size());
        for (Variant variant : list) {
            result.add(apply(variant));
        }
        return result;
    }

    public Variant apply(Variant variant) throws Exception {
        StudyEntry study = variant.getStudy(studyId);
        if (study == null) {
            return variant;
        }
        VariantStats stats = study.getStats(cohortName);
        if (stats == null) {
            return variant;
        }

        VariantHardyWeinbergStats hw = new VariantHardyWeinbergStats(
                stats.getGenotypeCount().getOrDefault(HOM_REF, 0) + stats.getGenotypeCount().getOrDefault(HOM_REF_PHASED, 0),
                stats.getGenotypeCount().getOrDefault(HET, 0) + stats.getGenotypeCount().getOrDefault(HET_PHASED, 0),
                stats.getGenotypeCount().getOrDefault(HOM_ALT, 0) + stats.getGenotypeCount().getOrDefault(HOM_ALT_PHASED, 0)
        );
        hw.calculate();

        study.addScore(new VariantScore(SCORE_ID, cohortName, null, hw.getChi2(), hw.getpValue()));

        return variant;
    }
}
