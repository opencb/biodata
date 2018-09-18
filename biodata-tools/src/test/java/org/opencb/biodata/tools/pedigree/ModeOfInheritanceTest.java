package org.opencb.biodata.tools.pedigree;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.commons.utils.ListUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

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
        assertEquals("son not 0/1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("son")).size());
        assertEquals("daughter not 0/0 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/0 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("mother")).size());
        assertEquals("father not 0/1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.dominant(family1, phenotype2, false);
        assertEquals("son not 0/0 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("son")).size());
        assertEquals("daughter not 0/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/1 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 0/0 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.dominant(family1, phenotype3, false);
        assertEquals("son not 0/0 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("son")).size());
        assertEquals("daughter not 0/0 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/0 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/0"),
                genotypes.get("mother")).size());
        assertEquals("father not 0/1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.dominant(family1, phenotype4, false);
        // This case is impossible. Son cannot be affected by a dominant disease if none of the parents have the disease.
        assertNull("At least one of the parents should be affected if the son is affected ", genotypes);

        genotypes = ModeOfInheritance.dominant(family2, phenotype1, false);
        assertNull("At least one of the parents should be affected if the son is affected ", genotypes);
    }

    @Test
    public void recessive() {
        Map<String, List<String>> genotypes = ModeOfInheritance.recessive(family1, phenotype1, false);
        assertEquals("son not 1/1 -> " + genotypes.get("son"), 1,
                ListUtils.intersection(Arrays.asList("1/1"), genotypes.get("son")).size());
        assertEquals("daughter not 0/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/1 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 1/1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("1/1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.recessive(family1, phenotype2, false);
        assertEquals("son not 0/1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0/1"), genotypes.get("son")).size());
        assertEquals("daughter not 1/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("1/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 1/1 -> " + genotypes.get("mother"), 1,
                ListUtils.intersection(Arrays.asList("1/1"), genotypes.get("mother")).size());
        assertEquals("father not 0/1 -> " + genotypes.get("father"), 1,
                ListUtils.intersection(Arrays.asList("0/1"), genotypes.get("father")).size());

        genotypes = ModeOfInheritance.recessive(family1, phenotype3, false);
        assertEquals("son not 0/1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("son")).size());
        assertEquals("daughter not 0/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/0, 0/1 -> " + genotypes.get("mother"), 2,
                ListUtils.intersection(Arrays.asList("0/0", "0/1"), genotypes.get("mother")).size());
        assertEquals("father not 1/1 -> " + genotypes.get("father"), 1,
                ListUtils.intersection(Arrays.asList("1/1"), genotypes.get("father")).size());

        genotypes = ModeOfInheritance.recessive(family1, phenotype4, false);
        assertEquals("son not 1/1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("1/1"),
                genotypes.get("son")).size());
        assertEquals("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), 2, ListUtils.intersection(Arrays.asList("0/0", "0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/1 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 0/1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.recessive(family2, phenotype1, false);
        assertNull("Individual 17 should be affected if both parents are affected", genotypes);
    }

    @Test
    public void xLinkedTest() {
        Map<String, List<String>> genotypes = ModeOfInheritance.xLinked(family1, phenotype1, false);
        assertEquals("son not 1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("1"), genotypes.get("son")).size());
        assertEquals("daughter not 0/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/1 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.xLinked(family1, phenotype2, false);
        assertEquals("Son should also be affected to follow the xLinked moi", null, genotypes);

        genotypes = ModeOfInheritance.xLinked(family1, phenotype3, false);
        assertEquals("son not 0 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("0"), genotypes.get("son")).size());
        assertEquals("daughter not 0/1 -> " + genotypes.get("daughter"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/0, 0/1 -> " + genotypes.get("mother"), 2, ListUtils.intersection(Arrays.asList("0/0", "0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("1"), genotypes.get("father"))
                .size());

        genotypes = ModeOfInheritance.xLinked(family1, phenotype4, false);
        assertEquals("son not 1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("1"), genotypes.get("son")).size());
        assertEquals("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), 2, ListUtils.intersection(Arrays.asList("0/0", "0/1"),
                genotypes.get("daughter")).size());
        assertEquals("mother not 0/1 -> " + genotypes.get("mother"), 1, ListUtils.intersection(Arrays.asList("0/1"),
                genotypes.get("mother")).size());
        assertEquals("father not 0 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("0"), genotypes.get("father")).size());

        genotypes = ModeOfInheritance.xLinked(family2, phenotype1, false);
        assertEquals("Some cases where the mother is affected and their sons are not. That doesn't follow the xLinked moi", null, genotypes);
    }

    @Test
    public void yLinkedTest() {
        Map<String, List<String>> genotypes = ModeOfInheritance.yLinked(family1, phenotype1);
        assertEquals("son not 1 -> " + genotypes.get("son"), 1, ListUtils.intersection(Arrays.asList("1"), genotypes.get("son")).size());
        assertEquals("daughter not [] -> " + genotypes.get("daughter"), 0, genotypes.get("daughter").size());
        assertEquals("mother not [] -> " + genotypes.get("mother"), 0, genotypes.get("mother").size());
        assertEquals("father not 1 -> " + genotypes.get("father"), 1, ListUtils.intersection(Arrays.asList("1"),
                genotypes.get("father")).size());

        genotypes = ModeOfInheritance.yLinked(family1, phenotype2);
        assertEquals("Girls cannot be affected in a Y-linked moi", null, genotypes);

        genotypes = ModeOfInheritance.yLinked(family1, phenotype3);
        assertEquals("If the father is affected, the son should also be affected", null, genotypes);

        genotypes = ModeOfInheritance.yLinked(family1, phenotype4);
        assertEquals("If the son is affected, the father should also be affected", null, genotypes);

        genotypes = ModeOfInheritance.yLinked(family2, phenotype1);
        assertEquals("Some cases where the father is affected and sons are not. That doesn't follow the xLinked moi", null, genotypes);
    }
}