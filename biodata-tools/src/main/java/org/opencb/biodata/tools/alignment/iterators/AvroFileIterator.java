package org.opencb.biodata.tools.alignment.iterators;

import org.apache.avro.file.DataFileStream;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.filters.ReadAlignmentFilters;

import java.util.Iterator;

/**
 * Created by pfurio on 27/10/16.
 */
public class AvroFileIterator implements Iterator<ReadAlignment>, AutoCloseable  {

    private DataFileStream<ReadAlignment> dataFileStream;
    private AlignmentFilters<ReadAlignment> filters;

    private ReadAlignment prevNext;

    public AvroFileIterator(DataFileStream<ReadAlignment> dataFileStream) {
        this(dataFileStream, null);
    }

    public AvroFileIterator(DataFileStream<ReadAlignment> dataFileStream, AlignmentFilters<ReadAlignment> filters) {
        this.dataFileStream = dataFileStream;
        if (filters == null) {
            filters = new ReadAlignmentFilters();
        }
        this.filters = filters;

        findNextMatch();
    }

    protected void findNextMatch() {
        while (dataFileStream.hasNext()) {
            ReadAlignment next = dataFileStream.next();
            if (filters.test(next)) {
                prevNext = next;
                return;
            }
        }
        prevNext = null;
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public ReadAlignment next() {
        ReadAlignment next = prevNext;
        findNextMatch();
        return next;
    }

    @Override
    public void close() throws Exception {
        dataFileStream.close();
    }
}
