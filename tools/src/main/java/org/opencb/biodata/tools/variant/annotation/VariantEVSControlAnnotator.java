package org.opencb.biodata.tools.variant.annotation;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.broad.tribble.readers.TabixReader;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFactory;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 */
public class VariantEVSControlAnnotator implements VariantAnnotator {

    private String tabixFile;
    private List<String> samples;
    private TabixReader tabix;
    private String prefix;

    public VariantEVSControlAnnotator(String infoPrefix, String control) {

        this.prefix = infoPrefix;
        this.tabixFile = control;
        try {
            this.tabix = new TabixReader(control);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void annot(List<Variant> batch) {
        Variant tabixRecord;

        for (Variant record : batch) {
            try {
                TabixReader.Iterator it = this.tabix.query(record.getChromosome() + ":" + record.getPosition() + "-" + record.getPosition());

                if (it != null) {
                    String line = it.next();
                    while (it != null && line != null) {
                        String[] fields = line.split("\t");
                        tabixRecord = VariantFactory.createVariantFromVcf(samples, fields);
                        if (tabixRecord.getReference().equals(record.getReference()) && tabixRecord.getAlternate().equals(record.getAlternate())) {
                            parseAndAnnot(record, tabixRecord);
                        }
                        line = it.next();
                    }
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            } catch (ArrayIndexOutOfBoundsException e) { // If the Chr does not exist in Controls... TabixReader throws ArrayIndexOut...
                continue;
            }
        }

    }

    private void parseAndAnnot(Variant record, Variant tabixRecord) { // TODO aaleman: Check this code (EVS)

        if (!tabixRecord.isIndel()) {
            Pattern patternGenot, patternGenotCount, patternAlleleCount;
            Matcher matcherGenot, matcherGenotCount, matcherAlleleCount;
            String info = ""; //  tabixRecord.getInfo();
            StringBuilder gts = new StringBuilder(this.prefix + "_gt=");
            Genotype g = null;
            int min = Integer.MAX_VALUE;
            int minPos = -1;
            String maf = ".";

            patternGenot = Pattern.compile("MAF=(\\d+\\.\\d+),(\\d+\\.\\d+),(\\d+\\.\\d+)");
            matcherGenot = patternGenot.matcher(info);
            if (matcherGenot.find()) {
//                record.addInfoField(this.prefix + "_maf=" + String.format("%.4f", Float.valueOf(matcherGenot.group(3)) / 100));
            }
            patternGenot = Pattern.compile("GTS=(([A-Z]+,?)*)");
            patternAlleleCount = Pattern.compile("TAC=((\\d+,?)*)");
            patternGenotCount = Pattern.compile(";GTC=((\\d+,?)*)");

            matcherGenot = patternGenot.matcher(info);
            matcherAlleleCount = patternAlleleCount.matcher(info);
            matcherGenotCount = patternGenotCount.matcher(info);
            if (matcherGenot.find() && matcherGenotCount.find() && matcherAlleleCount.find()) {

                String[] genotypes = matcherGenot.group(1).split(",");
                String[] genotypesCounts = matcherGenotCount.group(1).split(",");
                String[] alleleCounts = matcherAlleleCount.group(1).split(",");

                for (int i = 0; i < genotypes.length; i++) {
                    String gt = genotypes[i];


                    if (gt.length() == 1) {
                        g = new Genotype(gt + "/" + gt, tabixRecord.getReference(), tabixRecord.getAlternate());
                    } else if (gt.length() == 2) {
                        g = new Genotype(gt.charAt(0) + "/" + gt.charAt(1), tabixRecord.getReference(), tabixRecord.getAlternate());
                    } else {
                        System.out.println("gt.length() = " + gt.length());
                    }

                    g.setCount(Integer.valueOf(genotypesCounts[i]));
                    gts.append(g);

                    if (i + 1 < genotypes.length) {
                        gts.append(",");
                    }
                }


                for (int i = 0; i < alleleCounts.length; i++) {
                    int alleleCount = Integer.parseInt(alleleCounts[i]);
                    if (alleleCount < min) {
                        min = alleleCount;
                        minPos = i;
                    }
                }

                if (minPos < 0) {

                } else if (minPos == 0) {
                    maf = tabixRecord.getReference();
                } else {
                    maf = tabixRecord.getAlternate().split(",")[minPos - 1];
                }

            }

//            record.addInfoField(gts.toString());
//            record.addInfoField(this.prefix + "_amaf=" + maf);
        }


    }

    @Override
    public void annot(Variant elem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}