/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.variant.vcf4;

import htsjdk.tribble.TribbleException;
import htsjdk.tribble.util.ParsingUtils;
import htsjdk.variant.variantcontext.*;
import htsjdk.variant.variantcontext.LazyGenotypesContext.LazyData;
import htsjdk.variant.vcf.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author mh719
 *
 */
public class FullVcfCodec extends VCFCodec implements Serializable {

    private static final VCFTextTransformer percentEncodingTextTransformer = new VCFPercentEncodedTextTransformer();
    private static final VCFTextTransformer passThruTextTransformer = new VCFPassThruTextTransformer();
    private VCFTextTransformer vcfTextTransformer;

    public FullVcfCodec() {
    }

    public FullVcfCodec(final VCFHeader header, final VCFHeaderVersion version) {
        this.setVCFHeader(header, version);
        this.vcfTextTransformer = getTextTransformerForVCFVersion(version);
    }

    @Override
    public VCFHeader setVCFHeader(VCFHeader newHeader, VCFHeaderVersion newVersion) {
        VCFHeader vcfHeader = super.setVCFHeader(newHeader, newVersion);
        this.vcfTextTransformer = this.getTextTransformerForVCFVersion(newVersion);
        return vcfHeader;
    }

    private VCFTextTransformer getTextTransformerForVCFVersion(VCFHeaderVersion targetVersion) {
        return targetVersion != null && targetVersion.isAtLeastAsRecentAs(VCFHeaderVersion.VCF4_3)
                ? percentEncodingTextTransformer
                : passThruTextTransformer;
    }

    private static final int[] decodeInts(final String string) {
        List<String> split = ParsingUtils.split(string, ',');
        int [] values = new int[split.size()];
        try {
            for (int i = 0; i < values.length; i++) {
                values[i] = Integer.parseInt(split.get(i));
            }
        } catch (final NumberFormatException e) {
            return null;
        }
        return values;
    }

    public VCFHeaderVersion getVCFHeaderVersion() {
        return this.version;
    }

