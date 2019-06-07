/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.tools.variant;

import htsjdk.tribble.readers.LineIterator;
import htsjdk.tribble.readers.LineIteratorImpl;
import htsjdk.tribble.readers.LineReader;
import htsjdk.variant.variantcontext.Allele;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFConstants;
import htsjdk.variant.vcf.VCFHeader;
import htsjdk.variant.vcf.VCFHeaderVersion;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.FullVcfCodec;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFileMetadata;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.metadata.VariantStudyMetadata;
import org.opencb.biodata.tools.variant.converters.avro.VCFHeaderToVariantFileHeaderConverter;
import org.opencb.biodata.tools.variant.converters.avro.VariantContextToVariantConverter;
import org.opencb.commons.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.*;
import java.util.function.BiConsumer;

import static java.lang.Math.abs;

/**
 * Reads a VCF file using the library HTSJDK.
 *
 * Optionally, normalizes the variants.
 *
 * Created on 16/05/16.
 *
 * @author Jacobo Coll &lt;jVariantVcfHtsjdkReaderacobo167@gmail.com&gt;
 */
public class VariantVcfHtsjdkReader implements VariantReader {

    private static final String MATEID = "MATEID";
    private static final String MATE_CIPOS = "MATE_CIPOS";
    private static final int INTERACTING_DISTANCE_THRESHOLD = 300;
    private static final String PHASE_SET_TAG = "PS";
    private static final String VCF_MISSING_STRING = ".";
    private final Logger logger = LoggerFactory.getLogger(VariantVcfHtsjdkReader.class);

