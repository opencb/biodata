package org.opencb.biodata.formats.alignment.samtools.io;

import org.opencb.biodata.formats.alignment.samtools.SamtoolsFlagstats;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Path;

public class SamtoolsFlagstatsParser {

    public static SamtoolsFlagstats parse(Path path) throws IOException {
        try (FileReader fr = new FileReader(path.toFile())) {
            return parse(fr);
        }
    }

    public static SamtoolsFlagstats parse(Reader fr) throws IOException {
        SamtoolsFlagstats flagstats = new SamtoolsFlagstats();
        BufferedReader br = new BufferedReader(fr);

        String line;

        while ((line = br.readLine()) != null) {
            String[] splits = line.split(" ");
            int passed = Integer.parseInt(splits[0]);
            int failed = Integer.parseInt(splits[2]);
            if (line.contains("QC-passed")) {
                flagstats.setTotalQcPassed(passed);
                flagstats.setTotalReads(passed + failed);
            } else if (line.contains("secondary")) {
                flagstats.setSecondaryAlignments(passed);
            } else if (line.contains("supplementary")) {
                flagstats.setSupplementary(passed);
            } else if (line.contains("duplicates")) {
                flagstats.setDuplicates(passed);
            } else if (line.contains("paired in sequencing")) {
                flagstats.setPairedInSequencing(passed);
            } else if (line.contains("read1")) {
                flagstats.setRead1(passed);
            } else if (line.contains("read2")) {
                flagstats.setRead2(passed);
            } else if (line.contains("properly paired")) {
                flagstats.setProperlyPaired(passed);
            } else if (line.contains("with itself and mate mapped")) {
                flagstats.setSelfAndMateMapped(passed);
            } else if (line.contains("singletons")) {
                flagstats.setSingletons(passed);
            } else if (line.contains("mapQ>=5")) {
                flagstats.setDiffChrMapQ5(passed);
            } else if (line.contains("with mate mapped")) {
                flagstats.setMateMappedToDiffChr(passed);
            } else if (line.contains("mapped")) {
                // This must be the last if-else, as other lines may contain the key "mapped"
                flagstats.setMapped(passed);
            }
        }

        return flagstats;
    }
}
