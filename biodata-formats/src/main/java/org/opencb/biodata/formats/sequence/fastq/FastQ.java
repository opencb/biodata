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

package org.opencb.biodata.formats.sequence.fastq;

import java.util.HashMap;
import java.util.Map;
import org.opencb.biodata.formats.sequence.fasta.Fasta;

public class FastQ extends Fasta {

    /**
     * Sequence quality
     */
    private String quality;

    /**
     * Vector contanining PHRED or Solexa (depending on the encoding) quality scores
     */
    private int[] qualityScoresArray;

    /**
     * Average quality of the sequence
     */
    private double averageQuality;

    /**
     * Minimun sequence quality
     */
    private int minimumQuality;

    /**
     * Maximum sequence quality
     */
    private int maximumQuality;

    /**
     * FastQ Quality Encoding
     */
    private int encoding;

    /**
     * Constant representing SANGER ENCODING
     */
    public static final int SANGER_ENCODING = 0;

    /**
     * Constant representing ILLUMINA ENCODING
     */
    public static final int ILLUMINA_ENCODING = 1;

    /**
     * Constant representing SOLEXA ENCODING
     */
    public static final int SOLEXA_ENCODING = 2;

    /**
     * String array containing Encoding Names
     */
    public static String[] ENCODING_NAMES;

    /**
     * First char in the different quality scales
     */
    private static final char[] SCALE_OFFSET = {33, 64, 64};

    /**
     * Constant representing PHRED score type
     */
    private static final int PHRED_SCORE_TYPE = 0;

    /**
     * Constant representing Solexa score type
     */
    private static final int SOLEXA_SCORE_TYPE = 1;

    /**
     * Score type corresponding to each scale
     */
    private static final int[] SCALE_SCORE = {FastQ.PHRED_SCORE_TYPE, FastQ.PHRED_SCORE_TYPE, FastQ.SOLEXA_SCORE_TYPE};

    /**
     * Sequence ID Line first char
     */
    private static final String SEQ_ID_CHAR = "@";

    /**
     * Quality ID line first char
     */
    private static final String QUALITY_ID_CHAR = "+";

    /**
     * Solexa to Phred Score Map
     */
    private static Map<Integer, Integer> solexaToPhredMap;

    /**
     * Phred to Solexa Score Map
     */
    private static Map<Integer, Integer> phredToSolexaMap;

    static {
        // Encoding Names
        FastQ.ENCODING_NAMES = new String[3];
        FastQ.ENCODING_NAMES[FastQ.SANGER_ENCODING] = "Sanger";
        FastQ.ENCODING_NAMES[FastQ.ILLUMINA_ENCODING] = "Illumina";
        FastQ.ENCODING_NAMES[FastQ.SOLEXA_ENCODING] = "Solexa";

        // Solexa To Phred Score Map Initialization
        solexaToPhredMap = new HashMap<Integer, Integer>();
        solexaToPhredMap.put(-5, 1);
        solexaToPhredMap.put(-4, 1);
        solexaToPhredMap.put(-3, 2);
        solexaToPhredMap.put(-2, 2);
        solexaToPhredMap.put(-1, 3);
        solexaToPhredMap.put(0, 3);
        solexaToPhredMap.put(1, 4);
        solexaToPhredMap.put(2, 4);
        solexaToPhredMap.put(3, 5);
        solexaToPhredMap.put(4, 5);
        solexaToPhredMap.put(5, 6);
        solexaToPhredMap.put(6, 7);
        solexaToPhredMap.put(7, 8);
        solexaToPhredMap.put(8, 9);
        solexaToPhredMap.put(9, 10);

        // PHRED To Solexa Score Map Initialization
        phredToSolexaMap = new HashMap<Integer, Integer>();
        phredToSolexaMap.put(0, -5);
        phredToSolexaMap.put(1, -5);
        phredToSolexaMap.put(2, -2);
        phredToSolexaMap.put(3, 0);
        phredToSolexaMap.put(4, 2);
        phredToSolexaMap.put(5, 3);
        phredToSolexaMap.put(6, 5);
        phredToSolexaMap.put(7, 6);
        phredToSolexaMap.put(8, 7);
        phredToSolexaMap.put(9, 8);
    }

