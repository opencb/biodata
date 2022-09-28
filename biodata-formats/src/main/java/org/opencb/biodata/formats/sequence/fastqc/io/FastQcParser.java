package org.opencb.biodata.formats.sequence.fastqc.io;

import org.opencb.biodata.formats.sequence.fastqc.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public class FastQcParser {

    public static FastQcMetrics parse(File file) throws IOException {
        FastQcMetrics fastQcMetrics = new FastQcMetrics();

        FileReader fr = new FileReader(file);

        BufferedReader br = new BufferedReader(fr);

        // Skip first line
        br.readLine();

        String line;

        while ((line = br.readLine()) != null) {
            if (line.startsWith(">>") && !line.contains("END_MODULE")) {
                String status = line.split("\t")[1].toUpperCase();
                if (line.startsWith(">>Basic Statistics")) {
                    fastQcMetrics.getSummary().setBasicStatistics(status);
                    parseBasicStatistics(fastQcMetrics.getBasicStats(), br);
                } else if (line.startsWith(">>Per base sequence quality")) {
                    fastQcMetrics.getSummary().setPerBaseSeqQuality(status);
//                    parsePerBaseSeqQuality(fastQc.getPerBaseSeqQuality(), br);
                } else if (line.startsWith(">>Per tile sequence quality")) {
                    fastQcMetrics.getSummary().setPerTileSeqQuality(status);
//                    parsePerTileSeqQuality(fastQc.getPerTileSeqQuality(), br);
                } else if (line.startsWith(">>Per sequence quality scores")) {
                    fastQcMetrics.getSummary().setPerSeqQualityScores(status);
//                    parsePerSeqQualityScores(fastQc.getPerSeqQualityScore(), br);
                } else if (line.startsWith(">>Per base sequence content")) {
                    fastQcMetrics.getSummary().setPerBaseSeqContent(status);
//                    parsePerBaseSeqContent(fastQc.getPerBaseSeqContent(), br);
                } else if (line.startsWith(">>Per sequence GC content")) {
                    fastQcMetrics.getSummary().setPerSeqGcContent(status);
//                    parsePerSeqGcContent(fastQc.getPerSeqGcContent(), br);
                } else if (line.startsWith(">>Per base N content")) {
                    fastQcMetrics.getSummary().setPerBaseNContent(status);
//                    parsePerBaseNContent(fastQc.getPerBaseNContent(), br);
                } else if (line.startsWith(">>Sequence Length Distribution")) {
                    fastQcMetrics.getSummary().setSeqLengthDistribution(status);
//                    parseSeqLengthDistribution(fastQc.getSeqLengthDistribution(), br);
                } else if (line.startsWith(">>Sequence Duplication Levels")) {
                    fastQcMetrics.getSummary().setSeqDuplicationLevels(status);
//                    parseSeqDuplicationLevels(fastQc.getSeqDuplicationLevel(), br);
                } else if (line.startsWith(">>Overrepresented sequences")) {
                    fastQcMetrics.getSummary().setOverrepresentedSeqs(status);
//                    parseOverrepresentedSeqs(fastQc.getOverrepresentedSeq(), br);
                } else if (line.startsWith(">>Adapter Content")) {
                    fastQcMetrics.getSummary().setAdapterContent(status);
//                    parseAdapterContent(fastQc.getAdapterContent(), br);
                } else if (line.startsWith(">>Kmer Content")) {
                    fastQcMetrics.getSummary().setKmerContent(status);
//                    parseKmerContent(fastQc.getKmerContent(), br);
                }
            }
        }
        fr.close();

        return fastQcMetrics;
    }

    public static void addImages(Path path, FastQcMetrics fastQcMetrics) throws IOException {
        if (path.toFile().exists() && path.toFile().isDirectory()) {
            for (File imgFile : path.toFile().listFiles()) {
                if (imgFile.isFile() && imgFile.getName().endsWith("png")) {
                    fastQcMetrics.getFiles().add(imgFile.getAbsolutePath());
                }
            }
        }
    }

    private static void parseKmerContent(KmerContent kmerContent, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Sequence	Count	PValue	Obs/Exp Max	Max Obs/Exp Position
            kmerContent.getValues().add(new KmerContent.Value(fields[0], Integer.parseInt(fields[1]), Double.parseDouble(fields[2]),
                    Double.parseDouble(fields[3]), fields[4]));
        }
    }

    private static void parseAdapterContent(AdapterContent adapterContent, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Position	Illumina Universal Adapter	Illumina Small RNA 3' Adapter	Illumina Small RNA 5' Adapter	Nextera Transposase Sequence	SOLID S
            //        mall RNA Adapter
            adapterContent.getValues().add(new AdapterContent.Value(fields[0], Double.parseDouble(fields[1]), Double.parseDouble(fields[2]),
                    Double.parseDouble(fields[3]), Double.parseDouble(fields[4]), Double.parseDouble(fields[5])));
        }

    }

    private static void parseOverrepresentedSeqs(List<OverrepresentedSeq> overrepresentedSeqs, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Sequence	Count	Percentage	Possible Source
            overrepresentedSeqs.add(new OverrepresentedSeq(fields[0], Integer.parseInt(fields[1]), Double.parseDouble(fields[2]),
                    fields[3]));
        }
    }

    private static void parseSeqDuplicationLevels(SeqDuplicationLevel seqDuplicationLevels, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Duplication Level	Percentage of deduplicated	Percentage of total
            seqDuplicationLevels.getValues().add(new SeqDuplicationLevel.Value(fields[0], Double.parseDouble(fields[1]),
                    Double.parseDouble(fields[2])));
        }
    }

    private static void parseSeqLengthDistribution(SeqLengthDistribution seqLengthDistribution, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Length	Count
            seqLengthDistribution.getValues().put(fields[0], Double.parseDouble(fields[1]));
        }
    }

    private static void parsePerBaseNContent(PerBaseNContent perBaseNContent, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Base	N-Count
            perBaseNContent.getValues().put(fields[0], Double.parseDouble(fields[1]));
        }
    }

    private static void parsePerSeqGcContent(PerSeqGcContent perSeqGcContent, BufferedReader br) throws IOException {
        int i = 0;
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #GC Content	Count
            perSeqGcContent.getValues()[i++] = Double.parseDouble(fields[1]);
        }
    }

    private static void parsePerBaseSeqContent(PerBaseSeqContent perBaseSeqContent, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Base	G	A	T	C
            perBaseSeqContent.getValues().add(new PerBaseSeqContent.Value(fields[0], Double.parseDouble(fields[1]), Double.parseDouble(fields[2]),
                    Double.parseDouble(fields[3]), Double.parseDouble(fields[4])));
        }
    }

    private static void parsePerTileSeqQuality(PerTileSeqQuality perTileSeqQuality, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Tile	Base	Mean
            perTileSeqQuality.getValues().add(new PerTileSeqQuality.Value(fields[0], fields[1], Double.parseDouble(fields[2])));
        }
    }

    private static void parsePerSeqQualityScores(PerSeqQualityScore perSeqQualityScore, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Quality	Count
            perSeqQualityScore.getValues().put(Integer.parseInt(fields[0]), Double.parseDouble(fields[1]));
        }
    }

    private static void parsePerBaseSeqQuality(PerBaseSeqQuality perBaseSequenceQualities, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            // #Base	Mean	Median	Lower Quartile	Upper Quartile	10th Percentile	90th Percentile
            perBaseSequenceQualities.getValues().add(new PerBaseSeqQuality.Value(fields[0], Double.parseDouble(fields[1]),
                    Double.parseDouble(fields[2]), Double.parseDouble(fields[3]), Double.parseDouble(fields[4]),
                    Double.parseDouble(fields[5]), Double.parseDouble(fields[6])));
        }
    }

    private static void parseBasicStatistics(Map<String, String> basicStats, BufferedReader br) throws IOException {
        String line;
        while ((line = br.readLine()) != null) {
            if (line.startsWith("#")) {
                continue;
            }
            if (line.startsWith(">>END_MODULE")) {
                return;
            }

            String[] fields = line.split("\t");
            basicStats.put(fields[0], fields[1]);
        }
    }

}