    /**
     * create a genotype map
     *
     * @param str the string
     * @param alleles the list of alleles
     * @return a mapping of sample name to genotype object
     */
    public LazyGenotypesContext.LazyData createGenotypeMap(final String str,
                                                           final List<Allele> alleles,
                                                           final String chr,
                                                           final int pos) {
        if (genotypeParts == null) {
            genotypeParts = new String[header.getColumnCount() - NUM_STANDARD_FIELDS];
        }

        int nParts = ParsingUtils.split(str, genotypeParts, VCFConstants.FIELD_SEPARATOR_CHAR);
        if (nParts != genotypeParts.length) {
            generateException("there are " + (nParts - 1)
                    + " genotypes while the header requires that " + (genotypeParts.length - 1)
                    + " genotypes be present for all records at " + chr + ":" + pos, lineNo);
        }

        ArrayList<Genotype> genotypes = new ArrayList<>(nParts);

        // get the format keys
        List<String> genotypeKeys = ParsingUtils.split(genotypeParts[0], VCFConstants.GENOTYPE_FIELD_SEPARATOR_CHAR);

        // cycle through the sample names
        Iterator<String> sampleNameIterator = header.getGenotypeSamples().iterator();

        // clear out our allele mapping
        alleleMap.clear();

        // cycle through the genotype strings
        boolean PlIsSet = false;
        for (int genotypeOffset = 1; genotypeOffset < nParts; genotypeOffset++) {
            List<String> genotypeValues = ParsingUtils.split(genotypeParts[genotypeOffset], VCFConstants.GENOTYPE_FIELD_SEPARATOR_CHAR);
            genotypeValues = vcfTextTransformer.decodeText(genotypeValues);

            final String sampleName = sampleNameIterator.next();
            final GenotypeBuilder gb = new GenotypeBuilder(sampleName);

            // check to see if the value list is longer than the key list, which is a problem
            if (genotypeKeys.size() < genotypeValues.size()) {
                generateException("There are too many keys for the sample " + sampleName + ", "
                        + "keys = " + parts[8] + ", "
                        + "values = " + parts[genotypeOffset]);
            }

            int genotypeAlleleLocation = -1;
            if (!genotypeKeys.isEmpty()) {
                gb.maxAttributes(genotypeKeys.size() - 1);

                for (int i = 0; i < genotypeKeys.size(); i++) {
                    final String gtKey = genotypeKeys.get(i);
                    boolean missing = i >= genotypeValues.size();

                    // todo -- all of these on the fly parsing of the missing value should be static constants
                    if (gtKey.equals(VCFConstants.GENOTYPE_KEY)) {
                        genotypeAlleleLocation = i;
                        // opencb modification: add ALL the fields to the attributes. null for missing values!
                        gb.attribute(gtKey, genotypeValues.get(i));
                    } else if (missing) {
                        // if its truly missing (there no provided value) skip adding it to the attributes
                        // opencb modification: add ALL the fields to the attributes. null for missing values!
                        gb.attribute(gtKey, null);
                    } else if (gtKey.equals(VCFConstants.GENOTYPE_FILTER_KEY)) {
                        final List<String> filters = parseFilters(getCachedString(genotypeValues.get(i)));
                        if (filters != null) {
                            gb.filters(filters);
                        }
                        // opencb modification: add ALL the fields to the attributes. null for missing values!
                        gb.attribute(gtKey, filters);
                    } else if (genotypeValues.get(i).equals(VCFConstants.MISSING_VALUE_v4)) {
                        // don't add missing values to the map
                        // opencb modification: add ALL the fields to the attributes. null for missing values!
                        gb.attribute(gtKey, null);
                    } else {
                        if (gtKey.equals(VCFConstants.GENOTYPE_QUALITY_KEY)) {
                            if (genotypeValues.get(i).equals(VCFConstants.MISSING_GENOTYPE_QUALITY_v3)) {
                                gb.noGQ();
                            } else {
                                gb.GQ((int) Math.round(VCFUtils.parseVcfDouble(genotypeValues.get(i))));
                            }
                        } else if (gtKey.equals(VCFConstants.GENOTYPE_ALLELE_DEPTHS)) {
                            gb.AD(decodeInts(genotypeValues.get(i)));
                        } else if (gtKey.equals(VCFConstants.GENOTYPE_PL_KEY)) {
                            gb.PL(decodeInts(genotypeValues.get(i)));
                            PlIsSet = true;
                        } else if (gtKey.equals(VCFConstants.GENOTYPE_LIKELIHOODS_KEY)) {
                            // Do not overwrite PL with data from GL
                            if (!PlIsSet) {
                                gb.PL(GenotypeLikelihoods.fromGLField(genotypeValues.get(i)).getAsPLs());
                            }
                        } else if (gtKey.equals(VCFConstants.DEPTH_KEY)) {
                            gb.DP(Integer.parseInt(genotypeValues.get(i)));
                        }
                        // opencb modification: add ALL the fields to the attributes. null for missing values!
                        gb.attribute(gtKey, genotypeValues.get(i));
                    }
                }
            }

            // check to make sure we found a genotype field if our version is less than 4.1 file
            if ( ! version.isAtLeastAsRecentAs(VCFHeaderVersion.VCF4_1) && genotypeAlleleLocation == -1 )
                generateException("Unable to find the GT field for the record; the GT field is required before VCF4.1");
            if ( genotypeAlleleLocation > 0 )
                generateException("Saw GT field at position "
                        + genotypeAlleleLocation + ", but it must be at the first position for genotypes when present");

            final List<Allele> GTalleles = (genotypeAlleleLocation == -1
                    ? new ArrayList<>(0)
                    : parseGenotypeAlleles(genotypeValues.get(genotypeAlleleLocation), alleles, alleleMap));
            gb.alleles(GTalleles);
            gb.phased(genotypeAlleleLocation != -1
                    && genotypeValues.get(genotypeAlleleLocation).indexOf(VCFConstants.PHASED) != -1);

            // add it to the list
            try {
                genotypes.add(gb.make());
            } catch (TribbleException e) {
                throw new TribbleException.InternalCodecException(e.getMessage() + ", at position " + chr + ":" + pos);
            }
        }

        return new LazyGenotypesContext.LazyData(genotypes, header.getSampleNamesInOrder(), header.getSampleNameToOffset());
    }

}
