package org.opencb.biodata.tools.variant.converters.proto;

import junit.framework.TestCase;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericRecord;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.VariantAvro;
import org.opencb.biodata.models.variant.protobuf.VariantProto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class VariantAvroToVariantProtoConverterTest extends TestCase {

    public void testConvert() throws Exception {
        for (int r = 0; r < 100; r++) {
            VariantAvro record = getRandomValue(VariantAvro.getClassSchema());

            AvroToProtoConverter plainConverter = new VariantAvroToVariantProtoConverter();
            VariantAvroToVariantProtoConverter converter = new VariantAvroToVariantProtoConverter();

            VariantProto.Variant proto1 = converter.convert(new Variant(record));
            VariantProto.Variant proto2 = plainConverter.toProto(record, VariantProto.Variant.newBuilder()).build();

            String[] proto1Lines = proto1.toString().split("\n");
            String[] proto2Lines = proto2.toString().split("\n");
            assertEquals(proto1Lines.length, proto2Lines.length);
            for (int i = 0; i < proto1Lines.length; i++) {
                assertEquals(proto1Lines[i], proto2Lines[i]);
            }
            assertEquals(proto1, proto2);
        }
    }

    private static <T extends GenericRecord> T getRandomValue(Schema schema) throws Exception {
        GenericRecord record = (GenericRecord) Class.forName(schema.getFullName()).newInstance();

        for (Schema.Field subField : schema.getFields()) {
            Object value = getRandomValue(subField);
            record.put(subField.pos(), value);
        }
        return (T) record;
    }

    private static Object getRandomValue(Schema.Field field) throws Exception {
        switch (field.schema().getType()) {
            case RECORD:
                Schema schema = field.schema();
                GenericRecord record = (GenericRecord) Class.forName(schema.getFullName()).newInstance();

                for (Schema.Field subField : schema.getFields()) {
                    Object value = getRandomValue(subField);
                    record.put(subField.pos(), value);
                }
                return record;
            case ARRAY:
                List<Object> list = new ArrayList<>(3);
                for (int i = 0; i < 3; i++) {
                    list.add(getRandomValue(field.schema().getElementType()));
                }
                return list;
            case MAP:
                Map<String, Object> map = new LinkedHashMap<>();
                map.put("v1", getRandomValue(field.schema().getValueType()));
                map.put("v2", getRandomValue(field.schema().getValueType()));
                return map;
            case UNION:
                for (Schema s : field.schema().getTypes()) {
                    if (s.getType() != Schema.Type.NULL) {
                        return getRandomValue(s);
                    }
                }
                return null;
            case STRING:
                return RandomStringUtils.randomAlphanumeric(10);
            case INT:
                return RandomUtils.nextInt();
            case LONG:
                return RandomUtils.nextLong();
            case FLOAT:
                return RandomUtils.nextFloat();
            case DOUBLE:
                return RandomUtils.nextDouble();
            case BOOLEAN:
                return RandomUtils.nextBoolean();
            case NULL:
                return null;
            case ENUM:
                Class<? extends Enum> aClass = (Class<? extends Enum>) Class.forName(field.schema().getFullName());
                Object[] constants = aClass.getEnumConstants();
                return constants[RandomUtils.nextInt(0, constants.length)];
            default:
                throw new IllegalArgumentException("Unsupported type " + field.schema().getType());
        }
    }
}