    private final Path input;
    private InputStream inputStream;
    private final VariantStudyMetadata metadata;
    private final VariantFileMetadata fileMetadata;
    private final VariantNormalizer normalizer;
    private FullVcfCodec codec;
    private VCFHeader header;
    private VariantContextToVariantConverter converter;
    private LineIterator lineIterator;
    private List<String> headerLines;
    private Set<BiConsumer<String, RuntimeException>> malformHandlerSet = new HashSet<>();
    private boolean failOnError = false;
    private boolean ignorePhaseSet = true;
    private boolean combineBreakends = false;
    private final boolean closeInputStream;   // Do not close inputStream if is provided in constructor. Respect symmetrical open/close
    private VariantContext lastVariantContext = null;
    private HashMap<String, Variant> breakendMates;

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantStudyMetadata metadata) {
        this(inputStream, metadata, null);
    }

    public VariantVcfHtsjdkReader(InputStream inputStream, VariantStudyMetadata metadata, VariantNormalizer normalizer) {
        this.input = null;
        this.inputStream = Objects.requireNonNull(inputStream);
        this.metadata = Objects.requireNonNull(metadata);
        this.fileMetadata = new VariantFileMetadata(metadata.getFiles().get(0));
        this.normalizer = normalizer;
        this.closeInputStream = false; // Do not close input stream
    }

    public VariantVcfHtsjdkReader(Path input, VariantStudyMetadata metadata) {
        this(input, metadata, null);
    }

    public VariantVcfHtsjdkReader(Path input, VariantStudyMetadata metadata, VariantNormalizer normalizer) {
        this.input = Objects.requireNonNull(input);
        this.inputStream = null;
        this.metadata = Objects.requireNonNull(metadata);
        this.fileMetadata = new VariantFileMetadata(metadata.getFiles().get(0));
        this.normalizer = normalizer;
        this.closeInputStream = true; // Close input stream
    }

    public VariantVcfHtsjdkReader registerMalformatedVcfHandler(BiConsumer<String, RuntimeException> handler) {
        this.malformHandlerSet.add(handler);
        return this;
    }

    public VariantVcfHtsjdkReader setFailOnError(boolean failOnError) {
        this.failOnError = failOnError;
        return this;
    }

    public VariantVcfHtsjdkReader setIgnorePhaseSet(boolean ignorePhaseSet) {
        this.ignorePhaseSet = ignorePhaseSet;
        return this;
    }

    public VariantVcfHtsjdkReader setCombineBreakends(boolean combineBreakends) {
        this.combineBreakends = combineBreakends;
        breakendMates = new HashMap<>();
        return this;
    }

    @Override
    public boolean open() {
        if (inputStream == null) {
            try {
                inputStream = FileUtils.newInputStream(input);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
        return true;
    }

    @Override
    public boolean pre() {
        codec = new FullVcfCodec();
        lineIterator = codec.makeSourceFromStream(inputStream);

        // Read the header
        headerLines = new LinkedList<>();
        while (lineIterator.hasNext()) {
            String line = lineIterator.peek();
            if (line.startsWith(VCFHeader.HEADER_INDICATOR)) {
                headerLines.add(line);
                lineIterator.next();
            } else {
                break;
            }
        }

        // Parse the header
        header = (VCFHeader) codec.readActualHeader(new LineIteratorImpl(new LineReader() {
            Iterator<String> iterator = headerLines.iterator();
            @Override
            public String readLine() throws IOException {
                if (iterator.hasNext()) {
                    return iterator.next();
                } else {
                    return null;
                }
            }
            @Override public void close() {}
        }));

        // Create converters and fill VariantSource
        converter = new VariantContextToVariantConverter(metadata.getId(), fileMetadata.getId(), header.getSampleNamesInOrder());
        fileMetadata.setHeader(new VCFHeaderToVariantFileHeaderConverter().convert(header));
        fileMetadata.setSampleIds(header.getSampleNamesInOrder());

        if (normalizer != null) {
            normalizer.configure(fileMetadata.getHeader());
        }
        return true;
    }

    @Override
    public List<Variant> read(int batchSize) {
        List<VariantContext> variantContexts = new ArrayList<>(batchSize);

        // Add last variant context from last call to "read"
        if (lastVariantContext != null) {
            variantContexts.add(lastVariantContext);
        }

        lastVariantContext = readNextVariantContext();
        while (lastVariantContext != null && incompleteBatch(variantContexts, batchSize)) {
            variantContexts.add(lastVariantContext);
            lastVariantContext = readNextVariantContext();
        }

        List<Variant> variants = converter.apply(variantContexts);

        if (combineBreakends) {
            variants = runCombineBreakends(variants);
            // Reached end of file, no more variants - Drain unpaired breakends
            if (lastVariantContext == null) {
                // Singleton BNDs - BNDs that contain a MATEID in the info field, however, no BND was found in the
                // VCF with that MATEID
                Iterator<String> breakendIdIterator = breakendMates.keySet().iterator();
                while (breakendIdIterator.hasNext() && variants.size() < batchSize) {
                    String breakendId = breakendIdIterator.next();
                    variants.add(breakendMates.get(breakendId));
                    breakendMates.remove(breakendId);
                }
            }
        }

        if (normalizer != null) {
            variants = normalizer.apply(variants);
        }

        return variants;
    }

    private List<Variant> runCombineBreakends(List<Variant> variants) {
        List<Variant> variantListToReturn = new ArrayList<>(variants.size());
        for (Variant variant : variants) {
            if (StringUtils.isNotBlank(variant.getAlternate())) {
                byte[] alternateBytes = variant.getAlternate().getBytes();
                // Symbolic allele: CNV, DEL, DUP, INS, INV, BND
                if (Allele.wouldBeSymbolicAllele(alternateBytes)) {
                    // BND
                    if (alternateBytes[0] == '.' || alternateBytes[alternateBytes.length - 1] == '.'  // single breakend
                            || variant.getAlternate().contains("[")       // mated breakend
                            || variant.getAlternate().contains("]")) {

                        String mateId = getMateId(variant);
                        // If there's no mate specified, add BND
                        if (mateId != null) {
                            String breakendPairId = getBreakendPairId(variant.getId(), mateId);
                            // Mate was previously seen and stored, create Variant with the pair info, remove
                            // variantContext from sharedContext and continue
                            // WARNING: assuming BND positions cannot be multiallelic positions - there will always
                            // be just one alternate allele!
                            if (breakendMates.putIfAbsent(breakendPairId, variant) != null) {
                                variantListToReturn.add(combineBreakendPair(variant, breakendMates.get(breakendPairId)));
                                breakendMates.remove(breakendPairId);
                            // Mate not seen yet, variant has been saved in breakendMates, remove from input list
                            }
                        // Singleton BND, no mate specified within the INFO field; keep in the variant list as is
                        } else {
                            variantListToReturn.add(variant);
                        }
                    // Symbolic allele other than BND: CNV, DEL, DUP, INS, INV BND; keep in the variant list as is
                    } else {
                        variantListToReturn.add(variant);
                    }
                // Simple variant: SNV, short insertion, short deletion; add it; keep in the variant list as is
                } else {
                    variantListToReturn.add(variant);
                }
            }
        }

        return variantListToReturn;
    }

    private String getBreakendPairId(String mateId1, String mateId2) {
        // The id for the breakend pair will be the two BND Ids alphabetically sorted and concatenated by a '_'
        List<String> ids = Arrays.asList(mateId1, mateId2);
        Collections.sort(ids);

        return StringUtils.join(ids, "_");
    }

    private Variant combineBreakendPair(Variant variant, Variant variant1) {

        // Set mate position
        variant.getSv().getBreakend().getMate().setChromosome(variant1.getChromosome());
        variant.getSv().getBreakend().getMate().setPosition(variant1.getStart());

        // Check the second BND does have CIPOS
        if (variant1.getSv() != null
                && variant1.getSv().getCiStartLeft() != null
                && variant1.getSv().getCiStartRight() != null) {
            Integer ciPositionLeft = variant1.getSv().getCiStartLeft();
            Integer ciPositionRight = variant1.getSv().getCiStartRight();

            // Get CIPOS from second BND
            String ciposString = ciPositionLeft + VCFConstants.INFO_FIELD_ARRAY_SEPARATOR + ciPositionRight;

            // Set CIPOS string of the sencond BND as part of the file INFO field in the first BND
            Map<String, String> attributesMap = variant.getStudies().get(0).getFiles().get(0).getAttributes();
            attributesMap.put(MATE_CIPOS, ciposString);

            // CIPOS of the second breakend
            variant.getSv()
                    .getBreakend()
                    .getMate()
                    .setCiPositionLeft(ciPositionLeft);
            variant.getSv()
                    .getBreakend()
                    .getMate()
                    .setCiPositionRight(ciPositionRight);
        // If not, it's a precise call, just position is stored (above)
        }

        return variant;
    }


    private String getMateId(Variant variant) {
        if (variant.getStudies() != null) {
            if (!variant.getStudies().isEmpty()) {
                if (variant.getStudies().size() > 1) {
                    throw new RuntimeException("More than one study found for variant " + variant.toString()
                            + ". Only one expected. Please, check.");
                }
                StudyEntry studyEntry = variant.getStudies().get(0);
                if (studyEntry.getFiles() != null) {
                    if (!studyEntry.getFiles().isEmpty()) {
                        if (studyEntry.getFiles().size() > 1) {
                            throw new RuntimeException("More than one file found for variant " + variant.toString()
                                    + ". Only one expected. Please, check.");
                        }
                        FileEntry fileEntry = studyEntry.getFiles().get(0);

                        if (fileEntry.getAttributes() != null
                                && StringUtils.isNotBlank(
                                        fileEntry.getAttributes().get(MATEID))) {
                            return fileEntry.getAttributes().get(MATEID);

                        }
                    }
                }
            }
        }

        return null;
    }


    private boolean incompleteBatch(List<VariantContext> variantContexts, int batchSize) {
        // batchSize must be > 0
        // If batch already reached required batch size, check phase
        if (variantContexts.size() == batchSize) {
            // if phase should be ignored the batch is complete
            if (ignorePhaseSet) {
                return false;
            // if phase is to be considered, must check if the phase of the next variant context matches the phase
            // of current variant context. In such a case, the batch is incomplete since all variant contexts with
            // the same PS must be part of the same batch regardless of the batch size
            } else {
                // Assumes variantContexts.size() > 0
                VariantContext lastSavedVariantContext = variantContexts.get(variantContexts.size() - 1);

                // Rationale for including a distance threshold in here regardless of the phase set:
                //   Variants within a certain distance might be jointly reported in databases, e.g MNVs in ClinVar and
                //   pop frequency datasets. Two variants with different PS are not necessarily in a different
                //   chromosome copy; the fact of having a different PS simply states that the caller was able to
                //   determine the phase within two non-overlapping genomic regions, and WITHIN each of those regions,
                //   the chromosome copy location can be inferred for corresponding alleles. Alleles of variants each
                //   in a different region (different PS) could actually be located in the same copy.
                //   Since this logic is included for supporting phased variant annotation use case, not including this
                //   distance check could mean that two variants in the same chromosome copy, with different PS, being
                //   reported together by ClinVar (as an MNV, for example) could be missed.
                // Obviously if the phase set is the same the batch is incomplete
                return (abs(lastVariantContext.getStart() - lastSavedVariantContext.getStart())
                            < INTERACTING_DISTANCE_THRESHOLD)
                        || samePhaseSet(lastSavedVariantContext, lastVariantContext);
            }
        }

        // batch is always incomplete if the number of read variant contexts < batchSize, regardless of phase of the
        // items
        return true;
    }

    private boolean samePhaseSet(VariantContext variantContext, VariantContext variantContext1) {
        String phaseSet = getPhaseSet(variantContext);
        if (phaseSet != null) {
            String phaseSet1 = getPhaseSet(variantContext1);
            if (phaseSet1 != null) {
                return phaseSet.equals(phaseSet1);
            }
        }
        return false;
    }

    private String getPhaseSet(VariantContext variantContext) {
        htsjdk.variant.variantcontext.Genotype genotype = variantContext.getGenotype(0);
        Object attribute = genotype.getAnyAttribute(PHASE_SET_TAG);

        if (attribute != null) {
            if (attribute instanceof Collection) {
                throw new RuntimeException("Unexpected PS value: Phase set field found containing multiple values. " +
                        "See: " + variantContext.toString());
            } else if (isMissing(attribute.toString())) {
                return null;
            } else {
                return attribute.toString();
            }
        }
        //Can hts return null fields?
        //ABSOLUTELY, for missing values
        return null;
    }

    private boolean isMissing(String vcfFormatFieldValue) {
        return StringUtils.isBlank(vcfFormatFieldValue)
                || vcfFormatFieldValue.equals(VCF_MISSING_STRING);
    }

    private VariantContext readNextVariantContext() {
        String line;
        while (lineIterator.hasNext()) {
            line = lineIterator.next();
            if (StringUtils.isNotBlank(line) && !line.startsWith("#")) {
                try {
                    return codec.decode(line);
                } catch (RuntimeException e) {
                    logMalformatedLine(line, e);
                    if (failOnError) {
                        throw e;
                    }
                }

            }
        }
        return null;
    }

    private void logMalformatedLine(String line, RuntimeException exception) {
        logger.warn(exception.getMessage());
        for (BiConsumer<String, RuntimeException> consumer : this.malformHandlerSet) {
            consumer.accept(line, exception);
        }
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean close() {
        try {
            if (closeInputStream && inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public List<String> getSampleNames() {
        return header.getSampleNamesInOrder();
    }

    @Override
    public String getHeader() {
        return String.join("\n", headerLines);
    }

    public VCFHeader getVCFHeader() {
        return header;
    }

    public VCFHeaderVersion getVCFHeaderVersion() {
        return codec.getVCFHeaderVersion();
    }

    @Override
    public VariantFileMetadata getVariantFileMetadata() {
        return fileMetadata;
    }

    @Deprecated
    public VariantFileMetadata getMetadata() {
        return getVariantFileMetadata();
    }
}

