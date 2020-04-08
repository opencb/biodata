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

package org.opencb.biodata.models.clinical.interpretation;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.models.core.OntologyTermAnnotation;
import org.opencb.biodata.models.clinical.Phenotype;
import org.opencb.biodata.models.core.Xref;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import static org.opencb.biodata.models.clinical.interpretation.ClinicalProperty.Penetrance;

public class DiseasePanel {

    private String id;
    private String name;

    private List<PanelCategory> categories;
    private List<Phenotype> phenotypes;
    private List<String> tags;

    private List<VariantPanel> variants;
    private List<GenePanel> genes;
    private List<STR> strs;
    private List<RegionPanel> regions;
    private Map<String, Integer> stats;

    /**
     * Information taken from the source of this panel.
     * For instance if the panel is taken from PanelApp this will contain the id, name and version in PanelApp.
     */
    private SourcePanel source;
    private String creationDate;
    private String modificationDate;
    private String description;

    private Map<String, Object> attributes;


    public DiseasePanel() {
    }

    public DiseasePanel(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public DiseasePanel(String id, String name, List<PanelCategory> categories, List<Phenotype> phenotypes,
                        List<String> tags, List<VariantPanel> variants, List<GenePanel> genes, List<STR> strs,
                        List<RegionPanel> regions, Map<String, Integer> stats, SourcePanel source, String creationDate,
                        String modificationDate, String description, Map<String, Object> attributes) {
        this.id = id;
        this.name = name;
        this.categories = categories;
        this.phenotypes = phenotypes;
        this.tags = tags;
        this.variants = variants;
        this.genes = genes;
        this.strs = strs;
        this.regions = regions;
        this.stats = stats;
        this.source = source;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.description = description;
        this.attributes = attributes;
    }

    /**
     * Static method to load and parse a JSON string from an InputStream.
     * @param diseasePanelInputStream InputStream with the JSON string representing this panel.
     * @return A DiseasePanel object.
     * @throws IOException Propagate Jackson IOException.
     */
    public static DiseasePanel load(InputStream diseasePanelInputStream) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(diseasePanelInputStream, DiseasePanel.class);
    }


    public static class PanelCategory {

        private String name;
        private int level;

        public PanelCategory() {
        }

        public PanelCategory(String name, int level) {
            this.name = name;
            this.level = level;
        }

        public String getName() {
            return name;
        }

        public PanelCategory setName(String name) {
            this.name = name;
            return this;
        }

        public int getLevel() {
            return level;
        }

        public PanelCategory setLevel(int level) {
            this.level = level;
            return this;
        }
    }

    public static class SourcePanel {

        private String id;
        private String name;
        private String version;
        private String author;
        private String project;

        public SourcePanel() {
        }

        @Deprecated
        public SourcePanel(String id, String name, String version, String project) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.project = project;
        }

        public SourcePanel(String id, String name, String version, String author, String project) {
            this.id = id;
            this.name = name;
            this.version = version;
            this.author = author;
            this.project = project;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("SourcePanel{");
            sb.append("id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", version='").append(version).append('\'');
            sb.append(", author='").append(author).append('\'');
            sb.append(", project='").append(project).append('\'');
            sb.append('}');
            return sb.toString();
        }

        public String getId() {
            return id;
        }

        public SourcePanel setId(String id) {
            this.id = id;
            return this;
        }

        public String getName() {
            return name;
        }

        public SourcePanel setName(String name) {
            this.name = name;
            return this;
        }

        public String getVersion() {
            return version;
        }

        public SourcePanel setVersion(String version) {
            this.version = version;
            return this;
        }

        public String getAuthor() {
            return author;
        }

        public SourcePanel setAuthor(String author) {
            this.author = author;
            return this;
        }

        public String getProject() {
            return project;
        }

