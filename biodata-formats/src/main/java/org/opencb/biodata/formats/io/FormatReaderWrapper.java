package org.opencb.biodata.formats.io;


import org.opencb.commons.io.DataReader;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

/**
 * Created by susi on 14/04/16.
 */
public class FormatReaderWrapper<T> implements DataReader<T> {

    private final AbstractFormatReader<T> reader;


    public FormatReaderWrapper(AbstractFormatReader<T> reader) {
        this.reader = reader;
    }


    @Override
    public boolean close() {
        try {
            reader.close();
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }


    @Override
    public List<T> read(int batchSize) {
        try {
            return reader.read(batchSize);
        } catch (FileFormatException e) {
            throw new RuntimeException(e);
        }
    }
}
