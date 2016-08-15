package org.opencb.biodata.tools.variant.converter.ga4gh;

import ga4gh.Variants;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.tools.ga4gh.Ga4ghVariantFactory;
import org.opencb.biodata.tools.ga4gh.ProtoGa4GhVariantFactory;
import org.opencb.biodata.tools.variant.converter.Converter;

import java.util.*;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class Ga4ghVariantSetConverter<VS> implements Converter<VariantSource, VS> {

    private final Ga4ghVariantFactory<?, ?, ?, VS, ?> factory;

    public Ga4ghVariantSetConverter(Ga4ghVariantFactory<?, ?, ?, VS, ?> factory) {
        this.factory = factory;
    }

    public Ga4ghVariantSetConverter() {
        this((Ga4ghVariantFactory) new ProtoGa4GhVariantFactory());
    }

    public static Ga4ghVariantSetConverter<Variants.VariantSet> converter() {
        return new Ga4ghVariantSetConverter<>(new ProtoGa4GhVariantFactory());
    }

    @Override
    public VS convert(VariantSource source) {
        return apply(Collections.singletonList(source)).get(0);
    }

    @Override
    public List<VS> apply(List<VariantSource> variantSources) {
        Set<VS> gaVariantSets = new LinkedHashSet<>();

        for (VariantSource source : variantSources) {
            // TODO This header should be already split
            List<Object> metadata = new ArrayList<>();
            for (Map.Entry<String, List<Object>> entry : source.getHeader().getMeta().entrySet()) {
                String key = entry.getKey();
                for (Object o : entry.getValue()) {
                    Object variantSetMetadata;
                    if (o instanceof Map) {
                        variantSetMetadata = getMetadataLine(key, ((Map) o));
                    } else {
                        variantSetMetadata = factory.newVariantSetMetadata(key, o.toString(), "", "", "", "", Collections.emptyMap());
                    }
                    metadata.add(variantSetMetadata);
                }
            }

            @SuppressWarnings("unchecked")
            VS variantSet = (VS) factory.newVariantSet(source.getFileId(), source.getFileName(), source.getStudyId(), "", (List) metadata);
            gaVariantSets.add(variantSet);
        }

        return new ArrayList<>(gaVariantSets);
    }

    private Object getMetadataLine(String key, Map<String, String> map) {
        String id = "";
        String number = "";
        String type = "";
        String description = "";
        Map<String, List<String>> info = new HashMap<>();

        for (Map.Entry<String, String> e : map.entrySet()) {
            switch (e.getKey().toLowerCase()) {
                case "id":
                    id = e.getValue();
                    break;
                case "number":
                    number = e.getValue();
                    break;
                case "type":
                    type = e.getValue();
                    break;
                case "description":
                    description = e.getValue();
                    break;
                default:
                    if (e.getValue().contains(",")) {
                        info.put(e.getKey(), Arrays.asList(e.getValue().split(",")));
                    } else {
                        info.put(e.getKey(), Collections.singletonList(e.getValue()));
                    }
            }
        }
        return factory.newVariantSetMetadata(key, "", id, type, number, description, info);
    }

    protected Object getMetadataLine(String line) {

        String key = "";
        String value = "";
        String id = "";
        String number = "";
        String type = "";
        String description = "";
        Map<String, List<String>> info = Collections.emptyMap();

        // Split by square brackets that are NOT between quotes
        String[] split = line.split("(<|>)(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

        if (split.length > 1) { // Header entries like INFO or FORMAT
            // Remove leading ## and trailing equals symbol
            key = split[0].substring(2, split[0].length()-1);
            value = split[1];

            // Split by commas that are NOT between quotes
            String[] valueSplit = split[1].split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

            for (String pair : valueSplit) { // Key-value pairs
                String[] pairSplit = pair.split("=", 2);
                switch (pairSplit[0]) {
                    case "ID":
                        id = pairSplit[1];
                        break;
                    case "Number":
                        number = pairSplit[1];
                        break;
                    case "Type":
                        type = pairSplit[1];
                        break;
                    case "Description":
                        description = pairSplit[1];
                        break;
                    default:
//                        metadata.addInfo(pairSplit[0], pairSplit[1]);
                }
            }
        } else {
            // Simpler entry like "assembly=GRCh37"
            split = line.split("=", 2);
            // Remove leading ## and trailing equals symbol
            key = split[0].substring(2);
            id = split[0].substring(2);
            if (split.length > 1) {
                value = split[1];
            }
        }

        return factory.newVariantSetMetadata(key, value, id, type, number, description, info);
    }

}
