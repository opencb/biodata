package org.opencb.biodata.models.sequence;

/**
 * Created by jtarraga on 21/05/15.
 */
    @SuppressWarnings("all")
    @org.apache.avro.specific.AvroGenerated
    public class Read extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
        public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"Read\",\"namespace\":\"org.opencb.biodata.models.sequence\",\"fields\":[{\"name\":\"id\",\"type\":\"string\",\"doc\":\"The read ID.\"},{\"name\":\"sequence\",\"type\":\"string\",\"doc\":\"The full read sequence.\"},{\"name\":\"quality\",\"type\":\"string\",\"doc\":\"The full read quality.\"}]}");
        public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
        /** The read ID. */
        @Deprecated public java.lang.CharSequence id;
        /** The full read sequence. */
        @Deprecated public java.lang.CharSequence sequence;
        /** The full read quality. */
        @Deprecated public java.lang.CharSequence quality;

        /**
         * Default constructor.  Note that this does not initialize fields
         * to their default values from the schema.  If that is desired then
         * one should use <code>newBuilder()</code>.
         */
        public Read() {}

        /**
         * All-args constructor.
         */
        public Read(java.lang.CharSequence id, java.lang.CharSequence sequence, java.lang.CharSequence quality) {
            this.id = id;
            this.sequence = sequence;
            this.quality = quality;
        }

        public org.apache.avro.Schema getSchema() { return SCHEMA$; }
        // Used by DatumWriter.  Applications should not call.
        public java.lang.Object get(int field$) {
            switch (field$) {
                case 0: return id;
                case 1: return sequence;
                case 2: return quality;
                default: throw new org.apache.avro.AvroRuntimeException("Bad index");
            }
        }
        // Used by DatumReader.  Applications should not call.
        @SuppressWarnings(value="unchecked")
        public void put(int field$, java.lang.Object value$) {
            switch (field$) {
                case 0: id = (java.lang.CharSequence)value$; break;
                case 1: sequence = (java.lang.CharSequence)value$; break;
                case 2: quality = (java.lang.CharSequence)value$; break;
                default: throw new org.apache.avro.AvroRuntimeException("Bad index");
            }
        }

        /**
         * Gets the value of the 'id' field.
         * The read ID.   */
        public java.lang.CharSequence getId() {
            return id;
        }

        /**
         * Sets the value of the 'id' field.
         * The read ID.   * @param value the value to set.
         */
        public void setId(java.lang.CharSequence value) {
            this.id = value;
        }

        /**
         * Gets the value of the 'sequence' field.
         * The full read sequence.   */
        public java.lang.CharSequence getSequence() {
            return sequence;
        }

        /**
         * Sets the value of the 'sequence' field.
         * The full read sequence.   * @param value the value to set.
         */
        public void setSequence(java.lang.CharSequence value) {
            this.sequence = value;
        }

        /**
         * Gets the value of the 'quality' field.
         * The full read quality.   */
        public java.lang.CharSequence getQuality() {
            return quality;
        }

        /**
         * Sets the value of the 'quality' field.
         * The full read quality.   * @param value the value to set.
         */
        public void setQuality(java.lang.CharSequence value) {
            this.quality = value;
        }

        /** Creates a new Read RecordBuilder */
        public static Read.Builder newBuilder() {
            return new Read.Builder();
        }

        /** Creates a new Read RecordBuilder by copying an existing Builder */
        public static Read.Builder newBuilder(Read.Builder other) {
            return new Read.Builder(other);
        }

        /** Creates a new Read RecordBuilder by copying an existing Read instance */
        public static Read.Builder newBuilder(Read other) {
            return new Read.Builder(other);
        }

        /**
         * RecordBuilder for Read instances.
         */
        public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<Read>
                implements org.apache.avro.data.RecordBuilder<Read> {

            private java.lang.CharSequence id;
            private java.lang.CharSequence sequence;
            private java.lang.CharSequence quality;

            /** Creates a new Builder */
            private Builder() {
                super(Read.SCHEMA$);
            }

            /** Creates a Builder by copying an existing Builder */
            private Builder(Read.Builder other) {
                super(other);
                if (isValidValue(fields()[0], other.id)) {
                    this.id = data().deepCopy(fields()[0].schema(), other.id);
                    fieldSetFlags()[0] = true;
                }
                if (isValidValue(fields()[1], other.sequence)) {
                    this.sequence = data().deepCopy(fields()[1].schema(), other.sequence);
                    fieldSetFlags()[1] = true;
                }
                if (isValidValue(fields()[2], other.quality)) {
                    this.quality = data().deepCopy(fields()[2].schema(), other.quality);
                    fieldSetFlags()[2] = true;
                }
            }

            /** Creates a Builder by copying an existing Read instance */
            private Builder(Read other) {
                super(Read.SCHEMA$);
                if (isValidValue(fields()[0], other.id)) {
                    this.id = data().deepCopy(fields()[0].schema(), other.id);
                    fieldSetFlags()[0] = true;
                }
                if (isValidValue(fields()[1], other.sequence)) {
                    this.sequence = data().deepCopy(fields()[1].schema(), other.sequence);
                    fieldSetFlags()[1] = true;
                }
                if (isValidValue(fields()[2], other.quality)) {
                    this.quality = data().deepCopy(fields()[2].schema(), other.quality);
                    fieldSetFlags()[2] = true;
                }
            }

            /** Gets the value of the 'id' field */
            public java.lang.CharSequence getId() {
                return id;
            }

            /** Sets the value of the 'id' field */
            public Read.Builder setId(java.lang.CharSequence value) {
                validate(fields()[0], value);
                this.id = value;
                fieldSetFlags()[0] = true;
                return this;
            }

            /** Checks whether the 'id' field has been set */
            public boolean hasId() {
                return fieldSetFlags()[0];
            }

            /** Clears the value of the 'id' field */
            public Read.Builder clearId() {
                id = null;
                fieldSetFlags()[0] = false;
                return this;
            }

            /** Gets the value of the 'sequence' field */
            public java.lang.CharSequence getSequence() {
                return sequence;
            }

            /** Sets the value of the 'sequence' field */
            public Read.Builder setSequence(java.lang.CharSequence value) {
                validate(fields()[1], value);
                this.sequence = value;
                fieldSetFlags()[1] = true;
                return this;
            }

            /** Checks whether the 'sequence' field has been set */
            public boolean hasSequence() {
                return fieldSetFlags()[1];
            }

            /** Clears the value of the 'sequence' field */
            public Read.Builder clearSequence() {
                sequence = null;
                fieldSetFlags()[1] = false;
                return this;
            }

            /** Gets the value of the 'quality' field */
            public java.lang.CharSequence getQuality() {
                return quality;
            }

            /** Sets the value of the 'quality' field */
            public Read.Builder setQuality(java.lang.CharSequence value) {
                validate(fields()[2], value);
                this.quality = value;
                fieldSetFlags()[2] = true;
                return this;
            }

            /** Checks whether the 'quality' field has been set */
            public boolean hasQuality() {
                return fieldSetFlags()[2];
            }

            /** Clears the value of the 'quality' field */
            public Read.Builder clearQuality() {
                quality = null;
                fieldSetFlags()[2] = false;
                return this;
            }

            @Override
            public Read build() {
                try {
                    Read record = new Read();
                    record.id = fieldSetFlags()[0] ? this.id : (java.lang.CharSequence) defaultValue(fields()[0]);
                    record.sequence = fieldSetFlags()[1] ? this.sequence : (java.lang.CharSequence) defaultValue(fields()[1]);
                    record.quality = fieldSetFlags()[2] ? this.quality : (java.lang.CharSequence) defaultValue(fields()[2]);
                    return record;
                } catch (Exception e) {
                    throw new org.apache.avro.AvroRuntimeException(e);
                }
            }
        }
    }

