package org.opencb.biodata.formats.alignment.sam.io;


import net.sf.samtools.SAMFileHeader;
import net.sf.samtools.SAMFileWriterImpl;
import net.sf.samtools.SAMRecord;
import net.sf.samtools.SAMTextWriter;
import org.opencb.biodata.formats.alignment.AlignmentConverter;
import org.opencb.biodata.formats.alignment.io.AlignmentDataWriter;
import org.opencb.biodata.formats.sequence.fasta.dbadaptor.CellBaseSequenceDBAdaptor;
import org.opencb.biodata.formats.sequence.fasta.dbadaptor.SequenceDBAdaptor;
import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentHeader;
import org.opencb.biodata.models.alignment.exceptions.ShortReferenceSequenceException;
import org.opencb.biodata.models.feature.Region;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.opencb.biodata.formats.alignment.io.AlignmentDataReader;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 5:31 PM
 *
 * This class needs the SAMFileHeader to write. If it is not set, it will fail.
 */
public class AlignmentSamDataWriter implements AlignmentDataWriter {

    protected SAMFileWriterImpl writer;
    private SAMFileHeader samFileHeader;
    AlignmentDataReader reader;
    protected String filename;
    private AlignmentHeader header;
    private int maxSequenceSize = defaultMaxSequenceSize;   // max length of the reference sequence.
    private static final int defaultMaxSequenceSize = 100000;
    private String referenceSequence;
    private long referenceSequenceStart = -1;
    private boolean headerWritten = false;
    private boolean validSequence = false;
    //private SequenceDBAdaptor adaptor = new CellBaseSequenceDBAdaptor();


    public AlignmentSamDataWriter(String filename, AlignmentHeader header) {
        this.filename = filename;
        this.header = header;
        this.reader = null;
    }




    public AlignmentSamDataWriter(String filename, AlignmentDataReader reader) {
        this.header = null;
        this.filename = filename;
        this.reader = reader;
    }

    @Override
    public boolean open() {
        Path path;
        File file;
        path = Paths.get(this.filename);
        file = path.toFile();

        this.writer = new SAMTextWriter(file);
//        try {
//            this.adaptor.open();
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }

        return true;
    }

    @Override
    public boolean close() {
        writer.close();
        return true;
    }

    @Override
    public boolean pre() {
        if(header != null){
            header = reader.getHeader();
        }
        if(header != null){
            writeHeader(header);
            headerWritten = true;
        }
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Alignment element) {
        if (!headerWritten) {  // if samFileHeader wasn't available at pre()
            if (header == null) {
                header = reader.getHeader();
                //if header == null, return false
            }

            writeHeader(header);
            headerWritten = true;
        }
        
        if (!validSequence) {   //element.getUnclippedStart() < referenceSequenceStart
            getSequence(element.getChromosome(), element.getUnclippedStart());
        }
        // assert refseq correct
        SAMRecord samElement = null;

        try {
            samElement = AlignmentConverter.buildSAMRecord(element, samFileHeader, referenceSequence, referenceSequenceStart);
        } catch (ShortReferenceSequenceException e) {
            getSequence(element.getChromosome(), element.getUnclippedStart());
        }
        if(samElement == null){
            try {
                samElement = AlignmentConverter.buildSAMRecord(element, samFileHeader, referenceSequence, referenceSequenceStart);
            } catch (ShortReferenceSequenceException e) {
                System.out.println("[ERROR] Can't get the correct reference sequence");
                e.printStackTrace();
                return false;
            }
        }
        writer.addAlignment(samElement);
        return true;
    }

    private void getSequence(String chromosome, long pos){
        System.out.println("Asking for reference... " + pos + " - " + (pos + maxSequenceSize));
        validSequence = true;
        referenceSequenceStart = pos<1?1:pos;
        try {
            referenceSequence = AlignmentHelper.getSequence(
                    new Region(chromosome, (int)pos, (int)pos + maxSequenceSize));
        } catch (IOException e) {
            System.out.println("Could not get reference sequence");
        }
    }

    @Override
    public boolean write(List<Alignment> batch) {
        for(Alignment r : batch){
            if(!write(r))
                return false;
        }
        return true;
    }

    @Override
    public boolean writeHeader(AlignmentHeader head) {
        samFileHeader = AlignmentConverter.buildSAMFileHeader(head);

        writer.setSortOrder(samFileHeader.getSortOrder(), true);
        writer.setHeader(samFileHeader);
        return true;
    }

    public SAMFileHeader getSamFileHeader() {
        return samFileHeader;
    }

    public void setSamFileHeader(SAMFileHeader samFileHeader) {
        this.samFileHeader = samFileHeader;
    }

    public int getMaxSequenceSize() {
        return maxSequenceSize;
    }

    public void setMaxSequenceSize(int maxSequenceSize) {
        this.maxSequenceSize = maxSequenceSize;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getReferenceSequence() {
        return referenceSequence;
    }

    public void setReferenceSequence(String referenceSequence) {
        this.referenceSequence = referenceSequence;
    }

    public long getReferenceSequenceStart() {
        return referenceSequenceStart;
    }

    public void setReferenceSequenceStart(long referenceSequenceStart) {
        this.referenceSequenceStart = referenceSequenceStart;
    }


}
