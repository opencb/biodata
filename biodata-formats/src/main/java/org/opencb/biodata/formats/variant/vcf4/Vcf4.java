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

package org.opencb.biodata.formats.variant.vcf4;


import com.google.common.base.Joiner;

import java.util.*;


public class Vcf4 {

    private String fileFormat;
    private Map<String, String> metaInformation;
    private Map<String, VcfAlternateHeader> alternate;
    private Map<String, VcfFilterHeader> filter;
    private Map<String, VcfInfoHeader> info;
    private Map<String, VcfFormatHeader> format;
    private List<String> headerLine;
    private List<VcfRecord> records;
    private Map<String, Integer> samples;
    private List<String> sampleNames;

    public Vcf4() {
        this("VCFv4.0");
    }

    public Vcf4(String fileformat) {
        this.fileFormat = fileformat;
        metaInformation = new LinkedHashMap<>();

        alternate = new LinkedHashMap<>();
        filter = new LinkedHashMap<>();
        info = new LinkedHashMap<>();
        format = new LinkedHashMap<>();

        headerLine = new ArrayList<>(1);
        records = new ArrayList<>();
        samples = new LinkedHashMap<>(100);
        sampleNames = new ArrayList<>(100);
    }

    public void addMetaInfo(String key, String value) {
        if (metaInformation == null) {
            metaInformation = new HashMap<>();
        }
        metaInformation.put(key, value);
    }

    public void addAlternate(String id, String description) {
        if (alternate == null) {
            alternate = new LinkedHashMap<>();
        }
        alternate.put(id, new VcfAlternateHeader(id, description));
    }

    public void addFilter(String id, String description) {
        if (filter == null) {
            filter = new LinkedHashMap<>();
        }
        filter.put(id, new VcfFilterHeader(id, description));
    }

    public void addInfo(String id, String number, String type, String description) {
        if (info == null) {
            info = new LinkedHashMap<>();
        }
        info.put(id, new VcfInfoHeader(id, number, type, description));
    }

    public void addFormat(String id, String number, String type, String description) {
        if (format == null) {
            format = new LinkedHashMap<>();
        }
        format.put(id, new VcfFormatHeader(id, number, type, description));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("##fileformat=").append(fileFormat).append("\n");
        // print metaInformation
        Iterator<String> iter = metaInformation.keySet().iterator();
        String headerKey;
        while (iter.hasNext()) {
            headerKey = iter.next();
            stringBuilder.append("##").append(headerKey).append("=").append(metaInformation.get(headerKey)).append("\n");
        }

        for (VcfAlternateHeader vcfAlternateHeader : alternate.values()) {
            stringBuilder.append(vcfAlternateHeader.toString()).append("\n");
        }

        for (VcfFilterHeader vcfFilterHeader : filter.values()) {
            stringBuilder.append(vcfFilterHeader.toString()).append("\n");
        }

        for (VcfInfoHeader vcfInfoHeader : info.values()) {
            stringBuilder.append(vcfInfoHeader.toString()).append("\n");
        }

        for (VcfFormatHeader vcfFormatHeader : format.values()) {
            stringBuilder.append(vcfFormatHeader.toString()).append("\n");
        }

        // header and data lines
        stringBuilder.append("#").append(Joiner.on("\t").join(headerLine, "\t")).append("\n");
        for (VcfRecord vcfRecord : records) {
            stringBuilder.append(vcfRecord.toString()).append("\n");
        }
        return stringBuilder.toString().trim();
    }

    /**
     * @return the fileFormat
     */
    public String getFileFormat() {
        return fileFormat;
    }

    /**
     * @param fileFormat the fileFormat to set
     */
    public void setFileFormat(String fileFormat) {
        this.fileFormat = fileFormat;
    }

    /**
     * @return the metaInformation
     */
    public Map<String, String> getMetaInformation() {
        return metaInformation;
    }

    /**
     * @param metaInformation the metaInformation to set
     */
    public void setMetaInformation(Map<String, String> metaInformation) {
        this.metaInformation = metaInformation;
    }

    /**
     * @return the alternate
     */
    public Map<String, VcfAlternateHeader> getAlternate() {
        return alternate;
    }

    /**
     * @param alternate the alternate to set
     */
    public void setAlternate(Map<String, VcfAlternateHeader> alternate) {
        this.alternate = alternate;
    }

    /**
     * @return the filter
     */
    public Map<String, VcfFilterHeader> getFilter() {
        return filter;
    }

    /**
     * @param filter the filter to set
     */
    public void setFilter(Map<String, VcfFilterHeader> filter) {
        this.filter = filter;
    }

    /**
     * @return the info
     */
    public Map<String, VcfInfoHeader> getInfo() {
        return info;
    }

    /**
     * @param info the info to set
     */
    public void setInfo(Map<String, VcfInfoHeader> info) {
        this.info = info;
    }

    /**
     * @return the format
     */
    public Map<String, VcfFormatHeader> getFormat() {
        return format;
    }

    /**
     * @param format the format to set
     */
    public void setFormat(Map<String, VcfFormatHeader> format) {
        this.format = format;
    }

    /**
     * @return the headerLine
     */
    public List<String> getHeaderLine() {
        return headerLine;
    }

    /**
     * @param headerLine the headerLine to set
     */
    public void setHeaderLine(List<String> headerLine) {
        this.headerLine = headerLine;
        int i = 0;

        if (headerLine.size() > 9) {
            for (String sample : this.headerLine.subList(9, this.headerLine.size())) {
                samples.put(sample, i);
                sampleNames.add(sample);
                i++;
            }
        }

    }

    /**
     * @return the records
     */
    public List<VcfRecord> getRecords() {
        return records;
    }

    /**
     * @param records the records to set
     */
    public void setRecords(List<VcfRecord> records) {
        this.records = records;
    }

    public Map<String, Integer> getSamples() {
        return samples;
    }

    public List<String> getSampleNames() {
        return sampleNames;
    }
}
