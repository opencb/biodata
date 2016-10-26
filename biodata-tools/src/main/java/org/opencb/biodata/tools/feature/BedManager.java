package org.opencb.biodata.tools.feature;

import org.opencb.biodata.formats.feature.bed.Bed;

/**
 * Created by imedina on 26/10/16.
 */
public class BedManager extends FeatureManager<Bed> {


    public BedManager() {
        this.chromsomeColIndex = 1;
        this.startColIndex = 3;
        this.endColIndex = 4;
    }



}
