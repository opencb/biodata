package org.opencb.biodata.formats.alignment.io;


import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.commons.io.DataWriter;


/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 5:17 PM
 * To change this template use File | Settings | File Templates.
 */
public interface AlignmentDataWriter extends DataWriter<Alignment> {

    boolean writeHeader(AlignmentHeader head);

}
