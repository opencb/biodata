package org.opencb.biodata.tools.pedigree;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertTrue;

public class ModeOfInheritanceTest {

    Pedigree family1;
    Pedigree family2;
    Pedigree family3;

    Phenotype phenotype1;
    Phenotype phenotype2;
    Phenotype phenotype3;
    Phenotype phenotype4;

    @Before
    public void before() {
        phenotype1 = new Phenotype("disease1", "disease1", "");
        phenotype2 = new Phenotype("disease2", "disease2", "");
        phenotype3 = new Phenotype("disease3", "disease2", "");
        phenotype4 = new Phenotype("disease4", "disease2", "");

        Individual father = new Individual().setId("father").setSex(Individual.Sex.MALE)
                .setPhenotypes(Arrays.asList(phenotype1, phenotype3));
        Individual mother = new Individual().setId("mother").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype2));
        Individual daughter = new Individual().setId("daughter").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype2))
                .setMother(mother).setFather(father);
        Individual son = new Individual().setId("son").setSex(Individual.Sex.MALE)
                .setPhenotypes(Arrays.asList(phenotype1, phenotype4))
                .setMother(mother).setFather(father);
        family1 = new Pedigree()
                .setMembers(Arrays.asList(father, mother, daughter, son))
                .setPhenotypes(Arrays.asList(phenotype1, phenotype2, phenotype3, phenotype4));

        Individual ind1 = new Individual().setId("ind1").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype1));
        Individual ind2 = new Individual().setId("ind2").setSex(Individual.Sex.MALE);
        Individual ind3 = new Individual().setId("ind3").setSex(Individual.Sex.MALE);
        Individual ind4 = new Individual().setId("ind4").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype1));
        Individual ind5 = new Individual().setId("ind5").setSex(Individual.Sex.MALE)
                .setMother(ind1).setFather(ind2);
        Individual ind6 = new Individual().setId("ind6").setSex(Individual.Sex.FEMALE)
                .setMother(ind1).setFather(ind2);
        Individual ind7 = new Individual().setId("ind7").setSex(Individual.Sex.MALE)
                .setPhenotypes(Collections.singletonList(phenotype1))
                .setMother(ind4).setFather(ind3);
        Individual ind8 = new Individual().setId("ind8").setSex(Individual.Sex.MALE)
                .setMother(ind4).setFather(ind3);
        Individual ind9 = new Individual().setId("ind9").setSex(Individual.Sex.FEMALE);
        Individual ind10 = new Individual().setId("ind10").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype1));
        Individual ind11 = new Individual().setId("ind11").setSex(Individual.Sex.MALE)
                .setPhenotypes(Collections.singletonList(phenotype1))
                .setMother(ind6).setFather(ind7);
        Individual ind12 = new Individual().setId("ind12").setSex(Individual.Sex.FEMALE)
                .setMother(ind6).setFather(ind7);
        Individual ind13 = new Individual().setId("ind13").setSex(Individual.Sex.MALE)
                .setMother(ind6).setFather(ind7);
        Individual ind14 = new Individual().setId("ind14").setSex(Individual.Sex.FEMALE)
                .setMother(ind9).setFather(ind8);
        Individual ind15 = new Individual().setId("ind15").setSex(Individual.Sex.MALE)
                .setPhenotypes(Collections.singletonList(phenotype1))
                .setMother(ind9).setFather(ind8);
        Individual ind16 = new Individual().setId("ind16").setSex(Individual.Sex.FEMALE)
                .setPhenotypes(Collections.singletonList(phenotype1))
                .setMother(ind10).setFather(ind11);
        Individual ind17 = new Individual().setId("ind17").setSex(Individual.Sex.MALE)
                .setMother(ind10).setFather(ind11);
        Individual ind18 = new Individual().setId("ind18").setSex(Individual.Sex.MALE)
                .setPhenotypes(Collections.singletonList(phenotype1));
        family2 = new Pedigree()
                .setMembers(Arrays.asList(ind1, ind2, ind3, ind4, ind5, ind6, ind7, ind8, ind9, ind10, ind11, ind12, ind13, ind14, ind15,
                        ind16, ind17, ind18))
                .setPhenotypes(Arrays.asList(phenotype1));
    }

    @Test
    public void dominant() {
        Map<String, List<String>> genotypes = ModeOfInheritance.dominant(family1, phenotype1, false);
        assertTrue("son not 0/1 -> " + genotypes.get("son"), Arrays.asList("0/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0 -> " + genotypes.get("daughter"), Arrays.asList("0/0").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"), Arrays.asList("0/0").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"), Arrays.asList("0/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, phenotype2, false);
        assertTrue("son not 0/0 -> " + genotypes.get("son"), Arrays.asList("0/0").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"), Arrays.asList("0/1").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"), Arrays.asList("0/1").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/0 -> " + genotypes.get("father"), Arrays.asList("0/0").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, phenotype3, false);
        assertTrue("son not 0/0 -> " + genotypes.get("son"), Arrays.asList("0/0").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0 -> " + genotypes.get("daughter"), Arrays.asList("0/0").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"), Arrays.asList("0/0").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"), Arrays.asList("0/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, phenotype4, false);
        // TODO: Check, this case is impossible. Son cannot be affected by a dominant disease if none of the parents have the disease.
        // TODO: Should the method raise an error stating that it is an impossible situation?
        assertTrue("son not 0/1 -> " + genotypes.get("son"), Arrays.asList("0/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0 -> " + genotypes.get("daughter"), Arrays.asList("0/0").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"), Arrays.asList("0/0").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/0 -> " + genotypes.get("father"), Arrays.asList("0/0").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family2, phenotype1, false);
        assertTrue("ind1 not 0/1 -> " + genotypes.get("ind1"), Arrays.asList("0/1").containsAll(genotypes.get("ind1")));
        assertTrue("ind2 not 0/0 -> " + genotypes.get("ind2"), Arrays.asList("0/0").containsAll(genotypes.get("ind2")));
        assertTrue("ind3 not 0/0 -> " + genotypes.get("ind3"), Arrays.asList("0/0").containsAll(genotypes.get("ind3")));
        assertTrue("ind4 not 0/1 -> " + genotypes.get("ind4"), Arrays.asList("0/1").containsAll(genotypes.get("ind4")));
        assertTrue("ind5 not 0/0 -> " + genotypes.get("ind5"), Arrays.asList("0/0").containsAll(genotypes.get("ind5")));
        assertTrue("ind6 not 0/0 -> " + genotypes.get("ind6"), Arrays.asList("0/0").containsAll(genotypes.get("ind6")));
        assertTrue("ind7 not 0/1 -> " + genotypes.get("ind7"), Arrays.asList("0/1").containsAll(genotypes.get("ind7")));
        assertTrue("ind8 not 0/0 -> " + genotypes.get("ind8"), Arrays.asList("0/0").containsAll(genotypes.get("ind8")));
        assertTrue("ind9 not 0/0 -> " + genotypes.get("ind9"), Arrays.asList("0/0").containsAll(genotypes.get("ind9")));
        assertTrue("ind10 not 0/1 -> " + genotypes.get("ind10"), Arrays.asList("0/1").containsAll(genotypes.get("ind10")));
        assertTrue("ind11 not 0/1 -> " + genotypes.get("ind11"), Arrays.asList("0/1").containsAll(genotypes.get("ind11")));
        assertTrue("ind12 not 0/0 -> " + genotypes.get("ind12"), Arrays.asList("0/0").containsAll(genotypes.get("ind12")));
        assertTrue("ind13 not 0/0 -> " + genotypes.get("ind13"), Arrays.asList("0/0").containsAll(genotypes.get("ind13")));
        assertTrue("ind14 not 0/0 -> " + genotypes.get("ind14"), Arrays.asList("0/0").containsAll(genotypes.get("ind14")));
        assertTrue("ind15 not 0/1 -> " + genotypes.get("ind15"), Arrays.asList("0/1").containsAll(genotypes.get("ind15")));
        assertTrue("ind16 not 0/1, 1/1 -> " + genotypes.get("ind16"), Arrays.asList("0/1", "1/1").containsAll(genotypes.get("ind16")));
        assertTrue("ind17 not 0/0 -> " + genotypes.get("ind17"), Arrays.asList("0/0").containsAll(genotypes.get("ind17")));
        assertTrue("ind18 not 0/1 1/1-> " + genotypes.get("ind18"), Arrays.asList("0/1", "1/1").containsAll(genotypes.get("ind18")));
    }

    @Test
    public void recessive() {
        Map<String, List<String>> genotypes = ModeOfInheritance.recessive(family1, phenotype1, false);
        assertTrue("son not 1/1 -> " + genotypes.get("son"), Arrays.asList("1/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"), Arrays.asList("0/1").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"), Arrays.asList("0/1").containsAll(genotypes.get("mother")));
        assertTrue("father not 1/1 -> " + genotypes.get("father"), Arrays.asList("1/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, phenotype2, false);
        assertTrue("son not 0/1 -> " + genotypes.get("son"), Arrays.asList("0/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 1/1 -> " + genotypes.get("daughter"), Arrays.asList("1/1").containsAll(genotypes.get("daughter")));
        assertTrue("mother not 1/1 -> " + genotypes.get("mother"), Arrays.asList("1/1").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"), Arrays.asList("0/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, phenotype3, false);
        assertTrue("son not 0/0, 0/1 -> " + genotypes.get("son"), Arrays.asList("0/0", "0/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), Arrays.asList("0/0", "0/1")
                .containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0, 0/1 -> " + genotypes.get("mother"), Arrays.asList("0/0", "0/1").containsAll(genotypes.get("mother")));
        assertTrue("father not 1/1 -> " + genotypes.get("father"), Arrays.asList("1/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, phenotype4, false);
        assertTrue("son not 1/1 -> " + genotypes.get("son"), Arrays.asList("1/1").containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), Arrays.asList("0/0", "0/1")
                .containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"), Arrays.asList("0/1").containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"), Arrays.asList("0/1").containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family2, phenotype1, false);
        assertTrue("ind1 not 1/1 -> " + genotypes.get("ind1"), Arrays.asList("1/1").containsAll(genotypes.get("ind1")));
        assertTrue("ind2 not 0/0, 0/1 -> " + genotypes.get("ind2"), Arrays.asList("0/0", "0/1").containsAll(genotypes.get("ind2")));
        assertTrue("ind3 not 0/1 -> " + genotypes.get("ind3"), Arrays.asList("0/1").containsAll(genotypes.get("ind3")));
        assertTrue("ind4 not 1/1 -> " + genotypes.get("ind4"), Arrays.asList("1/1").containsAll(genotypes.get("ind4")));
        assertTrue("ind5 not 0/1 -> " + genotypes.get("ind5"), Arrays.asList("0/1").containsAll(genotypes.get("ind5")));
        assertTrue("ind6 not 0/1 -> " + genotypes.get("ind6"), Arrays.asList("0/1").containsAll(genotypes.get("ind6")));
        assertTrue("ind7 not 1/1 -> " + genotypes.get("ind7"), Arrays.asList("1/1").containsAll(genotypes.get("ind7")));
        assertTrue("ind8 not 0/1 -> " + genotypes.get("ind8"), Arrays.asList("0/1").containsAll(genotypes.get("ind8")));
        assertTrue("ind9 not 0/1 -> " + genotypes.get("ind9"), Arrays.asList("0/1").containsAll(genotypes.get("ind9")));
        assertTrue("ind10 not 1/1 -> " + genotypes.get("ind10"), Arrays.asList("1/1").containsAll(genotypes.get("ind10")));
        assertTrue("ind11 not 1/1 -> " + genotypes.get("ind11"), Arrays.asList("1/1").containsAll(genotypes.get("ind11")));
        assertTrue("ind12 not 0/1 -> " + genotypes.get("ind12"), Arrays.asList("0/1").containsAll(genotypes.get("ind12")));
        assertTrue("ind13 not 0/1 -> " + genotypes.get("ind13"), Arrays.asList("0/1").containsAll(genotypes.get("ind13")));
        assertTrue("ind14 not 0/0 0/1 -> " + genotypes.get("ind14"), Arrays.asList("0/0", "0/1").containsAll(genotypes.get("ind14")));
        assertTrue("ind15 not 1/1 -> " + genotypes.get("ind15"), Arrays.asList("1/1").containsAll(genotypes.get("ind15")));
        assertTrue("ind16 not 1/1 -> " + genotypes.get("ind16"), Arrays.asList("1/1").containsAll(genotypes.get("ind16")));
        assertTrue("ind18 not 1/1-> " + genotypes.get("ind18"), Arrays.asList("1/1").containsAll(genotypes.get("ind18")));
        // Impossible case: Ind 17 should also be affected
        assertTrue("ind17 not 0/1 -> " + genotypes.get("ind17"), Arrays.asList("0/1").containsAll(genotypes.get("ind17")));
    }
}