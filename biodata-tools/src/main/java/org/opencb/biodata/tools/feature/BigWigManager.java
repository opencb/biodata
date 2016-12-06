package org.opencb.biodata.tools.feature;

import org.broad.igv.bbfile.BBFileReader;
import org.broad.igv.bbfile.BigWigIterator;
import org.broad.igv.bbfile.WigItem;
import org.opencb.biodata.models.core.Region;
import org.opencb.commons.utils.FileUtils;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by imedina on 25/11/16.
 */
public class BigWigManager {

    private Path bigWigFilePath;
    private BBFileReader bbFileReader;

    public BigWigManager(Path path) throws IOException {
        this.bigWigFilePath = path;

        init();
    }

    private void init() throws IOException {
        FileUtils.checkPath(this.bigWigFilePath);
        bbFileReader = new BBFileReader(this.bigWigFilePath.toString());
    }

    public float[] query(Region region) throws IOException {
        BigWigIterator bigWigIterator = bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(),
                region.getChromosome(), region.getEnd(), true);
        float[] values = new float[region.getEnd() - region.getStart()];
        while (bigWigIterator.hasNext()) {
            WigItem next = bigWigIterator.next();
//            System.out.println(next.getChromosome() + ":" + next.getStartBase() + "-" + next.getEndBase()
//                    + ", " + next.getWigValue());
            for (int i = next.getStartBase(), j = next.getStartBase() - region.getStart();
                 i <= next.getEndBase();
                 i++, j++) {
                values[j] = next.getWigValue();
            }
        }
        return values;
    }

    public BigWigIterator iterator(Region region) throws IOException {
        return bbFileReader.getBigWigIterator(region.getChromosome(), region.getStart(), region.getChromosome(), region.getEnd(), true);
    }

    public void index() throws IOException {
        throw new UnsupportedOperationException("index not yet implementd!");
    }

    public void index(List<String> chromosomes) throws IOException {
        throw new UnsupportedOperationException("index not yet implementd!");
    }
}
