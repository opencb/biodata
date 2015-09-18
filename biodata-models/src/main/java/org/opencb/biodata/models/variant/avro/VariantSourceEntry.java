/**
 * Autogenerated by Avro
 * 
 * DO NOT EDIT DIRECTLY
 */
package org.opencb.biodata.models.variant.avro;  
@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public class VariantSourceEntry extends org.apache.avro.specific.SpecificRecordBase implements org.apache.avro.specific.SpecificRecord {
  public static final org.apache.avro.Schema SCHEMA$ = new org.apache.avro.Schema.Parser().parse("{\"type\":\"record\",\"name\":\"VariantSourceEntry\",\"namespace\":\"org.opencb.biodata.models.variant.avro\",\"fields\":[{\"name\":\"fileId\",\"type\":[\"null\",\"string\"]},{\"name\":\"studyId\",\"type\":[\"null\",\"string\"]},{\"name\":\"secondaryAlternates\",\"type\":[\"null\",{\"type\":\"array\",\"items\":\"string\"}],\"default\":null},{\"name\":\"format\",\"type\":\"string\"},{\"name\":\"samplesData\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"map\",\"values\":\"string\"}}},{\"name\":\"cohortStats\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"record\",\"name\":\"VariantStats\",\"fields\":[{\"name\":\"refAllele\",\"type\":[\"null\",\"string\"]},{\"name\":\"altAllele\",\"type\":[\"null\",\"string\"]},{\"name\":\"refAlleleCount\",\"type\":[\"null\",\"int\"]},{\"name\":\"altAlleleCount\",\"type\":[\"null\",\"int\"]},{\"name\":\"missingAlleles\",\"type\":[\"null\",\"int\"]},{\"name\":\"missingGenotypes\",\"type\":[\"null\",\"int\"]},{\"name\":\"refAlleleFreq\",\"type\":[\"null\",\"float\"]},{\"name\":\"altAlleleFreq\",\"type\":[\"null\",\"float\"]},{\"name\":\"maf\",\"type\":[\"null\",\"float\"]},{\"name\":\"mgf\",\"type\":[\"null\",\"float\"]},{\"name\":\"mafAllele\",\"type\":[\"null\",\"string\"]},{\"name\":\"mgfGenotype\",\"type\":[\"null\",\"string\"]},{\"name\":\"passedFilters\",\"type\":[\"null\",\"boolean\"]},{\"name\":\"mendelianErrors\",\"type\":[\"null\",\"int\"]},{\"name\":\"casesPercentDominant\",\"type\":[\"null\",\"float\"]},{\"name\":\"controlsPercentDominant\",\"type\":[\"null\",\"float\"]},{\"name\":\"casesPercentRecessive\",\"type\":[\"null\",\"float\"]},{\"name\":\"controlsPercentRecessive\",\"type\":[\"null\",\"float\"]},{\"name\":\"quality\",\"type\":[\"null\",\"float\"]},{\"name\":\"numSamples\",\"type\":[\"null\",\"int\"]},{\"name\":\"variantType\",\"type\":{\"type\":\"enum\",\"name\":\"VariantType\",\"symbols\":[\"SNP\",\"SNV\",\"MNP\",\"MNV\",\"INDEL\",\"SV\",\"CNV\",\"NO_VARIATION\",\"SYMBOLIC\",\"MIXED\"]}},{\"name\":\"hw\",\"type\":{\"type\":\"record\",\"name\":\"VariantHardyWeinbergStats\",\"fields\":[{\"name\":\"chi2\",\"type\":[\"null\",\"float\"]},{\"name\":\"pValue\",\"type\":[\"null\",\"float\"]},{\"name\":\"n\",\"type\":[\"null\",\"int\"]},{\"name\":\"n_AA_11\",\"type\":[\"null\",\"int\"]},{\"name\":\"n_Aa_10\",\"type\":[\"null\",\"int\"]},{\"name\":\"n_aa_00\",\"type\":[\"null\",\"int\"]},{\"name\":\"e_AA_11\",\"type\":[\"null\",\"float\"]},{\"name\":\"e_Aa_10\",\"type\":[\"null\",\"float\"]},{\"name\":\"e_aa_00\",\"type\":[\"null\",\"float\"]},{\"name\":\"p\",\"type\":[\"null\",\"float\"]},{\"name\":\"q\",\"type\":[\"null\",\"float\"]}]}}]}}},{\"name\":\"attributes\",\"type\":{\"type\":\"map\",\"values\":\"string\"}}]}");
  public static org.apache.avro.Schema getClassSchema() { return SCHEMA$; }
   private java.lang.CharSequence fileId;
   private java.lang.CharSequence studyId;
   private java.util.List<java.lang.CharSequence> secondaryAlternates;
   private java.lang.CharSequence format;
   private java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> samplesData;
   private java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> cohortStats;
   private java.util.Map<java.lang.CharSequence,java.lang.CharSequence> attributes;

  /**
   * Default constructor.  Note that this does not initialize fields
   * to their default values from the schema.  If that is desired then
   * one should use <code>newBuilder()</code>. 
   */
  public VariantSourceEntry() {}

  /**
   * All-args constructor.
   */
  public VariantSourceEntry(java.lang.CharSequence fileId, java.lang.CharSequence studyId, java.util.List<java.lang.CharSequence> secondaryAlternates, java.lang.CharSequence format, java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> samplesData, java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> cohortStats, java.util.Map<java.lang.CharSequence,java.lang.CharSequence> attributes) {
    this.fileId = fileId;
    this.studyId = studyId;
    this.secondaryAlternates = secondaryAlternates;
    this.format = format;
    this.samplesData = samplesData;
    this.cohortStats = cohortStats;
    this.attributes = attributes;
  }

  public org.apache.avro.Schema getSchema() { return SCHEMA$; }
  // Used by DatumWriter.  Applications should not call. 
  public java.lang.Object get(int field$) {
    switch (field$) {
    case 0: return fileId;
    case 1: return studyId;
    case 2: return secondaryAlternates;
    case 3: return format;
    case 4: return samplesData;
    case 5: return cohortStats;
    case 6: return attributes;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }
  // Used by DatumReader.  Applications should not call. 
  @SuppressWarnings(value="unchecked")
  public void put(int field$, java.lang.Object value$) {
    switch (field$) {
    case 0: fileId = (java.lang.CharSequence)value$; break;
    case 1: studyId = (java.lang.CharSequence)value$; break;
    case 2: secondaryAlternates = (java.util.List<java.lang.CharSequence>)value$; break;
    case 3: format = (java.lang.CharSequence)value$; break;
    case 4: samplesData = (java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>>)value$; break;
    case 5: cohortStats = (java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats>)value$; break;
    case 6: attributes = (java.util.Map<java.lang.CharSequence,java.lang.CharSequence>)value$; break;
    default: throw new org.apache.avro.AvroRuntimeException("Bad index");
    }
  }

  /**
   * Gets the value of the 'fileId' field.
   */
  public java.lang.CharSequence getFileId() {
    return fileId;
  }

  /**
   * Sets the value of the 'fileId' field.
   * @param value the value to set.
   */
  public void setFileId(java.lang.CharSequence value) {
    this.fileId = value;
  }

  /**
   * Gets the value of the 'studyId' field.
   */
  public java.lang.CharSequence getStudyId() {
    return studyId;
  }

  /**
   * Sets the value of the 'studyId' field.
   * @param value the value to set.
   */
  public void setStudyId(java.lang.CharSequence value) {
    this.studyId = value;
  }

  /**
   * Gets the value of the 'secondaryAlternates' field.
   */
  public java.util.List<java.lang.CharSequence> getSecondaryAlternates() {
    return secondaryAlternates;
  }

  /**
   * Sets the value of the 'secondaryAlternates' field.
   * @param value the value to set.
   */
  public void setSecondaryAlternates(java.util.List<java.lang.CharSequence> value) {
    this.secondaryAlternates = value;
  }

  /**
   * Gets the value of the 'format' field.
   */
  public java.lang.CharSequence getFormat() {
    return format;
  }

  /**
   * Sets the value of the 'format' field.
   * @param value the value to set.
   */
  public void setFormat(java.lang.CharSequence value) {
    this.format = value;
  }

  /**
   * Gets the value of the 'samplesData' field.
   */
  public java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> getSamplesData() {
    return samplesData;
  }

  /**
   * Sets the value of the 'samplesData' field.
   * @param value the value to set.
   */
  public void setSamplesData(java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> value) {
    this.samplesData = value;
  }

  /**
   * Gets the value of the 'cohortStats' field.
   */
  public java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> getCohortStats() {
    return cohortStats;
  }

  /**
   * Sets the value of the 'cohortStats' field.
   * @param value the value to set.
   */
  public void setCohortStats(java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> value) {
    this.cohortStats = value;
  }

  /**
   * Gets the value of the 'attributes' field.
   */
  public java.util.Map<java.lang.CharSequence,java.lang.CharSequence> getAttributes() {
    return attributes;
  }

  /**
   * Sets the value of the 'attributes' field.
   * @param value the value to set.
   */
  public void setAttributes(java.util.Map<java.lang.CharSequence,java.lang.CharSequence> value) {
    this.attributes = value;
  }

  /** Creates a new VariantSourceEntry RecordBuilder */
  public static org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder newBuilder() {
    return new org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder();
  }
  
  /** Creates a new VariantSourceEntry RecordBuilder by copying an existing Builder */
  public static org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder newBuilder(org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder other) {
    return new org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder(other);
  }
  
  /** Creates a new VariantSourceEntry RecordBuilder by copying an existing VariantSourceEntry instance */
  public static org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder newBuilder(org.opencb.biodata.models.variant.avro.VariantSourceEntry other) {
    return new org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder(other);
  }
  
  /**
   * RecordBuilder for VariantSourceEntry instances.
   */
  public static class Builder extends org.apache.avro.specific.SpecificRecordBuilderBase<VariantSourceEntry>
    implements org.apache.avro.data.RecordBuilder<VariantSourceEntry> {

    private java.lang.CharSequence fileId;
    private java.lang.CharSequence studyId;
    private java.util.List<java.lang.CharSequence> secondaryAlternates;
    private java.lang.CharSequence format;
    private java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> samplesData;
    private java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> cohortStats;
    private java.util.Map<java.lang.CharSequence,java.lang.CharSequence> attributes;

    /** Creates a new Builder */
    private Builder() {
      super(org.opencb.biodata.models.variant.avro.VariantSourceEntry.SCHEMA$);
    }
    
    /** Creates a Builder by copying an existing Builder */
    private Builder(org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder other) {
      super(other);
      if (isValidValue(fields()[0], other.fileId)) {
        this.fileId = data().deepCopy(fields()[0].schema(), other.fileId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.studyId)) {
        this.studyId = data().deepCopy(fields()[1].schema(), other.studyId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.secondaryAlternates)) {
        this.secondaryAlternates = data().deepCopy(fields()[2].schema(), other.secondaryAlternates);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.format)) {
        this.format = data().deepCopy(fields()[3].schema(), other.format);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.samplesData)) {
        this.samplesData = data().deepCopy(fields()[4].schema(), other.samplesData);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.cohortStats)) {
        this.cohortStats = data().deepCopy(fields()[5].schema(), other.cohortStats);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.attributes)) {
        this.attributes = data().deepCopy(fields()[6].schema(), other.attributes);
        fieldSetFlags()[6] = true;
      }
    }
    
    /** Creates a Builder by copying an existing VariantSourceEntry instance */
    private Builder(org.opencb.biodata.models.variant.avro.VariantSourceEntry other) {
            super(org.opencb.biodata.models.variant.avro.VariantSourceEntry.SCHEMA$);
      if (isValidValue(fields()[0], other.fileId)) {
        this.fileId = data().deepCopy(fields()[0].schema(), other.fileId);
        fieldSetFlags()[0] = true;
      }
      if (isValidValue(fields()[1], other.studyId)) {
        this.studyId = data().deepCopy(fields()[1].schema(), other.studyId);
        fieldSetFlags()[1] = true;
      }
      if (isValidValue(fields()[2], other.secondaryAlternates)) {
        this.secondaryAlternates = data().deepCopy(fields()[2].schema(), other.secondaryAlternates);
        fieldSetFlags()[2] = true;
      }
      if (isValidValue(fields()[3], other.format)) {
        this.format = data().deepCopy(fields()[3].schema(), other.format);
        fieldSetFlags()[3] = true;
      }
      if (isValidValue(fields()[4], other.samplesData)) {
        this.samplesData = data().deepCopy(fields()[4].schema(), other.samplesData);
        fieldSetFlags()[4] = true;
      }
      if (isValidValue(fields()[5], other.cohortStats)) {
        this.cohortStats = data().deepCopy(fields()[5].schema(), other.cohortStats);
        fieldSetFlags()[5] = true;
      }
      if (isValidValue(fields()[6], other.attributes)) {
        this.attributes = data().deepCopy(fields()[6].schema(), other.attributes);
        fieldSetFlags()[6] = true;
      }
    }

    /** Gets the value of the 'fileId' field */
    public java.lang.CharSequence getFileId() {
      return fileId;
    }
    
    /** Sets the value of the 'fileId' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setFileId(java.lang.CharSequence value) {
      validate(fields()[0], value);
      this.fileId = value;
      fieldSetFlags()[0] = true;
      return this; 
    }
    
    /** Checks whether the 'fileId' field has been set */
    public boolean hasFileId() {
      return fieldSetFlags()[0];
    }
    
    /** Clears the value of the 'fileId' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearFileId() {
      fileId = null;
      fieldSetFlags()[0] = false;
      return this;
    }

    /** Gets the value of the 'studyId' field */
    public java.lang.CharSequence getStudyId() {
      return studyId;
    }
    
    /** Sets the value of the 'studyId' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setStudyId(java.lang.CharSequence value) {
      validate(fields()[1], value);
      this.studyId = value;
      fieldSetFlags()[1] = true;
      return this; 
    }
    
    /** Checks whether the 'studyId' field has been set */
    public boolean hasStudyId() {
      return fieldSetFlags()[1];
    }
    
    /** Clears the value of the 'studyId' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearStudyId() {
      studyId = null;
      fieldSetFlags()[1] = false;
      return this;
    }

    /** Gets the value of the 'secondaryAlternates' field */
    public java.util.List<java.lang.CharSequence> getSecondaryAlternates() {
      return secondaryAlternates;
    }
    
    /** Sets the value of the 'secondaryAlternates' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setSecondaryAlternates(java.util.List<java.lang.CharSequence> value) {
      validate(fields()[2], value);
      this.secondaryAlternates = value;
      fieldSetFlags()[2] = true;
      return this; 
    }
    
    /** Checks whether the 'secondaryAlternates' field has been set */
    public boolean hasSecondaryAlternates() {
      return fieldSetFlags()[2];
    }
    
    /** Clears the value of the 'secondaryAlternates' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearSecondaryAlternates() {
      secondaryAlternates = null;
      fieldSetFlags()[2] = false;
      return this;
    }

    /** Gets the value of the 'format' field */
    public java.lang.CharSequence getFormat() {
      return format;
    }
    
    /** Sets the value of the 'format' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setFormat(java.lang.CharSequence value) {
      validate(fields()[3], value);
      this.format = value;
      fieldSetFlags()[3] = true;
      return this; 
    }
    
    /** Checks whether the 'format' field has been set */
    public boolean hasFormat() {
      return fieldSetFlags()[3];
    }
    
    /** Clears the value of the 'format' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearFormat() {
      format = null;
      fieldSetFlags()[3] = false;
      return this;
    }

    /** Gets the value of the 'samplesData' field */
    public java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> getSamplesData() {
      return samplesData;
    }
    
    /** Sets the value of the 'samplesData' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setSamplesData(java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>> value) {
      validate(fields()[4], value);
      this.samplesData = value;
      fieldSetFlags()[4] = true;
      return this; 
    }
    
    /** Checks whether the 'samplesData' field has been set */
    public boolean hasSamplesData() {
      return fieldSetFlags()[4];
    }
    
    /** Clears the value of the 'samplesData' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearSamplesData() {
      samplesData = null;
      fieldSetFlags()[4] = false;
      return this;
    }

    /** Gets the value of the 'cohortStats' field */
    public java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> getCohortStats() {
      return cohortStats;
    }
    
    /** Sets the value of the 'cohortStats' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setCohortStats(java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats> value) {
      validate(fields()[5], value);
      this.cohortStats = value;
      fieldSetFlags()[5] = true;
      return this; 
    }
    
    /** Checks whether the 'cohortStats' field has been set */
    public boolean hasCohortStats() {
      return fieldSetFlags()[5];
    }
    
    /** Clears the value of the 'cohortStats' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearCohortStats() {
      cohortStats = null;
      fieldSetFlags()[5] = false;
      return this;
    }

    /** Gets the value of the 'attributes' field */
    public java.util.Map<java.lang.CharSequence,java.lang.CharSequence> getAttributes() {
      return attributes;
    }
    
    /** Sets the value of the 'attributes' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder setAttributes(java.util.Map<java.lang.CharSequence,java.lang.CharSequence> value) {
      validate(fields()[6], value);
      this.attributes = value;
      fieldSetFlags()[6] = true;
      return this; 
    }
    
    /** Checks whether the 'attributes' field has been set */
    public boolean hasAttributes() {
      return fieldSetFlags()[6];
    }
    
    /** Clears the value of the 'attributes' field */
    public org.opencb.biodata.models.variant.avro.VariantSourceEntry.Builder clearAttributes() {
      attributes = null;
      fieldSetFlags()[6] = false;
      return this;
    }

    @Override
    public VariantSourceEntry build() {
      try {
        VariantSourceEntry record = new VariantSourceEntry();
        record.fileId = fieldSetFlags()[0] ? this.fileId : (java.lang.CharSequence) defaultValue(fields()[0]);
        record.studyId = fieldSetFlags()[1] ? this.studyId : (java.lang.CharSequence) defaultValue(fields()[1]);
        record.secondaryAlternates = fieldSetFlags()[2] ? this.secondaryAlternates : (java.util.List<java.lang.CharSequence>) defaultValue(fields()[2]);
        record.format = fieldSetFlags()[3] ? this.format : (java.lang.CharSequence) defaultValue(fields()[3]);
        record.samplesData = fieldSetFlags()[4] ? this.samplesData : (java.util.Map<java.lang.CharSequence,java.util.Map<java.lang.CharSequence,java.lang.CharSequence>>) defaultValue(fields()[4]);
        record.cohortStats = fieldSetFlags()[5] ? this.cohortStats : (java.util.Map<java.lang.CharSequence,org.opencb.biodata.models.variant.avro.VariantStats>) defaultValue(fields()[5]);
        record.attributes = fieldSetFlags()[6] ? this.attributes : (java.util.Map<java.lang.CharSequence,java.lang.CharSequence>) defaultValue(fields()[6]);
        return record;
      } catch (Exception e) {
        throw new org.apache.avro.AvroRuntimeException(e);
      }
    }
  }
}
