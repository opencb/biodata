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

package org.opencb.biodata.formats.feature.chemirdb;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;

public class ChemirDbParser {

    private static Logger logger = LoggerFactory.getLogger(ChemirDbParser.class);

    public static void parse(Path xlsxPath, ChemirDbParserCallback callback) throws IOException {
        logger.info("Parsing ChemirDB file: {}", xlsxPath);
        FileUtils.checkFile(xlsxPath);

        try (FileInputStream excelFile = new FileInputStream(xlsxPath.toFile());
             Workbook workbook = new XSSFWorkbook(excelFile)) {
            // Get the first sheet from the workbook
            Sheet sheet = workbook.getSheetAt(0);

            // Iterate over rows
            Iterator<Row> rowIterator = sheet.iterator();
            while (rowIterator.hasNext()) {
                Row currentRow = rowIterator.next();

                // Skip header row if needed (e.g., if first row is header)
                if (currentRow.getRowNum() == 0) {
                    continue;
                }

                // Iterate over cells in the current row
                Iterator<Cell> cellIterator = currentRow.iterator();
                while (cellIterator.hasNext()) {
                    Cell currentCell = cellIterator.next();
                }
            }
        } catch (IOException e) {
            throw new IOException("Error reading the ChemirDB file: " + e.getMessage(), e);
        }
        logger.info("ChemirDB file parsed successfully: {}", xlsxPath);
    }
}