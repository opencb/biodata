package org.opencb.biodata.tools.pedigree;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.commons.Phenotype;
import org.opencb.biodata.models.core.pedigree.Individual;
import org.opencb.biodata.models.core.pedigree.Pedigree;
import org.opencb.biodata.models.core.pedigree.PedigreeManager;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.VariantTestUtils;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;
import org.opencb.commons.utils.ListUtils;

import java.util.*;

import static org.junit.Assert.*;

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

        father = new Individual().setId("NA12877").setSex(Individual.Sex.MALE);
        mother = new Individual().setId("NA12878").setSex(Individual.Sex.FEMALE);
        daughter = new Individual().setId("NA12879").setSex(Individual.Sex.FEMALE)
                .setMother(mother).setFather(father);
        family3 = new Pedigree()
                .setMembers(Arrays.asList(father, mother, daughter))
                .setProband(daughter);
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

    @Test
    public void deNovoTest() {
        PedigreeManager pedigreeManager = new PedigreeManager(family3);
        List<Variant> variantList = Arrays.asList(
                // De novo variants will contain the id 1:10:ATT
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),

                // Not de novo will contain the id 2:10:ATT
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );

        List<Variant> variants = ModeOfInheritance.deNovoVariants(pedigreeManager.getWithoutChildren().get(0), variantList.iterator());
        assertEquals(12, variants.size());
        for (Variant variant : variants) {
            assertEquals("1:10:ATT:-", variant.toString());
        }

//        variants = ModeOfInheritance.deNovoVariants(pedigreeManager.getWithoutChildren().get(0), read("brca2-variants.vcf"));
//        for (Variant variant : variants) {
//            System.out.println(variant + ": " + variant.getStudies().get(0).getSampleData("NA12877", "GT") + " - "
//                    + variant.getStudies().get(0).getSampleData("NA12878", "GT") + " - "
//                    + variant.getStudies().get(0).getSampleData("NA12879", "GT"));
//        }
    }

    @Test
    public void compoundHeterozygousTest() throws Exception {
        List<Variant> variantList = Arrays.asList(
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );

        List<Variant> variants = ModeOfInheritance.compoundHeterozygosity(family3, variantList.iterator());
        assertTrue(variants.isEmpty());

        variantList = Arrays.asList(
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );
        variants = ModeOfInheritance.compoundHeterozygosity(family3, variantList.iterator());
        assertTrue(variants.isEmpty());

        variantList = Arrays.asList(
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );
        variants = ModeOfInheritance.compoundHeterozygosity(family3, variantList.iterator());
        assertTrue(variants.isEmpty());

        variantList = Arrays.asList(
                // Compound heterozygous will have the id 2:20:ATT:
                VariantTestUtils.generateVariant("2:20:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:20:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );
        variants = ModeOfInheritance.compoundHeterozygosity(family3, variantList.iterator());
        assertEquals(2, variants.size());

        for (Variant variant : variants) {
            assertEquals("2:20:ATT:-", variant.toString());
        }

//        for (Variant variant : variants) {
//            System.out.println(variant + ": " + variant.getStudies().get(0).getSampleData("NA12877", "GT") + " - "
//                    + variant.getStudies().get(0).getSampleData("NA12878", "GT") + " - "
//                    + variant.getStudies().get(0).getSampleData("NA12879", "GT"));
//        }
    }

    public Iterator<Variant> read(String file) {
        VariantStudyMetadata metadata = new VariantFileMetadata(file, file).toVariantStudyMetadata("study");
        VariantVcfHtsjdkReader reader = new VariantVcfHtsjdkReader(getClass().getResourceAsStream("/" + file), metadata);
        reader.open();
        reader.pre();

        List<Variant> read = reader.read(100000);

        reader.post();
        reader.close();

        return read.iterator();
    }
}