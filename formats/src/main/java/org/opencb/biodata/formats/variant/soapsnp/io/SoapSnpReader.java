package org.opencb.biodata.formats.variant.soapsnp.io;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.opencb.biodata.formats.io.AbstractFormatReader;
import org.opencb.biodata.formats.io.BeanReader;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.variant.soapsnp.SoapSnp;

public class SoapSnpReader extends AbstractFormatReader<SoapSnp> {

    private BeanReader<SoapSnp> beanReader;

    public SoapSnpReader(String filename) throws IOException, SecurityException, NoSuchMethodException {
        this(Paths.get(filename));
    }

    public SoapSnpReader(Path path) throws IOException, SecurityException, NoSuchMethodException {
        super(path);
        beanReader = new BeanReader<SoapSnp>(path, SoapSnp.class);
    }

    @Override
    public SoapSnp read() throws FileFormatException {
        try {
            return beanReader.read();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public SoapSnp read(String pattern) throws FileFormatException {
        try {
            return beanReader.read(pattern);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<SoapSnp> read(int numberLines) throws FileFormatException {
        try {
            return beanReader.read(numberLines);
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<SoapSnp> readAll() throws FileFormatException {
        try {
            return beanReader.readAll();
        } catch (Exception e) {
            throw new FileFormatException(e);
        }
    }

    @Override
    public List<SoapSnp> readAll(String pattern) throws FileFormatException {
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
