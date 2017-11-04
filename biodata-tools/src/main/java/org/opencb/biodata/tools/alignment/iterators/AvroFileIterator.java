/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.alignment.iterators;

import org.apache.avro.file.DataFileStream;
import org.ga4gh.models.ReadAlignment;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.filters.ReadAlignmentFilters;

import java.util.Iterator;

/**
 * This class implements an Iterator for ReadAlignment Avro files.
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
