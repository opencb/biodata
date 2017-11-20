/*
 * Copyright 2015 OpenCB
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opencb.biodata.formats.variant.vcf4.io;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.io.VariantWriter;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.StudyEntry;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 9/15/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantVcfDataWriter implements VariantWriter {

    private PrintWriter printer;
    private String filename;
    private VariantReader reader;
    private List<String> format;


    public VariantVcfDataWriter(VariantReader reader, String filename) {
        this.filename = filename;
        this.reader = reader;
    }

    @Override
    public boolean open() {
        try {
            printer = new PrintWriter(filename);
        } catch (FileNotFoundException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        printer.close();
        return true;
    }

    @Override
    public boolean pre() {
        printer.append(reader.getHeader());
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Variant elem) {
        StringBuilder sb = new StringBuilder();
        sb.append(elem.getChromosome()).append("\t");
        sb.append(elem.getStart()).append("\t");
        if (elem.getIds() == null || elem.getIds().isEmpty()) {
            sb.append(".").append("\t");
        } else {
            Iterator<String> iterator = elem.getIds().iterator();
            sb.append(iterator.next());
            while (iterator.hasNext()) {
                sb.append(";").append(iterator.next());
            }
            sb.append("\t");
        }
        sb.append(elem.getReference()).append("\t");
        sb.append(elem.getAlternate()).append("\t");

        StudyEntry file = elem.getSourceEntries().values().iterator().next();
        if (file == null) {
            // There must be a file associated with this variant
            return false;
        }

        if (file.hasAttribute("QUAL")) {
            sb.append(file.getAttribute("QUAL"));
        } else {
            sb.append(".");
        }
        sb.append("\t");

        if (file.hasAttribute("FILTER")) {
            sb.append(file.getAttribute("FILTER"));
        } else {
            sb.append(".");
        }
        sb.append("\t");

        if (format == null) {
            format = getFormatOrder(file);
        }

        sb.append(generateInfo(file.getAttributes())).append("\t");
        sb.append(Joiner.on(":").join(format)).append("\t");
        sb.append(generateSampleInfo(elem, file, format));

        printer.append(sb.toString()).append("\n"); // TODO aaleman: Create a Variant2Vcf converters.
        return true;
    }

    private String generateSampleInfo(Variant elem, StudyEntry file, List<String> format) {
        StringBuilder sb = new StringBuilder();

        Iterator<String> sampleIt = elem.getSampleNames(file.getStudyId()).iterator();
        Iterator<String> formatIt;
        Map<String, String> data;
        String sampleName, formatElem;
        while (sampleIt.hasNext()) {
            sampleName = sampleIt.next();
            data = file.getSampleDataAsMap(sampleName);
            formatIt = format.iterator();

            while (formatIt.hasNext()) {
                formatElem = formatIt.next();
                sb.append(data.get(formatElem));
                if (formatIt.hasNext()) {
                    sb.append(":");
                }
            }
            if (sampleIt.hasNext()) {
                sb.append("\t");
            }
        }

        return sb.toString();
    }

    private List<String> getFormatOrder(StudyEntry file) {
        return Lists.newArrayList(file.getFormatAsString().split(":"));
    }

    private String generateInfo(Map<String, String> attributes) {
        StringBuilder sb = new StringBuilder();

        Iterator<Map.Entry<String, String>> it = attributes.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();

            if (!entry.getKey().equalsIgnoreCase("QUAL") && !entry.getKey().equalsIgnoreCase("FILTER") && !entry.getKey().equalsIgnoreCase("src")) {
                sb.append(entry.getKey());
                if (!"".equals(entry.getValue())) {
                    sb.append("=").append(entry.getValue());
                }
                if (it.hasNext()) {
                    sb.append(";");
                }
            }
        }

        if (sb.length() == 0) {
            sb.append(".");
        }

        return sb.toString();
    }


    @Override
    public boolean write(List<Variant> batch) {
        for (Variant record : batch) {
            this.write(record);
        }

        return true;
    }

    @Override
    public void includeStats(boolean stats) {
    }

    @Override
    public void includeSamples(boolean samples) {
    }

    @Override
    public void includeEffect(boolean effect) {
    }

}
