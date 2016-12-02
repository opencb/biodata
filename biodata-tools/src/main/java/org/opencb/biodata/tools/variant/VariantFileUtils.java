package org.opencb.biodata.tools.variant;

import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.io.VariantVcfReader;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToAvroVcfHeaderConverter;
import org.opencb.biodata.tools.variant.converters.avro.VariantFileMetadataToVCFHeaderConverter;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created on 08/07/16.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantFileUtils {

    public static final String VARIANT_FILE_HEADER = "variantFileHeader";

    /**
     * Reads the VariantSource from a Vcf file given a file Path
     *
     * @param path    Path to the Vcf file
     * @param source  Optional source to fill up
     * @return        The read variant source
     * @throws IOException if an I/O error occurs
     */
    public static VariantSource readVariantSource(Path path, VariantSource source) throws IOException {
        Objects.requireNonNull(path);
        return readVariantSource(new VariantVcfReader(source, path.toString()), source);
    }

    /**
     * Reads the VariantSource from a Variant file given an initialized VariantReader
     *
     * @param reader    Initialized variant reader
     * @param source    Optional source to fill up
     * @return          The read variant source
     * @throws IOException if an I/O error occurs
     */
    public static VariantSource readVariantSource(VariantReader reader, VariantSource source) throws IOException {
        Objects.requireNonNull(reader);
        if (source == null) {
            source = new VariantSource("", "", "", "");
        }

        try {
            reader.open();
            reader.pre();

            String variantFileHeader = reader.getHeader();
            source.addMetadata(VARIANT_FILE_HEADER, variantFileHeader);
            if (source.getHeader() == null) {
                VCFHeader header = VariantFileMetadataToVCFHeaderConverter.parseVcfHeader(variantFileHeader);
                source.setHeader(new VCFHeaderToAvroVcfHeaderConverter().convert(header));
            }

            reader.post();
        } finally {
            reader.close();
        }
        return source;
    }

}
