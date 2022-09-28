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

@Deprecated
public class VcfAlternateHeader {
    private String id;
    private String description;

    public VcfAlternateHeader(String filterLine) {
        // ##ALT=<ID=q10,Description="Variants that not pass quality 10">
        String[] fields = filterLine.replaceAll("[\"<>]", "").split("=");
        // fields[2] ==> q10,Description
        this.id = fields[2].split(",")[0];
        // fields[3] ==> "Variants that not pass quality 10"
        this.description = fields[3];
    }

    public VcfAlternateHeader(String id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return "##ALT=<ID=" + id + ",Description=\"" + description + "\">";
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
