package org.opencb.biodata.formats.variant.cosmic;

import org.opencb.biodata.models.sequence.SequenceLocation;
import org.opencb.biodata.models.variant.avro.EvidenceEntry;

import java.util.List;

public interface CosmicParserCallback {
    boolean processEvidenceEntries(SequenceLocation sequenceLocation, List<EvidenceEntry> evidenceEntries);
}
