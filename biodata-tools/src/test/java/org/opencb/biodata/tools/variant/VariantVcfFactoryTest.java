/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.tools.variant;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.opencb.biodata.formats.variant.VariantFactory;
import org.opencb.biodata.formats.variant.vcf4.VariantVcfFactory;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;

import java.util.*;

import static org.junit.Assert.assertEquals;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Jose Miguel Mut Lopez &lt;jmmut@ebi.ac.uk&gt;
 */
public class VariantVcfFactoryTest {

    private VariantFileMetadata fileMetadata = new VariantFileMetadata("filename.vcf", "fileId");
    private VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("studyId");
    private VariantFactory factory = new VariantVcfFactory();
    private VariantNormalizer normalizer = new VariantNormalizer();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003");
        fileMetadata.setSampleIds(sampleNames);
    }

    @Test
    public void testCreateVariantFromVcfSameLengthRefAlt() {
        // Test when there are differences at the end of the sequence
        String line = "1\t1000\t.\tTCACCC\tTGACGG\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1005, "CACCC", "GACGG"));

        List<Variant> result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        // Test when there are not differences at the end of the sequence
        line = "1\t1000\t.\tTCACCC\tTGACGC\t.\t.\t.";

        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1004, "CACC", "GACG"));

        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfInsertionEmptyRef() {
        String line = "1\t1000\t.\t.\tTGACGC\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000 - 1, "", "TGACGC"));

        List<Variant> result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfEmptyAlt() {
        String line = "1\t1000\trs123\tT\t.\t.\t.\t.";
        List<Variant> variants = createAndNormalize(line);
        assertEquals(1, variants.size());
        variants.get(0).getType().equals(VariantType.NO_VARIATION);
    }

    @Test
    public void testCreateVariantFromVcfIndelNotEmptyFields() {
        String line = "1\t1000\t.\tCGATT\tTAC\t.\t.\t.";

        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000 + "CGATT".length() - 1, "CGATT", "TAC"));
        List<Variant> result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tAT\tA\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1001, "T", ""));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\t.\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 999, "", "ATC"));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tA\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1000, "", "TC"));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tAC\tACT\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1002, 1001, "", "T"));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        // Printing those that are not currently managed
        line = "1\t1000\t.\tAT\tT\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000, "A", ""));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tATC\tTC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1000, "A", ""));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tATC\tAC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1001, "T", ""));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tAC\tATC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1001, 1000, "", "T"));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);

        line = "1\t1000\t.\tATC\tGC\t.\t.\t.";
        expResult = new LinkedList<>();
        expResult.add(new Variant("1", 1000, 1001, "AT", "G"));
        result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariantFromVcfCoLocatedVariants_MainFields() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        fileMetadata.setSampleIds(sampleNames);

        String line = "1\t10040\t.\tTGACGTAACGATT\tT,TGACGTAACGGTT,TGACGTAATAC\t.\t.\t.\tGT\t0/0\t0/1\t0/2\t1/2"; // 4 samples

        // Check proper conversion of main fields
        List<Variant> expResult = new LinkedList<>();
        expResult.add(new Variant("1", 10040, 10040 + "TGACGTAACGAT".length() - 1, "TGACGTAACGAT", ""));
        expResult.add(new Variant("1", 10048, 10048 + "CGATT".length() - 1, "CGATT", "TAC"));
        expResult.add(new Variant("1", 10050, 10050 + "A".length() - 1, "A", "G"));

        List<Variant> result = createAndNormalize(line);
        result.stream().forEach(variant -> variant.setStudies(Collections.<StudyEntry>emptyList()));
        assertEquals(expResult, result);
    }

    @Test
    public void testCreateVariant_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005");
        fileMetadata.setSampleIds(sampleNames);

        String line = "1\t10040\trs123\tT\tC\t.\t.\t.\tGT\t0/0\t0/1\t0/.\t./1\t1/1"; // 5 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 10041, 10041 + "C".length() - 1, "T", "C");
        StudyEntry file0 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var0.addStudyEntry(file0);

        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "0/0");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "0/1");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "0/.");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "./1");
        Map<String, String> na005 = new HashMap<>();
        na005.put("GT", "1/1");

        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(4), na005);

        // Check proper conversion of samples
        List<Variant> result = createAndNormalize(line);
        assertEquals(1, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(var0.getStudy(metadata.getId()).getSamplesData(), getVar0.getStudy(metadata.getId()).getSamplesData());
    }

    @Test
    public void testCreateVariantFromVcfMultiallelicVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        fileMetadata.setSampleIds(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\t.\tGT:AD:DP:GQ:PL" +
                "\t0/1:10,5,0:17:94:94,0,286,4,5,6" +
                "\t0/2:0,1,8:15:43:222,0,43,4,5,6" +
                "\t0/0:.:18:.:." +
                "\t1/2:1,6,5:13:99:162,0,180,4,5,6"; // 4 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        StudyEntry file0 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var0.addStudyEntry(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        StudyEntry file1 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var1.addStudyEntry(file1);


        // Initialize expected samples in variant 1 (alt allele C)
        Map<String, String> na001_C = new HashMap<>();
        na001_C.put("GT", "0/1");
        na001_C.put("AD", "10,5,0");
        na001_C.put("DP", "17");
        na001_C.put("GQ", "94");
        na001_C.put("PL", "94,0,286,4,5,6");
        Map<String, String> na002_C = new HashMap<>();
        na002_C.put("GT", "0/2");
        na002_C.put("AD", "0,1,8");
        na002_C.put("DP", "15");
        na002_C.put("GQ", "43");
        na002_C.put("PL", "222,0,43,4,5,6");
        Map<String, String> na003_C = new HashMap<>();
        na003_C.put("GT", "0/0");
        na003_C.put("AD", ".");
        na003_C.put("DP", "18");
        na003_C.put("GQ", ".");
        na003_C.put("PL", ".");
        Map<String, String> na004_C = new HashMap<>();
        na004_C.put("GT", "1/2");
        na004_C.put("AD", "1,6,5");
        na004_C.put("DP", "13");
        na004_C.put("GQ", "99");
        na004_C.put("PL", "162,0,180,4,5,6");

        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004_C);

        // Initialize expected samples in variant 2 (alt allele G)
        Map<String, String> na001_G = new HashMap<>();
        na001_G.put("GT", "0/2");
        na001_G.put("AD", "10,0,5");
        na001_G.put("DP", "17");
        na001_G.put("GQ", "94");
        na001_G.put("PL", "94,4,6,0,5,286");
        Map<String, String> na002_G = new HashMap<>();
        na002_G.put("GT", "0/1");
        na002_G.put("AD", "0,8,1");
        na002_G.put("DP", "15");
        na002_G.put("GQ", "43");
        na002_G.put("PL", "222,4,6,0,5,43");
        Map<String, String> na003_G = new HashMap<>();
        na003_G.put("GT", "0/0");
        na003_G.put("AD", ".");
        na003_G.put("DP", "18");
        na003_G.put("GQ", ".");
        na003_G.put("PL", ".");
        Map<String, String> na004_G = new HashMap<>();
        na004_G.put("GT", "2/1");
        na004_G.put("AD", "1,5,6");
        na004_G.put("DP", "13");
        na004_G.put("GQ", "99");
        na004_G.put("PL", "162,4,6,0,5,180");
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001_G);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002_G);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003_G);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004_G);


        // Check proper conversion of samples and alternate alleles
        List<Variant> result = createAndNormalize(line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(
                var0.getStudy(metadata.getId()).getSamplesDataAsMap(),
                getVar0.getStudy(metadata.getId()).getSamplesDataAsMap());
        assertEquals(Collections.singletonList("G"), getVar0.getStudy(metadata.getId()).getSecondaryAlternatesAlleles());

        Variant getVar1 = result.get(1);
        assertEquals(
                var1.getStudy(metadata.getId()).getSamplesDataAsMap(),
                getVar1.getStudy(metadata.getId()).getSamplesDataAsMap());
        assertEquals(Collections.singletonList("C"), getVar1.getStudy(metadata.getId()).getSecondaryAlternatesAlleles());
    }

    @Test
    public void testCreateVariantFromVcfCoLocatedVariants_Samples() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004", "NA005", "NA006");
        fileMetadata.setSampleIds(sampleNames);
        String line = "1\t10040\trs123\tT\tC,GC\t.\t.\t.\tGT:GL" +
                "\t0/0:1,2,3,4,5,6" +
                "\t0/1:1,2,3,4,5,6" +
                "\t0/2:1,2,3,4,5,6" +
                "\t1/1:1,2,3,4,5,6" +
                "\t1/2:1,2,3,4,5,6" +
                "\t2/2:1,2,3,4,5,6"; // 6 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 10041, 10041 + "C".length() - 1, "T", "C");
        StudyEntry file0 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var0.addStudyEntry(file0);

        Variant var1 = new Variant("1", 10050, 10050 + "GC".length() - 1, "T", "GC");
        StudyEntry file1 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var1.addStudyEntry(file1);

        // Initialize expected samples in variant 1 (alt allele C)
        Map<String, String> na001_C = new HashMap<>();
        na001_C.put("GT", "0/0");
        na001_C.put("GL", "1,2,3,4,5,6");
        Map<String, String> na002_C = new HashMap<>();
        na002_C.put("GT", "0/1");
        na002_C.put("GL", "1,2,3,4,5,6");
        Map<String, String> na003_C = new HashMap<>();
        na003_C.put("GT", "0/2");
        na003_C.put("GL", "1,2,3,4,5,6");
        Map<String, String> na004_C = new HashMap<>();
        na004_C.put("GT", "1/1");
        na004_C.put("GL", "1,2,3,4,5,6");
        Map<String, String> na005_C = new HashMap<>();
        na005_C.put("GT", "1/2");
        na005_C.put("GL", "1,2,3,4,5,6");
        Map<String, String> na006_C = new HashMap<>();
        na006_C.put("GT", "2/2");
        na006_C.put("GL", "1,2,3,4,5,6");

        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(4), na005_C);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(5), na006_C);

        // TODO Initialize expected samples in variant 2 (alt allele GC)
        Map<String, String> na001_GC = new HashMap<>();
        na001_GC.put("GT", "0/0");
        na001_GC.put("GL", "1,4,6,2,5,3");
        Map<String, String> na002_GC = new HashMap<>();
        na002_GC.put("GT", "0/2");
        na002_GC.put("GL", "1,4,6,2,5,3");
        Map<String, String> na003_GC = new HashMap<>();
        na003_GC.put("GT", "0/1");
        na003_GC.put("GL", "1,4,6,2,5,3");
        Map<String, String> na004_GC = new HashMap<>();
        na004_GC.put("GT", "2/2");
        na004_GC.put("GL", "1,4,6,2,5,3");
        Map<String, String> na005_GC = new HashMap<>();
        na005_GC.put("GT", "2/1");
        na005_GC.put("GL", "1,4,6,2,5,3");
        Map<String, String> na006_GC = new HashMap<>();
        na006_GC.put("GT", "1/1");
        na006_GC.put("GL", "1,4,6,2,5,3");

        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001_GC);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002_GC);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003_GC);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004_GC);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(4), na005_GC);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(5), na006_GC);

        // Check proper conversion of samples
        List<Variant> result = createAndNormalize(line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        assertEquals(
                var0.getStudy(metadata.getId()).getSamplesDataAsMap(),
                getVar0.getStudy(metadata.getId()).getSamplesDataAsMap());
        assertEquals(Collections.singletonList("GC"), getVar0.getStudy(metadata.getId()).getSecondaryAlternatesAlleles());

        Variant getVar1 = result.get(1);
        assertEquals(
                var1.getStudy(metadata.getId()).getSamplesDataAsMap(),
                getVar1.getStudy(metadata.getId()).getSamplesDataAsMap());
        assertEquals(Collections.singletonList("C"), getVar1.getStudy(metadata.getId()).getSecondaryAlternatesAlleles());
    }

    @Test
    public void testCreateVariantWithMissingGenotypes() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        fileMetadata.setSampleIds(sampleNames);
        String line = "1\t1407616\t.\tC\tG\t43.74\tPASS\t.\tGT:AD:DP:GQ:PL\t./.:.:.:.:.\t1/1:0,2:2:6:71,6,0\t./.:.:.:.:.\t./.:.:.:.:.";

        // Initialize expected variants
        Variant var0 = new Variant("1", 1407616, 1407616, "C", "G");
        StudyEntry file0 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var0.addStudyEntry(file0);

        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "./.");
        na001.put("AD", ".");
        na001.put("DP", ".");
        na001.put("GQ", ".");
        na001.put("PL", ".");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "1/1");
        na002.put("AD", "0,2");
        na002.put("DP", "2");
        na002.put("GQ", "6");
        na002.put("PL", "71,6,0");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "./.");
        na003.put("AD", ".");
        na003.put("DP", ".");
        na003.put("GQ", ".");
        na003.put("PL", ".");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "./.");
        na004.put("AD", ".");
        na004.put("DP", ".");
        na004.put("GQ", ".");
        na004.put("PL", ".");

        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004);


        // Check proper conversion of samples
        List<Variant> result = createAndNormalize(line);
        assertEquals(1, result.size());

        Variant getVar0 = result.get(0);
        StudyEntry getFile0 = getVar0.getStudy(metadata.getId());

        Map<String, String> na001Data = getFile0.getSampleDataAsMap("NA001");
        assertEquals("./.", na001Data.get("GT"));
        assertEquals(".", na001Data.get("AD"));
        assertEquals(".", na001Data.get("DP"));
        assertEquals(".", na001Data.get("GQ"));
        assertEquals(".", na001Data.get("PL"));

        Map<String, String> na002Data = getFile0.getSampleDataAsMap("NA002");
        assertEquals("1/1", na002Data.get("GT"));
        assertEquals("0,2", na002Data.get("AD"));
        assertEquals("2", na002Data.get("DP"));
        assertEquals("6", na002Data.get("GQ"));
        assertEquals("71,6,0", na002Data.get("PL"));

        Map<String, String> na003Data = getFile0.getSampleDataAsMap("NA003");
        assertEquals("./.", na003Data.get("GT"));
        assertEquals(".", na003Data.get("AD"));
        assertEquals(".", na003Data.get("DP"));
        assertEquals(".", na003Data.get("GQ"));
        assertEquals(".", na003Data.get("PL"));

        Map<String, String> na004Data = getFile0.getSampleDataAsMap("NA004");
        assertEquals("./.", na004Data.get("GT"));
        assertEquals(".", na004Data.get("AD"));
        assertEquals(".", na004Data.get("DP"));
        assertEquals(".", na004Data.get("GQ"));
        assertEquals(".", na004Data.get("PL"));
    }

    @Test
    public void testParseInfo() {
        List<String> sampleNames = Arrays.asList("NA001", "NA002", "NA003", "NA004");
        fileMetadata.setSampleIds(sampleNames);
        String line ="1\t123456\t.\tT\tC,G\t110\tPASS\tAN=3;AC=1,2;AF=0.125,0.25;DP=63;NS=4;MQ=10685\tGT:AD:DP:GQ:PL\t"
                + "0/1:10,5:17:94:94,0,286\t0/2:3,8:15:43:222,0,43\t0/0:.:18:.:.\t0/2:7,6:13:0:162,0,180"; // 4 samples

        // Initialize expected variants
        Variant var0 = new Variant("1", 123456, 123456, "T", "C");
        StudyEntry file0 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var0.addStudyEntry(file0);

        Variant var1 = new Variant("1", 123456, 123456, "T", "G");
        StudyEntry file1 = new StudyEntry(fileMetadata.getId(), metadata.getId());
        var1.addStudyEntry(file1);


        // Initialize expected samples
        Map<String, String> na001 = new HashMap<>();
        na001.put("GT", "0/1");
        na001.put("AD", "10,5");
        na001.put("DP", "17");
        na001.put("GQ", "94");
        na001.put("PL", "94,0,286");
        Map<String, String> na002 = new HashMap<>();
        na002.put("GT", "0/1");
        na002.put("AD", "3,8");
        na002.put("DP", "15");
        na002.put("GQ", "43");
        na002.put("PL", "222,0,43");
        Map<String, String> na003 = new HashMap<>();
        na003.put("GT", "0/0");
        na003.put("AD", ".");
        na003.put("DP", "18");
        na003.put("GQ", ".");
        na003.put("PL", ".");
        Map<String, String> na004 = new HashMap<>();
        na004.put("GT", "0/1");
        na004.put("AD", "7,6");
        na004.put("DP", "13");
        na004.put("GQ", "0");
        na004.put("PL", "162,0,180");

        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003);
        var0.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004);

        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(0), na001);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(1), na002);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(2), na003);
        var1.getStudy(metadata.getId()).addSampleData(sampleNames.get(3), na004);


        // Check proper conversion of samples
        List<Variant> result = createAndNormalize(line);
        assertEquals(2, result.size());

        Variant getVar0 = result.get(0);
        StudyEntry getFile0 = getVar0.getStudy(metadata.getId());
        assertEquals(4, Integer.parseInt(getFile0.getAttribute("NS")));
