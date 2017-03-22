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

package org.opencb.biodata.tools.alignment.stats;

/**
 * Created by jtarraga on 22/05/15.
 */
public class SequenceInfo {
    public int numA;
    public int numT;
    public int numG;
    public int numC;
    public int numN;
    public int numQual;
    public int accQual;

    public SequenceInfo() {
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("SequenceInfo{");
        sb.append("numA=").append(numA);
        sb.append(", numT=").append(numT);
        sb.append(", numG=").append(numG);
        sb.append(", numC=").append(numC);
        sb.append(", numN=").append(numN);
        sb.append(", numQual=").append(numQual);
        sb.append(", accQual=").append(accQual);
        sb.append('}');
        return sb.toString();
    }
}
