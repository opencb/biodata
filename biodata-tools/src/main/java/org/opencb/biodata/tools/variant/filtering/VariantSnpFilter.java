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

package org.opencb.biodata.tools.variant.filtering;

import org.opencb.biodata.models.variant.Variant;


/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantSnpFilter extends VariantFilter {


    public VariantSnpFilter(int priority) {
        super(priority);
    }

    public VariantSnpFilter() {
        super();
    }

    @Override
    public boolean apply(Variant variant) {
        return (!variant.getId().equalsIgnoreCase(".") && !variant.getId().equalsIgnoreCase(""));
    }

}
