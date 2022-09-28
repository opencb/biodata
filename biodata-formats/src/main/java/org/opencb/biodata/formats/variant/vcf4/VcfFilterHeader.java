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

package org.opencb.biodata.formats.variant.vcf4;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Deprecated
public class VcfFilterHeader {
    private String id;
    private String description;

    /**
     * @param filterLine string in format: ##FILTER=<ID=q10,Description="Variants that not pass quality 10">
     */
    public VcfFilterHeader(String filterLine) {
        String pattern = "##FILTER=<ID=(.+),.*Description=\"(.*)\".*";
        Matcher m = Pattern.compile(pattern).matcher(filterLine);
        if (m.find()) {
            this.id = m.group(1);
            this.description = m.group(2);
        } else {
            // TODO: throw an exception, but as it would be a major API change it's postponed to later major release
            this.id = "";
            this.description = "";
        }
    }

    public VcfFilterHeader(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return "##FILTER=<ID=" + id + ",Description=\"" + description + "\">";
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
