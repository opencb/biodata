package org.opencb.biodata.models.clinical.qc;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.models.common.Image;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;

public class RelatednessTest {

    @Test
    public void writeModel() throws IOException {
        Relatedness relatedness = new Relatedness();
        relatedness.setScores(Collections.singletonList(new RelatednessScore()));
        relatedness.setImages(Collections.singletonList(new Image()));
        ObjectMapper objectMapper = new ObjectMapper();

        File file = Paths.get("/tmp/relatedness.json").toFile();
        objectMapper.writerFor(Relatedness.class).writeValue(file, relatedness);
        Assert.assertTrue(file.exists());

        InferredSex inferredSex = new InferredSex();
        inferredSex.setImages(Collections.singletonList(new Image()));
        file = Paths.get("/tmp/inferredSex.json").toFile();
        objectMapper.writerFor(InferredSex.class).writeValue(file, inferredSex);
        Assert.assertTrue(file.exists());

        MendelianError mendelianError = new MendelianError();
        mendelianError.setSampleAggregation(Collections.singletonList(
                new MendelianError.SampleAggregation().setSample("").setChromAggregation(Collections.singletonList(
                        new MendelianError.SampleAggregation.ChromosomeAggregation().setChromosome("").setErrorCodeAggregation(
                                new HashMap<>())))));
        mendelianError.setImages(Collections.singletonList(new Image()));
        file = Paths.get("/tmp/mendelianError.json").toFile();
        objectMapper.writerFor(MendelianError.class).writeValue(file, mendelianError);
        Assert.assertTrue(file.exists());
    }
}