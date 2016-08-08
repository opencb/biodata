package org.opencb.biodata.tools.variant.converter.ga4gh;

import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.tools.variant.converter.Converter;

import java.util.*;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public abstract class AbstractGa4ghVariantSetConverter<VS, VSM> implements Converter<VariantSource, VS> {

    public AbstractGa4ghVariantSetConverter() {
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
            List<VSM> metadata = new ArrayList<>();
            String header = source.getMetadata().get("header").toString();

            for (String line : header.split("\n")) {
                if (line.startsWith("#CHROM")) {
                    continue;
                }

                metadata.add(getMetadataLine(line));
            }

            VS variantSet = newVariantSet(source.getFileId(), source.getFileName(), source.getStudyId(), "", metadata);
            gaVariantSets.add(variantSet);
        }

        return new ArrayList<>(gaVariantSets);
    }

    abstract VS newVariantSet(String id, String name, String datasetId, String referenceSetId, List<VSM> metadata);

    protected VSM getMetadataLine(String line) {

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

        return newVariantSetMetadata(key, value, id, type, number, description, info);
    }

    protected abstract VSM newVariantSetMetadata(String key, String value, String id, String type, String number, String description,
                                                 Map<String, List<String>> info);
}
