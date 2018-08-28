package org.opencb.biodata.tools.sequence;

/**
 * Created on 27/06/18.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public interface SequenceAdaptor {

    String query(String contig, int start, int end) throws Exception;

}
