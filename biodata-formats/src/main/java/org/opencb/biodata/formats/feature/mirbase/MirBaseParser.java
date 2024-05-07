package org.opencb.biodata.formats.feature.mirbase;

import org.opencb.biodata.models.core.MiRnaGene;
import org.opencb.biodata.models.core.MiRnaMature;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Path;

public class MirBaseParser {

    private static final String ID_LABEL = "ID";
    private static final String AC_LABEL = "AC";
    private static final String DE_LABEL = "DE";
    private static final String FT_LABEL = "FT";
    private static final String SQ_LABEL = "SQ";
    private static final String END_OF_ITEM_LABEL = "XX";
    private static final String END_OF_RECORD_LABEL = "//";

    private static final String MIRNA_LABEL = "miRNA";

    private MirBaseParser() {
        throw new IllegalStateException("Utility class");
    }

    public static void parse(Path miRnaDatFile, String species, MirBaseParserCallback callback) throws IOException {
        try (BufferedReader datReader = new BufferedReader(new InputStreamReader(FileUtils.newInputStream(miRnaDatFile)))) {
            String miRBaseAccession = null;
            String miRBaseID = null;
            MiRnaGene miRnaGene = null;
            String line;
            while ((line = datReader.readLine()) != null) {
                String[] split = line.split("\\s+");
                switch (split[0]) {
                    case ID_LABEL: {
                        miRBaseID = split[1];
                        break;
                    }
                    case AC_LABEL: {
                        miRBaseAccession = split[1].split(";")[0];
                        break;
                    }
                    case DE_LABEL: {
                        if (line.contains(species)) {
                            miRnaGene = new MiRnaGene();
                            miRnaGene.setId(miRBaseID)
                                    .setAccession(miRBaseAccession);
                        }
                        break;
                    }
                    case FT_LABEL: {
                        if (miRnaGene != null && MIRNA_LABEL.equalsIgnoreCase(split[1])) {
                            processMiRnaMature(line, miRnaGene, datReader);
                        }
                        break;
                    }
                    case SQ_LABEL: {
                        if (miRnaGene != null) {
                            StringBuilder seq = new StringBuilder();
                            // Read until END_OF_RECORD_LABEL
                            while (!(line = datReader.readLine()).equals(END_OF_RECORD_LABEL)) {
                                split = line.split("\\s+");
                                for (int i = 1; i < split.length - 1; i++) {
                                    seq.append(split[i]);
                                }
                            }
                            miRnaGene.setSequence(seq.toString());

                            // Update mature sequences
                            for (MiRnaMature mature : miRnaGene.getMatures()) {
                                if (mature.getStart() > 0 && mature.getEnd() > 0) {
                                    mature.setSequence(miRnaGene.getSequence().substring(mature.getStart() - 1, mature.getEnd()));
                                }
                            }

                            // Callback
                            callback.processMiRnaGene(miRnaGene);
                            miRnaGene = null;
                        }
                        break;
                    }
                    default: {
                        // Do nothing
                        break;
                    }
                }
            }
        }
    }

    private static void processMiRnaMature(String headerLine, MiRnaGene miRnaGene, BufferedReader datReader) throws IOException {
        // Create MiRNA mature from header line,
        // e.g: FT   miRNA           6..27
        MiRnaMature miRnaMature = new MiRnaMature();
        String[] split = headerLine.split("\\s+");
        String[] pos = split[2].split("\\.\\.");
        miRnaMature.setStart(Integer.parseInt(pos[0]));
        miRnaMature.setEnd(Integer.parseInt(pos[1]));

        String line;
        while (!(line = datReader.readLine()).equals(END_OF_ITEM_LABEL)) {
            split = line.split("\\s+");
            if (split[0].equalsIgnoreCase(FT_LABEL) && split[1].equalsIgnoreCase(MIRNA_LABEL)) {
                processMiRnaMature(line, miRnaGene, datReader);
                break;
            } else {
                if (line.contains("accession=")) {
                    miRnaMature.setAccession(line.split("accession=")[1].replace("\"", ""));
                } else if (line.contains("product=")) {
                    miRnaMature.setId(line.split("product=")[1].replace("\"", ""));
                }
            }
        }
        miRnaGene.getMatures().add(miRnaMature);
    }
}
