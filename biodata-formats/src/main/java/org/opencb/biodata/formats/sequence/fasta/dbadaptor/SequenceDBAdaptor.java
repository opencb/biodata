package org.opencb.biodata.formats.sequence.fasta.dbadaptor;

import org.opencb.biodata.models.feature.Region;
import org.opencb.commons.containers.map.QueryOptions;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jacobo on 14/08/14.
 */
public abstract class SequenceDBAdaptor {

    protected Path credentialsPath;

    public SequenceDBAdaptor(){
    }

    public SequenceDBAdaptor(Path credentials){
        this.credentialsPath = credentials;
    }

    abstract public void open()  throws IOException;
    abstract public void close()  throws IOException;

    abstract public String getSequence(Region region) throws IOException;
    abstract public String getSequence(Region region, String species) throws IOException;

}
