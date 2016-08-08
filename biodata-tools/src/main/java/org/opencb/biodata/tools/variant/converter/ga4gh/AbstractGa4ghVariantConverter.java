package org.opencb.biodata.tools.variant.converter.ga4gh;

import org.apache.commons.lang.StringUtils;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.tools.variant.converter.Converter;

import java.util.*;

/**
 * Created on 08/08/16.
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public abstract class AbstractGa4ghVariantConverter<V, C> implements Converter<Variant, V> {

    private boolean addCallSetName;
    private Map<String, Integer> callSetNameId;

    public AbstractGa4ghVariantConverter() {
        this(true, Collections.emptyMap());
    }

    public AbstractGa4ghVariantConverter(boolean addCallSetName, Map<String, Integer> callSetNameId) {
        if (callSetNameId == null) {
            callSetNameId = Collections.emptyMap();
        }
        this.addCallSetName = addCallSetName;
        this.callSetNameId = callSetNameId;
    }

    abstract protected V newVariant(String id, String variantSetId, List<String> names, Long created, Long updated,
                                    String referenceName, Long start, Long end, String reference, List<String> alternates,
                                    Map<String, List<String>> info, List<C> calls) ;

    abstract protected C newCall(String callSetName, String callSetId, List<Integer> allelesIdx, String phaseSet,
                                 List<Double> genotypeLikelihood, Map<String, List<String>> info);


    @Override
    public V convert(Variant variant) {
        return apply(Collections.singletonList(variant)).get(0);
    }

    /**
     * @deprecated Use {@link #apply(List)} instead
     */
    @Deprecated
    public List<V> create(List<Variant> variants) {
        return apply(variants);
    }

    /**
     * Given a list of variants, creates the equivalent set using the GA4GH API.
     *
     * @param variants List of variants to transform
     * @return GA4GH variants representing the same data as the internal API ones
     */
    @Override
    public List<V> apply(List<Variant> variants) {
        List<V> gaVariants = new ArrayList<>(variants.size());

        for (Variant variant : variants) {
            String id = variant.toString();

            List<String> variantIds = new ArrayList<>(variant.getIds());

            for (StudyEntry study : variant.getStudies()) {
                List<String> alternates = new ArrayList<>(study.getSecondaryAlternatesAlleles().size() + 1);
                alternates.add(variant.getAlternate());
                alternates.addAll(study.getSecondaryAlternatesAlleles());

                //Optional
                Long time = 0L;

//                //Only required for "graph" mode
//                List<String> alleleIds = null;

                //VariableSet should be the study, the file, or be provided?
                String variantSetId = study.getStudyId();


                Map<String, List<String>> fileInfo = parseInfo(study.getFiles());
                List<C> calls = parseCalls(null, study);

                Long start = Long.valueOf(to0BasedStart(variant.getStart())); // Ga4gh uses 0-based positions.
                Long end = Long.valueOf(variant.getEnd());                    // 0-based end does not change

                V ga = newVariant(id, variantSetId, variantIds, time, time,
                        variant.getChromosome(), start, end, variant.getReference(), alternates, fileInfo, calls
                );

                gaVariants.add(ga);
            }
        }


        return gaVariants;
    }

    /**
     *
     * 0-based -> [start,end)
     * 1-based -> [start,end]
     *
     * a b c d e f
     * 0 1 2 3 4 5  <- 0-based from B to E : [1, 5)
     * 1 2 3 4 5 6  <- 1-based from B to E : [2, 5]
     *
     * @param start
     * @return
     */
    protected Integer to0BasedStart(Integer start) {
        return start - 1;
    }


    protected Map<String, List<String>> parseInfo(List<FileEntry> files) {
        Map<String, List<String>> parsedInfo = new HashMap<>();

        List<String> fileIds = new ArrayList<>(files.size());
        List<String> ori = new ArrayList<>(files.size());
        parsedInfo.put("FID", fileIds);
        parsedInfo.put("ORI", ori);
        int fileIdx = 0;
        for (FileEntry file : files) {
            fileIds.add(file.getFileId());
            ori.add(file.getCall());
            Map<String, String> attributes = file.getAttributes();
            for (Map.Entry<String, String> field : attributes.entrySet()) {
                List<String> value;
                if (parsedInfo.containsKey(field.getKey())) {
                    value = parsedInfo.get(field.getKey());
                } else {
                    value = Arrays.asList(new String[files.size()]);
                    parsedInfo.put(field.getKey(), value);
                }
                value.set(fileIdx, field.getValue());
            }
            fileIdx++;
        }
        return parsedInfo;
    }

    /**
     *
     * @param variantId Optional field. Only required if the calls is not being returned from the
     *                  server already contained within its `Variant`.
     * @param study
     * @return
     */
    protected List<C> parseCalls(String variantId, StudyEntry study) {
        List<C> calls = new LinkedList<>();

        for (String sample : study.getOrderedSamplesName()) {

            Integer id = callSetNameId.get(sample);
            String callSetId = null;
            String callSetName = null;
            if (id != null) {
                callSetId = id.toString();
            }
            if (addCallSetName) {
                callSetName = sample;
            }
            Map<String, List<String>> info = new HashMap<>();

            List<Integer> allelesIdx = Collections.emptyList();
            List<Double> genotypeLikelihood = Collections.emptyList();
            String phaseSet = "";

            for (String formatField : study.getFormat()) {
                String sampleData = study.getSampleData(sample, formatField);
                switch (formatField) {
                    case "GT":
                        // Transform genotype with form like 0|0 to the GA4GH style
                        Genotype genotype = new Genotype(sampleData);
                        allelesIdx = new ArrayList<>(genotype.getAllelesIdx().length);
                        for (int alleleIdx : genotype.getAllelesIdx()) {
                            if (alleleIdx >= 0) {
                                allelesIdx.add(alleleIdx);
                            }
                        }

                        if (phaseSet.isEmpty() && genotype.isPhased()) {
                            // It may be that the genotype is 0|0, but without PS field
                            // Set default phaseSet
                            phaseSet = "p";
                        }
                        break;
                    case "GL":
                        String[] split = sample.split(",");

                        genotypeLikelihood = new ArrayList<>(split.length);
                        for (String gl : split) {
                            genotypeLikelihood.add(Double.parseDouble(gl));
                        }
                        break;
                    case "PS":
                        if (StringUtils.isNotEmpty(sampleData) && !sampleData.equals(".")) {
                            phaseSet = sampleData;
                        }
                        break;
                    default:
                        info.put(formatField, Collections.singletonList(sampleData));
                }

                // Create call object
                C call = newCall(callSetName, callSetId, allelesIdx, phaseSet, genotypeLikelihood, info);
                calls.add(call);
            }
        }

        return calls;
    }
}
