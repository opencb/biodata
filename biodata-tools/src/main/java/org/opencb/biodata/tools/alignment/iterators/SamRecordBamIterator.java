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

/**
 * Created by pfurio on 27/10/16.
 */
public class SamRecordBamIterator extends BamIterator<SAMRecord> {

    public SamRecordBamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SamRecordBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        super(samRecordIterator, filters);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public SAMRecord next() {
        SAMRecord next = prevNext;
        findNextMatch();
        return next;
    }
}
