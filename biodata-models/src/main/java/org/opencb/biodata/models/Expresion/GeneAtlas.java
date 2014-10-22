package org.opencb.biodata.models.Expresion;

import java.util.List;

/**
 * Created by antonior on 10/16/14.
 */
public class GeneAtlas {

    private String gene_id;

    private String gene_name;

    public GeneAtlas(String gene_id, String gene_name, List<tissue> tissues) {
        this.gene_id = gene_id;
        this.gene_name = gene_name;
        this.tissues = tissues;
    }

    public List<tissue> getTissues() {
        return tissues;
    }

    public void setTissues(List<tissue> tissues) {
        this.tissues = tissues;
    }

    public String getGene_name() {
        return gene_name;
    }

    public void setGene_name(String gene_name) {
        this.gene_name = gene_name;
    }

    public String getGene_id() {
        return gene_id;
    }

    public void setGene_id(String gene_id) {
        this.gene_id = gene_id;
    }

    private List <tissue> tissues;





    public static class tissue{

        private String tissue_name;
        private String experiment;
        private Float expression_value;

        public tissue(String tissue_name, String experiment, Float expression_value) {
            this.tissue_name = tissue_name;
            this.experiment = experiment;
            this.expression_value = expression_value;
        }


        public String getTissue_name() {
            return tissue_name;
        }

        public void setTissue_name(String tissue_name) {
            this.tissue_name = tissue_name;
        }

        public String getExperiment() {
            return experiment;
        }

        public void setExperiment(String experiment) {
            this.experiment = experiment;
        }

        public Float getExpression_value() {
            return expression_value;
        }

        public void setExpression_value(Float expression_value) {
            this.expression_value = expression_value;
        }
    }

}
