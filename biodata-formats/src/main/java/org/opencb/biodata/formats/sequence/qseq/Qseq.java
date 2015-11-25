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

import org.opencb.biodata.formats.sequence.fastq.FastQ;

public class Qseq {

//	QSEQ is a format created by Illumina and it uses a single line of tab separated fields to denote read id information, sequence 
//	and quality. The fields for in a QSEQ path are Code:
//	MachineID     run#     lane#     tile#     x-coord     y-coord     index     read#     sequence     q-sores    p/f flag	

    private String machineId;

    private int run;

    private int lane;

    private int tile;

    private int xCoord;

    private int yCoord;

    private int index;

    /**
     * read number (1 or 2 for paired end runs)
     */
    private int readNumber;

    /**
     * Sequence
     */
    private String sequence;

    /**
     * quality String, in Phred 64 encoding
     */
    private String quality;

    /**
     * true if filteringPassed
     */
    private int filteringPassed;

    private static final String QSEQ_FIELD_SEPARATOR = "\t";

    public Qseq(String machineId, int run, int lane, int tile, int xcoord,
                int ycoord, int index, int readNumber, String sequence,
                String quality, int filteringPassed) {
        this.setMachineId(machineId);
        this.setRun(run);
        this.setLane(lane);
        this.setTile(tile);
        this.setXCoord(xcoord);
        this.setYCoord(ycoord);
        this.setIndex(index);
        this.readNumber = readNumber;
        this.sequence = sequence;
        this.quality = quality;
        this.filteringPassed = filteringPassed;
    }

    public String toString() {
        String qSeq = machineId + QSEQ_FIELD_SEPARATOR + run + QSEQ_FIELD_SEPARATOR +
                lane + QSEQ_FIELD_SEPARATOR + tile + QSEQ_FIELD_SEPARATOR +
                xCoord + QSEQ_FIELD_SEPARATOR + yCoord + QSEQ_FIELD_SEPARATOR +
                index + QSEQ_FIELD_SEPARATOR + readNumber + QSEQ_FIELD_SEPARATOR +
                sequence + QSEQ_FIELD_SEPARATOR + quality + QSEQ_FIELD_SEPARATOR +
                filteringPassed;
        return qSeq;
    }

    public FastQ toFastQ() {
        FastQ fastq;
        //@GA-I_0001:1:1:1036:19043#0/1
        String id = machineId + "_" + run + ":" + lane + ":" + tile + ":" + xCoord + ":" + yCoord + "#" + index + "/" + readNumber;
        fastq = new FastQ(id, "", sequence.replace(".", "N"), quality, FastQ.ILLUMINA_ENCODING);
        return fastq;
    }

    public int getReadNumber() {
        return readNumber;
    }

    public void setReadNumber(int readNumber) {
        this.readNumber = readNumber;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public void setFilteringPassed(int filteringPassed) {
        this.filteringPassed = filteringPassed;
    }

    public int getFilteringPassed() {
        return filteringPassed;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public int getRun() {
        return run;
    }

    public void setMachineId(String machineId) {
        this.machineId = machineId;
    }

    public String getMachineId() {
        return machineId;
    }

    public void setLane(int lane) {
        this.lane = lane;
    }

    public int getLane() {
        return lane;
    }

    public void setTile(int tile) {
        this.tile = tile;
    }

    public int getTile() {
        return tile;
    }

    public void setXCoord(int xcoord) {
        this.xCoord = xcoord;
    }

    public int getXCoord() {
        return xCoord;
    }

    public void setYCoord(int ycoord) {
        this.yCoord = ycoord;
    }

    public int getYCoord() {
        return yCoord;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
