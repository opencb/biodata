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

import org.opencb.biodata.models.sequence.Read;

/**
 * Created by jtarraga on 22/05/15.
 */
public class SequenceKmersCalculator {

    public SequenceKmers compute(final Read read, final int kvalue) {
        return compute(read.getSequence().toString(), kvalue);
    }

    public SequenceKmers compute(final String sequence, final int k) {
        SequenceKmers kmers = new SequenceKmers(k);

        final int len = sequence.length();
        final int stop = len - k;

        String kmer;
        for (int i = 0; i < stop; i++) {
            kmer = sequence.substring(i, i + k);
            if (!kmer.contains("N") && !kmer.contains("n")) {
                kmers.kmersMap.put(kmer, kmers.kmersMap.containsKey(kmer) ? kmers.kmersMap.get(kmer) + 1 : 1);
            }
        } // end for

        return kmers;
    }

    public void update(SequenceKmers src, SequenceKmers dest) {
        int value;
        for(String key:src.kmersMap.keySet()) {
            value = src.kmersMap.get(key);
            if (dest.kmersMap.containsKey(key)) {
                value += dest.kmersMap.get(key);
            }
            dest.kmersMap.put(key, value);
        }
    }
}
