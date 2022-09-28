package org.opencb.biodata.tools.pedigree;

import org.junit.Before;
import org.junit.Test;
import org.opencb.biodata.models.clinical.Disorder;
import org.opencb.biodata.models.clinical.pedigree.Member;
import org.opencb.biodata.models.clinical.pedigree.Pedigree;
import org.opencb.biodata.models.core.SexOntologyTermAnnotation;
import org.opencb.biodata.models.pedigree.IndividualProperty;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.VariantTestUtils;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.VariantVcfHtsjdkReader;
import org.opencb.commons.utils.ListUtils;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.opencb.biodata.models.clinical.ClinicalProperty.Penetrance;
import static org.opencb.biodata.tools.pedigree.ModeOfInheritance.*;

public class ModeOfInheritanceTest {

    Pedigree family1;
    Pedigree family2;
    Pedigree family3;

    // Incomplete family with proband and mother only
    Pedigree family4;

    Disorder disorder1;
    Disorder disorder2;
    Disorder disorder3;
    Disorder disorder4;

    @Before
    public void before() {
        disorder1 = new Disorder("disease1", "disease1", "", "", Collections.emptyList(), Collections.emptyMap());
        disorder2 = new Disorder("disease2", "disease2", "", "", Collections.emptyList(), Collections.emptyMap());
        disorder3 = new Disorder("disease3", "disease2", "", "", Collections.emptyList(), Collections.emptyMap());
        disorder4 = new Disorder("disease4", "disease2", "", "", Collections.emptyList(), Collections.emptyMap());

        Member father = new Member().setId("father").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Arrays.asList(disorder1, disorder3));
        Member mother = new Member().setId("mother").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder2));
        Member daughter = new Member().setId("daughter").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder2))
                .setMother(mother).setFather(father);
        Member son = new Member().setId("son").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Arrays.asList(disorder1, disorder4))
                .setMother(mother).setFather(father);
        family1 = new Pedigree()
                .setMembers(Arrays.asList(father, mother, daughter, son))
                .setDisorders(Arrays.asList(disorder1, disorder2, disorder3, disorder4));

        Member ind1 = new Member().setId("ind1").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder1));
        Member ind2 = new Member().setId("ind2").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()));
        Member ind3 = new Member().setId("ind3").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()));
        Member ind4 = new Member().setId("ind4").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder1));
        Member ind5 = new Member().setId("ind5").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setMother(ind1).setFather(ind2);
        Member ind6 = new Member().setId("ind6").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setMother(ind1).setFather(ind2);
        Member ind7 = new Member().setId("ind7").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Collections.singletonList(disorder1))
                .setMother(ind4).setFather(ind3);
        Member ind8 = new Member().setId("ind8").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setMother(ind4).setFather(ind3);
        Member ind9 = new Member().setId("ind9").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()));
        Member ind10 = new Member().setId("ind10").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder1));
        Member ind11 = new Member().setId("ind11").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Collections.singletonList(disorder1))
                .setMother(ind6).setFather(ind7);
        Member ind12 = new Member().setId("ind12").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setMother(ind6).setFather(ind7);
        Member ind13 = new Member().setId("ind13").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setMother(ind6).setFather(ind7);
        Member ind14 = new Member().setId("ind14").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setMother(ind9).setFather(ind8);
        Member ind15 = new Member().setId("ind15").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Collections.singletonList(disorder1))
                .setMother(ind9).setFather(ind8);
        Member ind16 = new Member().setId("ind16").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setDisorders(Collections.singletonList(disorder1))
                .setMother(ind10).setFather(ind11);
        Member ind17 = new Member().setId("ind17").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setMother(ind10).setFather(ind11);
        Member ind18 = new Member().setId("ind18").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()))
                .setDisorders(Collections.singletonList(disorder1));
        family2 = new Pedigree()
                .setMembers(Arrays.asList(ind1, ind2, ind3, ind4, ind5, ind6, ind7, ind8, ind9, ind10, ind11, ind12, ind13, ind14, ind15,
                        ind16, ind17, ind18))
                .setDisorders(Arrays.asList(disorder1));

        father = new Member().setId("NA12877").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.MALE.name()));
        mother = new Member().setId("NA12878").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()));
        daughter = new Member().setId("NA12879").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                .setMother(mother).setFather(father);
        family3 = new Pedigree()
                .setMembers(Arrays.asList(father, mother, daughter))
                .setProband(daughter);

        mother = new Member().setId("mother").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()));
        daughter =
                new Member().setId("daughter").setSex(new SexOntologyTermAnnotation().setId(IndividualProperty.Sex.FEMALE.name()))
                        .setDisorders(Collections.singletonList(disorder1))
                        .setMother(mother);
        family4 = new Pedigree()
                .setMembers(Arrays.asList(mother, daughter))
                .setProband(daughter)
                .setDisorders(Collections.singletonList(disorder1));
    }

    @Test
    public void dominant() {
        Map<String, List<String>> genotypes = ModeOfInheritance.dominant(family1, disorder1, Penetrance.COMPLETE);
        assertTrue("son not 0/1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, disorder2, Penetrance.COMPLETE);
        assertTrue("son not 0/0 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 0/0 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, disorder3, Penetrance.COMPLETE);
        assertTrue("son not 0/0 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/0 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.dominant(family1, disorder4, Penetrance.COMPLETE);
        // This case is impossible. Son cannot be affected by a dominant disease if none of the parents have the disease.
        assertTrue("At least one of the parents should be affected if the son is affected ", genotypes.size() == 4
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.dominant(family2, disorder1, Penetrance.COMPLETE);
        assertTrue("At least one of the parents should be affected if the son is affected ", genotypes.size() == 18
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void recessive() {
        Map<String, List<String>> genotypes = ModeOfInheritance.recessive(family1, disorder1, Penetrance.COMPLETE);
        assertTrue("son not 1/1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 1/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, disorder2, Penetrance.COMPLETE);
        assertTrue("son not 0/1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("son")));
        assertTrue("daughter not 1/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 1/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, disorder3, Penetrance.COMPLETE);
        assertTrue("son not 0/1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("daughter")));
        assertEquals("mother not 0/0, 0/1 -> " + genotypes.get("mother"), 2,
                ListUtils.intersection(
                        ListUtils.union(ModeOfInheritance.toGenotypeString(GENOTYPE_0_0), ModeOfInheritance.toGenotypeString(GENOTYPE_0_1)),
                        genotypes.get("mother")).size());
        assertTrue("father not 1/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family1, disorder4, Penetrance.COMPLETE);
        assertTrue("son not 1/1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1_1).containsAll(genotypes.get("son")));
        assertEquals("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), 2,
                ListUtils.intersection(
                        ListUtils.union(ModeOfInheritance.toGenotypeString(GENOTYPE_0_0), ModeOfInheritance.toGenotypeString(GENOTYPE_0_1)),
                        genotypes.get("daughter")).size());
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 0/1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.recessive(family2, disorder1, Penetrance.COMPLETE);
        assertTrue("Individual 17 should be affected if both parents are affected", genotypes.size() == 18
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void xLinkedTest() {
        Map<String, List<String>> genotypes = ModeOfInheritance.xLinked(family1, disorder1, false, Penetrance.COMPLETE);
        assertTrue("son not 1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("daughter")));
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.xLinked(family1, disorder2, false, Penetrance.COMPLETE);
        assertTrue("Son should also be affected to follow the xLinked moi", genotypes.size() == 4
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.xLinked(family1, disorder3, false, Penetrance.COMPLETE);
        assertTrue("son not 0 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0).containsAll(genotypes.get("son")));
        assertTrue("daughter not 0/1 -> " + genotypes.get("daughter"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("daughter")));
        assertEquals("mother not 0/0, 0/1 -> " + genotypes.get("mother"), 2,
                ListUtils.intersection(
                        ListUtils.union(ModeOfInheritance.toGenotypeString(GENOTYPE_0_0), ModeOfInheritance.toGenotypeString(GENOTYPE_0_1)),
                        genotypes.get("mother")).size());
        assertTrue("father not 1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("father")) );

        genotypes = ModeOfInheritance.xLinked(family1, disorder4, false, Penetrance.COMPLETE);
        assertTrue("son not 1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("son")));
        assertEquals("daughter not 0/0, 0/1 -> " + genotypes.get("daughter"), 2,
                ListUtils.intersection(
                        ListUtils.union(ModeOfInheritance.toGenotypeString(GENOTYPE_0_0), ModeOfInheritance.toGenotypeString(GENOTYPE_0_1)),
                        genotypes.get("daughter")).size());
        assertTrue("mother not 0/1 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_1).containsAll(genotypes.get("mother")));
        assertTrue("father not 0 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.xLinked(family2, disorder1, false, Penetrance.COMPLETE);
        assertTrue("Some cases where the mother is affected and their sons are not. That doesn't follow the xLinked moi",
                genotypes.size() == 18 && genotypes.entrySet().stream()
                        .filter(entry -> ListUtils.isNotEmpty(entry.getValue())).collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.xLinked(family4, disorder1, true, Penetrance.COMPLETE);
        assertEquals("daughter not 0/1, 0|1, 1|0 -> " + genotypes.get("daughter"), 3,
                ListUtils.intersection(ModeOfInheritance.toGenotypeString(GENOTYPE_0_1), genotypes.get("daughter")).size());
        assertTrue("mother not 0/0 -> " + genotypes.get("mother"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_0_0).containsAll(genotypes.get("mother")));
    }

    @Test
    public void yLinkedTest() {
        Map<String, List<String>> genotypes = ModeOfInheritance.yLinked(family1, disorder1, Penetrance.COMPLETE);
        assertTrue("son not 1 -> " + genotypes.get("son"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("son")));
        assertEquals("daughter not [] -> " + genotypes.get("daughter"), 0, genotypes.get("daughter").size());
        assertEquals("mother not [] -> " + genotypes.get("mother"), 0, genotypes.get("mother").size());
        assertTrue("father not 1 -> " + genotypes.get("father"),
                ModeOfInheritance.toGenotypeString(GENOTYPE_1).containsAll(genotypes.get("father")));

        genotypes = ModeOfInheritance.yLinked(family1, disorder2, Penetrance.COMPLETE);
        assertTrue("Girls cannot be affected in a Y-linked moi", genotypes.size() == 4 && genotypes.entrySet().stream()
                .filter(entry -> ListUtils.isNotEmpty(entry.getValue())).collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.yLinked(family1, disorder3, Penetrance.COMPLETE);
        assertTrue("If the father is affected, the son should also be affected", genotypes.size() == 4
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.yLinked(family1, disorder4, Penetrance.COMPLETE);
        assertTrue("If the son is affected, the father should also be affected", genotypes.size() == 4
                && genotypes.entrySet().stream().filter(entry -> ListUtils.isNotEmpty(entry.getValue()))
                .collect(Collectors.toList()).isEmpty());

        genotypes = ModeOfInheritance.yLinked(family2, disorder1, Penetrance.COMPLETE);
        assertTrue("Some cases where the father is affected and sons are not. That doesn't follow the xLinked moi",
                genotypes.size() == 18  && genotypes.entrySet().stream()
                        .filter(entry -> ListUtils.isNotEmpty(entry.getValue())).collect(Collectors.toList()).isEmpty());
    }

    @Test
    public void deNovoTest() {
        List<Variant> variantList = Arrays.asList(
                // De novo variants will contain the id 1:10:ATT
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("1:10:ATT:", "NA12877", "0/0", "NA12878", "0/0", "NA12879", "1/1"),

                // Other mendelian errors
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "1/1", "NA12878", "0/0", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "1/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("3:10:ATT:", "NA12877", "1/1", "NA12878", "1/1", "NA12879", "0/0"),

                // Not de novo will contain the id 2:10:ATT
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "1/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/0", "NA12878", "1/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/0"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "0/1"),
                VariantTestUtils.generateVariant("2:10:ATT:", "NA12877", "0/1", "NA12878", "0/1", "NA12879", "1/1")
        );

        List<Variant> variants = ModeOfInheritance.deNovo(variantList.iterator(), 2, 1, 0);
        assertEquals(6, variants.size());
        for (Variant variant : variants) {
            assertEquals("1:10:ATT:-", variant.toString());
        }
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