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

package org.opencb.biodata.formats.feature.bed.io;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.opencb.biodata.formats.feature.bed.Bed;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.BeanReader;
import org.opencb.biodata.formats.io.FileFormatException;

public class BedReader extends AbstractFormatReader<Bed> {

    private BeanReader<Bed> beanReader;

    public BedReader(String filename) throws IOException, SecurityException, NoSuchMethodException {
        this(Paths.get(filename));
    }

    public BedReader(Path file) throws IOException, SecurityException, NoSuchMethodException {
        super(file);
        beanReader = new BeanReader<>(file, Bed.class);
    }

    @Override
    public Bed read() throws FileFormatException {
        try {
            return beanReader.read();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public Bed read(String pattern) throws FileFormatException {
        try {
            return beanReader.read(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Bed> read(int number) throws FileFormatException {
        try {
            return beanReader.read(number);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Bed> readAll() throws FileFormatException {
        try {
            return beanReader.readAll();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Bed> readAll(String pattern) throws FileFormatException {
        try {
            return beanReader.readAll(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public int size() throws IOException {
        // TODO
//        return IOUtils.countLines(path);
        return -1;
    }

    @Override
    public void close() throws IOException {
        beanReader.close();
    }

}
