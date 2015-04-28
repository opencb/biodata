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

package org.opencb.biodata.formats.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractFormatReader<T> {

    protected Path path;
    protected Logger logger;

    protected AbstractFormatReader() {
        path = null;
        logger = LoggerFactory.getLogger(AbstractFormatReader.class);
//        logger.setLevel(Logger.DEBUG_LEVEL);
    }

    protected AbstractFormatReader(Path f) throws IOException {
        Files.exists(f);
        this.path = f;
        logger = LoggerFactory.getLogger(AbstractFormatReader.class);
//        logger.setLevel(Logger.DEBUG_LEVEL);
    }

    public abstract int size() throws IOException, FileFormatException;

    public abstract T read() throws FileFormatException;

    public abstract T read(String regexFilter) throws FileFormatException;

    public abstract List<T> read(int size) throws FileFormatException;

    public abstract List<T> readAll() throws FileFormatException, IOException;

    public abstract List<T> readAll(String pattern) throws FileFormatException;

    public abstract void close() throws IOException;

}
