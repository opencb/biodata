/*
 * <!--
 *   ~ Copyright 2015-2017 OpenCB
 *   ~
 *   ~ Licensed under the Apache License, Version 2.0 (the "License");
 *   ~ you may not use this file except in compliance with the License.
 *   ~ You may obtain a copy of the License at
 *   ~
 *   ~     http://www.apache.org/licenses/LICENSE-2.0
 *   ~
 *   ~ Unless required by applicable law or agreed to in writing, software
 *   ~ distributed under the License is distributed on an "AS IS" BASIS,
 *   ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   ~ See the License for the specific language governing permissions and
 *   ~ limitations under the License.
 *   -->
 *
 */

package org.opencb.biodata.models.core;

import java.util.List;

public class Snp {
    private String id;
    private String chromosome;
    private int position;
    private String reference;
    private List<String> alleles;
    private String type;
    private String source;
    private String version;
    private SnpAnnotation annotation;

    public Snp() {
    }

    public Snp(String id, String chromosome, int position, String reference, List<String> alleles, String type,
               String source, String version, SnpAnnotation annotation) {
        this.id = id;
        this.chromosome = chromosome;
        this.position = position;
        this.reference = reference;
        this.alleles = alleles;
        this.type = type;
        this.source = source;
        this.version = version;
        this.annotation = annotation;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Snp{");
        sb.append("id='").append(id).append('\'');
        sb.append(", chromosome='").append(chromosome).append('\'');
        sb.append(", position=").append(position);
        sb.append(", reference='").append(reference).append('\'');
        sb.append(", alleles=").append(alleles);
        sb.append(", type='").append(type).append('\'');
        sb.append(", source='").append(source).append('\'');
        sb.append(", version='").append(version).append('\'');
        sb.append(", annotation=").append(annotation);
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public Snp setId(String id) {
        this.id = id;
        return this;
    }

    public String getChromosome() {
        return chromosome;
    }

    public Snp setChromosome(String chromosome) {
        this.chromosome = chromosome;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public Snp setPosition(int position) {
        this.position = position;
        return this;
    }

    public String getReference() {
        return reference;
    }

    public Snp setReference(String reference) {
        this.reference = reference;
        return this;
    }

    public List<String> getAlleles() {
        return alleles;
    }

    public Snp setAlleles(List<String> alleles) {
        this.alleles = alleles;
        return this;
    }

    public String getType() {
        return type;
    }

    public Snp setType(String type) {
        this.type = type;
        return this;
    }

    public String getSource() {
        return source;
    }

    public Snp setSource(String source) {
        this.source = source;
        return this;
    }

    public String getVersion() {
        return version;
    }

    public Snp setVersion(String version) {
        this.version = version;
        return this;
    }

    public SnpAnnotation getAnnotation() {
        return annotation;
    }

    public Snp setAnnotation(SnpAnnotation annotation) {
        this.annotation = annotation;
        return this;
    }
}
