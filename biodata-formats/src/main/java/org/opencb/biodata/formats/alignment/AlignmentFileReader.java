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

import htsjdk.samtools.*;
import org.opencb.biodata.formats.alignment.AlignmentConverter;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.core.Region;
import org.opencb.commons.io.DataReader;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by imedina on 18/10/15.
 */
public class AlignmentFileReader implements DataReader<Alignment>, Iterable<Alignment> {

    private Path input;

    private SamReader samReader;
    private SAMRecordIterator samRecordIterator;

    public AlignmentFileReader(Path input) throws IOException {
        this.input = input;

        init();
    }

    private void init() throws IOException {
        FileUtils.checkFile(input);
    }

    public SAMRecordIterator nativeIterator() {
        if (samReader == null) {
            open();
        }
        return samRecordIterator;
    }

    @Override
    public boolean open() {
        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        samReader = srf.open(SamInputResource.of(input.toFile()));
        samRecordIterator = samReader.iterator();
        return samReader != null;
    }


    public AlignmentFileReaderIterator query(String chromosome, int start, int end, boolean contained) {
        SAMRecordIterator queryIterator = samReader.query(chromosome, start, end, contained);
        return new AlignmentFileReaderIterator(queryIterator);
    }

    public AlignmentFileReaderIterator query(Region region, boolean contained) {
        return query(region.getChromosome(), region.getStart(), region.getEnd(), contained);
    }

    public AlignmentFileReaderIterator query(List<Region> regions, boolean contained) {
        QueryInterval[] queryIntervals = new QueryInterval[regions.size()];
        int referenceIndex;
        SAMFileHeader samFileHeader = samReader.getFileHeader();
        for (int i = 0; i < regions.size(); i++) {
            referenceIndex = samFileHeader.getSequenceIndex(regions.get(i).getChromosome());
            queryIntervals[i] = new QueryInterval(referenceIndex, regions.get(i).getStart(), regions.get(i).getEnd());
        }
        SAMRecordIterator queryIterator = samReader.query(queryIntervals, contained);
        return new AlignmentFileReaderIterator(queryIterator);
    }

    @Override
    public List<Alignment> read() {
        return read(1);
    }

    @Override
    public List<Alignment> read(int batchSize) {
        List<Alignment> results = new ArrayList<>(batchSize);
        int count = 0;
        while (samRecordIterator.hasNext() && count++ < batchSize) {
            results.add(AlignmentConverter.buildAlignment(samRecordIterator.next()));
        }
        return results;
    }

    @Override
    public AlignmentFileReaderIterator iterator() {
        AlignmentFileReaderIterator alignmentFileReaderIterator = new AlignmentFileReaderIterator(nativeIterator());
        return alignmentFileReaderIterator;
    }


    @Override
    public boolean close() {
        if (samRecordIterator != null) {
            samRecordIterator.close();
        }

        if (samReader != null) {
            try {
                samReader.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;
    }


    /**
     * This class provides a GA4GH Alignment iterator of BAM files
     */
    public class AlignmentFileReaderIterator implements Iterator<Alignment> {

        private SAMRecordIterator samRecordIterator;

        public AlignmentFileReaderIterator(SAMRecordIterator samRecordIterator) {
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
}
