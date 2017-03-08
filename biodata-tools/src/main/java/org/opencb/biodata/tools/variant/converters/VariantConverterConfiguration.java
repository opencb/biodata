package org.opencb.biodata.tools.variant.converters;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jtarraga on 07/03/17.
 */
public class VariantConverterConfiguration {

    public class FormatField {
        public String name;
        public String type;
        public String desc;

        public FormatField(String name, String type, String desc) {
            this.name = name;
            this.type = type;
            this.desc = desc;
        }
    }

    private Set<String> cohortNames;
    private Set<String> annotations;
    private Set<String> sampleNames;
    private Set<FormatField> formats;

    public VariantConverterConfiguration(Set<String> cohortNames, Set<String> annotations, Set<String> sampleNames,
                                         Set<FormatField> formats) {
        this.cohortNames = cohortNames;
        this.annotations = annotations;
        this.sampleNames = sampleNames;
        this.formats = formats;
    }

    public VariantConverterConfiguration(List<String> cohortNames, List<String> annotations, List<String> sampleNames,
                                         List<FormatField> formats) {
        this.cohortNames = new HashSet<>(cohortNames);
        this.annotations = new HashSet<>(annotations);
        this.sampleNames = new HashSet<>(sampleNames);
        this.formats = new HashSet<>(formats);
    }

    public Set<String> getCohortNames() {
        return cohortNames;
    }

    public void setCohortNames(Set<String> cohortNames) {
        this.cohortNames = cohortNames;
    }

    public Set<String> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(Set<String> annotations) {
        this.annotations = annotations;
    }

    public Set<String> getSampleNames() {
        return sampleNames;
    }

    public void setSampleNames(Set<String> sampleNames) {
        this.sampleNames = sampleNames;
    }

    public Set<FormatField> getFormats() {
        return formats;
    }

    public void setFormats(Set<FormatField> formats) {
        this.formats = formats;
    }
}
