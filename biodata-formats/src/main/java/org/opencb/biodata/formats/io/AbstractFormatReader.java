package org.opencb.biodata.formats.io;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class AbstractFormatReader<T> implements AutoCloseable {

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
