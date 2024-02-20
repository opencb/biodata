package org.opencb.biodata.tools.variant;

import htsjdk.variant.variantcontext.VariantContext;
import org.junit.jupiter.api.Test;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.protobuf.VariantProto;
import org.opencb.biodata.tools.variant.filters.VariantContextFilters;
import org.opencb.biodata.tools.variant.filters.VariantFilters;
import org.opencb.biodata.tools.variant.iterators.VcfIterator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by jtarraga on 29/11/16.
 */
public class VcfManagerTest {
    String filename = "/ibs.vcf";

    private Path index(VcfManager vcfManager) throws IOException {
        return vcfManager.createIndex();
    }

    @Test
    public void createIndex() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        Path indexPath = index(vcfManager);
        assert(indexPath.toFile().exists());
    }

    @Test
    public void iterator() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        index(vcfManager);
        VcfIterator<VariantContext> iterator = vcfManager.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            VariantContext vc = iterator.next();
            System.out.println(vc);
            count++;
        }
        assertEquals(3, count);
    }

    @Test
    public void regionIterator() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        index(vcfManager);
        Region region = new Region("2", 2000000, 3000000);

        VariantFilters<VariantContext> filters = new VariantContextFilters();
        filters.addQualFilter(100);

        VcfIterator<VariantContext> iterator = vcfManager.iterator(region, filters);
        int count = 0;
        while (iterator.hasNext()) {
            VariantContext vc = iterator.next();
            System.out.println(vc);
            count++;
        }
        assertEquals(2, count);
    }

    @Test
    public void regionQuery() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        index(vcfManager);
        Region region = new Region("2", 2000000, 3000000);

        List<VariantContext> list = vcfManager.query(region);
        for (VariantContext vc: list) {
            System.out.println(vc);
        }

        assertEquals(2, list.size());
    }

    @Test
    public void regionQueryAvro() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        index(vcfManager);
        Region region = new Region("2", 2000000, 3000000);

        List<Variant> list = vcfManager.query(region, null, null, Variant.class);
        for (Variant v: list) {
            System.out.println(v);
        }

        assertEquals(2, list.size());
    }

    @Test
    public void regionQueryProto() throws Exception {
        Path path = Paths.get(getClass().getResource(filename).toURI());
        VcfManager vcfManager = new VcfManager(path);
        index(vcfManager);
        Region region = new Region("2", 2000000, 3000000);

        List<VariantProto.Variant> list = vcfManager.query(region, null, null, VariantProto.Variant.class);
        for (VariantProto.Variant v: list) {
            System.out.println(v);
        }

        assertEquals(2, list.size());
    }
}