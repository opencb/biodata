package org.opencb.biodata.tools.variant.algorithm;

import org.junit.jupiter.api.Test;
import org.opencb.biodata.formats.variant.vcf4.io.VariantVcfReader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.models.variant.stats.IdentityByDescent;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class IdentityByDescentClusteringTest {

    @Test
    public void testIBD() throws IOException {
        String fileName = "ibs.vcf";
        VariantFileMetadata fileMetadata = new VariantFileMetadata(fileName, "fid");
        VariantStudyMetadata metadata = fileMetadata.toVariantStudyMetadata("sid");

        VariantVcfReader variantReader = new VariantVcfReader(metadata, IdentityByStateClusteringTest.class.getClassLoader().getResource(fileName).getPath());
        variantReader.open();
        variantReader.pre();
        List<Variant> variants = variantReader.read(50);
        variantReader.post();
        variantReader.close();

        IdentityByDescentClustering ibdc = new IdentityByDescentClustering();
        List<String> samples = new ArrayList<>(variants.get(0).getStudy(metadata.getId()).getSamplesName());
        List<IdentityByDescent> ibds = ibdc.countIBD(variants, samples);

        InputStream inputStream = IdentityByStateClusteringTest.class.getClassLoader().getResourceAsStream("ibs.genome");
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        reader.readLine();  // ignore header

        assertIBD(ibdc, ibds, reader);
    }

    private void assertIBD(IdentityByDescentClustering ibdc, List<IdentityByDescent> ibds, BufferedReader reader) throws IOException {
        double delta = 0.0001;
        String line = reader.readLine();
        while (line != null) {
            String[] split = line.split("\t");
            int fileFirst = Integer.parseInt(split[0]);
            int fileSecond = Integer.parseInt(split[2]);
            int index = ibdc.getIbsClustering().getCompoundIndex(fileFirst, fileSecond);

            IdentityByDescent ibd = ibds.get(index);

            System.out.println(line);
            System.out.println(ibd.toString());
            System.out.println();

            String message = "elem " + fileFirst + ", " + fileSecond + " " + ibd.getIbd().toString();
            assertEquals(message, Float.parseFloat(split[6]), ibd.getIbd()[0], delta);
            assertEquals(message, Float.parseFloat(split[7]), ibd.getIbd()[1], delta);
            assertEquals(message, Float.parseFloat(split[8]), ibd.getIbd()[2], delta);
            assertEquals(message, Float.parseFloat(split[9]), ibd.getPihat(), delta);
            assertEquals(message, Integer.parseInt(split[14]), ibd.getIbs()[0]);
            assertEquals(message, Integer.parseInt(split[15]), ibd.getIbs()[1]);
            assertEquals(message, Integer.parseInt(split[16]), ibd.getIbs()[2]);
            assertEquals(message, Float.parseFloat(split[11]), ibd.getDistance(), delta);
            line = reader.readLine();
        }
    }
}