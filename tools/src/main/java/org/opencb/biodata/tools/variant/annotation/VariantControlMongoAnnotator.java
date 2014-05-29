package org.opencb.biodata.tools.variant.annotation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Joiner;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.opencb.biodata.models.variant.Variant;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantControlMongoAnnotator implements VariantAnnotator {


    @Override
    public void annot(List<Variant> batch) {

        Client clientNew = ClientBuilder.newClient();
        WebTarget webTarget = clientNew.target("http://ws-beta.bioinfo.cipf.es/controlsws/rest/");

        StringBuilder chunkVariants = new StringBuilder();
        DecimalFormat df = new DecimalFormat("#.####");
        for (Variant record : batch) {
            chunkVariants.append(record.getChromosome()).append(":");
            chunkVariants.append(record.getStart()).append(":");
            chunkVariants.append(record.getReference()).append(":");
            chunkVariants.append(record.getAlternate()).append(",");
        }

        Form form = new Form();
        form.param("positions", chunkVariants.substring(0, chunkVariants.length() - 1));
        Response response = webTarget.path("variants").request(MediaType.APPLICATION_JSON_TYPE).post(Entity.entity(form, MediaType.APPLICATION_FORM_URLENCODED_TYPE));
        ObjectMapper mapperNew = new ObjectMapper();
        JsonNode actualObj;
        String resp;
        try {
            resp = response.readEntity(String.class);
            actualObj = mapperNew.readTree(resp);
            Iterator<JsonNode> it = actualObj.get("response").iterator();
            int i = 0;
            while (it.hasNext()) {
                JsonNode aa = it.next();
                int numResult = aa.get("numResults").asInt();
                if (numResult == 1) {

                    Iterator<JsonNode> itResults = aa.get("result").iterator();
                    Variant v = batch.get(i);
                    while (itResults.hasNext()) {
                        JsonNode elem = itResults.next();

                        if (elem.has("sources")) {
                            Iterator<JsonNode> itSources = elem.get("sources").iterator();
                            while (itSources.hasNext()) {
                                JsonNode source = itSources.next();

                                List<String> gts = new ArrayList<>();
                                Iterator<Map.Entry<String, JsonNode>> gtIt = source.get("stats").get("genotypeCount").fields();
                                while (gtIt.hasNext()) {
                                    Map.Entry<String, JsonNode> gtElem = gtIt.next();
                                    String aux = gtElem.getKey() + ":" + gtElem.getValue().asInt();
                                    gts.add(aux);
                                }
                                v.addAttribute(source.get("sourceId").asText() + "_maf", "" + df.format(source.get("stats").get("maf").asDouble()));
                                v.addAttribute(source.get("sourceId").asText() + "_amaf", "" + source.get("stats").get("alleleMaf").asText());
                                v.addAttribute(source.get("sourceId").asText() + "_gt", Joiner.on(",").join(gts));
                            }
                        }
                    }
                }
                i++;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void annot(Variant elem) {
    }
}
