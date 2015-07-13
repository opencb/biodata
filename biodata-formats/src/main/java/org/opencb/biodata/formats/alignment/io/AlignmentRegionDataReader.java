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

package org.opencb.biodata.formats.alignment.io;

import org.opencb.biodata.models.alignment.Alignment;
import org.opencb.biodata.models.alignment.AlignmentRegion;
import org.opencb.commons.io.DataReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: jcoll
 * Date: 2/3/14
 * Time: 3:58 PM
 */
public class AlignmentRegionDataReader implements DataReader<AlignmentRegion> {

    private AlignmentDataReader alignmentDataReader;
    private Alignment prevAlignment;
    private int chunkSize;  //Max number of alignments in one AlignmentRegion.
    private int maxSequenceSize; //Maximum size for the total sequence. Count from the start of the first alignment to the end of the last alignment.
    private Logger logger = LoggerFactory.getLogger(AlignmentRegionDataReader.class);

    private static final int defaultChunkSize = 200000;
    private static final int defaultMaxSequenceSize = 100000;

    public AlignmentRegionDataReader(AlignmentDataReader alignmentDataReader){
        this(alignmentDataReader, defaultChunkSize);
    }

    public AlignmentRegionDataReader(AlignmentDataReader alignmentDataReader, int chunkSize){
        this(alignmentDataReader, chunkSize, defaultMaxSequenceSize);
    }
    public AlignmentRegionDataReader(AlignmentDataReader alignmentDataReader, int chunkSize, int maxSequenceSize){
        this.alignmentDataReader = alignmentDataReader;
        this.prevAlignment = null;
        this.chunkSize = chunkSize;
        this.maxSequenceSize = maxSequenceSize;
    }


    @Override
    public boolean open() {
        return alignmentDataReader.open();
    }

    @Override
    public boolean close() {
        return alignmentDataReader.close();
    }

    @Override
    public boolean pre() {
        return alignmentDataReader.pre();
    }

    @Override
    public boolean post() {
        return alignmentDataReader.post();
    }

    @Override
    public List<AlignmentRegion> read() {
        AlignmentRegion elem = readElem();
        return elem != null? Arrays.asList(elem) : null;
    }

    public AlignmentRegion readElem() {
        List<Alignment> alignmentList = new LinkedList<>();
        String chromosome;
        long start;
        long end;   //To have the correct "end" value,
        boolean overlappedEnd = true;

        //First initialisation
        if(prevAlignment == null){
            List<Alignment> read = alignmentDataReader.read();
            prevAlignment = read != null? read.get(0) : null;
            if(prevAlignment == null){  //Empty source
                return null;
            }
        }

        //Properties for the whole AlignmentRegion
        chromosome = prevAlignment.getChromosome();
        start = prevAlignment.getUnclippedStart();
        if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){
            end = prevAlignment.getUnclippedEnd();
        } else {
            end = start;
        }


        for(int i = 0; i < chunkSize; i++){
            alignmentList.add(prevAlignment);   //The prevAlignment is ready to be added.
            if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){
                if(end < prevAlignment.getUnclippedEnd()){
                    end = prevAlignment.getUnclippedEnd();  //Update the end only if is a valid segment.
                }
            }

            //Read new alignment.
            List<Alignment> read = alignmentDataReader.read();
            prevAlignment = read != null ? read.get(0) : null;
            
            //First stop condition: End of the chromosome or file
            if(prevAlignment == null || !chromosome.equals(prevAlignment.getChromosome())){
                overlappedEnd = false;
                break;  //Break when read alignments from other chromosome or if is the last element
            }

            //Second stop condition: Too big Region.
            if((prevAlignment.getFlags() & Alignment.SEGMENT_UNMAPPED) == 0){
                if((prevAlignment.getUnclippedEnd() - start) > maxSequenceSize){
                    if(prevAlignment.getUnclippedStart() > end){
                        //The start of the prevAlignment doesn't overlap with the end of the last inserted Alignment
                        overlappedEnd = false;
                    }
                    break;
                }
            }

        }

        //if(prevAlignment != null){
            //System.out.println("(prevAlignment.getUnclippedEnd() - start) = " +(prevAlignment.getUnclippedEnd() - start) + " overlappedEnd = " + overlappedEnd);
            //System.out.println("(alignmentList.get(alignmentList(size)-1).getEnd()) = " + (alignmentList.get(alignmentList.size()-1).getEnd()) + " start " + start + " i " + i);
            //System.out.println("(alignmentList.get(alignmentList(size)-1).getUnclippedEnd() - start) = " + (alignmentList.get(alignmentList.size()-1).getUnclippedEnd() - start));
         //}
        //System.out.println("start = " + start + ", end = " + end + ", length = " + (end - start) + ", alignments = "  + alignmentList.size());
        //logger.debug("start = " + start + ", end = " + end + ", length = " + (end - start) + ", alignments = "  + alignmentList.size());
        logger.info("start = " + start + ", end = " + end + ", length = " + (end - start) + ", alignments = "  + alignmentList.size());


        AlignmentRegion alignmentRegion = new AlignmentRegion(alignmentList, alignmentDataReader.getHeader());
        alignmentRegion.setOverlapEnd(overlappedEnd);

        alignmentRegion.setStart(start);
        alignmentRegion.setEnd(end);


        return alignmentRegion;
    }


    @Override
    public List<AlignmentRegion> read(int batchSize) {
        List<AlignmentRegion> alignmentRegionList = new LinkedList<>();
//        for(int i = 0; i < batchSize; i++){
//            alignmentRegionList.add(read());
//        }
        AlignmentRegion alignmentRegion;
        for(int i = 0; i < batchSize; i++){
            alignmentRegion = readElem();
            if(alignmentRegion != null){
                alignmentRegionList.add(alignmentRegion);
            }
        }
        return alignmentRegionList;
    }

    /**
     * Set maximum size for the AlignmentRegion from the start of the first alignment, to the end of the last alignment.
     *
     * @param maxSequenceSize Maximum size
     */
    public void setMaxSequenceSize(int maxSequenceSize) {
        this.maxSequenceSize = maxSequenceSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }

    /**
     * Set maximum number of Alignments in the AlignmentRegion result
     *
     * @param chunkSize Chunk Size
     */
    public void setChunkSize(int chunkSize) {
        this.chunkSize = chunkSize;
    }

}
