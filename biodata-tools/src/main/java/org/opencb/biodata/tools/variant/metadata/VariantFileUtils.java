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

package org.opencb.biodata.tools.variant.metadata;

import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.io.VariantVcfReader;
import org.opencb.biodata.models.variant.VariantFileMetadata;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Created on 08/07/16.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantFileUtils {

    /**
     * @deprecated We are not storing the header in plain text anymore
     */
    @Deprecated
    public static final String VARIANT_FILE_HEADER = "variantFileHeader";

    /**
     * Reads the VariantSource from a Vcf file given a file Path
     *
     * @param path    Path to the Vcf file
     * @param fileMetadata  Optional fileMetadata to fill up
     * @return        The read variant fileMetadata
     * @throws IOException if an I/O error occurs
     */
    public static VariantFileMetadata readVariantFileMetadata(Path path, VariantFileMetadata fileMetadata) throws IOException {
        Objects.requireNonNull(path);
        return readVariantFileMetadata(new VariantVcfReader(fileMetadata.toVariantDatasetMetadata(""), path.toString()), fileMetadata);
    }

    /**
     * Reads the VariantSource from a Variant file given an initialized VariantReader
     *
     * @param reader    Initialized variant reader
     * @param metadata    Optional metadata to fill up
     * @return          The read variant metadata
     * @throws IOException if an I/O error occurs
     */
    public static VariantFileMetadata readVariantFileMetadata(VariantReader reader, VariantFileMetadata metadata) throws IOException {
        Objects.requireNonNull(reader);
        if (metadata == null) {
            metadata = new VariantFileMetadata("", "");
        }

        try {
            reader.open();
            reader.pre();

            metadata.setHeader(reader.getVariantFileMetadata().getHeader());
            metadata.setSampleIds(reader.getVariantFileMetadata().getSampleIds());
            metadata.setStats(reader.getVariantFileMetadata().getStats());

            reader.post();
        } finally {
            reader.close();
        }
        return metadata;
    }

}
