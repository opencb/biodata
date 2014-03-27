package org.opencb.biodata.models.variant;

import java.util.*;
import org.opencb.biodata.models.variant.effect.VariantEffect;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class Variant {
    
    /**
     * Type of variation, which depends mostly on its length.
     * <ul>
     *  <li>SNVs involve a single nucleotide</li>
     *  <li>Indels are insertions or deletions of less than 50 nucleotides</li>
     *  <li>Structural variations are large changes of more than 50 nucleotides</li>
     *  <li>Copy-number variations alter the number of copies of a region</li>
     * </ul>
     */
    public enum VariantType { SNV, INDEL, SV, CNV };
    
    /**
     * Type of variation: single nucleotide, indel or structural variation.
     */
    private VariantType type;
    
    /**
     * Chromosome where the genomic variation occurred.
     */
    private String chromosome;
    
    /**
     * Position where the genomic variation starts.
     * <ul>
     *  <li>SNVs have the same start and end positions</li>
     *  <li>Insertions start in the last present position: if the first nucleotide 
     * is inserted in position 6, the start is position 5</li>
     *  <li>Deletions start in the first previously present position: if the first 
     * deleted nucleotide is in position 6, the start is position 6</li>
     * </ul>
     */
    private int start;
    
    /**
     * Position where the genomic variation ends.
     * <ul>
     *  <li>SNVs have the same start and end positions</li>
     *  <li>Insertions end in the first present position: if the last nucleotide 
     * is inserted in position 9, the end is position 10</li>
     *  <li>Deletions ends in the last previously present position: if the last 
     * deleted nucleotide is in position 9, the end is position 9</li>
     * </ul>
     */
    private int end;
    
    /**
     * Length of the genomic variation, which depends on the variation type.
     * <ul>
     *  <li>SNVs have a length of 1 nucleotide</li>
     *  <li>Indels have the length of the largest allele</li>
     * </ul>
     */
    private int length;
    
    /**
     * Reference allele.
     */
    private String reference;
    
    /**
     * Alternate allele.
     */
    private String alternate;
    
    /**
     * Unique identifier most commonly used for this genomic variation.
     */
    private String id;
    
    /**
     * Unique identifier following the HGVS nomenclature.
     */
    private Map<String, List<String>> hgvs;
    
    /**
     * Information specific to each file the variant was read from, such as 
     * samples or statistics.
     */
    private Map<String, ArchivedVariantFile> files;
    
//    /**
//     * Fields stored for each sample.
//     */
//    private String format;
//    
//    /**
//     * Genotypes and other sample-related information. The keys are the names
//     * of the samples. The values are pairs (field name, field value), such as
//     * (GT, A/C).
//     */
//    private Map<String, Map<String, String>> samplesData;
//    
//    /**
//     * Statistics of the genomic variation, such as its alleles/genotypes count 
//     * or its minimum allele frequency.
//     */
//    private VariantStats stats;
    
    /**
     * Possible effects of the genomic variation.
     */
    private List<VariantEffect> effect;

//    /**
//     * Optional attributes that probably depend on the format of the file the
//     * variant was initially read.
//     */
//    private Map<String, String> attributes;

    
    public Variant(String chromosome, int start, int end, String reference, String alternate) {
        if (start > end) {
            throw new IllegalArgumentException("End position must be greater than the start position");
        }
        
        this.setChromosome(chromosome);
        this.setStart(start);
        this.setEnd(end);
        this.reference = (reference != null) ? reference : "";
        this.alternate = (alternate != null) ? alternate : "";
        
        this.length = Math.max(this.reference.length(), this.alternate.length());
        if (this.reference.length() == this.alternate.length()) {
            this.type = VariantType.SNV;
        } else if (this.length <= 50) {
            this.type = VariantType.INDEL;
        } else {
            this.type = VariantType.SV;
        }
        
        this.hgvs = new HashMap<>();
        this.hgvs.put("genomic", Arrays.asList(chromosome + ":g." + start + reference + ">" + alternate));
        
        this.files = new HashMap<>();
//        this.samplesData = new LinkedHashMap<>();
        this.effect = new LinkedList<>();
//        this.attributes = new LinkedHashMap<>();
    }

    public VariantType getType() {
        return type;
    }

    public void setType(VariantType type) {
        this.type = type;
    }
    
    public String getChromosome() {
        return chromosome;
    }

    public final void setChromosome(String chromosome) {
        if (chromosome == null || chromosome.length() == 0) {
            throw new IllegalArgumentException("Chromosome must not be empty");
        }
        this.chromosome = chromosome.replaceAll("chrom | chrm | chr | ch", "");
    }

    public int getStart() {
        return start;
    }

    public final void setStart(int start) {
        if (start < 0) {
            throw new IllegalArgumentException("Start must be positive");
        }
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public final void setEnd(int end) {
        if (end < 0) {
            throw new IllegalArgumentException("End must be positive");
        }
        this.end = end;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
        this.length = Math.max(reference.length(), alternate.length());
    }

    public String getAlternate() {
        return alternate;
    }

    public void setAlternate(String alternate) {
        this.alternate = alternate;
        this.length = Math.max(reference.length(), alternate.length());
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // TODO Insert in attributes?
//    public void addId(String newId) {
//        if (!this.id.contains(newId)) {
//            if (this.id.equals(".")) {
//                this.id = newId;
//            } else {
//                this.id += ";" + newId;
//            }
//        }
//    }
    
    public Map<String, List<String>> getHgvs() {
        return hgvs;
    }
    
    public List<String> getHgvs(String type) {
        return hgvs.get(type);
    }

    public boolean addHgvs(String type, String value) {
        List<String> listByType = hgvs.get(type);
        if (listByType == null) {
            listByType = new ArrayList<>();
        }
        return listByType.add(value);
    }

    public Map<String, ArchivedVariantFile> getFiles() {
        return files;
    }
    
    public ArchivedVariantFile getFile(String fileId) {
        return files.get(fileId);
    }

    public void setFiles(Map<String, ArchivedVariantFile> files) {
        this.files = files;
    }
    
    public void addFile(ArchivedVariantFile file) {
        this.files.put(file.getFileId(), file);
    }
    
//    public String getFormat() {
//        return format;
//    }
//
//    public void setFormat(String format) {
//        this.format = format;
//    }
//
//    public Map<String, Map<String, String>> getSamplesData() {
//        return samplesData;
//    }
//
//    public Map<String, String> getSampleData(String sampleName) {
//        return samplesData.get(sampleName);
//    }
//
//    public VariantStats getStats() {
//        return stats;
//    }

    public VariantStats getStats(String fileId) {
        ArchivedVariantFile file = files.get(fileId);
        if (file == null) {
            return null;
        }
        return file.getStats();
    }
        
//    public void setStats(VariantStats stats) {
//        this.stats = stats;
//    }
//
    public List<VariantEffect> getEffect() {
        return effect;
    }

    public void setEffect(List<VariantEffect> effect) {
        this.effect = effect;
    }

    public boolean addEffect(VariantEffect e) {
        return this.effect.add(e);
    }

//    public Map<String, String> getAttributes() {
//        return attributes;
//    }
//
//    public void setAttributes(Map<String, String> attributes) {
//        this.attributes = attributes;
//    }
//
//    public void addAttribute(String key, String value) {
//        this.attributes.put(key, value);
//    }
//
//    public String getAttribute(String key) {
//        return this.attributes.get(key);
//    }
//
//    public boolean hasAttribute(String key) {
//        return this.attributes.containsKey(key);
//    }
//
//    public void addSampleData(String sampleName, Map<String, String> sampleData) {
//        this.samplesData.put(sampleName, sampleData);
//    }
//
//    public String getSampleData(String sampleName, String field) {
//        return this.samplesData.get(sampleName).get(field.toUpperCase());
//    }

    public Iterable<String> getSampleNames(String fileId) {
        ArchivedVariantFile file = files.get(fileId);
        if (file == null) {
            return null;
        }
        return file.getSampleNames();
    }

    public boolean isIndel() {
        return this.reference.length() != this.alternate.length();
    }

    @Override
    public String toString() {
        return "Variant{" +
                "chromosome='" + chromosome + '\'' +
                ", position=" + start + "-" + end + 
                ", reference='" + reference + '\'' +
                ", alternate='" + alternate + '\'' +
                ", id='" + id + '\'' +
//                ", format='" + format + '\'' +
//                ", samplesData=" + samplesData +
//                ", stats=" + stats +
//                ", effect=" + effect +
//                ", attributes=" + attributes +
                '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + Objects.hashCode(this.chromosome);
        hash = 37 * hash + this.start;
        hash = 37 * hash + this.end;
        hash = 37 * hash + Objects.hashCode(this.reference);
        hash = 37 * hash + Objects.hashCode(this.alternate);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Variant other = (Variant) obj;
        if (!Objects.equals(this.chromosome, other.chromosome)) {
            return false;
        }
        if (this.start != other.start) {
            return false;
        }
        if (this.end != other.end) {
            return false;
        }
        if (!Objects.equals(this.reference, other.reference)) {
            return false;
        }
        if (!Objects.equals(this.alternate, other.alternate)) {
            return false;
        }
        return true;
    }

    
}
