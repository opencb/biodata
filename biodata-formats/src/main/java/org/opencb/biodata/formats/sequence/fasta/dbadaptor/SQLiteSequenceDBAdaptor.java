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

package org.opencb.biodata.formats.sequence.fasta.dbadaptor;

import org.opencb.biodata.models.core.Region;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jacobo on 14/08/14.
 */
public class SQLiteSequenceDBAdaptor extends SequenceDBAdaptor {

    private Path input;

    /**
     *
     * @param input Accept formats: *.properties, *.sqlite.db
     */
    public SQLiteSequenceDBAdaptor(Path input) {
        throw new UnsupportedOperationException("Unimplemented");
    }

    @Override
    public void open() throws IOException {
    }

    @Override
    public void close() throws IOException {

    }

    @Override
    public String getSequence(Region region) throws IOException {
        return null;
    }

    @Override
    public String getSequence(Region region, String species) throws IOException {
        return null;
    }

    /**
     * Creates a input.sqlite.db.
     *
     * @param fastaInputFile Accept formats: *.fasta, *.fasta.gz
     */
    public void createDB(Path fastaInputFile){

    }

}
