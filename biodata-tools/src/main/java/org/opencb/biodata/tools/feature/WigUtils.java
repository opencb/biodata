package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMSequenceDictionary;
import htsjdk.samtools.SamReader;
import org.apache.commons.lang.StringUtils;
import org.broad.igv.bbfile.*;
import org.opencb.biodata.models.core.Region;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.CollectionUtils;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jtarraga on 02/12/16.
 */
public class WigUtils {

    public static final String WIG_DB = "wig.db";

    /**
     * Index the entire Wig file content in a SQLite database managed by the ChunkFrequencyManager.
     *
     * @param wigPath   Wig file
     * @return          Path to the database
     * @throws Exception
     */
    public static Path index(Path wigPath) throws Exception {
        Path dbPath = wigPath.getParent().resolve(WIG_DB);

        ChunkFrequencyManager chunkFrequencyManager = new ChunkFrequencyManager(dbPath);

        // get the chunk size
        int chunkSize = chunkFrequencyManager.getChunkSize();

        String chromosome = null;
        int step, span = 1, start = 1, end;
        int startChunk, endChunk, partial;

        boolean empty = true;
        List<Integer> values = new ArrayList<>();

        // reader
        BufferedReader bufferedReader = FileUtils.newBufferedReader(wigPath);
        // main loop
        String line = bufferedReader.readLine();
        while (line != null) {
            // check for header lines
            if (WigUtils.isHeaderLine(line)) {
                if (!empty) {
                    // save values for the current chromosome into the database
                    System.out.println("\tStoring " + values.size() + " values for " + chromosome);
//                    if (chromosome.equals("chr1")) {
                        computeAndSaveMeanValues(values, wigPath, chromosome, chunkSize, chunkFrequencyManager);
//                    }
                }

                System.out.println("Loading wig data:" + line);
                if (WigUtils.isVariableStep(line)) {
                    throw new UnsupportedOperationException("Wig coverage file with 'variableStep'"
                            + " is not supported yet.");
                }

                // update some values
                step = WigUtils.getStep(line);
                span = WigUtils.getSpan(line);
                start = WigUtils.getStart(line);
                chromosome = WigUtils.getChromosome(line);
                empty = true;
                values = new ArrayList<>();
                // sanity check
                if (start <= 0) {
                    throw new UnsupportedOperationException("Wig coverage file with"
                            + " 'start' <= 0, it must be greater than 0.");
                }
                if (start != 1) {
                    // we have to put zeros until the start position
                    for (int i = 0; i < start; i++) {
                        values.add(0);
                    }
                }
                if (step != 1) {
                    throw new UnsupportedOperationException("Wig coverage file with"
                            + " 'step' != 1 is not supported yet.");
                }
                // next line...
                line = bufferedReader.readLine();
            } else {
                if (values != null) {
                    end = start + span - 1;
                    startChunk = start / chunkSize;
                    endChunk = end / chunkSize;
                    for (int chunk = startChunk, pos = startChunk * chunkSize;
                         chunk <= endChunk;
                         chunk++, pos += chunkSize) {
                        // compute how many values are within the current chunk
                        // and update the chunk
                        partial = Math.min(end, pos + chunkSize) - Math.max(start, pos);
                        values.add(partial * Integer.parseInt(line));
                        empty = false;
                    }
                    start += span;
                }
                // next line...
                line = bufferedReader.readLine();
            }
        }

        if (!empty) {
            // save values for the current chromosome into the database
            System.out.println("\tStoring " + values.size() + " values for " + chromosome);
//            if (chromosome.equals("chr1")) {
                computeAndSaveMeanValues(values, wigPath, chromosome, chunkSize, chunkFrequencyManager);
//            }
        }

        return dbPath;
    }

    /**
     * Return true if the given line is a wig header line (fixed or variable step)
     *
     * @param headerLine    Wig header line
     * @return              True or false
     */
    public static boolean isHeaderLine(String headerLine) {
        return (headerLine.startsWith("fixedStep") || headerLine.startsWith("variableStep"));
    }

    /**
     * Return true if the given line is a "fixed step" header line
     *
     * @param headerLine    Wig header line
     * @return              True or false
     */
    public static boolean isFixedStep(String headerLine) {
        return (headerLine.startsWith("fixedStep"));
    }

    /**
     * Return true if the given line is a "variable step" header line
     *
     * @param headerLine    Wig header line
     * @return              True or false
     */
    public static boolean isVariableStep(String headerLine) {
        return (headerLine.startsWith("variableStep"));
    }

