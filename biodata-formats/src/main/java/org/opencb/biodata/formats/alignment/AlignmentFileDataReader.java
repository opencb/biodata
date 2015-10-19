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
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.biodata.models.core.Region;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 18/10/15.
 */
public class AlignmentFileDataReader implements AlignmentDataReader, Iterable<Alignment> {

    private Path input;

    private SamReader samReader;
    private SAMFileHeader samFileHeader;
    private SAMRecordIterator samRecordIterator;

    public AlignmentFileDataReader(Path input) throws IOException {
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
        if (samRecordIterator == null) {
            samRecordIterator = samReader.iterator();
        }
        return samRecordIterator;
    }

    @Override
    public boolean open() {
        SamReaderFactory srf = SamReaderFactory.make();
        srf.validationStringency(ValidationStringency.LENIENT);
        samReader = srf.open(SamInputResource.of(input.toFile()));
        samFileHeader = samReader.getFileHeader();
        samRecordIterator = samReader.iterator();
        return samReader != null;
    }


    @Override
    public AlignmentHeader getHeader() {
        return AlignmentConverter.buildAlignmentHeader(samFileHeader, "");
    }

    public AlignmentFileDataReaderIterator query(String chromosome, int start, int end, boolean contained) {
        SAMRecordIterator queryIterator = samReader.query(chromosome, start, end, contained);
        return new AlignmentFileDataReaderIterator(queryIterator);
    }

    public AlignmentFileDataReaderIterator query(Region region, boolean contained) {
        return query(region.getChromosome(), region.getStart(), region.getEnd(), contained);
    }

    public AlignmentFileDataReaderIterator query(List<Region> regions, boolean contained) {
        QueryInterval[] queryIntervals = new QueryInterval[regions.size()];
        int referenceIndex;
        SAMFileHeader samFileHeader = samReader.getFileHeader();
        for (int i = 0; i < regions.size(); i++) {
            referenceIndex = samFileHeader.getSequenceIndex(regions.get(i).getChromosome());
            queryIntervals[i] = new QueryInterval(referenceIndex, regions.get(i).getStart(), regions.get(i).getEnd());
        }
        SAMRecordIterator queryIterator = samReader.query(queryIntervals, contained);
        return new AlignmentFileDataReaderIterator(queryIterator);
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
    public AlignmentFileDataReaderIterator iterator() {
        AlignmentFileDataReaderIterator alignmentFileDataReaderIterator = new AlignmentFileDataReaderIterator(nativeIterator());
        return alignmentFileDataReaderIterator;
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

}
