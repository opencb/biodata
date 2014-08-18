package org.opencb.biodata.formats.alignment.io;



import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.biodata.models.alignment.AlignmentRegion;
import org.opencb.commons.io.DataWriter;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 2/7/14
 * Time: 5:43 PM
 *
 */
public class AlignmentRegionDataWriter implements DataWriter<AlignmentRegion> {
    private AlignmentDataWriter alignmentDataWriter;


    public AlignmentRegionDataWriter(AlignmentDataWriter alignmentDataWriter) {
        this.alignmentDataWriter = alignmentDataWriter;
    }

    @Override
    public boolean open() {
        return alignmentDataWriter.open();
    }

    @Override
    public boolean close() {
        return alignmentDataWriter.close();
    }

    @Override
    public boolean pre() {
        return alignmentDataWriter.pre();
    }

    @Override
    public boolean post() {
        return alignmentDataWriter.post();
    }

    @Override
    public boolean write(AlignmentRegion elem) {
        // get reference sequence
        for (Alignment alignment : elem.getAlignments()) {
            if(!alignmentDataWriter.write(alignment)){
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean write(List<AlignmentRegion> batch) {
        for(AlignmentRegion r : batch){
            if(!write(r))
                return false;
        }
        return true;
    }
}