    /**
     * Extract the chromosome value from the given Wig header line.
     *
     * @param headerLine    Header line where to look for the chromosome
     * @return              Chromosome value
     */
    public static String getChromosome(String headerLine) throws InvalidObjectException {
        String chromosome = getHeaderInfo("chrom", headerLine);
        if (chromosome == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'chrom' in the header line");
        }
        return chromosome;
    }

    /**
     * Extract the 'start' value from the given Wig header line.
     *
     * @param headerLine    Header line where to look for the 'start'
     * @return              Start value
     */
    public static int getStart(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("start", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'start' in the header line");
        }
        return Integer.parseInt(str);
    }

    /**
     * Extract the 'step' value from the given Wig header line.
     *
     * @param headerLine    Header line where to look for the 'step'
     * @return              Step value
     */
    public static int getStep(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("step", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'step' in the header line");
        }
        return Integer.parseInt(str);
    }

    /**
     * Extract the 'span' value from the given Wig header line.
     *
     * @param headerLine    Header line where to look for the 'span'
     * @return              Span value
     */
    public static int getSpan(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("span", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'span' in the header line");
        }
        return Integer.parseInt(str);
    }

    /**
     * Compute the mean values for an array and save them into the database using the ChunkFrequencyManger.
     * The array contains the total sum (counting) for each chunk. One element per chunk, and the values
     * have to be divided by the chunk size in order to compute the mean values.
     *
     * @param values        Array of values, one value per chunk
     * @param filePath      File target
     * @param chromosome    Chromosome target
     * @param chunkSize     Size of chunk, it will be used to compute the mean value for each chunk
     * @param chunkFrequencyManager     ChunkFrequencyManager to insert mean values to the database
     */
    public static void computeAndSaveMeanValues(List<Integer> values, Path filePath, String chromosome,
                                                int chunkSize, ChunkFrequencyManager chunkFrequencyManager)
            throws IOException {
        if (chromosome != null && values != null) {
            // compute mean values and save into the DB
            List<Integer> meanValues = new ArrayList<>(values.size());
            for (int v : values) {
                meanValues.add(v / chunkSize);
            }
            chunkFrequencyManager.insert(filePath, chromosome, meanValues);
        }
    }

    public static void validateRegion(Region region, BBFileReader bbFileReader) {
        String chrom = region.getChromosome();
        if (StringUtils.isEmpty(chrom)) {
            throw new IllegalArgumentException("Missing chromosome for region: " + region.toString());
        }

        if (bbFileReader.getChromosomeID(chrom) == -1) {
            if (chrom.startsWith("chr")) {
                chrom = chrom.replace("chr", "");
                if (bbFileReader.getChromosomeID(chrom) == -1) {
                    throw new IllegalArgumentException("Unknown chromosome: " + region.getChromosome());
                } else {
                    region.setChromosome(chrom);
                }
            } else {
                if (bbFileReader.getChromosomeID("chr" + chrom) == -1) {
                    throw new IllegalArgumentException("Unknown chromosome: " + region.getChromosome());
                } else {
                    region.setChromosome("chr" + chrom);
                }
            }
        }
    }

    public static long getTotalCounts(BBFileReader bbFileReader) throws IOException {
        long totalCounts = 0;

        if (bbFileReader.getZoomLevelCount() == 0) {
            BigWigIterator bigWigIterator = bbFileReader.getBigWigIterator();
            while (bigWigIterator.hasNext()) {
                WigItem next = bigWigIterator.next();
                totalCounts += ((next.getEndBase() - next.getStartBase()) * next.getWigValue());
            }
        } else {
            int zoom = bbFileReader.getZoomLevelCount();
            ZoomLevelIterator zoomLevelIterator = bbFileReader.getZoomLevelIterator(zoom);
            while (zoomLevelIterator.hasNext()) {
                ZoomDataRecord next = zoomLevelIterator.next();
                totalCounts += next.getSumData();
            }
        }

        return totalCounts;
    }

    /**
     * P R I V A T E   M E T H O D S
     */

    /**
     * Get information from a Wig header line.
     *
     * @param name          Name of the information, e.g.: span, chrom, step,...
     * @param headerLine    Header line where to search that information
     * @return              Value of the information
     */
    private static String getHeaderInfo(String name, String headerLine) {
        String[] fields = headerLine.split("[\t ]");
        for (String field : fields) {
            if (field.startsWith(name + "=")) {
                String[] subfields = field.split("=");
                return subfields[1];
            }
        }
        return null;
    }
}
