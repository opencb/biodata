/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

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

    public VcfIterator(CloseableIterator<VariantContext> variantContextIterator) {
        this(variantContextIterator, null);
    }

    public VcfIterator(CloseableIterator<VariantContext> variantContextIterator, VariantFilters<VariantContext> filters) {
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
