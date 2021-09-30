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

package org.opencb.biodata.tools.variant.stats.writer;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.tools.variant.converters.avro.VariantStatsToTsvConverter;
import org.opencb.commons.io.DataWriter;

import java.io.*;
import java.util.List;

/**
 * Exports the given variant stats into a TSV format.
 *
 * Created on 01/06/16.
 *
 * @see VariantStatsToTsvConverter
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantStatsTsvExporter implements DataWriter<Variant> {

    private Writer dataOutputStream;
    private final boolean closeStream;
    private int writtenVariants;
    private VariantStatsToTsvConverter converter;

    /**
     * Constructor.
     *
     * @param os        OutputStream. Won't be closed at the end.
     * @param study     Study to export.
     * @param cohorts   List of cohorts to export.
     */
    public VariantStatsTsvExporter(OutputStream os, String study, List<String> cohorts) {
        this(new OutputStreamWriter(os), study, cohorts);
    }

    /**
     * Constructor.
     *
     * @param writer      Writer. Won't be closed at the end
     * @param study       Study to export.
     * @param cohorts     List of cohorts to export.
     */
    public VariantStatsTsvExporter(Writer writer, String study, List<String> cohorts) {
        this.dataOutputStream = writer;
        this.closeStream = false;
        converter = new VariantStatsToTsvConverter(study, cohorts);
    }

    @Override
    public boolean close() {
        if (closeStream) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return true;
    }

    @Override
    public boolean pre() {
        try {
            dataOutputStream.write(converter.createHeader());
            dataOutputStream.write("\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        writtenVariants = 0;
        return true;
    }

    @Override
    public boolean post() {
        try {
            dataOutputStream.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public boolean write(List<Variant> batch) {
        for (Variant variant : batch) {
            write(variant);
        }
        return true;
    }

    /**
     * Exports a variant.
     *
     * @param variant Variant to print
     * @return        True by default.
     */
    @Override
    public boolean write(Variant variant) {
        String str = converter.convert(variant);
        if (str == null) {
            return true;
        }
        try {
            dataOutputStream.write(str);
            dataOutputStream.write("\n");
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        writtenVariants++;
        return true;
    }

    public int getWrittenVariants() {
        return writtenVariants;
    }
}
