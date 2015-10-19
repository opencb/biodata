/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.alignment;

import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.models.alignment.Alignment;

import java.util.Iterator;
import java.util.function.Consumer;

/**
 * This class provides a GA4GH Alignment iterator of BAM files
 */
public class AlignmentFileDataReaderIterator implements Iterator<Alignment> {

    private SAMRecordIterator samRecordIterator;

    public AlignmentFileDataReaderIterator(SAMRecordIterator samRecordIterator) {
        this.samRecordIterator = samRecordIterator;
    }

    @Override
    public boolean hasNext() {
        return samRecordIterator.hasNext();
    }

    @Override
    public Alignment next() {
        SAMRecord samRecord = samRecordIterator.next();
        Alignment alignment = AlignmentConverter.buildAlignment(samRecord);
        return alignment;
    }

    @Override
    public void remove() {
        samRecordIterator.remove();
    }

    @Override
    public void forEachRemaining(Consumer<? super Alignment> action) {
        // TODO converto action to SAMRecord
//            samRecordIterator.forEachRemaining((Consumer<? super SAMRecord>) action);
    }

    public SAMRecordIterator getSamRecordIterator() {
        return samRecordIterator;
    }
}
