package org.opencb.biodata.formats.variant.vcf4.io;

import java.util.List;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.commons.io.DataReader;

/**
 * Created with IntelliJ IDEA.
 * User: aaleman
 * Date: 8/30/13
 * Time: 12:24 PM
 * To change this template use File | Settings | File Templates.
 */
public interface VariantReader extends DataReader<Variant> {
    public List<String> getSampleNames();

    public String getHeader();
}