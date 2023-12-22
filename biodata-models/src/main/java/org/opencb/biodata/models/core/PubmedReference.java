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

package org.opencb.biodata.models.core;

public class PubmedReference {
    private String id;
    private String title;
    private String jounal;
    private String date;
    private String url;

    public PubmedReference() {
    }

    public PubmedReference(String id, String title, String jounal, String date, String url) {
        this.id = id;
        this.title = title;
        this.jounal = jounal;
        this.date = date;
        this.url = url;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("PubmedReference{");
        sb.append("id='").append(id).append('\'');
        sb.append(", title='").append(title).append('\'');
        sb.append(", jounal='").append(jounal).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append(", url='").append(url).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public PubmedReference setId(String id) {
        this.id = id;
        return this;
    }

    public String getTitle() {
        return title;
    }

    public PubmedReference setTitle(String title) {
        this.title = title;
        return this;
    }

    public String getJounal() {
        return jounal;
    }

    public PubmedReference setJounal(String jounal) {
        this.jounal = jounal;
        return this;
    }

    public String getDate() {
        return date;
    }

    public PubmedReference setDate(String date) {
        this.date = date;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public PubmedReference setUrl(String url) {
        this.url = url;
        return this;
    }
}
