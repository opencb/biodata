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

package org.opencb.biodata.formats.feature.gtf.io;

import org.opencb.biodata.formats.feature.gtf.Gtf;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class GtfReader {

    private BufferedReader bufferedReader;
    private File file;

    GtfReader(Reader reader) throws IOException, SecurityException, NoSuchMethodException {
        this.bufferedReader = new BufferedReader(reader);
    }

    public GtfReader(Path path) throws IOException, SecurityException, NoSuchMethodException {
        Files.exists(path);
        this.file = file;
        bufferedReader = FileUtils.newBufferedReader(path);
//        bufferedReader = new BufferedReader(new FileReader(file));
    }

    public Gtf read() throws FileFormatException {
        final Pattern kvpattern = Pattern.compile("(\\S*)\\s\"(.*)\"");
        try {
            String line = "";
            while ((line = bufferedReader.readLine()) != null && (line.trim().equals("") || line.startsWith("#"))) {
                ;
            }
            if (line != null) {
                String[] fields = line.split("\t");
                Map<String, String> attributes = new HashMap<String, String>();
                String[] attrFields = fields[8].split(";");
                String k, v;
                for (int i = 0; i < attrFields.length; i++) {
                    final Matcher matcher = kvpattern.matcher(attrFields[i].trim());
                    if (matcher.matches()) {
                        k = matcher.group(1);
                        v = matcher.group(2);
                        if(attributes.containsKey(k)) {
                            attributes.put(k, attributes.get(k)+","+v);
                        } else {
                            attributes.put(k, v);
                        }
                    } else {
                        System.err.println("GTF attribute '" + attrFields[i] + "' size must be 2. At line:\n\t"+line);
                    }
                }
                return new Gtf(fields[0], fields[1], fields[2], Integer.parseInt(fields[3]), Integer.parseInt(fields[4]), fields[5], fields[6], fields[7], attributes);
            } else {
                return null;
            }
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    public List<Gtf> read(int numberLines) throws FileFormatException {
        List<Gtf> records = new ArrayList<Gtf>(numberLines);
        try {
            int cont = 0;
            Gtf gtf;
            while ((gtf = read()) != null && cont < numberLines) {
                records.add(gtf);
                cont++;
            }
            return records;
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    public List<Gtf> readAll() throws FileFormatException {
        List<Gtf> records = new ArrayList<Gtf>();
        try {
            Gtf gtf;
            while ((gtf = read()) != null) {
                records.add(gtf);
            }
            return records;
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    public int size() throws IOException, FileFormatException {
        // TODO
//        return IOUtils.countLines(file);
        return -1;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }

}
