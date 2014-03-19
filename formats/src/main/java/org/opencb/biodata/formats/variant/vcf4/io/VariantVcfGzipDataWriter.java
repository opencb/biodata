package org.opencb.biodata.formats.variant.vcf4.io;


import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.opencb.biodata.models.variant.Variant;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 9/15/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantVcfGzipDataWriter implements VariantWriter {

    private VariantReader reader;
    private BufferedWriter printer;
    private String filename;


    public VariantVcfGzipDataWriter(VariantReader reader, String filename) {
        this.filename = filename;
        this.reader = reader;
    }

    @Override
    public boolean open() {

        boolean res = true;
        try {
            printer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(this.filename))));
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }

        return res;
    }

    @Override
    public boolean close() {

        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean pre() {

        boolean res = true;
        try {
            printer.append(reader.getHeader()).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Variant elem) {
        boolean res = true;

        try {
            printer.append(elem.toString()).append("\n");
        } catch (IOException e) {
            e.printStackTrace();
            res = false;

        }
        return res;
    }

    @Override
    public boolean write(List<Variant> batch) {

        boolean res = true;
        try {
            for (Variant record : batch) {
                printer.append(record.toString()).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
            res = false;
        }
        return res;
    }

    @Override
    public void includeStats(boolean stats) {
    }

    @Override
    public void includeSamples(boolean samples) {
    }

    @Override
    public void includeEffect(boolean effect) {
    }
}