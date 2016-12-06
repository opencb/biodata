package org.opencb.biodata.formats.wig;

import java.io.IOException;
import java.io.InvalidObjectException;

/**
 * Created by jtarraga on 02/12/16.
 */
public class WigUtils {

    public static boolean isHeaderLine(String headerLine) {
        return (headerLine.startsWith("fixedStep") || headerLine.startsWith("variableStep"));
    }

    public static boolean isFixedStep(String headerLine) {
        return (headerLine.startsWith("fixedStep"));
    }

    public static boolean isVariableStep(String headerLine) {
        return (headerLine.startsWith("variableStep"));
    }

    public static String getChromosome(String headerLine) throws InvalidObjectException {
        String chromosome = getHeaderInfo("chrom", headerLine);
        if (chromosome == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'chrom' in the header line");
        }
        return chromosome;
    }

    public static int getStart(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("start", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'start' in the header line");
        }
        return Integer.parseInt(str);
    }

    public static int getStep(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("step", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'step' in the header line");
        }
        return Integer.parseInt(str);
    }

    public static int getSpan(String headerLine) throws InvalidObjectException {
        String str = getHeaderInfo("span", headerLine);
        if (str == null) {
            throw new InvalidObjectException("WigFile format, it could not find 'span' in the header line");
        }
        return Integer.parseInt(str);
    }

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
