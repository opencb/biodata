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
import org.opencb.biodata.models.core.chimerdb.ChimerKb;
import org.opencb.biodata.models.core.chimerdb.ChimerKbGeneBreakpoint;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

import static org.opencb.biodata.formats.feature.chimerdb.ChimerPubParser.getIntCellValue;
import static org.opencb.biodata.formats.feature.chimerdb.ChimerPubParser.getStringCellValue;

public class ChimerKbParser {

    private static Logger logger = LoggerFactory.getLogger(ChimerKbParser.class);

    public static void parse(Path xlsxPath, ChimerDbParserCallback<ChimerKb> callback) throws IOException {
        logger.info("Parsing ChimerKB file: {}", xlsxPath);
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

                ChimerKb chimerKb = new ChimerKb();

                // 0    1               2       3           4           5               6               7       8       9          10
                // id	ChimerDB_Type	Source	webSource	Fusion_pair	5Gene_Junction	3Gene_Junction	H_gene	H_chr	H_position	H_strand
                // 11       12      13          14          15                  16                  17              18
                // T_gene	T_chr	T_position	T_strand	Genomic_breakpoint	Exonic_breakpoint	Breakpoint_Type	Genome_Build_Version
                // 19   20      21          22      23          24      25          26                  27          28
                // PMID	Disease	Validation	Frame	Chr_info	Kinase	Oncogene	Tumor_suppressor	Receptor	Transcription_Factor
                // 29           30          31
                // ChimerPub	ChimerSeq	ChimerSeq+

                // ID
                chimerKb.setId(String.valueOf((int) row.getCell(0).getNumericCellValue()));

                // ChimerDB Type
                strValue = getStringCellValue(row, 1);
                if (strValue != null) {
                    chimerKb.setChimerDbType(strValue);
                }

                // Source (different from source provided by user)
                strValue = getStringCellValue(row, 2);
                if (strValue != null) {
                    chimerKb.setChimerSource(strValue);
                }

                // Web source
                strValue = getStringCellValue(row, 3);
                if (strValue != null) {
                    chimerKb.setWebSource(strValue);
                }

                // Fusion pair
                strValue = getStringCellValue(row, 4);
                if (strValue != null) {
                    chimerKb.setFusionPair(strValue);
                }

                // Gene junctions (5 and 3)
                strValue = getStringCellValue(row, 5);
                if (strValue != null) {
                    chimerKb.setFiveGeneJunction(strValue);
                }
                strValue = getStringCellValue(row, 6);
                if (strValue != null) {
                    chimerKb.setThreeGeneJunction(strValue);
                }

                // Head gene breakpoint
                ChimerKbGeneBreakpoint head = new ChimerKbGeneBreakpoint();
                strValue = getStringCellValue(row, 7);
                if (strValue != null) {
                    head.setGene(strValue);
                }
                strValue = getStringCellValue(row, 8);
                if (strValue != null) {
                    if (strValue.startsWith("chr") || strValue.startsWith("Chr") || strValue.startsWith("CHR")) {
                        // Remove 'chr' prefix if present
                        strValue = strValue.substring(3);
                    }
                    head.setChromosome(strValue);
                }
                intValue = getIntCellValue(row, 9);
                if (intValue != null) {
                    head.setPosition(intValue);
                }
                strValue = getStringCellValue(row, 10);
                if (strValue != null) {
                    head.setStrand(strValue);
                }
                chimerKb.setHeadGene(head);

                // Tail gene breakpoint
                ChimerKbGeneBreakpoint tail = new ChimerKbGeneBreakpoint();
                strValue = getStringCellValue(row, 11);
                if (strValue != null) {
                    tail.setGene(strValue);
                }
                strValue = getStringCellValue(row, 12);
                if (strValue != null) {
                    if (strValue.startsWith("chr") || strValue.startsWith("Chr") || strValue.startsWith("CHR")) {
                        // Remove 'chr' prefix if present
                        strValue = strValue.substring(3);
                    }
                    tail.setChromosome(strValue);
                }
                intValue = getIntCellValue(row, 13);
                if (intValue != null) {
                    tail.setPosition(intValue);
                }
                strValue = getStringCellValue(row, 14);
                if (strValue != null) {
                    tail.setStrand(strValue);
                }
                chimerKb.setTailGene(tail);

                // Genomic breakpoint
                intValue = getIntCellValue(row, 15);
                if (intValue != null) {
                    chimerKb.setGenomicBreakpoint(intValue == 1);
                }

                // Exonic breakpoint
                intValue = getIntCellValue(row, 16);
                if (intValue != null) {
                    chimerKb.setExonicBreakpoint(intValue == 1);
                }

                // Breakpoint type
                strValue = getStringCellValue(row, 17);
                if (strValue != null) {
                    chimerKb.setBreakpointType(strValue);
                }

                // Genome build version
                strValue = getStringCellValue(row, 18);
                if (strValue != null) {
                    chimerKb.setGenomeBuildVersion(strValue);
                }

                // Publications
                strValue = getStringCellValue(row, 19);
                if (strValue != null) {
                    chimerKb.setPmid(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                } else {
                    intValue = getIntCellValue(row, 19);
                    if (intValue != null && intValue > 0) {
                        chimerKb.setPmid(Arrays.asList(String.valueOf(intValue)));
                    }
                }

                // Diseases
                strValue = getStringCellValue(row, 20);
                if (strValue != null) {
                    chimerKb.setDiseases(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                }

                // Validations
                strValue = getStringCellValue(row, 21);
                if (strValue != null) {
                    chimerKb.setValidations(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                }

                // Frame
                strValue = getStringCellValue(row, 22);
                if (strValue != null) {
                    chimerKb.setFrame(strValue);
                }

                // Chromosome info
                strValue = getStringCellValue(row, 23);
                if (strValue != null) {
                    chimerKb.setChrInfo(strValue);
                }

                // Kinase
                strValue = getStringCellValue(row, 24);
                if (strValue != null) {
                    chimerKb.setKinase(strValue.equalsIgnoreCase("kinase"));
                }

                // Oncogene
                strValue = getStringCellValue(row, 25);
                if (strValue != null) {
                    chimerKb.setOncogene(strValue.equalsIgnoreCase("oncogene"));
                }

                // Tumor suppressor
                strValue = getStringCellValue(row, 26);
                if (strValue != null) {
                    chimerKb.setTumorSuppressor(strValue.equalsIgnoreCase("Tumor suppressor gene"));
                }

                // Receptor
                strValue = getStringCellValue(row, 27);
                if (strValue != null) {
                    chimerKb.setReceptor(strValue.equalsIgnoreCase("Receptor"));
                }

                // Transcription factor
                strValue = getStringCellValue(row, 28);
                if (strValue != null) {
                    chimerKb.setTranscriptionFactor(strValue.equalsIgnoreCase("Transcription factor"));
                }

                // ChimerPub
                strValue = getStringCellValue(row, 29);
                if (strValue != null) {
                    chimerKb.setChimerPub(strValue.equalsIgnoreCase("Pub"));
                }

                // ChimerSeq
                strValue = getStringCellValue(row, 30);
                if (strValue != null) {
                    chimerKb.setChimerSeq(strValue.equalsIgnoreCase("Seq"));
                }

                // ChimerSeq+
                strValue = getStringCellValue(row, 31);
                if (strValue != null) {
                    chimerKb.setChimerSeqPlus(strValue.equalsIgnoreCase("Seq+"));
                }

                // Callback to process the gene fusion
                callback.processChimerDbObject(chimerKb);
            }
        } catch (IOException e) {
            throw new IOException("Error reading the ChimerKB file: " + e.getMessage(), e);
        }
        logger.info("ChimerKB file parsed successfully: {}", xlsxPath);
    }
}