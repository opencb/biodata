package org.opencb.biodata.models.variant;

import java.util.List;

/**
 *
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantStudy {
    
    public enum StudyType { FAMILY, TRIO, CONTROL, CASE, CASE_CONTROL, COLLECTION };
    
    private String name;
    
    private String id;
    
    private String description;
    
    private int taxonomyId;
    
    private String speciesCommonName;
    
    private String speciesScientificName;
    
    private String sourceType;
    
    private String center;
    
    private String material;
    
    private String scope;
    
    private StudyType type;
    
    private String experimentType;
    
    private String assembly;
    
    private String platform;
    
    private int numVariants;
    
    private int numSamples;
    
    private List<VariantSource> sources;

    
    public VariantStudy() {
        this(null, null);
    }

    public VariantStudy(String studyName, String studyId) {
        this(studyName, studyId, null);
    }

    public VariantStudy(String studyName, String studyId, List<VariantSource> sources) {
        this(studyName, studyId, sources, null, -1, null, null, null, null, null, null, null, null, null, null, -1, -1);
    }

    public VariantStudy(String studyName, String studyId, List<VariantSource> sources, String description, 
            int speciesId, String speciesCommonName, String speciesScientificName, String sourceType, String center, 
            String material, String scope, StudyType type, String experimentType, String referenceAssembly, 
            String platform, int numVariants, int numSamples) {
        this.name = studyName;
        this.id = studyId;
        this.description = description;
        this.taxonomyId = speciesId;
        this.speciesCommonName = speciesCommonName;
        this.speciesScientificName = speciesScientificName;
        this.sourceType = sourceType;
        this.center = center;
        this.material = material;
        this.scope = scope;
        this.type = type;
        this.experimentType = experimentType;
        this.assembly = referenceAssembly;
        this.platform = platform;
        this.numVariants = numVariants;
        this.numSamples = numSamples;
        this.sources = sources;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getTaxonomyId() {
        return taxonomyId;
    }

    public void setTaxonomyId(int taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    public String getSpeciesCommonName() {
        return speciesCommonName;
    }

    public void setSpeciesCommonName(String speciesCommonName) {
        this.speciesCommonName = speciesCommonName;
    }

    public String getSpeciesScientificName() {
        return speciesScientificName;
    }

    public void setSpeciesScientificName(String speciesScientificName) {
        this.speciesScientificName = speciesScientificName;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }

    public String getCenter() {
        return center;
    }

    public void setCenter(String center) {
        this.center = center;
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

    public StudyType getType() {
        return type;
    }

    public void setType(StudyType type) {
        this.type = type;
    }

    public String getExperimentType() {
        return experimentType;
    }

    public void setExperimentType(String experimentType) {
        this.experimentType = experimentType;
    }

    public String getAssembly() {
        return assembly;
    }

    public void setAssembly(String assembly) {
        this.assembly = assembly;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getNumVariants() {
        return numVariants;
    }

    public void setNumVariants(int numVariants) {
        this.numVariants = numVariants;
    }

    public int getNumSamples() {
        return numSamples;
    }

    public void setNumSamples(int numSamples) {
        this.numSamples = numSamples;
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
