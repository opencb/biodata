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

package org.opencb.biodata.tools.variant.writers;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.SequenceOntologyTerm;
import org.opencb.commons.io.DataWriter;

import java.io.*;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.List;
import java.util.Map;

/**
 *
 * @link http://grch37.ensembl.org/info/docs/tools/vep/vep_formats.html#tab
 */
public class EnsemblVepOutputWriter implements DataWriter<Variant> {

    private final Writer dataOutputStream;
    private final boolean closeStream;
    private int writtenVariants;

    public EnsemblVepOutputWriter(OutputStream os) {
        this(new OutputStreamWriter(os));
    }

    public EnsemblVepOutputWriter(Writer dataOutputStream) {
        this.dataOutputStream = dataOutputStream;
        this.closeStream = false;
    }

    @Override
    public boolean pre() {
        writtenVariants = 0;
        try {
//            dataOutputStream.write("## ENSEMBL VARIANT EFFECT PREDICTOR v104.0");
            dataOutputStream.write("## Output produced at " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date.from(Instant.now())) + "\n");

            // Metadata description
//            dataOutputStream.write("## Connected to homo_sapiens_core_104_38 on ensembldb.ensembl.org");
//            dataOutputStream.write("## Using cache in /homes/user/.vep/homo_sapiens/104_GRCh38");
//            dataOutputStream.write("## Using API version 104, DB version 104");
//            dataOutputStream.write("## polyphen version 2.2.2");
//            dataOutputStream.write("## sift version sift5.2.2");
//            dataOutputStream.write("## COSMIC version 78");
//            dataOutputStream.write("## ESP version 20141103");
//            dataOutputStream.write("## gencode version GENCODE 25");
//            dataOutputStream.write("## genebuild version 2014-07");
//            dataOutputStream.write("## HGMD-PUBLIC version 20162");
//            dataOutputStream.write("## regbuild version 16");
//            dataOutputStream.write("## assembly version GRCh38.p7");
//            dataOutputStream.write("## ClinVar version 201610");
//            dataOutputStream.write("## dbSNP version 147");

            // Columns
            dataOutputStream.write("## Column descriptions:\n");
            dataOutputStream.write("## Uploaded_variation : Identifier of uploaded variant as chromosome_start_alleles\n");
            dataOutputStream.write("## Location : Location of variant in standard coordinate format (chr:start or chr:start-end)\n");
            dataOutputStream.write("## Allele : The variant allele used to calculate the consequence\n");
            dataOutputStream.write("## Gene : Stable ID of affected gene\n");
            dataOutputStream.write("## Feature : Stable ID of feature\n");
            dataOutputStream.write("## Feature_type : Type of feature - Transcript, RegulatoryFeature or MotifFeature\n");
            dataOutputStream.write("## Consequence : Consequence type\n");
            dataOutputStream.write("## cDNA_position : Relative position of base pair in cDNA sequence\n");
            dataOutputStream.write("## CDS_position : Relative position of base pair in coding sequence\n");
            dataOutputStream.write("## Protein_position : Relative position of amino acid in protein\n");
            dataOutputStream.write("## Amino_acids : Reference and variant amino acids\n");
            dataOutputStream.write("## Codons : Reference and variant codon sequence\n");
            dataOutputStream.write("## Existing_variation : Identifier(s) of co-located known variants\n");
            dataOutputStream.write("## Extra column keys: This column contains extra information as key=value pairs separated by \";\"\n");
            // Other columns. None extra columns are being used right now
//            dataOutputStream.write("## REF_ALLELE : the reference allele\n");
//            dataOutputStream.write("## HGVSc : the HGVS coding sequence name\n");
//            dataOutputStream.write("## HGVSp : the HGVS protein sequence name\n");
//            dataOutputStream.write("## HGVSg : the HGVS genomic sequence name\n");
//            dataOutputStream.write("## IMPACT : Subjective impact classification of consequence type\n");
//            dataOutputStream.write("## DISTANCE : Shortest distance from variant to transcript\n");
//            dataOutputStream.write("## STRAND : Strand of the feature (1/-1)\n");
//            dataOutputStream.write("## FLAGS : Transcript quality flags\n");
            dataOutputStream.write(buildLine(new StringBuilder(),
                    "#Uploaded_variation",
                    "Location",
                    "Allele", "Gene",
                    "Feature",
                    "Feature_type",
                    "Consequence",
                    "cDNA_position",
                    "CDS_position",
                    "Protein_position",
                    "Amino_acids",
                    "Codons",
                    "Existing_variation",
                    "EXTRA"
            ).toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return true;
    }

    @Override
    public boolean write(List<Variant> batch) {
        for (Variant variant : batch) {
            write(variant);
        }
        return true;
    }

    @Override
    public boolean write(Variant variant) {
        StringBuilder sb = new StringBuilder();
        String variantId, location, alternateAllele;
        String gene, feature, featureType, consequence, cdnaPosition, cdsPosiiton, proteinPosition, aaChange, codonChange;
        Map<String, String> extra;
        try {
//            variantId = variant.getChromosome() + ":" + variant.getStart() + ":" + variant.getReference() + ":" + variant.getAlternate();
            variantId = variant.getChromosome() + "_" + variant.getStart() + "_"
                    + (variant.getReference().isEmpty() ? "-" : variant.getReference())
                    + "/"
                    + (variant.getAlternate().isEmpty() ? "-" : variant.getAlternate());
            location = variant.getChromosome() + ":" + variant.getStart() + "-" + variant.getEnd();
            alternateAllele = variant.getAlternate();

            if (variant.getAnnotation() != null && CollectionUtils.isNotEmpty(variant.getAnnotation().getConsequenceTypes())) {
                for (ConsequenceType consequenceType : variant.getAnnotation().getConsequenceTypes()) {
                    gene = StringUtils.isNotEmpty(consequenceType.getGeneName()) ? consequenceType.getGeneName() : "-";
                    if (StringUtils.isNotEmpty(consequenceType.getTranscriptId())) {
                        feature = consequenceType.getTranscriptId();
                        featureType = "transcript";
                        cdnaPosition = String.valueOf(consequenceType.getCdnaPosition());
                        cdsPosiiton = String.valueOf(consequenceType.getCdsPosition());
                        codonChange = StringUtils.isEmpty(consequenceType.getCodon()) ? "-" : consequenceType.getCodon();
                        if (consequenceType.getProteinVariantAnnotation() != null) {
                            proteinPosition = String.valueOf(consequenceType.getProteinVariantAnnotation().getPosition());
                            aaChange = consequenceType.getProteinVariantAnnotation().getAlternate();
                        } else {
                            proteinPosition = "-";
                            aaChange = "-";
                        }
                    } else {
                        feature = "-";
                        featureType = "-";
                        cdnaPosition = "-";
                        cdsPosiiton = "-";
                        codonChange = "-";
                        proteinPosition = "-";
                        aaChange = "-";
                    }
                    for (SequenceOntologyTerm sequenceOntologyTerm : consequenceType.getSequenceOntologyTerms()) {
                        consequence = sequenceOntologyTerm.getName();
                        buildLine(sb, variantId, location, alternateAllele, gene, feature, featureType, consequence, cdnaPosition, cdsPosiiton, proteinPosition, aaChange, codonChange, "-", "-");
                    }
                }
            } else {
                buildLine(sb, variantId, location, alternateAllele, "-", "-", "-", "-", "-", "-", "-", "-", "-", "-", "-");
            }

            dataOutputStream.write(sb.toString());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        writtenVariants++;
        return true;
    }

    private StringBuilder buildLine(StringBuilder sb, String variantId, String location, String alternateAllele, String gene, String feature,
                                    String featureType, String consequence, String cdnaPosition, String cdsPosiiton, String proteinPosition,
                                    String aaChange, String codonChange, String existingVariant, String extra) {
        return sb
                .append(variantId).append('\t')
                .append(location).append('\t')
                .append(alternateAllele).append('\t')

                .append(gene).append('\t')
                .append(feature).append('\t')
                .append(featureType).append('\t')
                .append(consequence).append('\t')

                .append(cdnaPosition).append('\t')
                .append(cdsPosiiton).append('\t')
                .append(proteinPosition).append('\t')
                .append(aaChange).append('\t')
                .append(codonChange).append('\t')

                .append(existingVariant).append('\t')
                .append(extra).append('\n');
    }

    @Override
    public boolean post() {
        try {
            dataOutputStream.flush();
            return true;
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public boolean close() {
        if (closeStream) {
            try {
                dataOutputStream.close();
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        return true;
    }

    public int getWrittenVariants() {
        return writtenVariants;
    }

    public EnsemblVepOutputWriter setWrittenVariants(int writtenVariants) {
        this.writtenVariants = writtenVariants;
        return this;
    }
}