//        assertEquals(2, Integer.parseInt(getFile0.getAttribute("AN")));
        assertEquals(1, Integer.parseInt(getFile0.getAttribute("AC").split(",")[0]));
        assertEquals(0.125, Double.parseDouble(getFile0.getAttribute("AF").split(",")[0]), 1e-8);
        assertEquals(63, Integer.parseInt(getFile0.getAttribute("DP")));
        assertEquals(10685, Integer.parseInt(getFile0.getAttribute("MQ")));
//        assertEquals(1, Integer.parseInt(getFile0.getAttribute("MQ0")));

        Variant getVar1 = result.get(1);
        StudyEntry getFile1 = getVar1.getStudy(metadata.getId());
        assertEquals(4, Integer.parseInt(getFile1.getAttribute("NS")));
//        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AN")));
        assertEquals(2, Integer.parseInt(getFile1.getAttribute("AC").split(",")[1]));
        assertEquals(0.25, Double.parseDouble(getFile1.getAttribute("AF").split(",")[1]), 1e-8);
        assertEquals(63, Integer.parseInt(getFile1.getAttribute("DP")));
        assertEquals(10685, Integer.parseInt(getFile1.getAttribute("MQ")));
//        assertEquals(1, Integer.parseInt(getFile1.getAttribute("MQ0")));
    }

    private List<Variant> createAndNormalize(String line) {
        return normalizer.apply(factory.create(metadata, line));
    }

}
