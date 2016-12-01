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

package org.opencb.biodata.models.alignment;


import org.opencb.biodata.models.alignment.stats.MeanCoverage;
import org.opencb.biodata.models.alignment.stats.RegionCoverage;
import org.opencb.biodata.models.core.Region;

import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cgonzalez@cipf.es&gt;
 */
@Deprecated
public class AlignmentRegion {

    private String chromosome;
    private long start;     //Unclipped Start of the first Alignment
    private long end;       //Unclipped End of the last Alignment
    private boolean overlapEnd;     //Indicates if the last alignment is overlapped with the next alignment

    private List<Alignment> alignments;     //Sorted Alignments
    private RegionCoverage coverage;
    private List<MeanCoverage> meanCoverage;
    private AlignmentHeader header;

    public AlignmentRegion(String chromosome, long start, long end) {
        this(chromosome, start, end, null, null, null);
    }


    public AlignmentRegion(List<Alignment> alignments, AlignmentHeader header) {
        Alignment firstAlignment = alignments.get(0);
        Alignment lastAlignment = alignments.get(alignments.size()-1);
        //if(!firstAlignment.getChromosome().equals(lastAlignment.getChromosome())) //TODO jcoll: Limit this
        //System.out.println("All alignments must be in the same chromosome");
        this.chromosome = firstAlignment.getChromosome();
        this.start = firstAlignment.getUnclippedStart();
        this.end = lastAlignment.getUnclippedEnd();
        this.alignments = alignments;
        this.coverage = null;
        this.header = header;
    }

    public AlignmentRegion(String chromosome, long start, long end, List<Alignment> alignments, RegionCoverage coverage, AlignmentHeader header) {
        this.chromosome = chromosome;
        this.start = start;
        this.end = end;
        this.alignments = alignments;
        this.coverage = coverage;
        this.header = header;
    }

    public List<Alignment> getAlignments() {
        return alignments;
    }

    public void setAlignments(List<Alignment> alignments) {
        this.alignments = alignments;
    }

    public String getChromosome() {
        return chromosome;
    }

    public void setChromosome(String chromosome) {
        this.chromosome = chromosome;
    }

    public RegionCoverage getCoverage() {
        return coverage;
    }

    public void setCoverage(RegionCoverage coverage) {
        this.coverage = coverage;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }


    public boolean isOverlapEnd() {
        return overlapEnd;
    }

    public void setOverlapEnd(boolean overlapEnd) {
        this.overlapEnd = overlapEnd;
    }


    public List<MeanCoverage> getMeanCoverage() {
        return meanCoverage;
    }

    public void setMeanCoverage(List<MeanCoverage> meanCoverage) {

        this.meanCoverage = meanCoverage;
    }

    public Region getRegion(){
        return new Region(chromosome, (int)start, (int)end);
    }

    public AlignmentHeader getHeader() {
        return header;
    }

    public void setHeader(AlignmentHeader header) {
        this.header = header;
    }
}
