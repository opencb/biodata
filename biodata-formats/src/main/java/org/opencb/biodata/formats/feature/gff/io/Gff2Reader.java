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

package org.opencb.biodata.formats.feature.gff.io;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.opencb.biodata.formats.feature.gff.Gff2;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.BeanReader;
import org.opencb.biodata.formats.io.FileFormatException;

public class Gff2Reader extends AbstractFormatReader<Gff2> {

    private BeanReader<Gff2> beanReader;

    public Gff2Reader(String filename) throws IOException, SecurityException, NoSuchMethodException {
        this(Paths.get(filename));
    }

    public Gff2Reader(Path path) throws IOException, SecurityException, NoSuchMethodException {
        super(path);
        beanReader = new BeanReader<Gff2>(path, Gff2.class);
    }

    @Override
    public Gff2 read() throws FileFormatException {
        try {
            return beanReader.read();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public Gff2 read(String pattern) throws FileFormatException {
        try {
            return beanReader.read(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff2> read(int numberLines) throws FileFormatException {
        try {
            return beanReader.read(numberLines);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff2> readAll() throws FileFormatException {
        try {
            return beanReader.readAll();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff2> readAll(String pattern) throws FileFormatException {
        try {
            return beanReader.readAll(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public int size() throws IOException, FileFormatException {
        // TODO
//        return IOUtils.countLines(path);
        return -1;
    }

    @Override
    public void close() throws IOException {
        beanReader.close();
    }
}
