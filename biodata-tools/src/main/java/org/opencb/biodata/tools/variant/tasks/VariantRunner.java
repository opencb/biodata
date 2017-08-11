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

package org.opencb.biodata.tools.variant.tasks;

import java.util.List;
import org.opencb.biodata.formats.pedigree.io.PedigreeReader;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.io.VariantWriter;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.tools.variant.VariantFileUtils;
import org.opencb.commons.run.Runner;
import org.opencb.commons.run.Task;
/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@Deprecated
public class VariantRunner extends Runner<Variant> {

    protected VariantFileMetadata source;

    public VariantRunner(VariantFileMetadata study, VariantReader reader, PedigreeReader pedReader,
                         List<VariantWriter> writer, List<Task<Variant>> tasks) {
        super(reader, writer, tasks);
        this.source = study;
        parsePhenotypes(pedReader);
    }

    public VariantRunner(VariantFileMetadata study, VariantReader reader, PedigreeReader pedReader,
                         List<VariantWriter> writer, List<Task<Variant>> tasks, int batchSize) {
        super(reader, writer, tasks, batchSize);
        this.source = study;
        parsePhenotypes(pedReader);
    }

    private void parsePhenotypes(PedigreeReader pedReader) {
//        if (pedReader != null) {
//            pedReader.open();
//            source.setPedigree(pedReader.read().get(0));
//            pedReader.close();
//        }
    }

    public VariantFileMetadata getStudy() {
        return source;
    }

    public void setStudy(VariantFileMetadata study) {
        this.source = study;
    }

//    @Override
//    protected void readerInit() {
//        super.readerInit();
//        source.addMetadata(VariantFileUtils.VARIANT_FILE_HEADER, ((VariantReader) reader).getHeader());
//    }

}
