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
import org.opencb.biodata.models.variant.avro.GeneFusion;
import org.opencb.biodata.models.variant.avro.GeneFusionBreakpoint;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Iterator;

public class ChimerDbParser {

    private static Logger logger = LoggerFactory.getLogger(ChimerDbParser.class);

    public static void parse(Path xlsxPath, GeneFusionParserCallback callback) throws IOException {
        logger.info("Parsing ChemirDB file: {}", xlsxPath);
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

                GeneFusion geneFusion = new GeneFusion();

                // 0    1               2       3           4           5               6               7       8       9          10
                // id	ChimerDB_Type	Source	webSource	Fusion_pair	5Gene_Junction	3Gene_Junction	H_gene	H_chr	H_position	H_strand
                // 11       12      13          14          15                  16                  17              18
                // T_gene	T_chr	T_position	T_strand	Genomic_breakpoint	Exonic_breakpoint	Breakpoint_Type	Genome_Build_Version
                // 19   20      21          22      23          24      25          26                  27          28
                // PMID	Disease	Validation	Frame	Chr_info	Kinase	Oncogene	Tumor_suppressor	Receptor	Transcription_Factor
                // 29           30          31
                // ChimerPub	ChimerSeq	ChimerSeq+

                geneFusion.setId(String.valueOf((int) row.getCell(0).getNumericCellValue()));
                geneFusion.setSource("chimerdb");
                geneFusion.setPair(row.getCell(4).getStringCellValue());

                if (row.getCell(5) != null && StringUtils.isNotEmpty(row.getCell(5).getStringCellValue())) {
                    geneFusion.setGene5PrimeJunction(row.getCell(5).getStringCellValue());
                }
                if (row.getCell(6) != null && StringUtils.isNotEmpty(row.getCell(6).getStringCellValue())) {
                    geneFusion.setGene3PrimeJunction(row.getCell(6).getStringCellValue());
                }

                // Head gene breakpoint
                GeneFusionBreakpoint head = new GeneFusionBreakpoint();
                if (row.getCell(7) != null && StringUtils.isNotEmpty(row.getCell(7).getStringCellValue())) {
                    head.setGeneName(row.getCell(7).getStringCellValue());
                }
                if (row.getCell(8) != null && StringUtils.isNotEmpty(row.getCell(8).getStringCellValue())) {
                    head.setChromosome(row.getCell(8).getStringCellValue());
                }
                if (row.getCell(9) != null) {
                    // The excel file may contain errors in this cell, so we need to check the type
                    if (row.getCell(9).getCellType() == CellType.STRING) {
                        logger.warn("Error in cell 9 (H_position), expected numeric value but found string: {}",
                                row.getCell(9).getStringCellValue());
                    } else if (row.getCell(9).getCellType() == CellType.NUMERIC) {
                        if (row.getCell(9).getNumericCellValue() > 0) {
                            head.setPosition((int) row.getCell(9).getNumericCellValue());
                        }
                    }
                }
                if (row.getCell(10) != null && StringUtils.isNotEmpty(row.getCell(10).getStringCellValue())) {
                    head.setStrand(row.getCell(10).getStringCellValue());
                }
                geneFusion.setHeadGene(head);

                // Tail gene breakpoint
                GeneFusionBreakpoint tail = new GeneFusionBreakpoint();
                if (row.getCell(11) != null && StringUtils.isNotEmpty(row.getCell(11).getStringCellValue())) {
                    tail.setGeneName(row.getCell(11).getStringCellValue());
                }
                if (row.getCell(12) != null) {
                    if (row.getCell(12).getCellType() == CellType.STRING) {
                        if (StringUtils.isNotEmpty(row.getCell(12).getStringCellValue())) {
                            tail.setChromosome(row.getCell(12).getStringCellValue());
                        }
                    } else {
                        logger.warn("Error in cell 12 (T_chromosome), it is not a string value");
                    }
                }
                if (row.getCell(13) != null) {
                    // The excel file may contain errors in this cell, so we need to check the type
                    if (row.getCell(13).getCellType() == CellType.STRING) {
                        logger.warn("Error in cell 13 (T_position), expected numeric value but found string: {}",
                                row.getCell(13).getStringCellValue());
                    } else if (row.getCell(13).getCellType() == CellType.NUMERIC) {
                        if (row.getCell(13).getNumericCellValue() > 0) {
                            tail.setPosition((int) row.getCell(13).getNumericCellValue());
                        }
                    }
                }
                if (row.getCell(14) != null && StringUtils.isNotEmpty(row.getCell(14).getStringCellValue())) {
                    tail.setStrand(row.getCell(14).getStringCellValue());
                }
                geneFusion.setTailGene(tail);

                // Publications
                if (row.getCell(19) != null) {
                    if (row.getCell(19).getCellType() == CellType.STRING) {
                        if (StringUtils.isNotEmpty(row.getCell(19).getStringCellValue())) {
                            geneFusion.setPublications(Arrays.asList(row.getCell(19).getStringCellValue().split(",")));
                        }
                    } else if (row.getCell(19).getCellType() == CellType.NUMERIC) {
                        if (row.getCell(19).getNumericCellValue() > 0) {
                            geneFusion.setPublications(Arrays.asList(String.valueOf((int) row.getCell(19).getNumericCellValue())));
                        }
                    }
                }

                // Diseases
                if (row.getCell(20) != null && StringUtils.isNotEmpty(row.getCell(20).getStringCellValue())) {
                    geneFusion.setDiseases(Arrays.asList(row.getCell(20).getStringCellValue().split(",")));
                }

                // Validations
                if (row.getCell(21) != null && StringUtils.isNotEmpty(row.getCell(21).getStringCellValue())) {
                    geneFusion.setValidations(Arrays.asList(row.getCell(21).getStringCellValue().split(",")));
                }

                // Attributes
                if (row.getCell(15) != null && row.getCell(15).getNumericCellValue() == 1) {
                    geneFusion.getAttributes().put("genomic_breakpoint", String.valueOf(true));
                }
                if (row.getCell(16) != null && row.getCell(16).getNumericCellValue() == 1) {
                    geneFusion.getAttributes().put("exomic_breakpoint", String.valueOf(true));
                }
                if (row.getCell(23) != null && StringUtils.isNotEmpty(row.getCell(23).getStringCellValue())) {
                    geneFusion.getAttributes().put("chr_info", row.getCell(23).getStringCellValue());
                }
                if (row.getCell(24) != null && StringUtils.isNotEmpty(row.getCell(24).getStringCellValue())) {
                    geneFusion.getAttributes().put("kinase", String.valueOf(true));
                }
                if (row.getCell(25) != null && StringUtils.isNotEmpty(row.getCell(25).getStringCellValue())) {
                    geneFusion.getAttributes().put("oncogene", String.valueOf(true));
                }
                if (row.getCell(26) != null && StringUtils.isNotEmpty(row.getCell(26).getStringCellValue())) {
                    geneFusion.getAttributes().put("tumor_supressor", String.valueOf(true));
                }
                if (row.getCell(27) != null && StringUtils.isNotEmpty(row.getCell(27).getStringCellValue())) {
                    geneFusion.getAttributes().put("receptor", String.valueOf(true));
                }
                if (row.getCell(28) != null && StringUtils.isNotEmpty(row.getCell(28).getStringCellValue())) {
                    geneFusion.getAttributes().put("transcriptor_factor", String.valueOf(true));
                }

                // Callback to process the gene fusion
                callback.processGeneFusion(geneFusion);
            }
        } catch (IOException e) {
            throw new IOException("Error reading the ChemirDB file: " + e.getMessage(), e);
        }
        logger.info("ChemirDB file parsed successfully: {}", xlsxPath);
    }
}