package org.opencb.biodata.models.variant.annotation;

import java.util.List;

/**
 * Created by fjlopez on 16/09/15.
 */
public class VariantTraitAssociation {

    private List<Cosmic> cosmicList;
    private List<Gwas> gwasList;
    private List<Clinvar> clinvarList;

    public VariantTraitAssociation() {}

    public VariantTraitAssociation(List<Cosmic> cosmicList, List<Gwas> gwasList, List<Clinvar> clinvarList) {
        this.cosmicList = cosmicList;
        this.gwasList = gwasList;
        this.clinvarList = clinvarList;
    }

    public List<Cosmic> getCosmicList() { return cosmicList; }

    public void setCosmicList(List<Cosmic> cosmicList) {
        this.cosmicList = cosmicList;
    }

    public List<Gwas> getGwasList() {
        return gwasList;
    }

    public void setGwasList(List<Gwas> gwasList) {
        this.gwasList = gwasList;
    }

    public List<Clinvar> getClinvarList() {
        return clinvarList;
    }

    public void setClinvarList(List<Clinvar> clinvarList) {
        this.clinvarList = clinvarList;
    }

    public int size() {
        return cosmicList.size()+gwasList.size()+clinvarList.size();
    }
}
