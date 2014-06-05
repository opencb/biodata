package org.opencb.biodata.tools.variant.annotation;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.models.variant.Variant;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantSNPAnnotator implements VariantAnnotator {

    private static final String CELLBASE_URL = "http://www.ebi.ac.uk/cellbase/webservices/";

    private WebTarget webResource;

    public VariantSNPAnnotator() {
        webResource = ClientBuilder.newClient().target(CELLBASE_URL + "/rest/v3/hsapiens/genomic/position");
    }

    @Override
    public void annot(List<Variant> batch) {
        StringBuilder positions = new StringBuilder();
        for (Variant record : batch) {
            positions.append(record.getChromosome()).append(":").append(record.getStart()).append(",");
        }

        Form form = new Form();
        form.param("position", positions.substring(0, positions.length() - 1));

        Response response = webResource.path("snp").queryParam("include", "id").request().post(
                Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        ObjectMapper mapper = new ObjectMapper();

        try {
            String resp = response.readEntity(String.class);
            JsonNode actualObj = mapper.readTree(resp);
            Iterator<JsonNode> it = actualObj.get("response").iterator();

            int cont = 0;
            while (it.hasNext()) {
                JsonNode snp = it.next();
                if (snp.get("numResults").asInt() > 0) {
                    Iterator<JsonNode> itResults = snp.get("result").iterator();

                    // TODO Accept multiple identifiers via xrefs
//                    while (itResults.hasNext()) {
                    if (itResults.hasNext()) {
                        String rs = itResults.next().get("id").asText();
                        if (rs.startsWith("rs")) {
//                            batch.get(cont).addId(rs);
                            batch.get(cont).setId(rs);
                        }
                    }
                }
                cont++;
            }

        } catch (JsonParseException ex) {
            Logger.getLogger(VariantSNPAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(VariantSNPAnnotator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void annot(Variant elem) {
        annot(Arrays.asList(elem));
    }

}
