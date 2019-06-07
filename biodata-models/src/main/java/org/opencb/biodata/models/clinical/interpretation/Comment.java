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

package org.opencb.biodata.models.clinical.interpretation;

public class Comment {

    private String author;
    private String type;
    @Deprecated
    private String text;
    private String message;
    private String date;

    public Comment() {
    }

    public Comment(String author, String type, String message, String date) {
        this.author = author;
        this.type = type;
        this.message = message;
        this.text = message;
        this.date = date;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Comment{");
        sb.append("author='").append(author).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", message='").append(message).append('\'');
        sb.append(", date='").append(date).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getAuthor() {
        return author;
    }

    public Comment setAuthor(String author) {
        this.author = author;
        return this;
    }

    public String getType() {
        return type;
    }

    public Comment setType(String type) {
        this.type = type;
        return this;
    }

    @Deprecated
    public String getText() {
        return text;
    }

    @Deprecated
    public Comment setText(String text) {
        this.text = text;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public Comment setMessage(String message) {
        this.message = message;
        return this;
    }

    public String getDate() {
        return date;
    }

    public Comment setDate(String date) {
        this.date = date;
        return this;
    }

}
