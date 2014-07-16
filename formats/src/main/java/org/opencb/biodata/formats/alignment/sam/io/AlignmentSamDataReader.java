package org.opencb.biodata.formats.alignment.sam.io;

import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileReader;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMRecordIterator;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;
import org.opencb.biodata.formats.alignment.AlignmentFactory;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;

/**
 * Created with IntelliJ IDEA.
 * User: imedina
 * Date: 10/30/13
 * Time: 5:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class AlignmentSamDataReader implements AlignmentDataReader<Alignment> {

    private final String filename;
    private final String studyName;
    private SAMFileReader reader;
    public SAMFileHeader samHeader;
    public AlignmentHeader header;
    private SAMRecordIterator iterator;
    private boolean enableFileSource;

    public AlignmentSamDataReader(String filename, String studyName){
        this(filename,studyName, false);
    }
    public AlignmentSamDataReader(String filename, String studyName, boolean enableFileSource) {
        this.filename = filename;
        this.enableFileSource = enableFileSource;
        this.studyName = studyName;
    }

    @Override
    public boolean open() {

        Path path;
        File file;
        path = Paths.get(this.filename);
        if(!Files.exists(path))
            return false;
        file = path.toFile();

        reader = new SAMFileReader(file);
        if(enableFileSource){
            reader.enableFileSource(true);
        }
        reader.setValidationStringency(SAMFileReader.ValidationStringency.LENIENT);
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
        header = AlignmentFactory.buildAlignmentHeader(samHeader, studyName);
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
            //alignment = new Alignment(record, null);
            alignment = AlignmentFactory.buildAlignment(record);
            /*
            alignment = new Alignment(record.getReadName(), record.getReferenceName(), record.getAlignmentStart(), record.getAlignmentEnd(),
                record.getUnclippedStart(), record.getUnclippedEnd(), record.getReadLength(),
                record.getMappingQuality(), record.getBaseQualityString(),
                record.getMateReferenceName(), record.getMateAlignmentStart(),
                record.getInferredInsertSize(), record.getFlags(),
                AlignmentHelper.getDifferencesFromCigar(record,null), null);
                */
//            Alignment alignment = new Alignment(record, null, record.getReadString());

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
