package org.opencb.biodata.models.variant;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantStudy {
    
    private String studyName;
    
    private String studyId;
    
    private String description;
    
    private String species;
    
    private String material;
    
    private String scope;
    
    private String type;
    
    private List<VariantSource> sources;

    public VariantStudy() {
        this(null, null);
    }

    public VariantStudy(String studyName, String studyId) {
        this(studyName, studyId, null);
    }

    public VariantStudy(String studyName, String studyId, List<VariantSource> sources) {
        this(studyName, studyId, sources, null, null, null, null, null);
    }

    public VariantStudy(String studyName, String studyId, List<VariantSource> sources, String description, 
            String species, String material, String scope, String type) {
        this.studyName = studyName;
        this.studyId = studyId;
        this.description = description;
        this.species = species;
        this.material = material;
        this.scope = scope;
        this.type = type;
        this.sources = sources != null ? sources : new ArrayList();
    }

    public String getStudyName() {
        return studyName;
    }

    public void setStudyName(String studyName) {
        this.studyName = studyName;
    }

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getMaterial() {
        return material;
    }

    public void setMaterial(String material) {
        this.material = material;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<VariantSource> getSources() {
        return sources;
    }

    public void setSources(List<VariantSource> sources) {
        this.sources = sources;
    }
    
    public boolean addSource(VariantSource source) {
        return this.sources.add(source);
    }
}
