package org.opencb.biodata.tools.variant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import java.io.IOException;
import org.opencb.biodata.models.variant.annotation.VariantEffect;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class JsonEffectDeserializer extends JsonDeserializer<VariantEffect> {

    @Override
    public VariantEffect deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode node = jp.getCodec().readTree(jp);
        
        VariantEffect effect = new VariantEffect(node.get("chromosome").asText(), node.get("position").asInt(), 
                node.get("referenceAllele").asText(), node.get("alternativeAllele").asText());
        
        effect.setGeneId(node.get("geneId").asText());
        effect.setGeneName(node.get("geneName").asText());
//        annotation.setGeneNameSource(null); // TODO Not in JSON input
        
        effect.setFeatureId(node.get("featureId").asText());
        effect.setFeatureType(node.get("featureType").asText());
        effect.setFeatureBiotype(node.get("featureBiotype").asText());
        effect.setFeatureStrand(node.get("featureStrand").asText());
        // TODO Present in JSON input but not in class
        // annotation.setFeatureName(node.get("featureName").asText());
        
//        annotation.setcDnaPosition(-1); // TODO Not in JSON input
//        annotation.setCcdsId(null);     // TODO Not in JSON input
//        annotation.setCdsPosition(-1);  // TODO Not in JSON input
//        annotation.setProteinId(null);  // TODO Not in JSON input
//        annotation.setProteinDomains(new String[0]);    // TODO Not in JSON input
        effect.setProteinPosition(node.get("aaPosition").asInt());
        
        effect.setAminoacidChange(node.get("aminoacidChange").asText());
        effect.setCodonChange(node.get("codonChange").asText());
        
//        annotation.setVariationId(null); // TODO Not in JSON input
//        annotation.setStructuralVariantsId(new String[0]);  // TODO Not in JSON input
        
        // TODO Return multiple SO in one entry
        String[] so = node.get("consequenceType").asText().split(":");
        if (so.length > 1) {
            effect.setConsequenceTypes(new int[] { Integer.parseInt(so[1]) });
        }
        
//        annotation.setCanonical(true);  // TODO Not in JSON input
//        annotation.setHgvsc(null);      // TODO Not in JSON input
//        annotation.setHgvsp(null);      // TODO Not in JSON input
//        annotation.setIntronNumber(null);  // TODO Not in JSON input (VEP returns like 1/15)
//        annotation.setExonNumber(null);  // TODO Not in JSON input (VEP returns like 4/15)
//        annotation.setVariantToTranscriptDistance(-1);  // TODO Not in JSON input
//        annotation.setClinicalSignificance(null);  // TODO Not in JSON input
//        annotation.setPubmed(new String[0]);  // TODO Not in JSON input
        
        return effect;
    }
    
}
