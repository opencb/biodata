/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant.converters.avro;

import htsjdk.tribble.FeatureCodecHeader;
import htsjdk.tribble.readers.LineIterator;
import htsjdk.variant.vcf.VCFCodec;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.models.variant.avro.legacy.VariantSource;
import org.opencb.biodata.tools.variant.metadata.VariantMetadataUtils;
import org.opencb.biodata.tools.Converter;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * Created on 14/10/15
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
@Deprecated
public class VariantSourceToVCFHeaderConverter implements Converter<VariantSource, VCFHeader> {

    @Override
    public VCFHeader convert(VariantSource variantFileMetadata) {
        if (variantFileMetadata.getMetadata().containsKey(VariantMetadataUtils.VARIANT_FILE_HEADER)) {
            String variantFileHeader = variantFileMetadata.getMetadata().get(VariantMetadataUtils.VARIANT_FILE_HEADER).toString();
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
