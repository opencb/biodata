package org.opencb.biodata.formats.feature.gff.io;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.opencb.biodata.formats.feature.gff.Gff;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.BeanReader;
import org.opencb.biodata.formats.io.FileFormatException;

public class GffReader extends AbstractFormatReader<Gff> {

    private BeanReader<Gff> beanReader;

    public GffReader(String filename) throws IOException, SecurityException, NoSuchMethodException {
        this(Paths.get(filename));
    }

    public GffReader(Path path) throws IOException, SecurityException, NoSuchMethodException {
        super(path);
        beanReader = new BeanReader<>(path, Gff.class);
    }

    @Override
    public Gff read() throws FileFormatException {
        try {
            return beanReader.read();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public Gff read(String pattern) throws FileFormatException {
        try {
            return beanReader.read(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff> read(int numberLines) throws FileFormatException {
        try {
            return beanReader.read(numberLines);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff> readAll() throws FileFormatException {
        try {
            return beanReader.readAll();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<Gff> readAll(String pattern) throws FileFormatException {
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
