package org.opencb.biodata.models.variant;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.opencb.biodata.models.pedigree.Pedigree;
import org.opencb.biodata.models.variant.stats.VariantGlobalStats;


/**
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantSource {

    private String name;
    private String alias;
    private String description;
    private List<String> authors;
    private List<String> samples; // names
    private Pedigree pedigree;
    private List<String> sources;
    private Map<String, String> metadata;
    private VariantGlobalStats stats;

    // TEST
    private Map<String, Integer> consequenceTypes;

    public VariantSource(String name, String alias, String description, List<String> authors, List<String> sources) {
        this.name = name;
        this.alias = alias;
        this.description = description;
        this.authors = authors;
        this.sources = sources;
        this.metadata = new HashMap<>();
        // TODO initialize pedigree?

        // TEST
        this.consequenceTypes = new LinkedHashMap<>(20);
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getSources() {
        return sources;
    }

    public void setSources(List<String> sources) {
        this.sources = sources;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getSamples() {
        return samples;
    }

    public void setSamples(List<String> samples) {
        this.samples = samples;
    }

    public Pedigree getPedigree() {
        return pedigree;
    }

    public void setPedigree(Pedigree pedigree) {
        this.pedigree = pedigree;
    }

    public VariantGlobalStats getStats() {
        return stats;
    }

    public void setStats(VariantGlobalStats stats) {
        this.stats = stats;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public void setAuthors(List<String> authors) {
        this.authors = authors;
    }

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public void addMetadata(String key, String value) {
        this.metadata.put(key, value);
    }

    public Map<String, Integer> getConsequenceTypes() {
        return consequenceTypes;
    }

    public void setConsequenceTypes(Map<String, Integer> consequenceTypes) {
        this.consequenceTypes = consequenceTypes;
    }

    @Override
    public String toString() {
        return "VariantStudy{" +
                "name='" + name + '\'' +
                ", alias='" + alias + '\'' +
                ", description='" + description + '\'' +
                ", authors=" + authors +
                ", samples=" + samples +
                ", pedigree=" + pedigree +
                ", sources=" + sources +
                ", metadata=" + metadata +
                ", stats=" + stats +
                '}';
    }
}
