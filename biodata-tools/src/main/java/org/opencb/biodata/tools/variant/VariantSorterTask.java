package org.opencb.biodata.tools.variant;

/**
 * Variant sorter task.
 * Perform a minimal sorting on variants.
 * Will fail if variants two consecutive variants are separated more than "bufferSize".
 * This task can not run in multiple threads.
 * Is not thread-safe.
 * Best usage is concatenated to a DataReader, or in a single task thread ParallelTaskRunner
 */
public class VariantSorterTask extends VariantDeduplicationTask {

    public VariantSorterTask(int bufferSize) {
        super(variants -> variants, bufferSize);
    }
}
