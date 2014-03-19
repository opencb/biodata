package org.opencb.biodata.formats.sequence.fastq.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.sequence.fastq.FastQ;

public class FastaQReader extends AbstractFormatReader<FastQ> {

//    private TextFileReader fileReader;
    private BufferedReader bufferedReader;

    private static final String SEQ_ID_CHAR = "@";

    private static final String QUALITY_ID_CHAR = "+";

    private int encoding;

//    public FastaQReader(InputStream input, int encoding) throws IOException {
//        this.fileReader = new TextFileReader(input);
//        this.encoding = encoding;
//    }
//
//    public FastaQReader(String fileName, int encoding) throws IOException {
//        this(new File(fileName), encoding);
//    }
//
//    public FastaQReader(String fileName) throws IOException {
//        this(new File(fileName));
//    }

    public FastaQReader(Path path) throws IOException {
        this(path, FastQ.SANGER_ENCODING);
    }

    public FastaQReader(Path path, int encoding) throws IOException {
        super(path);
//        this.fileReader = new TextFileReader(file.getAbsolutePath());
        this.encoding = encoding;

        if(path.toFile().getName().endsWith(".gz")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
        }else {
            bufferedReader = Files.newBufferedReader(path, Charset.defaultCharset());
        }
    }

    @Override
    public void close() throws IOException {
        this.bufferedReader.close();
    }

    @Override
    public List<FastQ> readAll() throws FileFormatException {
        List<FastQ> fastaList = new ArrayList<FastQ>();

        FastQ fasta;
        while ((fasta = this.read()) != null) {
            fastaList.add(fasta);
        }

        return fastaList;
    }

    @Override
    public List<FastQ> readAll(String regexFilter) throws FileFormatException {
        List<FastQ> fastaList = new ArrayList<FastQ>();

        FastQ fasta;
        while ((fasta = this.read(regexFilter)) != null) {
            fastaList.add(fasta);
        }

        return fastaList;
    }

    public FastQ read() throws FileFormatException {
        FastQ fasta = null;

        try {
            // Read Id Line. If it's null, the end of path has been reached
            String idLine = this.readIdLine();
            if (idLine != null) {
                // Obtain Id and Desc from Id Line
                String id = idLine.split("\\s")[0].substring(1);
                String desc = idLine.substring(id.length() + 1);

                // Read Sequence
                StringBuilder sequenceBuilder = new StringBuilder();
                int numSequenceLines = this.readSequenceLines(sequenceBuilder);
                String sequence = sequenceBuilder.toString().trim();

                // Read Quality
                StringBuilder qualityBuilder = this.readQualityLines(numSequenceLines);
                String quality = qualityBuilder.toString().trim();

                // Check that sequence and quality sizes are equal
                this.checkQualitySize(id, sequence, quality);

                // Build Fasta object
                fasta = new FastQ(id, desc.trim(), sequence, quality, this.encoding);
            }
        } catch (IOException ex) {
            throw new FileFormatException(ex);
        }

        return fasta;
    }

    public int size() throws IOException, FileFormatException {
        int size = 0;
        while (this.read() != null) {
            size++;
        }
        return size;
    }

    @Override
    public FastQ read(String regexFilter) throws FileFormatException {
        FastQ seq = this.read();
        boolean found = false;
        while (!found && seq != null) {
            if (seq.getId().matches(regexFilter)) {
                found = true;
            } else {
                seq = this.read();
            }
        }
        return seq;
    }

    private String readIdLine() throws FileFormatException, IOException {
        String idLine;

        // TODO: Comprobar si hay lineas de basura antes de la primera secuencia,
        //		 en lugar de lanzar una excepcion directamente
        idLine = this.bufferedReader.readLine();
        if ((idLine != null) && !idLine.startsWith(FastaQReader.SEQ_ID_CHAR)) {
            throw new FileFormatException("Incorrect ID Line: " + idLine);
        }

        return idLine;
    }

    private int readSequenceLines(StringBuilder sequenceBuilder) throws FileFormatException, IOException {
        int numSequenceLines = 0;
        // read the sequence string
        String line = this.bufferedReader.readLine();
        while (line != null && !line.startsWith(FastaQReader.QUALITY_ID_CHAR)) {
            // check the sequence format and throws a FileFormatException if it's wrong
            checkSequence(line);
            sequenceBuilder.append(line);
            numSequenceLines++;
            line = this.bufferedReader.readLine();
        }

        return numSequenceLines;
    }


    private StringBuilder readQualityLines(int numSequenceLines) throws IOException, FileFormatException {
        StringBuilder qualityBuilder = new StringBuilder();

        String line;
        int numLinesRead = 1;
        while ((numLinesRead <= numSequenceLines) && (line = this.bufferedReader.readLine()) != null) {
            // check the sequence format and throws a FileFormatException if it's wrong
            checkQuality(line);
            qualityBuilder.append(line);
            numLinesRead++;
        }

        return qualityBuilder;
    }

    private void checkSequence(String sequence) throws FileFormatException {
        // TODO: Por ahora no hacemos comprobacion alguna y nos creemos que la secuencia viene bien
    }

    private void checkQuality(String sequence) throws FileFormatException {
        // TODO: Por ahora no hacemos comprobacion alguna y nos creemos que la secuencia viene bien
    }

    /**
     * Check that the sequence and quality strings have the same length
     *
     * @param id       - FastQ id
     * @param sequence - FastQ sequence string
     * @param quality  - FastQ quality string
     * @throws FileFormatException - If the sequence and quality strings have different lengths
     */
    private void checkQualitySize(String id, String sequence, String quality) throws FileFormatException {
        if (sequence.length() != quality.length()) {
            throw new FileFormatException("Quality and Sequence lenghts are different in Fasta " + id);
        }
    }

    @Override
    public List<FastQ> read(int size) throws FileFormatException {
        // TODO Auto-generated method stub
        return null;
    }

}
