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
