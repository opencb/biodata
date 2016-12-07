package org.opencb.biodata.tools.feature;

import htsjdk.samtools.SAMFileHeader;
import org.opencb.biodata.tools.alignment.BamUtils;
import org.opencb.biodata.tools.commons.ChunkFrequencyManager;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.nio.file.Path;
import java.util.*;

/**
 * Created by jtarraga on 02/12/16.
 */
public class WigUtils {

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
     * Index the entire Wig file content in a SQLite database managed by the ChunkFrequencyManager.
     *
     * @param wigPath   Wig file
     * @param bamPath   BAM file associated to the Wig file
     * @param cfManager ChunkFrequencyManager who manages the SQLite database
     * @throws Exception
     */
    public static void index(Path wigPath, Path bamPath, ChunkFrequencyManager cfManager) throws Exception {
        SAMFileHeader samHeader = BamUtils.getFileHeader(bamPath);
        List chromosomeNames = new ArrayList<String>();
        samHeader.getSequenceDictionary().getSequences().forEach(s -> chromosomeNames.add(s.getSequenceName()));
        index(chromosomeNames, wigPath, bamPath, cfManager);
    }

    /**
     * Index the values for the given chromosomes of the Wig file in
     * a SQLite database managed by the ChunkFrequencyManager.
     *
     * @param wigPath   Wig file
     * @param bamPath   BAM file associated to the Wig file
     * @param cfManager ChunkFrequencyManager who manages the SQLite database
     * @throws Exception
     */
    public static void index(List<String> chromosomes, Path wigPath, Path bamPath, ChunkFrequencyManager cfManager) throws Exception {
        // insert file into the DB and get its ID, and the ChunkSize as well
        int fileId = cfManager.insertFile(bamPath);
        int chunkSize = cfManager.readChunkSize();

        // for efficiency purposes
        Set<String> chromSet = new HashSet<>();
        chromosomes.forEach(c -> chromSet.add(c));
        SAMFileHeader samHeader = BamUtils.getFileHeader(bamPath);
        Map<String, Integer> chromMap = new HashMap<>();
        samHeader.getSequenceDictionary().getSequences()
                .forEach(s -> chromMap.put(s.getSequenceName(), s.getSequenceLength()));

        String chromosome = null;
        int step, span = 1, start = 1, end;
        int startChunk, endChunk, partial, chromosomeSize = 0;

        int[] values = null;

        // reader
        BufferedReader bufferedReader = FileUtils.newBufferedReader(wigPath);
        // main loop
        String line = bufferedReader.readLine();
        while (line != null) {
            // check for header lines
            if (WigUtils.isHeaderLine(line)) {
                System.out.println("Loading wig data:" + line);
                if (WigUtils.isVariableStep(line)) {
                    throw new UnsupportedOperationException("Wig coverage file with 'variableStep'"
                            + " is not supported yet.");
                }

                // save values for the current chromosome into the database
                computeAndSaveMeanValues(values, fileId, chromosome, chunkSize, cfManager);

                // update some values
                values = null;
                step = WigUtils.getStep(line);
                span = WigUtils.getSpan(line);
                start = WigUtils.getStart(line);
                chromosome = WigUtils.getChromosome(line);
                if (chromSet.contains(chromosome)) {
                    chromosomeSize = chromMap.get(chromosome);
                    values = new int[chromosomeSize / chunkSize + 1];
                    if (step != 1) {
                        throw new UnsupportedOperationException("Wig coverage file with"
                                + " 'step' != 1 is not supported yet.");
                    }
                }
                // next line...
                line = bufferedReader.readLine();
            } else {
                if (values != null) {
                    end = Math.min(start + span - 1, chromosomeSize);
                    startChunk = start / chunkSize;
                    endChunk = end / chunkSize;
                    for (int chunk = startChunk, pos = startChunk * chunkSize;
                         chunk <= endChunk;
                         chunk++, pos += chunkSize) {
                        // compute how many values are within the current chunk
                        // and update the chunk
                        partial = Math.min(end, pos + chunkSize) - Math.max(start, pos);
                        try {
                            values[chunk] += (partial * Integer.parseInt(line));
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                        }
                    }
                    start += span;
                }
                // next line...
                line = bufferedReader.readLine();
            }
        }
        // save values for the last chromosome into the database
        computeAndSaveMeanValues(values, fileId, chromosome, chunkSize, cfManager);
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

    /**
     * Compute the mean values for an array and save them into the database using the ChunkFrequencyManger.
     * The array contains the total sum (counting) for each chunk. One element per chunk, and the values
     * have to be divided by the chunk size in order to compute the mean values.
     *
     * @param values        Array of values, one value per chunk
     * @param fileId        FileId associated
     * @param chromosome    Chromosome
     * @param chunkSize     Size of chunk, it will be used to compute the mean value for each chunk
     * @param cfManager     ChunkFrequencyManager to insert mean values to the database
     */
    private static void computeAndSaveMeanValues(int[] values, int fileId, String chromosome,
                                                 int chunkSize, ChunkFrequencyManager cfManager) throws IOException {
        if (values != null) {
            // compute mean values and save into the DB
            List<Integer> meanValues = new ArrayList<>(values.length);
            for (int v : values) {
                meanValues.add(v / chunkSize);
            }
            cfManager.insert(chromosome, meanValues, fileId);
        }
    }
}
