package org.opencb.biodata.tools.variant.tasks;

import java.util.List;
import org.opencb.biodata.formats.pedigree.io.PedigreeReader;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.io.VariantWriter;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.commons.run.Runner;
import org.opencb.commons.run.Task;
/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantRunner extends Runner<Variant> {

    protected VariantSource source;

    public VariantRunner(VariantSource study, VariantReader reader, PedigreeReader pedReader, 
            List<VariantWriter> writer, List<Task<Variant>> tasks) {
        super(reader, writer, tasks);
        this.source = study;
        parsePhenotypes(pedReader);
    }

    public VariantRunner(VariantSource study, VariantReader reader, PedigreeReader pedReader, 
            List<VariantWriter> writer, List<Task<Variant>> tasks, int batchSize) {
        super(reader, writer, tasks, batchSize);
        this.source = study;
        parsePhenotypes(pedReader);
    }

    private void parsePhenotypes(PedigreeReader pedReader) {
        if (pedReader != null) {
            pedReader.open();
            source.setPedigree(pedReader.read().get(0));
            pedReader.close();
        }
    }

    public VariantSource getStudy() {
        return source;
    }

    public void setStudy(VariantSource study) {
        this.source = study;
    }

    @Override
    protected void readerInit() {
        super.readerInit();
        source.addMetadata("variantFileHeader", ((VariantReader) reader).getHeader());
    }

}
