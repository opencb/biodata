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

package org.opencb.biodata.formats.feature.chimerdb;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencb.biodata.models.core.chimerdb.ChimerSeq;
import org.opencb.biodata.models.core.chimerdb.ChimerSeqGeneBreakpoint;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

import static org.opencb.biodata.formats.feature.chimerdb.ChimerPubParser.getIntCellValue;
import static org.opencb.biodata.formats.feature.chimerdb.ChimerPubParser.getStringCellValue;

public class ChimerSeqParser {

    private static Logger logger = LoggerFactory.getLogger(ChimerSeqParser.class);

    public static void parse(Path xlsxPath, ChimerDbParserCallback<ChimerSeq> callback) throws IOException {
        logger.info("Parsing ChimerSeq file: {}", xlsxPath);
        FileUtils.checkFile(xlsxPath);

        try (FileInputStream excelFile = new FileInputStream(xlsxPath.toFile());
             Workbook workbook = new XSSFWorkbook(excelFile)) {
            // Get the first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over rows
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // Skip header row if needed (e.g., if first row is header)
                if (row.getRowNum() == 0) {
                    continue;
                }

                String strValue;
                Integer intValue;

                ChimerSeq chimerSeq = new ChimerSeq();

                // 0    1               2       3           4           5       6       7           8           9       10      11
                // id	ChimerDB_Type	Source	webSource	Fusion_pair	H_gene	H_chr	H_position	H_strand	T_gene	T_chr	T_position
                // 12       13                  14                      15          16          17              18
                // T_strand Genomic_breakpoint	Genome_Build_Version	Cancertype	BarcodeID	Seed_reads_num	Spanning_pairs_num
                // 19                   20          21          22      23          24          25                  26
                // Junction_reads_num	Frame       Chr_info	H_locus	H_kinase	H_oncogene	H_tumor_suppressor	H_receptor
                // 27                       28      29          30
                // H_transcription_factor	T_locus	T_kinase	T_oncogene
                // 31                   32           33                     34          35          36
                // T_tumor_suppressor	T_receptor	T_transcription_factor	ChimerKB	ChimerPub	Highly_Reliable_Seq

                // ID
                chimerSeq.setId(String.valueOf((int) row.getCell(0).getNumericCellValue()));

                // ChimerDB Type
                strValue = getStringCellValue(row, 1);
                if (strValue != null) {
                    chimerSeq.setChimerDbType(strValue);
                }

                // Source (different from source provided by user)
                strValue = getStringCellValue(row, 2);
                if (strValue != null) {
                    chimerSeq.setChimerSource(strValue);
                }

                // Web source
                strValue = getStringCellValue(row, 3);
                if (strValue != null) {
                    chimerSeq.setWebSource(strValue);
                }

                // Fusion pair
                strValue = getStringCellValue(row, 4);
                if (strValue != null) {
                    chimerSeq.setFusionPair(strValue);
                }

                // Head gene breakpoint
                ChimerSeqGeneBreakpoint head = new ChimerSeqGeneBreakpoint();
                strValue = getStringCellValue(row, 5);
                if (strValue != null) {
                    head.setGene(strValue);
                }
                strValue = getStringCellValue(row, 6);
                if (strValue != null) {
                    if (strValue.startsWith("chr") || strValue.startsWith("Chr") || strValue.startsWith("CHR")) {
                        // Remove 'chr' prefix if present
                        strValue = strValue.substring(3);
                    }
                    head.setChromosome(strValue);
                }
                intValue = getIntCellValue(row, 7);
                if (intValue != null) {
                    head.setPosition(intValue);
                }
                strValue = getStringCellValue(row, 8);
                if (strValue != null) {
                    head.setStrand(strValue);
                }
                strValue = getStringCellValue(row, 22);
                if (strValue != null) {
                    head.setLocus(strValue);
                }
                strValue = getStringCellValue(row, 23);
                if (strValue != null) {
                    head.setKinase(strValue.equalsIgnoreCase("kinase"));
                }
                strValue = getStringCellValue(row, 24);
                if (strValue != null) {
                    head.setOncogene(strValue.equalsIgnoreCase("oncogene"));
                }
                strValue = getStringCellValue(row, 25);
                if (strValue != null) {
                    head.setTumorSuppressor(strValue.equalsIgnoreCase("Tumor suppressor gene"));
                }
                strValue = getStringCellValue(row, 26);
                if (strValue != null) {
                    head.setReceptor(strValue.equalsIgnoreCase("Receptor"));
                }
                strValue = getStringCellValue(row, 27);
                if (strValue != null) {
                    head.setTranscriptionFactor(strValue.equalsIgnoreCase("Transcription factor"));
                }
                chimerSeq.setHeadGene(head);

                // Tail gene breakpoint
                ChimerSeqGeneBreakpoint tail = new ChimerSeqGeneBreakpoint();
                strValue = getStringCellValue(row, 9);
                if (strValue != null) {
                    tail.setGene(strValue);
                }
                strValue = getStringCellValue(row, 10);
                if (strValue != null) {
                    if (strValue.startsWith("chr") || strValue.startsWith("Chr") || strValue.startsWith("CHR")) {
                        // Remove 'chr' prefix if present
                        strValue = strValue.substring(3);
                    }
                    tail.setChromosome(strValue);
                }
                intValue = getIntCellValue(row, 11);
                if (intValue != null) {
                    tail.setPosition(intValue);
                }
                strValue = getStringCellValue(row, 12);
                if (strValue != null) {
                    tail.setStrand(strValue);
                }
                strValue = getStringCellValue(row, 28);
                if (strValue != null) {
                    tail.setLocus(strValue);
                }
                strValue = getStringCellValue(row, 29);
                if (strValue != null) {
                    tail.setKinase(strValue.equalsIgnoreCase("kinase"));
                }
                strValue = getStringCellValue(row, 30);
                if (strValue != null) {
                    tail.setOncogene(strValue.equalsIgnoreCase("oncogene"));
                }
                strValue = getStringCellValue(row, 31);
                if (strValue != null) {
                    tail.setTumorSuppressor(strValue.equalsIgnoreCase("Tumor suppressor gene"));
                }
                strValue = getStringCellValue(row, 32);
                if (strValue != null) {
                    tail.setReceptor(strValue.equalsIgnoreCase("Receptor"));
                }
                strValue = getStringCellValue(row, 33);
                if (strValue != null) {
                    tail.setTranscriptionFactor(strValue.equalsIgnoreCase("Transcription factor"));
                }
                chimerSeq.setTailGene(tail);

                // Genomic breakpoint
                strValue = getStringCellValue(row, 13);
                if (intValue != null) {
                    chimerSeq.setGenomicBreakpoint(strValue);
                }

                // Genome build version
                strValue = getStringCellValue(row, 14);
                if (strValue != null) {
                    chimerSeq.setGenomeBuildVersion(strValue);
                }

                // Cancer type
                strValue = getStringCellValue(row, 15);
                if (intValue != null) {
                    chimerSeq.setCancerType(strValue);
                }

                // Barcode ID
                strValue = getStringCellValue(row, 16);
                if (strValue != null) {
                    chimerSeq.setBarcodeId(strValue);
                }

                // Seed reads number
                intValue = getIntCellValue(row, 17);
                if (intValue != null) {
                    chimerSeq.setSeedReadsNum(intValue);
                }

                // Spanning pairs number
                intValue = getIntCellValue(row, 18);
                if (intValue != null) {
                    chimerSeq.setSpanningPairsNum(intValue);
                }

                // Junction reads number
                intValue = getIntCellValue(row, 19);
                if (intValue != null) {
                    chimerSeq.setJunctionReadsNum(intValue);
                }

                // Frame
                strValue = getStringCellValue(row, 20);
                if (strValue != null) {
                    chimerSeq.setFrame(strValue);
                }

                // Chromosome info
                strValue = getStringCellValue(row, 21);
                if (strValue != null) {
                    chimerSeq.setChrInfo(strValue);
                }

                // ChimerKb
                strValue = getStringCellValue(row, 34);
                if (strValue != null) {
                    chimerSeq.setChimerKb(strValue.equalsIgnoreCase("KB"));
                }

                // ChimerPub
                strValue = getStringCellValue(row, 35);
                if (strValue != null) {
                    chimerSeq.setChimerPub(strValue.equalsIgnoreCase("Pub"));
                }

                // Highly reliable sequence
                strValue = getStringCellValue(row, 36);
                if (strValue != null) {
                    chimerSeq.setHighlyReliableSeq(strValue.equalsIgnoreCase("Seq+"));
                }

                // Callback to process the gene fusion
                callback.processChimerDbObject(chimerSeq);
            }
        } catch (IOException e) {
            throw new IOException("Error reading the ChimerKB file: " + e.getMessage(), e);
        }
        logger.info("ChimerKB file parsed successfully: {}", xlsxPath);
    }
}