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

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;
import org.opencb.biodata.tools.alignment.filters.SamRecordFilters;

import java.util.Iterator;

/**
 * This class implements an Iterator for BAM files that can filter reads.
 * There are three subclasses depending on the data model to be returned: SAMRecord, ReadAlignment in Avro and ReadAlignment in Protobuff.
 */
public abstract class BamIterator<T> implements Iterator<T>, AutoCloseable {

    private SAMRecordIterator samRecordIterator;
    protected AlignmentFilters<SAMRecord> filters;

    protected SAMRecord prevNext;

    public BamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public BamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this.samRecordIterator = samRecordIterator;
        if (filters == null) {
            filters = new SamRecordFilters();
        }
        this.filters = filters;

        findNextMatch();
    }

    protected void findNextMatch() {
        prevNext = null;
        while (samRecordIterator.hasNext()) {
            SAMRecord next = samRecordIterator.next();
            if (filters.test(next)) {
                prevNext = next;
                return;
            }
        }
    }

    @Override
    public void close() {
        samRecordIterator.close();
    }

}
