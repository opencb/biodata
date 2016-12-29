package org.opencb.biodata.formats.pedigree;

import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by imedina on 11/10/16.
 */
public class PedigreeManager {

    public PedigreeManager() {
    }

    /**
     * Parse a Pedigree file and return a Pedigree object.
     *
     * @param   pedigreePath    Path to the Pedigree file
     * @return                  Pedigree object
     * @throws IOException
     */
    public Pedigree parse(Path pedigreePath) throws IOException {
        FileUtils.checkFile(pedigreePath);

        Map<String, Individual> individualMap = new HashMap<>();
        List<String> individualStringLines = Files.readAllLines(pedigreePath);

        String line, key;
        String[] labels = null;
        String[] fields;

        Individual individual;

        // first loop: initializing individual map
        for (int i = 0; i < individualStringLines.size(); i++) {
            line = individualStringLines.get(i);
            if (line != null) {
                fields = line.split("\t");
                if (fields.length < 6) {
                    throw new IOException("Pedigree file '" + pedigreePath + "' contains " + fields.length
                            + ", it must contain minimum 6 columns!");
                }
                if (i == 0 && line.startsWith("#")) {
                    // header with variables
                    // TODO: is it mandatory ??
                    labels = line.split("\t");
                } else {
                    // normal line
                    individual = new Individual()
                            .setId(fields[1])
                            .setFamily(fields[0])
                            .setSex(fields[4])
                            .setPhenotype(fields[5]);

                    if (fields.length > 6) {
                        // TODO: checking existing labels ??
                        Map<String, Object> vars = new HashMap<>();
                        for (int j = 6; j < fields.length; j++) {
                            vars.put(labels[j], fields[j]);
                        }
                        individual.setVariables(vars);
                    }
                    key = Pedigree.key(individual);
                    individualMap.put(key, individual);
                }
            }
        }

        // second loop: setting fathers, mothers, partners and children
        Individual father, mother;
        for (int i = 1; i < individualStringLines.size(); i++) {
            line = individualStringLines.get(i);
            if (line != null) {
                fields = line.split("\t");
                if (!line.startsWith("#")) {

                    // updating individual
                    individual = individualMap.get(Pedigree.key(fields[0], fields[1]));

                    // setting father and children
                    key = Pedigree.key(fields[0], fields[2]);
                    father = individualMap.get(key);
                    if (father != null) {
                        individual.setFather(father);
                        if (father.getChildren() == null) {
                            father.setChildren(new LinkedHashSet<>());
                        }
                        father.getChildren().add(individual);
                    }

                    // setting mother and children
                    key = Pedigree.key(fields[0], fields[3]);
                    mother = individualMap.get(key);
                    if (mother != null) {
                        individual.setMother(mother);
                        if (mother.getChildren() == null) {
                            mother.setChildren(new LinkedHashSet<>());
                        }
                        mother.getChildren().add(individual);
                    }

                    // setting partners
                    if (father != null && mother != null) {
                        father.setPartner(mother);
                        mother.setPartner(father);
                    }
                }
            }
        }

        // create the Pedigree object with the map of individuals
        return new Pedigree(individualMap);
    }

    /**
     * Save a Pedigree object into a Pedigree format file.
     *
     * @param pedigree      Pedigree object
     * @param pedigreePath  Path to the Pedigree file
     */
    public void save(Pedigree pedigree, Path pedigreePath) throws IOException {
        BufferedWriter writer = FileUtils.newBufferedWriter(pedigreePath);
        StringBuilder line = new StringBuilder();

        // TODO: check order labels, header line and individual lines !!

        // header line
        line.append("#");
        pedigree.getVariables().forEach((s, variableField) -> line.append(s).append("\t"));
        writer.write(line.toString());

        // main lines (individual data)
        for (Individual individual: pedigree.getIndividuals().values()) {
            // mandatory fields
            line.setLength(0);
            line.append(individual.getFamily()).append("\t").append(individual.getId()).append("\t")
                    .append(individual.getFather() != null ? individual.getFather().getId() : 0).append("\t")
                    .append(individual.getMother() != null ? individual.getMother().getId() : 0).append("\t")
                    .append(individual.getSex()).append("\t").append(individual.getPhenotype()).append("\t");

            // custom fields (optional)
            individual.getVariables().forEach(((s1, o) -> line.append(o).append("\t")));

            // write line
            writer.write(line.toString());
        }

        // close
        writer.close();
    }
}
