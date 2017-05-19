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

package org.opencb.biodata.formats.pedigree;

import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 11/10/16.
 */
public class PedigreeManager {


    public PedigreeManager() {
    }

    public Pedigree parse(Path pedigreePath) throws IOException {
        FileUtils.checkFile(pedigreePath);


        List<String> individualStringLines = Files.readAllLines(pedigreePath);

        List<Individual> individuals = new ArrayList<>(individualStringLines.size());
        for (int i = 0; i < individualStringLines.size(); i++) {
            if (i == 0 && individualStringLines.get(i).startsWith("#")) {
                // Header with variables
            } else {
                // normal line

            }
        }

        // Create the Pedigree object with the accumulated data
        Pedigree pedigree = new Pedigree();
        pedigree.addIndividuals(individuals);

        return pedigree;
    }
}
