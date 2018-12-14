package org.opencb.biodata.models.variant.stats;

import java.util.Arrays;

public class IdentityByDescent extends IdentityByState {
    private double[] ibd;
    private double pihat;

    public IdentityByDescent() {
        super();

        ibd = new double[]{0, 0, 0};
        pihat = 0;
    }

    public IdentityByDescent(double z0, double z1, double z2, double pihat) {
        this(new double[]{z0, z1, z2}, pihat);
    }

    public IdentityByDescent(double[] ibd, double pihat) {
        super();

        this.ibd = ibd;
        this.pihat = pihat;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IdentityByDescent{");
        sb.append("ibd=").append(Arrays.toString(ibd));
        sb.append(", pihat=").append(pihat);
        sb.append(", ibs=").append(Arrays.toString(ibs));
        sb.append(", distance=").append(this.getDistance());
        sb.append('}');
        return sb.toString();
    }

    public double[] getIbd() {
        return ibd;
    }

    public IdentityByDescent setIbd(double[] ibd) {
        this.ibd = ibd;
        return this;
    }

    public double getPihat() {
        return pihat;
    }

    public IdentityByDescent setPihat(double pihat) {
        this.pihat = pihat;
        return this;
    }
}
