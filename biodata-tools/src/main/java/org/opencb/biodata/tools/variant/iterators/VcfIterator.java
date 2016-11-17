package org.opencb.biodata.tools.variant.iterators;

import htsjdk.samtools.util.CloseableIterator;
import htsjdk.variant.variantcontext.VariantContext;
import org.opencb.biodata.tools.variant.filters.VariantContextFilters;
import org.opencb.biodata.tools.variant.filters.VariantFilters;

import java.util.Iterator;

/**
 * Created by joaquin on 11/14/16.
 */
public abstract class VcfIterator<T> implements Iterator<T>, AutoCloseable {

        private CloseableIterator<VariantContext> variantContextIterator;
        protected VariantFilters<VariantContext> filters;

        protected VariantContext prevNext;

        public VcfIterator(CloseableIterator<T> variantContextIterator) {
            this(variantContextIterator, null);
        }

        public VcfIterator(CloseableIterator variantContextIterator, VariantFilters<VariantContext> filters) {
            this.variantContextIterator = variantContextIterator;
            if (filters == null) {
                filters = new VariantContextFilters();
            }
            this.filters = filters;

            findNextMatch();
        }

        protected void findNextMatch() {
            prevNext = null;
            while (variantContextIterator.hasNext()) {
                VariantContext next = variantContextIterator.next();
                if (filters.test(next)) {
                    prevNext = next;
                    return;
                }
            }
        }

        @Override
        public void close() throws Exception {
            variantContextIterator.close();
        }

    }
