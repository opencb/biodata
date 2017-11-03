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

import ga4gh.Reads;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.tools.alignment.converters.SAMRecordToProtoReadAlignmentBiConverter;
import org.opencb.biodata.tools.alignment.filters.AlignmentFilters;

/**
 * Created by pfurio on 25/10/16.
 */
public class SAMRecordToProtoReadAlignmentBamIterator extends BamIterator<Reads.ReadAlignment> {

    private SAMRecordToProtoReadAlignmentBiConverter protoReadAlignmentConverter;

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator) {
        this(samRecordIterator, null);
    }

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters) {
        this(samRecordIterator, filters, true);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentBiConverter();
    }

    public SAMRecordToProtoReadAlignmentBamIterator(SAMRecordIterator samRecordIterator, AlignmentFilters<SAMRecord> filters,
                                                    boolean binQualities) {
        super(samRecordIterator, filters);
        protoReadAlignmentConverter = new SAMRecordToProtoReadAlignmentBiConverter(binQualities);
    }

    @Override
    public boolean hasNext() {
        return prevNext != null;
    }

    @Override
    public Reads.ReadAlignment next() {
        Reads.ReadAlignment readAlignment = protoReadAlignmentConverter.to(prevNext);
        findNextMatch();
        return readAlignment;
    }

}
