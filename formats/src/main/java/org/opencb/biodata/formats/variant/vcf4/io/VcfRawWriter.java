package org.opencb.biodata.formats.variant.vcf4.io;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import org.opencb.biodata.formats.variant.vcf4.VcfRecord;
import org.opencb.commons.io.DataWriter;

/**
 * 
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VcfRawWriter implements DataWriter<VcfRecord> {

    private VcfRawReader reader;
    private PrintWriter printer;
    private String filename;


    public VcfRawWriter(VcfRawReader reader, String filename) {
        this.reader = reader;
        this.filename = filename;
    }

    @Override
    public boolean open() {
        boolean res = true;
        try {
            printer = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            res = false;
        }

        return res;
    }

    @Override
    public boolean close() {
        printer.close();
        return true;
    }

    @Override
    public boolean pre() {
        printer.append(reader.getHeader());
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(VcfRecord elem) {
        return write(Arrays.asList(elem));
    }

    @Override
    public boolean write(List<VcfRecord> batch) {
        for (VcfRecord record : batch) {
            printer.append(record.toString()).append("\n");
        }
        return true;
    }
    
}