        public SourcePanel setProject(String project) {
            this.project = project;
            return this;
        }
    }

    public static class VariantPanel extends Common {

        private String reference;
        private String alternate;

        public VariantPanel() {
        }

        public VariantPanel(String id, List<Xref> xrefs, String modeOfInheritance, Penetrance penetrance,
                            String confidence, List<String> evidences, List<String> publications,
                            List<OntologyTermAnnotation> phenotypes, List<Coordinate> coordinates, String reference,
                            String alternate) {
            super(id, xrefs, modeOfInheritance, penetrance, confidence, evidences, publications, phenotypes, coordinates);
            this.reference = reference;
            this.alternate = alternate;
        }

        @Override
        public String toString() {
            return "VariantPanel{" +
                    "id='" + id + '\'' +
                    ", xrefs=" + xrefs +
                    ", modeOfInheritance='" + modeOfInheritance + '\'' +
                    ", penetrance=" + penetrance +
                    ", confidence='" + confidence + '\'' +
                    ", evidences=" + evidences +
                    ", publications=" + publications +
                    ", phenotypes=" + phenotypes +
                    ", coordinates=" + coordinates +
                    ", reference='" + reference + '\'' +
                    ", alternate='" + alternate + '\'' +
                    '}';
        }

        public String getReference() {
            return reference;
        }

        public void setReference(String reference) {
            this.reference = reference;
        }

        public String getAlternate() {
            return alternate;
        }

        public void setAlternate(String alternate) {
            this.alternate = alternate;
        }
    }


    public static class Coordinate {

        private String assembly;
        private String location;
        private String source;

        public Coordinate() {
        }

        public Coordinate(String assembly, String location, String source) {
            this.assembly = assembly;
            this.location = location;
            this.source = source;
        }

        @Override
        public String toString() {
            return "Coordinate{" +
                    "assembly='" + assembly + '\'' +
                    ", location='" + location + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }

        public String getAssembly() {
            return assembly;
        }

        public Coordinate setAssembly(String assembly) {
            this.assembly = assembly;
            return this;
        }

        public String getLocation() {
            return location;
        }

        public Coordinate setLocation(String location) {
            this.location = location;
            return this;
        }

        public String getSource() {
            return source;
        }

        public Coordinate setSource(String source) {
            this.source = source;
            return this;
        }
    }


    public static class Common {

        protected String id;
        protected List<Xref> xrefs;
        protected String modeOfInheritance;
        protected Penetrance penetrance;
        protected String confidence;
        protected List<String> evidences;
        protected List<String> publications;
        protected List<OntologyTermAnnotation> phenotypes;
        protected List<Coordinate> coordinates;

        public Common() {
        }

        public Common(String id, List<Xref> xrefs, String modeOfInheritance, Penetrance penetrance, String confidence,
                      List<String> evidences, List<String> publications, List<OntologyTermAnnotation> phenotypes,
                      List<Coordinate> coordinates) {
            this.id = id;
            this.xrefs = xrefs;
            this.modeOfInheritance = modeOfInheritance;
            this.penetrance = penetrance;
            this.confidence = confidence;
            this.evidences = evidences;
            this.publications = publications;
            this.phenotypes = phenotypes;
            this.coordinates = coordinates;
        }

        @Override
        public String toString() {
            return "Common{" +
                    "id='" + id + '\'' +
                    ", xrefs=" + xrefs +
                    ", modeOfInheritance='" + modeOfInheritance + '\'' +
                    ", penetrance=" + penetrance +
                    ", confidence='" + confidence + '\'' +
                    ", evidences=" + evidences +
                    ", publications=" + publications +
                    ", phenotypes=" + phenotypes +
                    ", coordinates=" + coordinates +
                    '}';
        }

        public String getId() {
            return id;
        }

        public Common setId(String id) {
            this.id = id;
            return this;
        }

        public String getModeOfInheritance() {
            return modeOfInheritance;
        }

        public Common setModeOfInheritance(String modeOfInheritance) {
            this.modeOfInheritance = modeOfInheritance;
            return this;
        }

        public Penetrance getPenetrance() {
            return penetrance;
        }

        public Common setPenetrance(Penetrance penetrance) {
            this.penetrance = penetrance;
            return this;
        }

        public String getConfidence() {
            return confidence;
        }

        public Common setConfidence(String confidence) {
            this.confidence = confidence;
            return this;
        }

        public List<String> getEvidences() {
            return evidences;
        }

        public Common setEvidences(List<String> evidences) {
            this.evidences = evidences;
            return this;
        }

        public List<String> getPublications() {
            return publications;
        }

        public Common setPublications(List<String> publications) {
            this.publications = publications;
            return this;
        }

        public List<OntologyTermAnnotation> getPhenotypes() {
            return phenotypes;
        }

        public Common setPhenotypes(List<OntologyTermAnnotation> phenotypes) {
            this.phenotypes = phenotypes;
            return this;
        }

        public List<Xref> getXrefs() {
            return xrefs;
        }

        public Common setXrefs(List<Xref> xrefs) {
            this.xrefs = xrefs;
            return this;
        }

        public List<Coordinate> getCoordinates() {
            return coordinates;
        }

        public Common setCoordinates(List<Coordinate> coordinates) {
            this.coordinates = coordinates;
            return this;
        }
    }

    public static class RegionPanel extends Common {

        private String description;
        private VariantType typeOfVariants;
        private String haploinsufficiencyScore;
        private String triplosensitivityScore;
        private int requiredOverlapPercentage;

        public RegionPanel() {
        }

        public RegionPanel(String name, List<Xref> xrefs, String modeOfInheritance, Penetrance penetrance,
                           String confidence, List<String> evidences, List<String> publications,
                           List<OntologyTermAnnotation> phenotypes, List<Coordinate> coordinates, String description,
                           VariantType typeOfVariants, String haploinsufficiencyScore, String triplosensitivityScore,
                           int requiredOverlapPercentage) {
            super(name, xrefs, modeOfInheritance, penetrance, confidence, evidences, publications, phenotypes,
                    coordinates);
            this.description = description;
            this.typeOfVariants = typeOfVariants;
            this.haploinsufficiencyScore = haploinsufficiencyScore;
            this.triplosensitivityScore = triplosensitivityScore;
            this.requiredOverlapPercentage = requiredOverlapPercentage;
        }

        @Override
        public String toString() {
            return "RegionPanel{" +
                    "id='" + id + '\'' +
                    ", xrefs=" + xrefs +
                    ", modeOfInheritance='" + modeOfInheritance + '\'' +
                    ", penetrance=" + penetrance +
                    ", confidence='" + confidence + '\'' +
                    ", evidences=" + evidences +
                    ", publications=" + publications +
                    ", phenotypes=" + phenotypes +
                    ", coordinates=" + coordinates +
                    ", description='" + description + '\'' +
                    ", typeOfVariants=" + typeOfVariants +
                    ", haploinsufficiencyScore='" + haploinsufficiencyScore + '\'' +
                    ", triplosensitivityScore='" + triplosensitivityScore + '\'' +
                    ", requiredOverlapPercentage=" + requiredOverlapPercentage +
                    '}';
        }

        public String getDescription() {
            return description;
        }

        public RegionPanel setDescription(String description) {
            this.description = description;
            return this;
        }

        public VariantType getTypeOfVariants() {
            return typeOfVariants;
        }

        public RegionPanel setTypeOfVariants(VariantType typeOfVariants) {
            this.typeOfVariants = typeOfVariants;
            return this;
        }

        public String getHaploinsufficiencyScore() {
            return haploinsufficiencyScore;
        }

        public RegionPanel setHaploinsufficiencyScore(String haploinsufficiencyScore) {
            this.haploinsufficiencyScore = haploinsufficiencyScore;
            return this;
        }

        public String getTriplosensitivityScore() {
            return triplosensitivityScore;
        }

        public RegionPanel setTriplosensitivityScore(String triplosensitivityScore) {
            this.triplosensitivityScore = triplosensitivityScore;
            return this;
        }

        public int getRequiredOverlapPercentage() {
            return requiredOverlapPercentage;
        }

        public RegionPanel setRequiredOverlapPercentage(int requiredOverlapPercentage) {
            this.requiredOverlapPercentage = requiredOverlapPercentage;
            return this;
        }

    }

    public enum VariantType {
        LOSS,
        GAIN,
        INDEL
    }

    public static class STR extends Common {

        private String repeatedSequence;
        private int normalRepeats;
        private int pathogenicRepeats;

        public STR() {
        }

        public STR(String id, List<Xref> xrefs, String modeOfInheritance, Penetrance penetrance, String confidence,
                   List<String> evidences, List<String> publications, List<OntologyTermAnnotation> phenotypes,
                   List<Coordinate> coordinates, String repeatedSequence, int normalRepeats, int pathogenicRepeats) {
            super(id, xrefs, modeOfInheritance, penetrance, confidence, evidences, publications, phenotypes,
                    coordinates);
            this.repeatedSequence = repeatedSequence;
            this.normalRepeats = normalRepeats;
            this.pathogenicRepeats = pathogenicRepeats;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("STR{");
            sb.append("id='").append(id).append('\'');
            sb.append(", xrefs=").append(xrefs);
            sb.append(", modeOfInheritance='").append(modeOfInheritance).append('\'');
            sb.append(", penetrance=").append(penetrance);
            sb.append(", confidence='").append(confidence).append('\'');
            sb.append(", evidences=").append(evidences);
            sb.append(", publications=").append(publications);
            sb.append(", phenotypes=").append(phenotypes);
            sb.append(", coordinates=").append(coordinates);
            sb.append(", repeatedSequence='").append(repeatedSequence).append('\'');
            sb.append(", normalRepeats=").append(normalRepeats);
            sb.append(", pathogenicRepeats=").append(pathogenicRepeats);
            sb.append('}');
            return sb.toString();
        }

        public String getRepeatedSequence() {
            return repeatedSequence;
        }

        public STR setRepeatedSequence(String repeatedSequence) {
            this.repeatedSequence = repeatedSequence;
            return this;
        }

        public int getNormalRepeats() {
            return normalRepeats;
        }

        public STR setNormalRepeats(int normalRepeats) {
            this.normalRepeats = normalRepeats;
            return this;
        }

        public int getPathogenicRepeats() {
            return pathogenicRepeats;
        }

        public STR setPathogenicRepeats(int pathogenicRepeats) {
            this.pathogenicRepeats = pathogenicRepeats;
            return this;
        }
    }

    public static class GenePanel extends Common {

        /**
         * HGNC Gene Symbol is used as name.
         */
        private String name;

        public GenePanel() {
        }

        public GenePanel(String id, String name, List<Xref> xrefs, String modeOfInheritance, Penetrance penetrance,
                         String confidence, List<String> evidences, List<String> publications,
                         List<OntologyTermAnnotation> phenotypes, List<Coordinate> coordinates) {
            super(id, xrefs, modeOfInheritance, penetrance, confidence, evidences, publications, phenotypes,
                    coordinates);
            this.name = name;
        }

        @Override
        public String toString() {
            final StringBuilder sb = new StringBuilder("GenePanel{");
            sb.append("id='").append(id).append('\'');
            sb.append(", name='").append(name).append('\'');
            sb.append(", xrefs=").append(xrefs);
            sb.append(", modeOfInheritance=").append(modeOfInheritance);
            sb.append(", penetrance=").append(penetrance);
            sb.append(", confidence='").append(confidence).append('\'');
            sb.append(", evidences=").append(evidences);
            sb.append(", publications=").append(publications);
            sb.append('}');
            return sb.toString();
        }

        public String getName() {
            return name;
        }

        public GenePanel setName(String name) {
            this.name = name;
            return this;
        }

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DiseasePanel{");
        sb.append("id='").append(id).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", categories=").append(categories);
        sb.append(", phenotypes=").append(phenotypes);
        sb.append(", tags=").append(tags);
        sb.append(", variants=").append(variants);
        sb.append(", genes=").append(genes);
        sb.append(", strs=").append(strs);
        sb.append(", regions=").append(regions);
        sb.append(", stats=").append(stats);
        sb.append(", source=").append(source);
        sb.append(", creationDate='").append(creationDate).append('\'');
        sb.append(", modificationDate='").append(modificationDate).append('\'');
        sb.append(", description='").append(description).append('\'');
        sb.append(", attributes=").append(attributes);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public DiseasePanel setId(String id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public DiseasePanel setName(String name) {
        this.name = name;
        return this;
    }

    public List<PanelCategory> getCategories() {
        return categories;
    }

    public DiseasePanel setCategories(List<PanelCategory> categories) {
        this.categories = categories;
        return this;
    }

    public List<Phenotype> getPhenotypes() {
        return phenotypes;
    }

    public DiseasePanel setPhenotypes(List<Phenotype> phenotypes) {
        this.phenotypes = phenotypes;
        return this;
    }

    public List<String> getTags() {
        return tags;
    }

    public DiseasePanel setTags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public List<VariantPanel> getVariants() {
        return variants;
    }

    public DiseasePanel setVariants(List<VariantPanel> variants) {
        this.variants = variants;
        return this;
    }

    public List<GenePanel> getGenes() {
        return genes;
    }

    public DiseasePanel setGenes(List<GenePanel> genes) {
        this.genes = genes;
        return this;
    }

    public List<RegionPanel> getRegions() {
        return regions;
    }

    public DiseasePanel setRegions(List<RegionPanel> regions) {
        this.regions = regions;
        return this;
    }

    public List<STR> getStrs() {
        return strs;
    }

    public DiseasePanel setStrs(List<STR> strs) {
        this.strs = strs;
        return this;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }

    public DiseasePanel setStats(Map<String, Integer> stats) {
        this.stats = stats;
        return this;
    }

    public SourcePanel getSource() {
        return source;
    }

    public DiseasePanel setSource(SourcePanel source) {
        this.source = source;
        return this;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public DiseasePanel setCreationDate(String creationDate) {
        this.creationDate = creationDate;
        return this;
    }

    public String getModificationDate() {
        return modificationDate;
    }

    public DiseasePanel setModificationDate(String modificationDate) {
        this.modificationDate = modificationDate;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public DiseasePanel setDescription(String description) {
        this.description = description;
        return this;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public DiseasePanel setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }
}
