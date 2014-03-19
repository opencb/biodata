package org.opencb.biodata.formats.variant.vcf4.io;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import org.opencb.biodata.models.variant.Variant;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 9/15/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantVcfDataWriter implements VariantWriter {

    private PrintWriter printer;
    private String filename;
    private VariantReader reader;

    public VariantVcfDataWriter(VariantReader reader, String filename) {
        this.filename = filename;
        this.reader = reader;
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

        printer.append(reader.getHeader()).append("\n");
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Variant elem) {
        printer.append(elem.toString()).append("\n"); // TODO aaleman: Create a Variant2Vcf converter.
        return true;
    }


    @Override
    public boolean write(List<Variant> batch) {

        for (Variant record : batch) {
            printer.append(record.toString()).append("\n"); // TODO aaleman: Create a Variant2Vcf converter.
        }

        return true;
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
