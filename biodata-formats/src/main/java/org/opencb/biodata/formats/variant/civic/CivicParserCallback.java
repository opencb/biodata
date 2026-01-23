package org.opencb.biodata.formats.variant.civic;

import org.opencb.biodata.models.core.civic.CivicVariant;

public interface CivicParserCallback {
    boolean processCivicVariant(CivicVariant civicVariant);
}
