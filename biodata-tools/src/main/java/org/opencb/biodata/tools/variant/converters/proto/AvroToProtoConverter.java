package org.opencb.biodata.tools.variant.converters.proto;

import com.google.protobuf.Descriptors;
import com.google.protobuf.MapEntry;
import com.google.protobuf.Message;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class AvroToProtoConverter {
    private final Map<String, Message.Builder> builders = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    protected <Builder extends Message.Builder> Builder toProto(GenericRecord record, Builder builder) {
        return toProto(record, builder, record.getSchema().getName());
    }

    protected <Builder extends Message.Builder> Builder toProto(GenericRecord record, Builder builder, String context) {
        Descriptors.Descriptor descriptor = builder.getDescriptorForType();
        Map<String, Descriptors.FieldDescriptor> protoFieldsMap = new HashMap<>();
        for (Descriptors.FieldDescriptor fieldDescriptor : descriptor.getFields()) {
            protoFieldsMap.put(fieldDescriptor.getName(), fieldDescriptor);
            protoFieldsMap.put(fieldDescriptor.getJsonName(), fieldDescriptor);
        }

        if (descriptor.getFields().size() != record.getSchema().getFields().size()) {
            List<String> missingInAvro = new ArrayList<>();
            List<String> missingInProto = new ArrayList<>();
            for (Schema.Field avroField : record.getSchema().getFields()) {
                if (!protoFieldsMap.containsKey(avroField.name())) {
                    missingInProto.add(avroField.name());
                }
            }
            for (Descriptors.FieldDescriptor protoField : descriptor.getFields()) {
                if (record.getSchema().getField(protoField.getName()) == null
                        && record.getSchema().getField(protoField.getJsonName()) == null) {
                    missingInAvro.add(protoField.getName());
                }
            }
            logger.warn("Number of fields mismatch at " + context + ": Avro fields="
                    + record.getSchema().getFields().size() + ", Proto fields=" + descriptor.getFields().size()
                    + ", missing avro fields: " + missingInAvro + ", missing proto fields: " + missingInProto);
            throw new IllegalArgumentException("Number of fields mismatch at " + context + ": Avro fields="
                    + record.getSchema().getFields().size() + ", Proto fields=" + descriptor.getFields().size());
        }

        for (Schema.Field avroField : record.getSchema().getFields()) {
            Descriptors.FieldDescriptor protoField = protoFieldsMap.get(avroField.name());
            Object protoValue;
            if (protoField == null) {
                throw new IllegalArgumentException("Field " + context + "." + avroField.name() + " not found in Proto message "
                        + descriptor.getFullName());
            }
            Object avroValue = record.get(avroField.pos());
            switch (avroField.schema().getType()) {
                case RECORD:
                    protoValue = toProto((GenericRecord) avroValue, protoField, context + "." + avroField.name());
                    break;
                case ENUM:
                    protoValue = protoField.getEnumType().findValueByName(avroValue.toString());
                    break;
                case ARRAY:
                    Collection<?> avroCollection = (Collection<?>) avroValue;
                    List<Object> protoList = new ArrayList<>(avroCollection.size());
                    for (Object avroElement : avroCollection) {
                        Object protoElement = toProto(avroElement, protoField, context + "." + avroField.name());
                        protoList.add(protoElement);
                    }
                    protoValue = protoList;
                    break;
                case MAP:
                    Map<?, ?> avroMap = (Map<?, ?>) avroValue;
                    Map<Object, Object> protoMap = new HashMap<>();
                    for (Map.Entry<?, ?> entry : avroMap.entrySet()) {
                        Object protoElement = toProto(entry.getValue(), protoField, context + "." + avroField.name());
                        protoMap.put(entry.getKey().toString(), protoElement);
                    }
                    protoValue = protoMap;
                    break;
                case UNION:
                    protoValue = toProto(avroValue, protoField, context + "." + avroField.name());
                    break;
                case FIXED:
                    throw new UnsupportedOperationException("FIXED type not supported yet");
                case STRING:
                case BYTES:
                case INT:
                case LONG:
                case FLOAT:
                case DOUBLE:
                case BOOLEAN:
                case NULL:
                    protoValue = avroValue; // Nothing to do
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported Avro type " + avroField.schema().getType()
                            + " for field " + context + "." + avroField.name());
            }

            if (protoValue != null) {
                if (protoValue instanceof Message.Builder) {
                    protoValue = ((Message.Builder) protoValue).build();
                }
                try {
                    if (protoField.isMapField()) {
                        Map<Object, Object> mapValue = (Map<Object, Object>) protoValue;
                        MapEntry<Object, Object> defaultInstance = MapEntry.newDefaultInstance(protoField.getMessageType(),
                                protoField.getMessageType().getFields().get(0).getLiteType(), "",
                                protoField.getMessageType().getFields().get(1).getLiteType(), null
                        );
                        for (Map.Entry<Object, Object> entry : mapValue.entrySet()) {
                            builder.addRepeatedField(protoField, defaultInstance.newBuilderForType()
                                    .setKey(entry.getKey()).setValue(entry.getValue()).build());
                        }
                    } else if (protoField.isRepeated()) {
                        Collection c = protoValue instanceof Collection ? ((Collection) protoValue) : Collections.singletonList(protoValue);
                        for (Object o1 : c) {
                            builder.addRepeatedField(protoField, o1);
                        }
                    } else {
                        builder.setField(protoField, protoValue);
                    }
                } catch (RuntimeException e) {
                    Descriptors.GenericDescriptor d;
                    if (protoField.getJavaType() == Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                        d = protoField.getMessageType();
                    } else if (protoField.getJavaType() == Descriptors.FieldDescriptor.JavaType.ENUM ) {
                        d = protoField.getEnumType();
                    } else {
                        logger.warn("Type = " + protoField.getJavaType());
                        d = descriptor;
                    }
                    logger.warn("Error adding field '" + context + "." + protoField.getName() + "' type: " + d.toProto() + " value: " + protoValue, e);
                    throw e;
                }
            }
        }

        return builder;
    }

    private Object toProto(Object o, Descriptors.FieldDescriptor fieldDescriptor, String context) {
        if (o instanceof GenericRecord) {
            return toProto((GenericRecord) o, fieldDescriptor, context).build();
        } else if (o instanceof Collection) {
            List<Object> list = new ArrayList<>(((Collection<Object>) o).size());
            for (Object o1 : ((Collection<Object>) o)) {
                Object proto = toProto(o1, fieldDescriptor, context);
                list.add(proto);
            }
            return list;
        } else if (o instanceof Map) {
            Map<Object, Object> map = new HashMap<>();
            Descriptors.FieldDescriptor valueDescriptor = fieldDescriptor.getMessageType().getFields().get(1);
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) o).entrySet()) {
                map.put(entry.getKey().toString(), toProto(entry.getValue(), valueDescriptor, context));
            }
            return map;
        } else if (o instanceof Enum) {
            return fieldDescriptor.getEnumType().findValueByName(o.toString());
        } else {
            return o;
        }
    }

    private Message.Builder toProto(GenericRecord record, Descriptors.FieldDescriptor fieldDescriptor, String context) {
        Descriptors.Descriptor messageType = fieldDescriptor.getMessageType();
        Message.Builder builder = builders.computeIfAbsent(messageType.getFullName(), (key) -> newBuilder(messageType))
                .getDefaultInstanceForType().newBuilderForType();
        return toProto(record, builder, context);
    }

    private static Message.Builder newBuilder(Descriptors.Descriptor messageType) {
        String clazzName = messageType.getFullName().replace("protobuf.opencb.", "");
        clazzName = clazzName.replace(".", "$");
        clazzName = messageType.getFile().toProto().getOptions().getJavaPackage() + "." + messageType.getFile().toProto().getOptions().getJavaOuterClassname() + "$" + clazzName;
        Message.Builder builder;
        try {
            Class<?> aClass = Class.forName(clazzName);
            // Invoke static method "newBuilder"
            builder = (Message.Builder) aClass.getMethod("newBuilder").invoke(null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return builder;
    }

    protected <T> void set(Supplier<T> source, Consumer<T> target) {
        T t = source.get();
        if (Objects.nonNull(t)) {
            target.accept(t);
        }
    }

    protected <TA, TP> void set(Supplier<TA> source, Consumer<TP> target, Function<TA, TP> mapper) {
        TA a = source.get();
        if (Objects.nonNull(a)) {
            TP p = mapper.apply(a);
            target.accept(p);
        }
    }
}