    public FastQ(String id, String description, String sequence, String quality) {
        this(id, description, sequence, quality, FastQ.SANGER_ENCODING);
    }

    public FastQ(String id, String description, String sequence, String quality, int encoding) {
        super(id, description, sequence);
        this.encoding = encoding;
        this.setQuality(quality);
    }

    public FastQ(Fasta fasta, int[] qualArray, int encoding) {
        super(fasta.getId(), fasta.getDescription(), fasta.getSeq());
        this.qualityScoresArray = qualArray;
        this.obtainQualityStringFromQualityScoresArray(encoding);
        this.obtainQualityMarks();
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
        this.obtainQualityScoresFromQualityString();
    }

    public double getAverageQuality() {
        return this.averageQuality;
    }

    public void setAverageQuality(int averageQuality) {
        this.averageQuality = averageQuality;
    }

    public int getMaximumQuality() {
        return this.maximumQuality;
    }

    public void setMaximumQuality(int maximumQuality) {
        this.maximumQuality = maximumQuality;
    }

    public int getMinimumQuality() {
        return this.minimumQuality;
    }

    public void setMinimumQuality(int minimumQuality) {
        this.minimumQuality = minimumQuality;
    }

    public int[] getQualityScoresArray() {
        return this.qualityScoresArray;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(FastQ.SEQ_ID_CHAR).append(this.id);
        if (!this.description.equals("")) {
            sb.append(" ").append(this.description);
        }
        sb.append("\n");

        // Split and append the sequence in lines with a maximum size of SEQ_OUTPUT_MAX_LENGTH
        int n = 0;
        while (this.size() > ((n + 1) * Fasta.SEQ_OUTPUT_MAX_LENGTH)) {
            sb.append(this.sequence.substring(n * Fasta.SEQ_OUTPUT_MAX_LENGTH, (n + 1) * Fasta.SEQ_OUTPUT_MAX_LENGTH)).append("\n");
            n++;
        }
        sb.append(this.sequence.substring(n * Fasta.SEQ_OUTPUT_MAX_LENGTH)).append("\n");

        // Split and append the quality in lines with a maximum size of SEQ_OUTPUT_MAX_LENGTH
        sb.append(FastQ.QUALITY_ID_CHAR).append("\n");
        n = 0;
        while (this.size() > ((n + 1) * Fasta.SEQ_OUTPUT_MAX_LENGTH)) {
            sb.append(this.quality.substring(n * Fasta.SEQ_OUTPUT_MAX_LENGTH, (n + 1) * Fasta.SEQ_OUTPUT_MAX_LENGTH)).append("\n");
            n++;
        }
        sb.append(this.quality.substring(n * Fasta.SEQ_OUTPUT_MAX_LENGTH));

        return (sb.toString());
    }

    public String toFastaString() {
        return super.toString();
    }

    /**
     * this method obtains the minimum, maximum, and average quality values from the quality scores array
     */
    private void obtainQualityMarks() {
        int total = 0;
        for (int i = 0; i < this.qualityScoresArray.length; i++) {
            total += this.qualityScoresArray[i];
            this.maximumQuality = Math.max(this.qualityScoresArray[i], this.maximumQuality);
            this.minimumQuality = Math.min(this.qualityScoresArray[i], this.minimumQuality);
        }
        this.averageQuality = (double) total / this.quality.length();
    }

    /**
     * This method obtain the quality scores array corresponding to the quality char sequence,
     * depending on the sequence's encoding, and calculate sequence's average quality and
     * maximum and minimum individual quality scores
     */
    private void obtainQualityScoresFromQualityString() {
        int total = 0;
        this.maximumQuality = Integer.MIN_VALUE;
        this.minimumQuality = Integer.MAX_VALUE;
        // quality int array initialization
        qualityScoresArray = new int[this.quality.length()];

        // Transform each character in the quality String into a integer, depending on
        // the quality scale, and obtain the average, minimum and maximum values
        for (int i = 0; i < this.quality.length(); i++) {
            char c = this.quality.charAt(i);
            qualityScoresArray[i] = c - FastQ.SCALE_OFFSET[this.encoding];
            total += this.qualityScoresArray[i];

            this.maximumQuality = Math.max(this.qualityScoresArray[i], this.maximumQuality);
            this.minimumQuality = Math.min(this.qualityScoresArray[i], this.minimumQuality);
        }
        this.averageQuality = (double) total / this.quality.length();
    }

