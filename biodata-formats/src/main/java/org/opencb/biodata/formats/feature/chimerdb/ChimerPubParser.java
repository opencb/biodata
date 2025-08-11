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

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencb.biodata.models.core.chimerdb.ChimerPub;
import org.opencb.biodata.models.core.chimerdb.ChimerPubGeneBreakpoint;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;

public class ChimerPubParser {

    private static Logger logger = LoggerFactory.getLogger(ChimerPubParser.class);

    public static void parse(Path xlsxPath, ChimerDbParserCallback<ChimerPub> callback) throws IOException {
        logger.info("Parsing ChimerPub file: {}", xlsxPath);
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
                Double doubleValue;

                ChimerPub chimerPub = new ChimerPub();

                // 0    1           2               3       4       5       6      7        8           9       10          11
                // id	Fusion_pair	Translocation	H_gene	T_gene	PMID	Score  Disease	Validation	Kinase	Oncogene	Tumor_suppressor
                // 12       13                      14          15          16          17                  18
                // Receptor Transcription_Factor	ChimerKB	ChimerSeq	ChimerSeq+	Sentence_highlight	H_gene_highlight
                // 19                20                 21
                // T_gene_highlight  Disease_highlight	Validation_highlight

                // ID
                chimerPub.setId(String.valueOf((int) row.getCell(0).getNumericCellValue()));

                // Fusion pair
                strValue = getStringCellValue(row, 1);
                if (strValue != null) {
                    chimerPub.setFusionPair(strValue);
                }

                // Translocation
                strValue = getStringCellValue(row, 2);
                if (strValue != null) {
                    chimerPub.setTranslocation(strValue);
                }

                // Head gene breakpoint
                ChimerPubGeneBreakpoint head = new ChimerPubGeneBreakpoint();
                strValue = getStringCellValue(row, 3);
                if (strValue != null) {
                    head.setGene(strValue);
                }
                strValue = getStringCellValue(row, 18);
                if (strValue != null) {
                    head.setHighlight(strValue);
                }
                chimerPub.setHeadGene(head);

                // Tail gene breakpoint
                ChimerPubGeneBreakpoint tail = new ChimerPubGeneBreakpoint();
                strValue = getStringCellValue(row, 4);
                if (strValue != null) {
                    tail.setGene(strValue);
                }
                strValue = getStringCellValue(row, 19);
                if (strValue != null) {
                    tail.setHighlight(strValue);
                }
                chimerPub.setTailGene(tail);

                // Publications
                strValue = getStringCellValue(row, 5);
                if (strValue != null) {
                    chimerPub.setPmid(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                } else {
                    intValue = getIntCellValue(row, 5);
                    if (intValue != null && intValue > 0) {
                        chimerPub.setPmid(Arrays.asList(String.valueOf(intValue)));
                    }
                }

                // Score
                doubleValue = getDoubleCellValue(row, 6);
                if (doubleValue != null) {
                    chimerPub.setScore(doubleValue);
                }

                // Diseases
                strValue = getStringCellValue(row, 7);
                if (strValue != null) {
                    chimerPub.setDiseases(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                }

                // Validations
                strValue = getStringCellValue(row, 8);
                if (strValue != null) {
                    chimerPub.setValidations(Arrays.stream(strValue.split(",")).map(String::trim).collect(Collectors.toList()));
                }

                // Kinase
                strValue = getStringCellValue(row, 9);
                if (strValue != null) {
                    chimerPub.setKinase(strValue.equalsIgnoreCase("kinase"));
                }

                // Oncogene
                strValue = getStringCellValue(row, 10);
                if (strValue != null) {
                    chimerPub.setOncogene(strValue.equalsIgnoreCase("oncogene"));
                }

                // Tumor suppressor
                strValue = getStringCellValue(row, 11);
                if (strValue != null) {
                    chimerPub.setTumorSuppressor(strValue.equalsIgnoreCase("Tumor suppressor gene"));
                }

                // Receptor
                strValue = getStringCellValue(row, 12);
                if (strValue != null) {
                    chimerPub.setReceptor(strValue.equalsIgnoreCase("Receptor"));
                }

                // Transcription factor
                strValue = getStringCellValue(row, 13);
                if (strValue != null) {
                    chimerPub.setTranscriptionFactor(strValue.equalsIgnoreCase("Transcription factor"));
                }

                // ChimerKb
                strValue = getStringCellValue(row, 14);
                if (strValue != null) {
                    chimerPub.setChimerKb(strValue.equalsIgnoreCase("KB"));
                }

                // ChimerSeq
                strValue = getStringCellValue(row, 15);
                if (strValue != null) {
                    chimerPub.setChimerSeq(strValue.equalsIgnoreCase("Seq"));
                }

                // ChimerSeq+
                strValue = getStringCellValue(row, 16);
                if (strValue != null) {
                    chimerPub.setChimerSeqPlus(strValue.equalsIgnoreCase("Seq+"));
                }

                // Sentence highlight
                strValue = getStringCellValue(row, 17);
                if (strValue != null) {
                    chimerPub.setSenteceHighlight(strValue);
                }

                // Disease highlight
                strValue = getStringCellValue(row, 20);
                if (strValue != null) {
                    chimerPub.setDiseaseHighlight(strValue);
                }

                // Validation highlight
                strValue = getStringCellValue(row, 21);
                if (strValue != null) {
                    chimerPub.setValidationHighlight(strValue);
                }

                // Callback to process the gene fusion
                callback.processChimerDbObject(chimerPub);
            }
        } catch (IOException e) {
            throw new IOException("Error reading the ChimerPub file: " + e.getMessage(), e);
        }
        logger.info("ChimerPub file parsed successfully: {}", xlsxPath);
    }

    public static Integer getIntCellValue(Row row, int index) {
        if (row.getCell(index) == null) {
            return null;
        }
        if (row.getCell(index).getCellType() != CellType.NUMERIC) {
            logger.warn("Error in cell {} of row {}, expected NUMERIC but found {}", index, row.getRowNum(),
                    row.getCell(index).getCellType());
            return null;
        }
        return (int) row.getCell(index).getNumericCellValue();
    }

    public static Double getDoubleCellValue(Row row, int index) {
        if (row.getCell(index) == null) {
            return null;
        }
        if (row.getCell(index).getCellType() != CellType.NUMERIC) {
            logger.warn("Error in cell {} of row {}, expected NUMERIC but found {}", index, row.getRowNum(),
                    row.getCell(index).getCellType());
            return null;
        }
        return row.getCell(index).getNumericCellValue();
    }

    public static String getStringCellValue(Row row, int index) {
        if (row.getCell(index) == null) {
            return null;
        }
        if (row.getCell(index).getCellType() != CellType.STRING) {
            logger.warn("Error in cell {} of row {}, expected STRING but found {}", index, row.getRowNum(),
                    row.getCell(index).getCellType());
            return null;
        }
        if (StringUtils.isNotEmpty(row.getCell(index).getStringCellValue())) {
            return row.getCell(index).getStringCellValue();
        }
        return null;
    }
}