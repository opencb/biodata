package org.opencb.biodata.formats.sequence.fasta.dbadaptor;

import org.opencb.biodata.models.feature.Region;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jacobo on 14/08/14.
 */
public class SQLiteSequenceDBAdaptor extends SequenceDBAdaptor {

    private Path input;

    /**
     *
     * @param input Accept formats: *.properties, *.sqlite.db
     */
    public SQLiteSequenceDBAdaptor(Path input) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    @Override
    public void open() throws IOException {
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String getSequence(Region region) throws IOException {
        return null;
    }

    @Override
    public String getSequence(Region region, String species) throws IOException {
        return null;
    }

    /**
     * Creates a input.sqlite.db.
     *
     * @param fastaInputFile Accept formats: *.fasta, *.fasta.gz
     */
    public void createDB(Path fastaInputFile){

    }

}
