package org.opencb.biodata.formats.pedigree.io;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import org.opencb.biodata.models.pedigree.Individual;
import org.opencb.biodata.models.pedigree.Pedigree;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class PedigreePedReader implements PedigreeReader {

    private String filename;
    private Pedigree ped;
    private BufferedReader reader;

    public PedigreePedReader(String filename) {
        this.filename = filename;
        ped = new Pedigree();
    }

    @Override
    public boolean open() {
        try {
            reader = new BufferedReader(new FileReader(this.filename));
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        try {
            reader.close();
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean pre() {
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public List<Pedigree> read() {
        String line;
        Individual ind, father, mother;
        String[] fields;
        String sampleId, familyId, fatherId, motherId, sex, phenotype;
        Set<Individual> family;
        String[] auxFields = null;

        try {
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#")) {
                    this.parseHeader(line);
                } else {
                    fields = line.split("\t");
                    familyId = fields[0];
                    sampleId = fields[1];
                    fatherId = fields[2];
                    motherId = fields[3];
                    sex = fields[4];
                    phenotype = fields[5];

                    if (fields.length > 6) {
                        auxFields = Arrays.copyOfRange(fields, 6, fields.length);
                    }

                    family = ped.getFamily(familyId);
                    if (family == null) {
                        family = new TreeSet<>();
                        ped.addFamily(familyId, family);
                    }

                    ind = new Individual(sampleId, familyId, null, null, sex, phenotype, auxFields);
                    ind.setFatherId(fatherId);
                    ind.setMotherId(motherId);
                    ped.addIndividual(ind);
                    family.add(ind);


                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Map.Entry<String, Individual> entry : ped.getIndividuals().entrySet()) {
            ind = entry.getValue();
            father = ped.getIndividual(ind.getFatherId());
            mother = ped.getIndividual(ind.getMotherId());

            ind.setFather(father);
            ind.setMother(mother);

            if (mother != null) {
                mother.addChild(ind);
            }
            if (father != null) {
                father.addChild(ind);

            }
        }
        
        return Arrays.asList(ped);
    }

    @Override
    public List<Pedigree> read(int batchSize) {
        return null;
    }

    private void parseHeader(String lineHeader) {
        String header = lineHeader.substring(1, lineHeader.length());
        String[] allFields = header.split("\t");

        allFields = Arrays.copyOfRange(allFields, 6, allFields.length);
        for (int i = 0; i < allFields.length; i++) {
            ped.getFields().put(allFields[i], i);
        }
    }
}
