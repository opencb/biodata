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

package org.opencb.biodata.tools.variant.tasks;

import org.opencb.commons.run.Task;

import java.io.IOException;
import java.util.List;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.tools.variant.annotation.VariantAnnotator;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
@Deprecated
public class VariantAnnotTask extends Task<Variant> {

    private List<VariantAnnotator> annotations;

    public VariantAnnotTask(List<VariantAnnotator> annots) {
        super();
        this.annotations = annots;
    }

    public VariantAnnotTask(List<VariantAnnotator> annots, int priority) {
        super(priority);
        this.annotations = annots;
    }

    @Override
    public boolean apply(List<Variant> batch) throws IOException {

        for (VariantAnnotator annotation : annotations) {
            annotation.annot(batch);
        }

        return true;
    }
}
