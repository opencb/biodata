package org.opencb.biodata.formats.alignment.sam.io;

import net.sf.samtools.BAMFileWriter;
import org.opencb.biodata.models.alignment.AlignmentHeader;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 5:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentBamDataWriter extends AlignmentSamDataWriter  {


    public AlignmentBamDataWriter(Path input, AlignmentHeader header) {
        super(input, header);
    }

    public AlignmentBamDataWriter(Path input, AlignmentDataReader reader) {
        super(input, reader);
    }

    @Override
    public boolean open() {
        if(this.input.toFile().exists()) {
            writer = new BAMFileWriter(this.input.toFile());
            return true;
        } else {
            return false;
        }
    }

}
