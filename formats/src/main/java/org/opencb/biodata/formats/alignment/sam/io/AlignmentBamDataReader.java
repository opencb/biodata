package org.opencb.biodata.formats.alignment.sam.io;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentBamDataReader extends AlignmentSamDataReader {
    public AlignmentBamDataReader(String filename, String studyName) {
        super(filename, studyName);
    }

    public AlignmentBamDataReader(String bamFile, String studyName, boolean enableFileSource) {
        super(bamFile, studyName, enableFileSource);
    }
}
