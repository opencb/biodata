package org.opencb.biodata.formats.sequence.qseq.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.sequence.qseq.Qseq;

public class QseqReader extends AbstractFormatReader<Qseq> {

//    private TextFileReader fileReader;
    private BufferedReader bufferedReader;


    public QseqReader(String fileName) throws IOException {
        this(Paths.get(fileName));
    }

    public QseqReader(Path path) throws IOException {
        super(path);
//        this.fileReader = new TextFileReader(file.getAbsolutePath());

        if(path.toFile().getName().endsWith(".gz")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
        }else {
            bufferedReader = Files.newBufferedReader(path, Charset.defaultCharset());
        }

    }


    @Override
    public void close() throws IOException {
        bufferedReader.close();
    }

    @Override
    public Qseq read() throws FileFormatException {
        Qseq qseq = null;

        try {
            String line = bufferedReader.readLine();
            if (line != null) {
                // Parse all line columns
                String[] fields = line.split("\t");
                if (fields.length != 11) {
                    throw new FileFormatException("Incorrect Qseq line: " + line);
                }

                String machineId = fields[0];
                int run = Integer.parseInt(fields[1]);
                int lane = Integer.parseInt(fields[2]);
                int tile = Integer.parseInt(fields[3]);
                int xCoord = Integer.parseInt(fields[4]);
                int yCoord = Integer.parseInt(fields[5]);
                int index = Integer.parseInt(fields[6]);
                int readId = Integer.parseInt(fields[7]);
                String seq = fields[8];
                String quality = fields[9];
                int filteringPassed = Integer.parseInt(fields[10]);

                // Build Qseq object
                qseq = new Qseq(machineId, run, lane, tile, xCoord, yCoord, index, readId, seq, quality, filteringPassed);
            }
        } catch (IOException ex) {
            throw new FileFormatException(ex);
        }

        return qseq;
    }

    @Override
    public Qseq read(String regexFilter) throws FileFormatException {
        Qseq qseq = this.read();
        boolean found = false;
        while (!found && qseq != null) {
            if (qseq.getMachineId().matches(regexFilter)) {
                found = true;
            } else {
                qseq = this.read();
            }
        }
        return qseq;
    }

    @Override
    public List<Qseq> readAll() throws FileFormatException {
        List<Qseq> fastaList = new ArrayList<Qseq>();

        Qseq qseq;
        while ((qseq = this.read()) != null) {
            fastaList.add(qseq);
        }

        return fastaList;
    }

    @Override
    public List<Qseq> readAll(String regexFilter) throws FileFormatException {
        List<Qseq> fastaList = new ArrayList<Qseq>();

        Qseq qseq;
        while ((qseq = this.read(regexFilter)) != null) {
            fastaList.add(qseq);
        }

        return fastaList;
    }

    @Override
    public int size() throws IOException, FileFormatException {
        int size = 0;
        while (this.read() != null) {
            size++;
        }
        return size;
    }

    @Override
    public List<Qseq> read(int size) throws FileFormatException {
        // TODO Auto-generated method stub
        return null;
    }

}
