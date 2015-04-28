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
import org.opencb.biodata.formats.sequence.qseq.Qual;

public class QualReader extends AbstractFormatReader<Qual> {

//    private TextFileReader fileReader;
    private BufferedReader bufferedReader;

    private String lastLineRead = null;

    private boolean endOfFileReached = false;

    public QualReader(String fileName) throws IOException {
        this(Paths.get(fileName));
    }

    public QualReader(Path path) throws IOException {
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
    public Qual read() throws FileFormatException {
        Qual qual = null;

        if (!this.endOfFileReached) {
            try {
                // Read Id and Desc
                String idLine = this.readIdLine();
                String id = idLine.split("\\s")[0].substring(1);
                String desc = idLine.substring(id.length() + 1);

                // Read Sequence
                int[] qualities = this.readQualityLines();

                // Build Fasta object
                qual = new Qual(id, desc.trim(), qualities);

            } catch (IOException ex) {
                throw new FileFormatException(ex);
            }
        }

        return qual;
    }

    @Override
    public Qual read(String regexFilter) throws FileFormatException {
        Qual qual = this.read();
        boolean found = false;
        while (!found && qual != null) {
            if (qual.getId().matches(regexFilter)) {
                found = true;
            } else {
                qual = this.read();
            }
        }
        return qual;
    }

    @Override
    public List<Qual> readAll() throws FileFormatException {
        List<Qual> qualList = new ArrayList<Qual>();

        Qual qual;
        while ((qual = this.read()) != null) {
            qualList.add(qual);
        }

        return qualList;
    }

    @Override
    public List<Qual> readAll(String regexFilter) throws FileFormatException {
        List<Qual> qualList = new ArrayList<Qual>();

        Qual qual;
        while ((qual = this.read(regexFilter)) != null) {
            qualList.add(qual);
        }

        return qualList;
    }

    @Override
    public int size() throws IOException, FileFormatException {
        int size = 0;
        String line;
        while ((line = this.bufferedReader.readLine()) != null) {
            if (line.startsWith(Qual.SEQ_ID_CHAR)) {
                size++;
            }
        }
        return size;
    }

    private String readIdLine() throws FileFormatException, IOException {
        String idLine;
        // If no previous sequences have been read, read the first(s) line(s)
        if (this.lastLineRead == null) {
            // TODO: Comprobar si hay lineas de basura antes de la primera secuencia,
            //		 en lugar de lanzar una excepcion directamente
            idLine = this.bufferedReader.readLine();
            if (!idLine.startsWith(Qual.SEQ_ID_CHAR)) {
                throw new FileFormatException("Incorrect ID Line: " + idLine);
            }
        } else {
            idLine = this.lastLineRead;
        }
        return idLine;
    }

    private int[] readQualityLines() throws FileFormatException, IOException {
        int[] qualities;
        StringBuilder qualStringBuilder = new StringBuilder();
        String line = this.bufferedReader.readLine();
        while (line != null && !line.startsWith(Qual.SEQ_ID_CHAR)) {
            qualStringBuilder.append(line);
            line = this.bufferedReader.readLine();
        }

        // convert the stringBuilder into a int array
        String[] stringQualities = qualStringBuilder.toString().replaceAll("255", "0").split("\\s");
        qualities = new int[stringQualities.length];
        for(int i=0; i<stringQualities.length; i++) {
            qualities[i] = Integer.parseInt(stringQualities[i]);
        }
//        qualities = ArrayUtils.toIntArray(qualStringBuilder.toString().replaceAll("255", "0").split("\\s"), 0);

        // Check if we have reached a new sequence or the end of path
        if (line != null) {
            this.lastLineRead = line;
        } else {
            this.endOfFileReached = true;
        }

        return qualities;
    }

    @Override
    public List<Qual> read(int size) throws FileFormatException {
        // TODO Auto-generated method stub
        return null;
    }

}
