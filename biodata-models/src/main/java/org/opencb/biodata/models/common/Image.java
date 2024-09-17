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

package org.opencb.biodata.models.common;

public class Image {

    private String name;
    private String base64;
    private String description;

    public Image() {
        this("", "", "");
    }

    public Image(String name, String base64, String description) {
        this.name = name;
        this.base64 = base64;
        this.description = description;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Image{");
        sb.append("name='").append(name).append('\'');
        sb.append(", base64='").append(base64).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getName() {
        return name;
    }

    public Image setName(String name) {
        this.name = name;
        return this;
    }

    public String getBase64() {
        return base64;
    }

    public Image setBase64(String base64) {
        this.base64 = base64;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public Image setDescription(String description) {
        this.description = description;
        return this;
    }
}
