package org.opencb.biodata.tools.variant;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.multipart.FormDataMultiPart;
import com.sun.jersey.multipart.impl.MultiPartWriter;
import org.opencb.biodata.models.variant.Variant;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.opencb.biodata.models.variant.effect.ProteinSubstitutionScores.PolyphenEffect;
import org.opencb.biodata.models.variant.effect.ProteinSubstitutionScores.SiftEffect;
import org.opencb.biodata.models.variant.effect.VariantEffect;

/**
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class EffectCalculator {

    public static Map<Variant, Set<VariantEffect>> getEffects(List<Variant> batch) {
        if (batch.isEmpty()) {
            return new HashMap<>(batch.size());
        }

        StringBuilder chunkVcfRecords = new StringBuilder();
        ClientConfig cc = new DefaultClientConfig();
        cc.getClasses().add(MultiPartWriter.class);
        Client client = Client.create(cc);
        WebResource webResource = client.resource("http://ws.bioinfo.cipf.es/cellbase/rest/latest/hsa/genomic/variant/");

        for (Variant record : batch) {
            chunkVcfRecords.append(record.getChromosome()).append(":");
            chunkVcfRecords.append(record.getStart()).append(":");
            chunkVcfRecords.append(record.getReference()).append(":");
            chunkVcfRecords.append(record.getAlternate().isEmpty() ? "-" : record.getAlternate()).append(",");
        }

        ObjectMapper mapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();
        module.addDeserializer(VariantEffect.class, new JsonEffectDeserializer());
        mapper.registerModule(module);
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            
        FormDataMultiPart formDataMultiPart = new FormDataMultiPart();
        formDataMultiPart.field("variants", chunkVcfRecords.toString());

        try {
            String response = webResource.path("consequence_type").queryParam("of", "json").type(MediaType.MULTIPART_FORM_DATA).post(String.class, formDataMultiPart);
            List<VariantEffect> batchEffect = mapper.readValue(response, mapper.getTypeFactory().constructCollectionType(List.class, VariantEffect.class));
            return groupEffectsByVariant(batch, batchEffect);
        } catch (IOException e) {
            System.err.println(chunkVcfRecords.toString());
            e.printStackTrace();
        } catch (com.sun.jersey.api.client.UniformInterfaceException ex) {
            System.err.println(ex.getMessage());
            ex.printStackTrace();
        }

        return new HashMap<>(batch.size());
    }

    public static Map<Variant, Set<VariantEffect>> getEffectsWithPolyphenAndSift(List<Variant> batch) {
        Map<Variant, Set<VariantEffect>> batchEffect = getEffects(batch);
        for (Map.Entry<Variant, Set<VariantEffect>> effect : batchEffect.entrySet()) {
            setPolyphenSift(effect.getKey(), effect.getValue());
        }
        
        return batchEffect;
    }

    private static void setPolyphenSift(Variant variant, Set<VariantEffect> batchEffect) {
        if (batchEffect.isEmpty()) {
            return;
        }

        javax.ws.rs.client.Client clientNew = ClientBuilder.newClient();
        WebTarget webTarget = clientNew.target("http://ws-beta.bioinfo.cipf.es/cellbase/rest/v3/hsapiens/feature/transcript/");

        for (VariantEffect effect : batchEffect) {
            if (effect.getProteinPosition()!= -1 && !effect.getFeatureId().isEmpty() && effect.getAminoacidChange().length() == 3) {
                String change = effect.getAminoacidChange().split("/")[1];

                Response newResponse = webTarget.path(effect.getFeatureId()).path("function_prediction")
                        .queryParam("aaPosition", effect.getProteinPosition()).queryParam("aaChange", change)
                        .request(MediaType.APPLICATION_JSON_TYPE).get();

                ObjectMapper mapperNew = new ObjectMapper();
                JsonNode actualObj;

                String resp = null;
                try {
                    resp = newResponse.readEntity(String.class);
                    actualObj = mapperNew.readTree(resp);
                    Iterator<JsonNode> it = actualObj.get("response").iterator();

                    while (it.hasNext()) {
                        JsonNode polyphenNode = it.next();
                        if (polyphenNode.get("numResults").asInt() > 0) {
                            Iterator<JsonNode> itResults = polyphenNode.get("result").iterator();
                            while (itResults.hasNext()) {
                                JsonNode aaNode = itResults.next();

                                if (aaNode.has("aaPositions") && aaNode.get("aaPositions").has(String.valueOf(effect.getProteinPosition()))
                                        && aaNode.get("aaPositions").get("" + effect.getProteinPosition()).has(change)) {
                                    JsonNode valueNode = aaNode.get("aaPositions").get(String.valueOf(effect.getProteinPosition())).get(change);

                                    if (valueNode.has("ss") && valueNode.has("ps") && valueNode.has("se") && valueNode.has("pe")) {
                                        if (!valueNode.get("ss").isNull()) {
                                            variant.getAnnotation().getProteinSubstitutionScores().setSiftScore((float) valueNode.get("ss").asDouble());
                                        }

                                        if (!valueNode.get("ps").isNull()) {
                                            variant.getAnnotation().getProteinSubstitutionScores().setPolyphenScore((float) valueNode.get("ps").asDouble());
                                        }

                                        if (!valueNode.get("se").isNull()) {
                                            int sift = valueNode.get("se").asInt();
                                            if (sift == 0 || sift == 1) {
                                                variant.getAnnotation().getProteinSubstitutionScores().setSiftEffect(SiftEffect.values()[sift]);
                                            }
                                        }
                                        if (!valueNode.get("pe").isNull()) {
                                            int polyphen = valueNode.get("pe").asInt();
                                            if (polyphen >= 0 && polyphen <= 3) {
                                                variant.getAnnotation().getProteinSubstitutionScores().setPolyphenEffect(PolyphenEffect.values()[polyphen]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                } catch (JsonParseException e) {
                    System.err.println(resp);
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public static void setEffects(List<Variant> batch) {
        setEffects(batch, false, false);
    }

    public static void setEffects(List<Variant> batch, boolean force, boolean withPolyphenSIFT) {
        List<Variant> noEffects;

        if (force) {
            noEffects = batch;
        } else {
            noEffects = new ArrayList<>(batch.size());
            for (Variant v : batch) {
                if (v.getAnnotation().getEffects().isEmpty()) {
                    noEffects.add(v);
                }
            }
        }

        Map<Variant, Set<VariantEffect>> effects = getEffects(noEffects);

        if (withPolyphenSIFT) {
            for (Map.Entry<Variant, Set<VariantEffect>> effect : effects.entrySet()) {
                setPolyphenSift(effect.getKey(), effect.getValue());
            }
        }

        for (Map.Entry<Variant, Set<VariantEffect>> effectsPerVariant : effects.entrySet()) {
            for (VariantEffect effect : effectsPerVariant.getValue()) {
                effectsPerVariant.getKey().addEffect(effect.getAlternateAllele(), effect);
            }
        }
    }
    
    private static Map<Variant, Set<VariantEffect>> groupEffectsByVariant(List<Variant> batch, List<VariantEffect> effects) {
        Map<Variant, Set<VariantEffect>> groupedEffects = new HashMap<>();
        List<VariantEffect> auxEffects = new LinkedList<>(effects);
        
        for (Variant variant : batch) {
            Set<VariantEffect> effectsByVariant = new HashSet<>();
            Iterator<VariantEffect> effectsIterator = auxEffects.iterator();
            
            while(effectsIterator.hasNext()) {
                VariantEffect effect = effectsIterator.next();
                if (variant.getChromosome().equals(effect.getChromosome())
                        && variant.getStart() == effect.getPosition()
                        && variant.getReference().equals(effect.getReferenceAllele())
                        && variant.getAlternate().equals(effect.getAlternateAllele())) {
                    effectsByVariant.add(effect);
                    effectsIterator.remove();
                }
            }
            
            groupedEffects.put(variant, effectsByVariant);
        }
        
        return groupedEffects;
    }
}
