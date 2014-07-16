package org.opencb.biodata.formats.alignment.io;


import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.commons.io.DataReader;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AlignmentDataReader<T> extends DataReader<T> {

    public AlignmentHeader getHeader();
}
