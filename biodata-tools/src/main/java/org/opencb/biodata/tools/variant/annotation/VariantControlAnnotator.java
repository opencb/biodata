package org.opencb.biodata.tools.variant.annotation;

import java.io.IOException;
import java.util.*;

import net.sf.samtools.util.StringUtil;
import org.broad.tribble.readers.TabixReader;
import org.opencb.biodata.models.feature.Genotype;
import org.opencb.biodata.models.variant.*;
import org.opencb.biodata.models.variant.stats.VariantStats;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 */
public class VariantControlAnnotator implements VariantAnnotator {

    // private TabixReader tabix;
    private String tabixFile;
    private List<String> samples;
    private Map<String, String> controlList;
    private Map<Long, Map<String, TabixReader>> multipleControlsTabix;
    private Map<Long, TabixReader> tabix;
    private String prefix;
    private boolean single;
    private VariantSource source = new VariantSource("CONTROL","CONTROL","CONTROL","CONTROL");
    private VariantFactory factory = new VariantVcfFactory();

    public VariantControlAnnotator(String infoPrefix, String control) {

        this.prefix = infoPrefix;
        this.tabixFile = control;
        this.tabix = new LinkedHashMap<>();
        TabixReader tabixReader;
        try {
            tabixReader = new TabixReader(this.tabixFile);
            String line;
            while ((line = tabixReader.readLine()) != null && !line.startsWith("#CHROM")) {
            }

            String[] fields = line.split("\t");

            samples = new ArrayList<>(fields.length - 9);
            for (int i = 9, j = 0; i < fields.length; i++, j++) {
                samples.add(fields[i]);

            }

            tabixReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        source.setSamples(samples);
        single = true;

    }

    public VariantControlAnnotator(String infoPrefix, Map<String, String> controlList) {

        this.prefix = infoPrefix;
        this.controlList = controlList;
        multipleControlsTabix = new LinkedHashMap<>(5);

        boolean b = true;
        TabixReader t;

        try {
            for (Map.Entry<String, String> entry : controlList.entrySet()) {

                t = new TabixReader(entry.getValue());

                if (b) {
                    b = false;

                    String line;
                    while ((line = t.readLine()) != null && !line.startsWith("#CHROM")) {};

                    String[] fields = line.split("\t");

                    samples = new ArrayList<>(fields.length - 9);
                    for (int i = 9, j = 0; i < fields.length; i++, j++) {
                        samples.add(fields[i]);

                    }
                }
                t.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        single = false;

    }

    @Override
    public void annot(List<Variant> batch) {
        if (single) {
            singleAnnot(batch);
        } else {
            multipleAnnot(batch);
        }
    }

    private void singleAnnot(List<Variant> batch) {
        List<Variant> listRecords;

        long pid = Thread.currentThread().getId();
        TabixReader currentTabix = null;


        if (tabix.containsKey(pid)) {
            currentTabix = tabix.get(pid);
        } else {
            try {
                currentTabix = new TabixReader(this.tabixFile);
                tabix.put(pid, currentTabix);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        for (Variant record : batch) {

            if (currentTabix != null) {
                try {

                    TabixReader.Iterator it = currentTabix.query(record.getChromosome() + ":" + record.getStart() + "-" + record.getEnd());

                    String line;
                    while (it != null && (line = it.next()) != null) {

                        listRecords = factory.create(source, line);

                        VariantStats.calculateStatsForVariantsList(listRecords, null);

                        for(Variant v : listRecords){
                            if(v.getReference().equals(record.getReference()) && v.getAlternate().equals(record.getAlternate())){
                                VariantSourceEntry avf = v.getSourceEntry("CONTROL", null);

                                String gt = StringUtil.join(",", joinGenotypes(avf.getStats().getGenotypesCount()));
                                String maf = String.format("%.4f", avf.getStats().getMaf());
                                String amaf = avf.getStats().getMafAllele();
                                for(Map.Entry<String, VariantSourceEntry> entry: record.getSourceEntries().entrySet()){
                                    entry.getValue().addAttribute(this.prefix + "_gt", gt);
                                    entry.getValue().addAttribute(this.prefix + "_maf", maf);
                                    entry.getValue().addAttribute(this.prefix + "_amaf", amaf);
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) {
                    continue;
                }
            }
        }

    }

    private String joinGenotypes(Map<Genotype, Integer> genotypesCount) {

        StringBuilder sb = new StringBuilder();

        int size = 0;
        for(Map.Entry<Genotype,Integer> entry: genotypesCount.entrySet()){

            sb.append(entry.getKey()).append(":").append(entry.getValue());

            if(size + 1 < genotypesCount.size()){
                sb.append(",");
            }
            size++;

        }

        return sb.toString();
    }

    private void multipleAnnot(List<Variant> batch) {

        Variant tabixRecord;
        List<Variant> listRecords;

        TabixReader currentTabix;

        long pid = Thread.currentThread().getId();
        Map<String, TabixReader> tabixMap;
        List<Variant> controlBatch = new ArrayList<>(batch.size());
        Map<Variant, Integer> map = new LinkedHashMap<>(batch.size());


        if (multipleControlsTabix.containsKey(pid)) {
            tabixMap = multipleControlsTabix.get(pid);
        } else {
            tabixMap = new LinkedHashMap<>();
            multipleControlsTabix.put(pid, tabixMap);
        }


        int cont = 0;
        for (Variant record : batch) {
            currentTabix = null;

            if (tabixMap.containsKey(record.getChromosome())) {
                currentTabix = tabixMap.get(record.getChromosome());
            } else {
                if (controlList.containsKey(record.getChromosome())) {
                    try {
                        currentTabix = new TabixReader(controlList.get(record.getChromosome()));
                        tabixMap.put(record.getChromosome(), currentTabix);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            if (currentTabix != null) {
                try {

                    TabixReader.Iterator it = currentTabix.query(record.getChromosome() + ":" + record.getStart() + "-" + record.getStart());

                    String line;
                    while (it != null && (line = it.next()) != null) {

                        listRecords = factory.create(source, line);

                        if(listRecords.size() > 0){

                            tabixRecord = listRecords.get(0);

                            if (tabixRecord.getReference().equals(record.getReference()) && tabixRecord.getAlternate().equals(record.getAlternate())) {
                                controlBatch.add(tabixRecord);
                                map.put(record, cont++);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ArrayIndexOutOfBoundsException e) { // If the Chr does not exist in Controls... TabixReader throws ArrayIndexOut...
                    continue;
                }
            }


        }

        VariantStats.calculateStatsForVariantsList(controlBatch, null);

        for (Variant record : batch) {

            if (map.containsKey(record)) {
                VariantSourceEntry avf = record.getSourceEntry("CONTROL", null);
                avf.addAttribute(this.prefix + "_gt", StringUtil.join(",", avf.getStats().getGenotypesCount()));
                avf.addAttribute(this.prefix + "_maf", String.format("%.4f", avf.getStats().getMaf()));
                avf.addAttribute(this.prefix + "_amaf", avf.getStats().getMafAllele());
            }
        }

    }

    @Override
    public void annot(Variant elem) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
