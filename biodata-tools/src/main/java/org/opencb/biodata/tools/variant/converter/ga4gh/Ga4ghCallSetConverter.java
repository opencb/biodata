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
public class Ga4ghCallSetConverter<CS> implements Converter<VariantSource, List<CS>> {

    private final Ga4ghVariantFactory<?, ?, CS, ?, ?> factory;

    public Ga4ghCallSetConverter(Ga4ghVariantFactory<?, ?, CS, ?, ?> factory) {
        this.factory = factory;
    }

    public Ga4ghCallSetConverter() {
        this((Ga4ghVariantFactory) new ProtoGa4GhVariantFactory());
    }

    public static Ga4ghCallSetConverter<Variants.CallSet> converter() {
        return new Ga4ghCallSetConverter<>(new ProtoGa4GhVariantFactory());
    }

    @Override
    public List<CS> convert(VariantSource source) {
        return convert(source.getFileId(), source.getSamples());
    }

    public List<CS> convert(List<String> variantSetIds, List<List<String>> callSets) {
        List<CS> sets = new ArrayList<>();

        Iterator<String> variantSetIdIterator = variantSetIds.iterator();
        Iterator<List<String>> callSetsIterator = callSets.iterator();

        while (variantSetIdIterator.hasNext() && callSetsIterator.hasNext()) {
            String fileId = variantSetIdIterator.next();
            List<String> callsInFile = callSetsIterator.next();

            convert(fileId, callsInFile, sets);
        }

        return sets;
    }

    private List<CS> convert(String fileId, List<String> callsInFile) {
        return convert(fileId, callsInFile, new ArrayList<>());
    }

    private List<CS> convert(String fileId, List<String> callsInFile, List<CS> sets) {
        long time = 0;

        // Add all samples in the file
        for (String callName : callsInFile) {
            ArrayList<String> variantSetIds = new ArrayList<>(1);
            variantSetIds.add(fileId);
            CS callset = factory.newCallSet("", callName, "", variantSetIds, time, time, Collections.emptyMap());
            sets.add(callset);
        }
        return sets;
    }

}
