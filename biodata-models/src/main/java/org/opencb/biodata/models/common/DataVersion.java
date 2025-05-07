/*
 * Copyright 2015-2020 OpenCB
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

package org.opencb.biodata.models.common;

import org.opencb.commons.datastore.core.ObjectMap;

import java.util.ArrayList;
import java.util.List;

public class DataVersion {

    private String data;
    private String name;
    private String version;
    private String date;
    private String species;
    private String assembly;
    private List<String> files;
    private List<String> urls;
    private ObjectMap attributes;

    public DataVersion() {
        files = new ArrayList<>();
        urls = new ArrayList<>();
        attributes = new ObjectMap();
    }

    public DataVersion(String data, String name, String version, String date, String species, String assembly, List<String> files,
                       List<String> urls, ObjectMap attributes) {
        this.data = data;
        this.name = name;
        this.version = version;
        this.date = date;
        this.species = species;
        this.assembly = assembly;
        this.files = files;
        this.urls = urls;
        this.attributes = attributes;
    }

    public String getData() {
        return data;
    }

    public DataVersion setData(String data) {
        this.data = data;
        return this;
    }

    public String getName() {
        return name;
    }

    public DataVersion setName(String name) {
        this.name = name;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public DataVersion setVersion(String version) {
        this.version = version;
        return this;
    }

    public String getDate() {
        return date;
    }

    public DataVersion setDate(String date) {
        this.date = date;
        return this;
    }

    public String getSpecies() {
        return species;
    }

    public DataVersion setSpecies(String species) {
        this.species = species;
        return this;
    }

    public String getAssembly() {
        return assembly;
    }

    public DataVersion setAssembly(String assembly) {
        this.assembly = assembly;
        return this;
    }

    public List<String> getFiles() {
        return files;
    }

    public DataVersion setFiles(List<String> files) {
        this.files = files;
        return this;
    }

    public List<String> getUrls() {
        return urls;
    }

    public DataVersion setUrls(List<String> urls) {
        this.urls = urls;
        return this;
    }

    public ObjectMap getAttributes() {
        return attributes;
    }

    public DataVersion setAttributes(ObjectMap attributes) {
        this.attributes = attributes;
        return this;
    }
}
