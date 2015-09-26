/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.sequence.fasta.dbadaptor;

import org.opencb.biodata.models.feature.Region;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Created by jacobo on 14/08/14.
 */
public abstract class SequenceDBAdaptor {

    protected Path credentialsPath;

    public SequenceDBAdaptor(){
    }

    public SequenceDBAdaptor(Path credentials){
        this.credentialsPath = credentials;
    }

    abstract public void open()  throws IOException;
    abstract public void close()  throws IOException;

    abstract public String getSequence(Region region) throws IOException;
    abstract public String getSequence(Region region, String species) throws IOException;

}
