package org.opencb.biodata.models.variant.stats;

import java.util.ArrayList;
import java.util.List;

public class LoF {
    private List<Gene> genes;
    private List<String> hpos;

    public class Gene {
        private String ensemblGeneId;
        private int numAltAlleles;
        private List<String> hpos;

        public Gene() {
            this.ensemblGeneId = "";
            this.numAltAlleles = 0;
            hpos = new ArrayList<>();
        }

        public String getEnsemblGeneId() {
            return ensemblGeneId;
        }

        public Gene setEnsemblGeneId(String ensemblGeneId) {
            this.ensemblGeneId = ensemblGeneId;
            return this;
        }

        public int getNumAltAlleles() {
            return numAltAlleles;
        }

        public Gene setNumAltAlleles(int numAltAlleles) {
            this.numAltAlleles = numAltAlleles;
            return this;
        }

        public List<String> getHpos() {
            return hpos;
        }

        public Gene setHpos(List<String> hpos) {
            this.hpos = hpos;
            return this;
        }
    }

    public Gene newGene() {
        return new Gene();
    }

    public LoF() {
        genes = new ArrayList<>();
        hpos = new ArrayList<>();
    }

    public List<Gene> getGenes() {
        return genes;
    }

    public LoF setGenes(List<Gene> genes) {
        this.genes = genes;
        return this;
    }

    public List<String> getHpos() {
        return hpos;
    }

    public LoF setHpos(List<String> hpos) {
        this.hpos = hpos;
        return this;
    }
}
