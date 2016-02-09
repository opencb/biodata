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

package org.opencb.biodata.formats.alignment.sam.io;

import java.nio.file.Path;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 12/3/13
 * Time: 7:29 PM
 * To change this template use File | Settings | File Templates.
 */
@Deprecated
public class AlignmentBamDataReader extends AlignmentSamDataReader {
    public AlignmentBamDataReader(Path input, String studyName) {
        super(input, studyName);
    }

    public AlignmentBamDataReader(Path bamPath, String studyName, boolean enableFileSource) {
        super(bamPath, studyName, enableFileSource);
    }
}
