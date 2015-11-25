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

package org.opencb.biodata.formats.sequence.qseq;

import java.util.Arrays;


public class Qual {

    /**
     * Qual ID
     */
    private String id;

    /**
     * Qual Description
     */
    private String description;

    /**
     * Qualities vector
     */
    private int[] qualityArray;

    public static final String SEQ_ID_CHAR = ">";

    public Qual(String id, String description, int[] qual) {
        super();
        this.id = id;
        this.description = description;
        this.qualityArray = qual;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int[] getQualityArray() {
        return qualityArray;
    }

    public void setQualityArray(int[] qualityArray) {
        this.qualityArray = qualityArray;
    }

    public int size() {
        return this.qualityArray.length;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(Qual.SEQ_ID_CHAR + this.id);
        if (this.description != null && !this.description.equals("")) {
            sb.append(" " + this.description);
        }

        sb.append("\n" + Arrays.toString(this.qualityArray));
        return (sb.toString());
    }

}
