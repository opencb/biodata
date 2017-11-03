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

package org.opencb.biodata.formats.sequence.fasta.io;

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
import org.opencb.biodata.formats.sequence.fasta.Fasta;

public class FastaReader extends AbstractFormatReader<Fasta> {

//    private TextFileReader fileReader;
    private BufferedReader bufferedReader;

    private static final String SEQ_ID_CHAR = ">";

    private String lastLineRead = null;

    private boolean endOfFileReached = false;

    public FastaReader(String fileName) throws IOException {
        this(Paths.get(fileName));
    }

    public FastaReader(Path path) throws IOException {
        super(path);
//        this.fileReader = new TextFileReader(file.getAbsolutePath());
//
        if(path.toFile().getName().endsWith(".gz")) {
            bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
        }else {
            bufferedReader = Files.newBufferedReader(path, Charset.defaultCharset());
        }
    }


    @Override
    public Fasta read() throws FileFormatException {
        Fasta fasta = null;

        if (!this.endOfFileReached) {
            try {
                // Read Id and Desc
                String idLine = this.readIdLine();
                String[] fields = idLine.split("\\s",2);
                String id = fields[0].substring(1);
                String desc = "";
                if (fields.length > 1) {
                    desc = fields[1];
                }

                // Read Sequence
                StringBuilder sequenceBuilder = this.readSequenceLines();

                // Build Fasta object
                fasta = new Fasta(id, desc.trim(), sequenceBuilder.toString().trim());

            } catch (IOException ex) {
                throw new FileFormatException(ex);
            }
        }

        return fasta;
    }

    @Override
    public int size() throws IOException {
        int size = 0;
        String line;
        while ((line = this.bufferedReader.readLine()) != null) {
            if (line.startsWith(FastaReader.SEQ_ID_CHAR)) {
                size++;
            }
        }
        return size;
    }


    @Override
    public void close() throws IOException {
        this.bufferedReader.close();

    }

    @Override
    public Fasta read(String regexFilter) throws FileFormatException {
        Fasta seq = this.read();
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

    @Override
    public List<Fasta> readAll() throws FileFormatException {
        List<Fasta> fastaList = new ArrayList<Fasta>();

        Fasta fasta;
        while ((fasta = this.read()) != null) {
            fastaList.add(fasta);
        }

        return fastaList;
    }

    @Override
    public List<Fasta> readAll(String regexFilter) throws FileFormatException {
        List<Fasta> fastaList = new ArrayList<Fasta>();

        Fasta fasta;
        while ((fasta = this.read(regexFilter)) != null) {
            fastaList.add(fasta);
        }

        return fastaList;
    }

    private String readIdLine() throws FileFormatException, IOException {
        String idLine;
        // If no previous sequences have been read, read the first(s) line(s)
        if (this.lastLineRead == null) {
            // TODO: Comprobar si hay lineas de basura antes de la primera secuencia,
            //		 en lugar de lanzar una excepcion directamente
            idLine = this.bufferedReader.readLine();
            if (!idLine.startsWith(FastaReader.SEQ_ID_CHAR)) {
                throw new FileFormatException("Incorrect ID Line: " + idLine);
            }
        } else {
            idLine = this.lastLineRead;
        }
        return idLine;
    }

    private StringBuilder readSequenceLines() throws FileFormatException, IOException {
        // read the sequence chars
        StringBuilder sequenceBuilder = new StringBuilder();
        String line = this.bufferedReader.readLine();
        while (line != null && !line.startsWith(FastaReader.SEQ_ID_CHAR)) {
            // check the sequence format and throws a FileFormatException if it's wrong
            checkSequence(line);
            sequenceBuilder.append(line);
            line = this.bufferedReader.readLine();
        }

        // Check if we have reached a new sequence or the end of path
        if (line != null) {
            this.lastLineRead = line;
        } else {
            this.endOfFileReached = true;
        }

        return sequenceBuilder;
    }

    private void checkSequence(String sequence) throws FileFormatException {
        // Por ahora no hacemos comprobacion alguna y nos creemos que la secuencia viene bien
    }

    @Override
    public List<Fasta> read(int size) throws FileFormatException {
        // TODO Auto-generated method stub
        return null;
    }


}
