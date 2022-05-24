package org.opencb.biodata.formats.pubmed;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.opencb.biodata.formats.pubmed.generated.PubmedArticle;
import org.opencb.biodata.formats.pubmed.generated.PubmedArticleSet;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class PubMedParserTest {

    @Test
    public void loadXml() throws JAXBException, IOException {
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