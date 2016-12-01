package org.opencb.biodata.tools.feature;

/**
 * Created by imedina on 26/10/16.
 */
public abstract class FeatureManager<T> {

    protected int chromsomeColIndex;
    protected int startColIndex;
    protected int endColIndex;


    public void index() {

    }

    public T query(String chromosome, int start, int end) {
        return null;
    }
}
