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

import com.google.protobuf.Descriptors;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.Message;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.opencb.biodata.models.variant.StudyEntry;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantBuilder;
import org.opencb.biodata.models.variant.avro.FileEntry;
import org.opencb.biodata.models.variant.avro.SampleEntry;
import org.opencb.biodata.models.variant.protobuf.VariantAnnotationProto;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.models.variant.stats.VariantStats;
import org.opencb.biodata.tools.Converter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created on 10/01/17.
 *
 * @author Jacobo Coll &lt;jacobo167@gmail.com&gt;
 */
public class VariantAvroToVariantProtoConverter implements Converter<Variant, VariantProto.Variant> {
    private final Logger logger = LoggerFactory.getLogger(getClass());

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
        set(variant::getType, t -> builder.setType(VariantBuilder.getProtoVariantType(t)));

        if (variant.getStudies() != null) {
            for (StudyEntry study : variant.getStudies()) {
                VariantProto.StudyEntry.Builder studyBuilder = toProto(study);
                builder.addStudies(studyBuilder);
            }
        }
        if (variant.getAnnotation() != null) {
            builder.setAnnotation(toProto(variant.getAnnotation(), VariantAnnotationProto.VariantAnnotation.newBuilder()));
        }
        return builder.build();
    }

    private VariantProto.StudyEntry.Builder toProto(StudyEntry study) {
        VariantProto.StudyEntry.Builder studyBuilder = VariantProto.StudyEntry.newBuilder();
        studyBuilder.setStudyId(study.getStudyId());
        set(study::getStudyId, studyBuilder::setStudyId);
        set(study::getSampleDataKeys, studyBuilder::addAllSampleDataKeys);
        for (SampleEntry sampleEntry : study.getSamples()) {
            studyBuilder.addSamples(VariantProto.SampleEntry.newBuilder().addAllData(sampleEntry.getData()));
        }

        for (Map.Entry<String, VariantStats> entry : study.getStats().entrySet()) {
            VariantStats stats = entry.getValue();
            VariantProto.VariantStats.Builder variantStats = toProto(stats);
            studyBuilder.putStats(entry.getKey(), variantStats.build());
        }
        for (FileEntry fileEntry : study.getFiles()) {
            VariantProto.FileEntry.Builder fileBuilder = toProto(fileEntry);
            studyBuilder.addFiles(fileBuilder);
        }
        return studyBuilder;
    }

    private VariantProto.FileEntry.Builder toProto(FileEntry fileEntry) {
        VariantProto.FileEntry.Builder fileBuilder = VariantProto.FileEntry.newBuilder();
        set(fileEntry::getFileId, fileBuilder::setFileId);
        set(fileEntry::getData, fileBuilder::putAllData);
        set(fileEntry::getCall, fileBuilder::setCall);
        return fileBuilder;
    }

    private VariantProto.VariantStats.Builder toProto(VariantStats stats) {
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
        return statsBuilder;
    }

    private Object toProto(Object o, Descriptors.FieldDescriptor fieldDescriptor) {
        if (o instanceof GenericRecord) {
            return toProto((GenericRecord) o, fieldDescriptor).build();
        } else if (o instanceof Collection) {
            return ((Collection<Object>) o).stream().map(o1 -> toProto(o1, fieldDescriptor)).collect(Collectors.toList());
        } else if (o instanceof Map) {
            Map<Object, Object> map = new HashMap<>();
            ((Map<Object, Object>) o).forEach((k, v) -> map.put(k.toString(), toProto(v, fieldDescriptor)));
            return map;
        } else {
            return o;
        }
    }

    private Message.Builder toProto(GenericRecord record, Descriptors.FieldDescriptor fieldDescriptor) {
        Descriptors.Descriptor messageType = fieldDescriptor.getMessageType();
        DynamicMessage.Builder builder = DynamicMessage.newBuilder(messageType);
        return toProto(record, builder);
    }

    private <Builder extends Message.Builder> Builder toProto(GenericRecord record, Builder builder) {

        Descriptors.Descriptor descriptor = builder.getDescriptorForType();
        Map<String, Descriptors.FieldDescriptor> map = new HashMap<>();
        for (Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            map.put(fieldDescriptor.getName(), fieldDescriptor);
            map.put(fieldDescriptor.getJsonName(), fieldDescriptor);
        }

        for (Schema.Field field : record.getSchema().getFields()) {
//            Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(field.name());
            Descriptors.FieldDescriptor fieldDescriptor = map.get(field.name());
            Object o = record.get(field.pos());
            if (fieldDescriptor == null) {
//                logger.warn("Field " + field.name() + " not found! ");
                continue;
            }
            if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                o = toProto(o, fieldDescriptor);
            }
            if (o != null) {
                try {
                    if (fieldDescriptor.isRepeated()) {
                        Collection c = o instanceof Collection ? ((Collection) o) : Collections.singletonList(o);
                        for (Object o1 : c) {
                            if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                                o1 = fieldDescriptor.getEnumType().findValueByName(String.valueOf(o1));
                            }
                            builder.addRepeatedField(fieldDescriptor, o1);
                        }
                    } else {
                        if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM) {
                            o = fieldDescriptor.getEnumType().findValueByName(String.valueOf(o));
                        }
                        builder.setField(fieldDescriptor, o);
                    }
                } catch (RuntimeException e) {
                    Descriptors.GenericDescriptor d;
                    if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        d = fieldDescriptor.getMessageType();
                    } else if (fieldDescriptor.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM ) {
                        d = fieldDescriptor.getEnumType();
                    } else {
                        logger.warn("Type = " + fieldDescriptor.getJavaType());
                        d = descriptor;
                    }
                    logger.warn("Error adding field '" + fieldDescriptor.getName() + "' type: " + d.toProto() + " value: " + o);
                //    throw e;
                }
            }
        }

        return builder;
    }

    private <T> void set(Supplier<T> source, Consumer<T> target) {
        T t = source.get();
        if (Objects.nonNull(t)) {
            target.accept(t);
        }
    }
}