    /**
     * Change the encoding of the sequence, and recalculates the quality scores array
     *
     * @param newEncoding - New quality encoding
     */
    public void changeEncoding(int newEncoding) {
        if (this.encoding != newEncoding) {
            int oldEncoding = this.encoding;
            // Transform the quality scores, if necessary
            this.transformQualityScoresArray(oldEncoding, newEncoding);

            // Transform the quality string
            this.obtainQualityStringFromQualityScoresArray(newEncoding);
            this.encoding = newEncoding;
        }
    }


    /**
     * Transform the quality scores array if the score types of the encodings are different
     *
     * @param oldEncoding - old quality encoding type
     * @param newEncoding - new quality encoding type
     */
    private void transformQualityScoresArray(int oldEncoding, int newEncoding) {
        // If the score types of the encodings are different
        if (FastQ.SCALE_SCORE[oldEncoding] != FastQ.SCALE_SCORE[newEncoding]) {
            // Score Map selection
            Map<Integer, Integer> scoreMap;
            if (FastQ.SCALE_SCORE[oldEncoding] == FastQ.PHRED_SCORE_TYPE) {
                scoreMap = FastQ.phredToSolexaMap;
            } else {
                scoreMap = FastQ.solexaToPhredMap;
            }
            // Transform each quality score in the quality scores array
            for (int i = 0; i < this.qualityScoresArray.length; i++) {
                if (qualityScoresArray[i] < 10) {
                    qualityScoresArray[i] = scoreMap.get(qualityScoresArray[i]);
                }
            }
        }
    }

    /**
     * Obtain the quality string in the indicated quality encoding, from the quality scores array
     *
     * @param encoding - quality encoding
     */
    private void obtainQualityStringFromQualityScoresArray(int encoding) {
        char[] qualityChars = new char[this.qualityScoresArray.length];
        // add the scale offset to each individual score and transform the result to a char
        for (int i = 0; i < this.qualityScoresArray.length; i++) {
            qualityChars[i] = (char) (this.qualityScoresArray[i] + FastQ.SCALE_OFFSET[encoding]);
        }
        this.quality = new String(qualityChars);
    }

    /**
     * Trim the sequence's tail, if it is longer than a determined size
     *
     * @param maxSize - Maximum size allowed
     */
    public void trimSequenceTail(int maxSize) {
        // Trim sequence and quality strings
        this.setSeq(this.sequence.substring(0, maxSize));
        this.setQuality(this.quality.substring(0, maxSize));
    }

    /**
     * Trim the sequence removing the first 'n' characters
     *
     * @param n - Number of characters to remove
     */
    public void lTrim(int n) {
        super.lTrim(n);
        this.setQuality(this.quality.substring(n));
    }

    /**
     * Trim the sequence removing the last 'n' characters
     *
     * @param n - Number of characters to remove
     */
    public void rTrim(int n) {
        super.rTrim(n);
        this.setQuality(this.quality.substring(0, this.quality.length() - n));
    }

    /**
     * Returns the average quality of the last elements of the sequence
     *
     * @param numElements - Number of elements whose quality will be returned
     * @return - Average quality of the last elements of the sequence
     */
    public float getSequenceTailAverageQuality(int numElements) {
        float quality = -1;
        if (this.size() >= numElements) {
            // Sum the quality of the last 'n' elements of the sequence,
            // and divide the result by 'n' to obtain the average value
            int totalTailQuality = 0;
            for (int i = 1; i <= numElements; i++) {
                totalTailQuality += this.qualityScoresArray[this.size() - i];
            }
            quality = totalTailQuality / numElements;
        }
        return quality;
    }

    /**
     * Check if the given quality encoding type is valid
     *
     * @param encoding - encoding to check
     * @return boolean - true if the encoding is valid
     */
    public static boolean validQualityEncoding(int encoding) {
        return (encoding == FastQ.SANGER_ENCODING || encoding == FastQ.SOLEXA_ENCODING || encoding == FastQ.ILLUMINA_ENCODING);
    }

}
