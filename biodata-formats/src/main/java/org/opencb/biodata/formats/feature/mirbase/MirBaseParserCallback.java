package org.opencb.biodata.formats.feature.mirbase;

import org.opencb.biodata.models.core.MiRnaGene;

public interface MirBaseParserCallback {
        boolean processMiRnaGene(MiRnaGene miRnaGene);
}
