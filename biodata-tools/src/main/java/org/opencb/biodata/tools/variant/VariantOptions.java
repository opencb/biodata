package org.opencb.biodata.tools.variant;

/**
 * Created by jtarraga on 29/11/16.
 */
public class VariantOptions {

    private int limit;

    public static final int DEFAULT_LIMIT = 50000;

    public VariantOptions() {
        this(DEFAULT_LIMIT);
    }

    public VariantOptions(int limit) {
        this.limit = limit;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("VariantOptions{");
        sb.append("limit=").append(limit);
        sb.append('}');
        return sb.toString();
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

}
