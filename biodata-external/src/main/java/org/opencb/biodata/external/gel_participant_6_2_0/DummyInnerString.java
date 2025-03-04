/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.opencb.biodata.external.gel_participant_6_2_0;
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class DummyInnerString extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"DummyInnerString\",\"namespace\":\"org.dummy.avro\",\"fields\":[{\"name\":\"myId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private String myId;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public DummyInnerString() {}

  /**
   * All-args constructor.
   */
  public DummyInnerString(String myId) {
    this.myId = myId;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public Object get(int field$) {
    switch (field$) {
    case 0: return myId;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, Object value$) {
    switch (field$) {
    case 0: myId = (String)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'myId' field.
   */
  public String getMyId() {
    return myId;
  }

  /**
   * Sets the value of the 'myId' field.
   * @param value the value to set.
   */
  public void setMyId(String value) {
    this.myId = value;
  }

  /** Creates a new DummyInnerString RecordBuilder */
  public static Builder newBuilder() {
    return new Builder();
  }
  
  /** Creates a new DummyInnerString RecordBuilder by copying an existing Builder */
  public static Builder newBuilder(Builder other) {
    return new Builder(other);
  }
  
  /** Creates a new DummyInnerString RecordBuilder by copying an existing DummyInnerString instance */
  public static Builder newBuilder(DummyInnerString other) {
    return new Builder(other);
  }
  
  /**
   * RecordBuilder for DummyInnerString instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<DummyInnerString>
    implements org.apache.avro.data.RecordBuilder<DummyInnerString> {

    private String myId;

    /** Creates a new Builder */
    private Builder() {
      super(DummyInnerString.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.myId)) {
        this.myId = data().deepCopy(fields()[0].schema(), other.myId);
        fieldSetFlags()[0] = true;
      }
    }
    
    /** Creates a Builder by copying an existing DummyInnerString instance */
    private Builder(DummyInnerString other) {
            super(DummyInnerString.SCHEMA$);
      if (isValidValue(fields()[0], other.myId)) {
        this.myId = data().deepCopy(fields()[0].schema(), other.myId);
        fieldSetFlags()[0] = true;
      }
    }

    /** Gets the value of the 'myId' field */
    public String getMyId() {
      return myId;
    }
    
    /** Sets the value of the 'myId' field */
    public Builder setMyId(String value) {
      validate(fields()[0], value);
      this.myId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'myId' field has been set */
    public boolean hasMyId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'myId' field */
    public Builder clearMyId() {
      myId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    @Override
    public DummyInnerString build() {
      try {
        DummyInnerString record = new DummyInnerString();
        record.myId = fieldSetFlags()[0] ? this.myId : (String) defaultValue(fields()[0]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
