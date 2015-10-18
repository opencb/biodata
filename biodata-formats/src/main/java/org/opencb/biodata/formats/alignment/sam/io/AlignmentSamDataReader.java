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

package org.opencb.biodata.formats.alignment.sam.io;

import htsjdk.samtools.SAMFileHeader;
import htsjdk.samtools.SAMFileReader;
import htsjdk.samtools.SAMRecord;
import htsjdk.samtools.SAMRecordIterator;
import org.opencb.biodata.formats.alignment.AlignmentConverter;
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: imedina
 * Date: 10/30/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AlignmentSamDataReader implements AlignmentDataReader {

    private final Path input;
    private final String studyName;
    private SAMFileReader reader;
    public SAMFileHeader samHeader;
    public AlignmentHeader header;
    private SAMRecordIterator iterator;
    private boolean enableFileSource;

    public AlignmentSamDataReader(Path input, String studyName){
        this(input,studyName, false);
    }
    public AlignmentSamDataReader(Path input, String studyName, boolean enableFileSource) {
        this.input = input;
        this.enableFileSource = enableFileSource;
        this.studyName = studyName;
    }

    @Override
    public boolean open() {

        if(!Files.exists(input))
            return false;

        reader = new SAMFileReader(input.toFile());
        if(enableFileSource){
            reader.enableFileSource(true);
        }
        reader.setValidationStringency(SAMFileReader.getDefaultValidationStringency().LENIENT);
        iterator = reader.iterator();

        return true;
    }

    @Override
    public boolean close() {
        reader.close();
        return true;
    }

    @Override
    public boolean pre() {
        samHeader = reader.getFileHeader();
        header = AlignmentConverter.buildAlignmentHeader(samHeader, studyName);
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public List<Alignment> read() {
        Alignment elem = readElem();
        return elem != null? Arrays.asList(elem) : null;
    }

    public Alignment readElem() {
        Alignment alignment = null;


        SAMRecord record = null;
        if(iterator.hasNext()){
            record = iterator.next();
            alignment = AlignmentConverter.buildAlignment(record);
        }
        return alignment;
    }

    @Override
    public List<Alignment> read(int batchSize) {
        List<Alignment> listRecords = new ArrayList<>(batchSize);
        Alignment elem;

        for (int i = 0; (i < batchSize) && (elem = this.readElem()) != null; i++) {
            listRecords.add(elem);
        }

        return listRecords;
    }
    
    @Override
    public AlignmentHeader getHeader(){
        return header;
    }
    public SAMFileHeader getSamHeader(){
        return samHeader;
    }

}
