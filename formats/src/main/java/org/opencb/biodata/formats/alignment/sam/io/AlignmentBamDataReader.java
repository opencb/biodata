package org.opencb.biodata.formats.alignment.sam.io;

import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentBamDataReader extends AlignmentSamDataReader {
    public AlignmentBamDataReader(Path input, String studyName) {
        super(input, studyName);
    }

    public AlignmentBamDataReader(Path bamPath, String studyName, boolean enableFileSource) {
        super(bamPath, studyName, enableFileSource);
    }
}
