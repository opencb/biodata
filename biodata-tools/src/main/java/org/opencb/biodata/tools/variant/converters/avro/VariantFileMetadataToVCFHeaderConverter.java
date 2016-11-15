package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.tribble.FeatureCodecHeader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.models.variant.avro.VariantFileMetadata;
import org.opencb.biodata.tools.variant.VariantFileUtils;
import org.opencb.biodata.tools.variant.converters.Converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created on 14/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantFileMetadataToVCFHeaderConverter implements Converter<VariantFileMetadata, VCFHeader> {

    @Override
    public VCFHeader convert(VariantFileMetadata variantFileMetadata) {
        if (variantFileMetadata.getMetadata().containsKey(VariantFileUtils.VARIANT_FILE_HEADER)) {
            String variantFileHeader = variantFileMetadata.getMetadata().get(VariantFileUtils.VARIANT_FILE_HEADER).toString();
            try {
                return parseVcfHeader(variantFileHeader);
            } catch (IOException e) {
                throw new IllegalArgumentException(e);
            }
        } else {
            return null;
        }
    }

    public static VCFHeader parseVcfHeader(String variantFileHeader) throws IOException {
        VCFCodec vcfCodec = new VCFCodec();
        LineIterator source = vcfCodec.makeSourceFromStream(new ByteArrayInputStream(variantFileHeader.getBytes()));
        FeatureCodecHeader featureCodecHeader = vcfCodec.readHeader(source);
        return (VCFHeader) featureCodecHeader.getHeaderValue();
    }
}
