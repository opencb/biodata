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

import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.pedigree.Multiples;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.commons.utils.FileUtils;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by imedina on 11/10/16.
 */
public class PedigreeParser {

    /**
     * Parse a Pedigree file and return a list of Pedigree objects.
     *
     * @param   pedigreePath    Path to the Pedigree file
     * @return                  List of Pedigree objects
     * @throws IOException
     */
    public static List<Pedigree> parse(Path pedigreePath) throws IOException {
        FileUtils.checkFile(pedigreePath);

        Map<String, Pedigree> pedigreeMap = new HashMap<>();
        Map<String, Member> individualMap = new HashMap<>();

        String pedigreeName, individualName;
        Member.Sex sex;
        Member.AffectionStatus affectionStatus;
        //String line, key;
        String[] fields, labels = null;

        Member member;

        // Read the whole pedigree file
        List<String> individualStringLines = Files.readAllLines(pedigreePath);

        // First loop: initializing individual map
        for (int i = 0; i < individualStringLines.size(); i++) {
            String line = individualStringLines.get(i);
            if (line != null) {
                fields = line.split("[ \t]");
                if (fields.length < 6) {
                    throw new IOException("Pedigree file '" + pedigreePath + "' contains " + fields.length
                            + ", it must contain minimum 6 columns!\n" + line);
                }
                if (i == 0 && line.startsWith("#")) {
                    // header with variables, labels are optional
                    labels = line.split("[ \t]");
                } else {
                    // normal line
                    pedigreeName = fields[0];
                    individualName = fields[1];
                    sex = Member.Sex.getEnum(fields[4]);
                    affectionStatus = Member.AffectionStatus.getEnum(fields[5]);
                    if (!pedigreeMap.containsKey(pedigreeName)) {
                        pedigreeMap.put(pedigreeName, new Pedigree(pedigreeName, new ArrayList<>(), new HashMap<>()));
                    }

                    member = new Member(individualName, sex);

                    // labels are optional
                    if (labels != null && fields.length > 6 && labels.length == fields.length) {
                        Map<String, Object> attributes = new HashMap<>();
                        for (int j = 6; j < fields.length; j++) {
                            attributes.put(labels[j], fields[j]);
                        }
                        member.setAttributes(attributes);
                    }
                    pedigreeMap.get(pedigreeName).getMembers().add(member);
                    individualMap.put(pedigreeName + "_" + individualName, member);
                }
            }
        }

        // second loop: setting fathers, mothers, partners and children
        for (int i = 1; i < individualStringLines.size(); i++) {
            String line = individualStringLines.get(i);
            if (line != null) {
                fields = line.split("[ \t]");
                if (!line.startsWith("#")) {
                    // update father, mother and child
                    Member father = individualMap.get(fields[0] + "_" + fields[2]);
                    Member mother = individualMap.get(fields[0] + "_" + fields[3]);
                    Member child = individualMap.get(fields[0] + "_" + fields[1]);

                    // setting father and children
                    if (father != null) {
                        child.setFather(father);
                        if (father.getMultiples() == null) {
                            Multiples multiples = new Multiples().setType("children").setSiblings(new ArrayList<>());
                            father.setMultiples(multiples);
                        }
                        father.getMultiples().getSiblings().add(child.getName());
                    }

                    // setting mother and children
                    if (mother != null) {
                        child.setMother(mother);
                        if (mother.getMultiples() == null) {
                            Multiples multiples = new Multiples().setType("children").setSiblings(new ArrayList<>());
                            mother.setMultiples(multiples);
                        }
                        mother.getMultiples().getSiblings().add(child.getName());
                    }
                }
            }
        }

        // create the list of Pedigree objects from the map
        return new ArrayList<>(pedigreeMap.values());
    }

    /**
     * Save a Pedigree object into a Pedigree format file.
     *
     * @param pedigree      Pedigree object
     * @param pedigreePath  Path to the Pedigree file
     */
    public static void save(Pedigree pedigree, Path pedigreePath) throws IOException {
        final OutputStream os = new FileOutputStream(pedigreePath.toFile());
        // Sanity check
        if (pedigree != null) {
            writeHeader(pedigree, os);
            write(pedigree, os);
        }
        // Close
        os.close();
    }

    public static void save(List<Pedigree> pedigrees, Path pedigreePath) throws IOException {
        final OutputStream os = new FileOutputStream(pedigreePath.toFile());
        // Sanity check
        if (pedigrees != null && !pedigrees.isEmpty()) {
            writeHeader(pedigrees.get(0), os);
            for (Pedigree pedigree : pedigrees) {
                write(pedigree, os);
            }
        }
        // Close
        os.close();
    }

    private static void writeHeader(Pedigree pedigree, OutputStream os) throws IOException {
        StringBuilder line = new StringBuilder();

        // TODO: check order labels, header line and individual lines !!
        // header line
        line.append("#family").append("\t").append("individual").append("\t").append("father").append("\t")
                .append("mother").append("\t").append("sex").append("\t").append("affection");
        pedigree.getMembers().get(0).getAttributes().forEach((k, v) -> line.append("\t").append(k));
        os.write(line.toString().getBytes());
        os.write("\n".getBytes());
    }

    private static void write(Pedigree pedigree, OutputStream os) throws IOException {
//        final PrintStream writer = new PrintStream(os);

        StringBuilder line = new StringBuilder();

        // main lines (individual data)
        for (Member member : pedigree.getMembers()) {
            // mandatory fields
            line.setLength(0);
            line.append(pedigree.getName()).append("\t").append(member.getName()).append("\t")
                    .append(member.getFather() != null ? member.getFather().getName() : 0).append("\t")
                    .append(member.getMother() != null ? member.getMother().getName() : 0).append("\t")
                    .append(member.getSex().getValue());

            // custom fields (optional)
            if (member.getAttributes() != null) {
                member.getAttributes().forEach(((k, v) -> line.append("\t").append(v)));
            }

            // write line
            os.write(line.toString().getBytes());
            os.write("\n".getBytes());
        }

        // close
        //writer.close();
    }
}
