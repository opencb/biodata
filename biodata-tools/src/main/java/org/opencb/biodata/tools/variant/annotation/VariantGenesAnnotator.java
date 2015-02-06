package org.opencb.biodata.tools.variant.annotation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opencb.biodata.models.feature.Gene;
import org.opencb.biodata.models.variant.VariantSourceEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.annotation.VariantEffect;
import org.opencb.datastore.core.QueryResponse;
import org.opencb.datastore.core.QueryResult;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
@Deprecated
public class VariantGenesAnnotator implements VariantAnnotator {

    private String geneNameTag;

    private static final String CELLBASE_URL = "http://wwwdev.ebi.ac.uk/cellbase/webservices";

    private WebTarget webResource;

    public VariantGenesAnnotator() {
        this("GeneNames");
    }

    public VariantGenesAnnotator(String geneNameTag) {
        this.geneNameTag = geneNameTag;
        webResource = ClientBuilder.newClient().target(CELLBASE_URL + "/rest/v3/hsapiens/genomic/region");
    }

    @Override
    public void annot(Variant elem) {
        annot(Arrays.asList(elem));
    }

    @Override
    public void annot(List<Variant> batch) {
        StringBuilder positions = new StringBuilder();
        for (Variant record : batch) {
            positions.append(record.getChromosome()).append(":").append(record.getStart()).append("-").append(record.getEnd()).append(",");
        }

        Form form = new Form();
        form.param("region", positions.substring(0, positions.length() - 1));

        Response response = webResource.path("gene").queryParam("exclude", "transcripts,chunkIds").request()
                .post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            String resp = response.readEntity(String.class);

            QueryResponse<QueryResult<Gene>> qr = mapper.readValue(resp,
                    new TypeReference<QueryResponse<QueryResult<Gene>>>() {
                    }
            );

            int i = 0;
            for (QueryResult<Gene> queryResult : qr.getResponse()) {
                for (Gene gene : queryResult.getResult()) {
                    Variant variant = batch.get(i);
                    variant.getAnnotation().addGene(gene);
                    for (Map.Entry<String, VariantSourceEntry> file : variant.getSourceEntries().entrySet()) {
                        annotGeneName(variant, file.getValue());
                    }
                }
                i++;
            }

        } catch (JsonParseException ex) {
            Logger.getLogger(VariantSNPAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VariantSNPAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void annotGeneName(Variant variant, VariantSourceEntry file) {
        Set<String> geneNames = new HashSet<>();

        for (List<VariantEffect> list : variant.getAnnotation().getEffects().values()) {
            for (VariantEffect ct : list) {
                if (!ct.getGeneName().isEmpty()) {
                    geneNames.add(ct.getGeneName());
                }
            }
        }

        if (geneNames.size() > 0) {
             file.addAttribute(this.geneNameTag, Joiner.on(",").join(geneNames));
        }

    }

}
