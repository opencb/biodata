package org.opencb.biodata.formats.sequence.ascat;

import java.util.ArrayList;
import java.util.List;

public class AscatMetrics {


    private int ploidy;
    private double aberrantCellFraction;
    private List<String> images;

    public AscatMetrics() {
        this(0,0,new ArrayList<>());
    }

    public AscatMetrics(int ploidy, double aberrantCellFraction, List<String> images) {
        this.ploidy = ploidy;
        this.aberrantCellFraction = aberrantCellFraction;
        this.images = images;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AscatMetrics{");
        sb.append("ploidy=").append(ploidy);
        sb.append(", aberrantCellFraction=").append(aberrantCellFraction);
        sb.append(", images=").append(images);
        sb.append('}');
        return sb.toString();
    }

    public int getPloidy() {
        return ploidy;
    }

    public AscatMetrics setPloidy(int ploidy) {
        this.ploidy = ploidy;
        return this;
    }

    public double getAberrantCellFraction() {
        return aberrantCellFraction;
    }

    public AscatMetrics setAberrantCellFraction(double aberrantCellFraction) {
        this.aberrantCellFraction = aberrantCellFraction;
        return this;
    }

    public List<String> getImages() {
        return images;
    }

    public AscatMetrics setImages(List<String> images) {
        this.images = images;
        return this;
    }
}
