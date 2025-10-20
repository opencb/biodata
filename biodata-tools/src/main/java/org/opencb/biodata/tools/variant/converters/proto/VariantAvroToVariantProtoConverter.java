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

package org.opencb.biodata.tools.variant.converters.proto;

import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.*;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.commons.Converter;

/**
 * Created on 10/01/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAvroToVariantProtoConverter extends AvroToProtoConverter implements Converter<Variant, VariantProto.Variant> {

    @Override
    public VariantProto.Variant convert(Variant variant) {
        VariantProto.Variant.Builder builder = VariantProto.Variant.newBuilder();

        set(variant::getChromosome, builder::setChromosome);
        set(variant::getId, builder::setId);
        set(variant::getNames, builder::addAllNames);
        set(variant::getStart, builder::setStart);
        set(variant::getEnd, builder::setEnd);
        set(variant::getLength, builder::setLength);
        set(variant::getReference, builder::setReference);
        set(variant::getAlternate, builder::setAlternate);
        set(variant::getStrand, builder::setStrand);
        set(variant::getType, t -> builder.setType(VariantBuilder.getProtoVariantType(t)));
        set(variant::getSv, builder::setSv, sv -> toProto(sv, VariantProto.StructuralVariation.newBuilder(), "variant.sv"));

        if (variant.getStudies() != null) {
            for (org.opencb.biodata.models.variant.avro.StudyEntry studyEntry : variant.getImpl().getStudies()) {
                VariantProto.StudyEntry.Builder studyBuilder = toProto(studyEntry);
                builder.addStudies(studyBuilder);
            }
        }
        if (variant.getAnnotation() != null) {
            builder.setAnnotation(toProto(variant.getAnnotation(), VariantAnnotationProto.VariantAnnotation.newBuilder(), "variant.annotation"));
        }
        return builder.build();
    }

    private VariantProto.StudyEntry.Builder toProto(org.opencb.biodata.models.variant.avro.StudyEntry study) {
        VariantProto.StudyEntry.Builder studyBuilder = VariantProto.StudyEntry.newBuilder();
        studyBuilder.setStudyId(study.getStudyId());
        set(study::getStudyId, studyBuilder::setStudyId);
        set(study::getSampleDataKeys, studyBuilder::addAllSampleDataKeys);
        for (SampleEntry sampleEntry : study.getSamples()) {
            studyBuilder.addSamples(toProto(sampleEntry)
            );
        }

        for (org.opencb.biodata.models.variant.avro.VariantStats stats : study.getStats()) {
            VariantProto.VariantStats.Builder variantStats = toProto(stats);
            studyBuilder.addStats(variantStats.build());
        }
        for (FileEntry fileEntry : study.getFiles()) {
            VariantProto.FileEntry.Builder fileBuilder = toProto(fileEntry);
            studyBuilder.addFiles(fileBuilder);
        }
        if (study.getSecondaryAlternates() != null) {
            for (AlternateCoordinate secondaryAlternate : study.getSecondaryAlternates()) {
                VariantProto.AlternateCoordinate.Builder altBuilder = VariantProto.AlternateCoordinate.newBuilder();
                set(secondaryAlternate::getChromosome, altBuilder::setChromosome);
                set(secondaryAlternate::getStart, altBuilder::setStart);
                set(secondaryAlternate::getEnd, altBuilder::setEnd);
                set(secondaryAlternate::getReference, altBuilder::setReference);
                set(secondaryAlternate::getAlternate, altBuilder::setAlternate);
                set(secondaryAlternate::getType, t -> altBuilder.setType(VariantBuilder.getProtoVariantType(t)));
                studyBuilder.addSecondaryAlternates(altBuilder);
            }
        }
        if (study.getScores() != null) {
            for (VariantScore score : study.getScores()) {
                VariantProto.VariantScore.Builder scoreBuilder = VariantProto.VariantScore.newBuilder();
                set(score::getId, scoreBuilder::setId);
                set(score::getScore, scoreBuilder::setScore);
                set(score::getPValue, scoreBuilder::setPValue);
                set(score::getCohort1, scoreBuilder::setCohort1);
                set(score::getCohort2, scoreBuilder::setCohort2);
                studyBuilder.addScores(scoreBuilder);
            }
        }
        if (study.getIssues() != null) {
            for (IssueEntry issue : study.getIssues()) {
                VariantProto.IssueEntry.Builder issueBuilder = VariantProto.IssueEntry.newBuilder();
                set(issue::getType, issueType -> issueBuilder.setType(VariantProto.IssueEntry.IssueType.valueOf(issueType.name())));
                set(issue::getData, issueBuilder::putAllData);
                set(issue::getSample, sample -> issueBuilder.setSample(toProto(sample)));

                studyBuilder.addIssues(issueBuilder);
            }
        }
        return studyBuilder;
    }

    private static VariantProto.SampleEntry toProto(SampleEntry sampleEntry) {
        return VariantProto.SampleEntry.newBuilder()
                .setSampleId(sampleEntry.getSampleId())
                .addAllData(sampleEntry.getData())
                .setFileIndex(sampleEntry.getFileIndex())
                .build();
    }

    private VariantProto.FileEntry.Builder toProto(FileEntry fileEntry) {
        VariantProto.FileEntry.Builder fileBuilder = VariantProto.FileEntry.newBuilder();
        set(fileEntry::getFileId, fileBuilder::setFileId);
        set(fileEntry::getData, fileBuilder::putAllData);
        set(fileEntry::getCall, originalCall -> {
            fileBuilder.setCall(VariantProto.OriginalCall.newBuilder()
                    .setVariantId(originalCall.getVariantId())
                    .setAlleleIndex(originalCall.getAlleleIndex()));
        });
        return fileBuilder;
    }

    private VariantProto.VariantStats.Builder toProto(org.opencb.biodata.models.variant.avro.VariantStats stats) {
        VariantProto.VariantStats.Builder statsBuilder = VariantProto.VariantStats.newBuilder();
        set(stats::getAlleleCount, statsBuilder::setAlleleCount);
        set(stats::getRefAlleleCount, statsBuilder::setRefAlleleCount);
        set(stats::getAltAlleleCount, statsBuilder::setAltAlleleCount);
        set(stats::getGenotypeCount, map -> map.forEach((gt, n) -> statsBuilder.putGenotypeCount(gt.toString(), n)));
        set(stats::getGenotypeFreq, map -> map.forEach((gt, n) -> statsBuilder.putGenotypeFreq(gt.toString(), n)));
        set(stats::getMissingAlleleCount, statsBuilder::setMissingAlleleCount);
        set(stats::getMissingGenotypeCount, statsBuilder::setMissingGenotypeCount);
        set(stats::getRefAlleleFreq, statsBuilder::setRefAlleleFreq);
        set(stats::getAltAlleleFreq, statsBuilder::setAltAlleleFreq);
        set(stats::getMaf, statsBuilder::setMaf);
        set(stats::getMgf, statsBuilder::setMgf);
        set(stats::getMafAllele, statsBuilder::setMafAllele);
        set(stats::getMgfGenotype, statsBuilder::setMgfGenotype);
        set(stats::getFilterCount, map -> map.forEach(statsBuilder::putFilterCount));
        set(stats::getFilterFreq, map -> map.forEach(statsBuilder::putFilterFreq));
        set(stats::getQualityAvg, statsBuilder::setQualityAvg);
        set(stats::getCohortId, statsBuilder::setCohortId);
        set(stats::getSampleCount, statsBuilder::setSampleCount);
        set(stats::getFileCount, statsBuilder::setFileCount);
        set(stats::getQualityCount, statsBuilder::setQualityCount);
        return statsBuilder;
    }
}
