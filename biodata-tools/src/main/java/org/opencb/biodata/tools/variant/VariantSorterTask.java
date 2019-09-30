package org.opencb.biodata.tools.variant;

public class VariantSorterTask extends VariantDeduplicationTask {

    public VariantSorterTask(int bufferSize) {
        super(variants -> variants, bufferSize);
    }
}
