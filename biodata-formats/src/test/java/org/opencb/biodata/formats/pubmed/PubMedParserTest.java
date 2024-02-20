package org.opencb.biodata.formats.pubmed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.formats.pubmed.v233jaxb.PubmedArticle;
import org.opencb.biodata.formats.pubmed.v233jaxb.PubmedArticleSet;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PubMedParserTest {

    @Test
    public void loadXml() throws JAXBException, IOException {

        JAXBContext jaxbCtx = JAXBContext.newInstance(PubMedParser.PUBMED_CONTEXT);
        if (jaxbCtx instanceof com.sun.xml.bind.v2.runtime.JAXBContextImpl) {
            System.out.println("JAXB Version: " +
                    ((com.sun.xml.bind.v2.runtime.JAXBContextImpl) jaxbCtx).getBuildId());
        }
        else {
            System.out.println("Unknown JAXB implementation: " + jaxbCtx.getClass().getName());
        }

        Path pubmedFile = Paths.get(getClass().getResource("/pubmed.test.xml").getPath());

        PubmedArticleSet res = (PubmedArticleSet) PubMedParser.loadXMLInfo(pubmedFile.toAbsolutePath().toString());
        List<Object> articles = res.getPubmedArticleOrPubmedBookArticle();
        Assert.assertEquals(2, articles.size());
        Assert.assertEquals("34878743", ((PubmedArticle) articles.get(0)).getMedlineCitation().getPMID().getContent());

        // Data model to JSON string
        PubmedArticle article = (PubmedArticle) articles.get(0);
        String json = new ObjectMapper().writer().writeValueAsString(article);

        // JSON string to data model
        PubmedArticle article2 = new ObjectMapper().readerFor(PubmedArticle.class).readValue(json);
        String json2 = new ObjectMapper().writer().writeValueAsString(article2);

        Assert.assertEquals(json, json2);
    }
}