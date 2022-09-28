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

package org.opencb.biodata.formats.variant.annotation.io;

//import org.opencb.biodata.models.variant.annotation.ConsequenceType;
//import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.apache.commons.lang3.StringUtils;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.avro.ConsequenceType;
import org.opencb.biodata.models.variant.avro.VariantAnnotation;

import org.opencb.biodata.models.variant.avro.VariantType;
import org.opencb.commons.io.DataWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by fjlopez on 12/02/15.
 */
@Deprecated
public class VepFormatWriter implements DataWriter<Variant> {

    String filename;
    BufferedWriter bw;

    private static final Map<VariantType, String> SymbolicToVepTag = new HashMap<>(5);

    static {
        SymbolicToVepTag.put(VariantType.CNV, "CNV");
        SymbolicToVepTag.put(VariantType.COPY_NUMBER, "CNV");
        SymbolicToVepTag.put(VariantType.COPY_NUMBER_GAIN, "CNV");
        SymbolicToVepTag.put(VariantType.COPY_NUMBER_LOSS, "CNV");
        SymbolicToVepTag.put(VariantType.DELETION, "deletion");
        SymbolicToVepTag.put(VariantType.INSERTION, "insertion");
        SymbolicToVepTag.put(VariantType.INVERSION, "INV");
        SymbolicToVepTag.put(VariantType.DUPLICATION, "duplication");
    }

    public VepFormatWriter() {}

    public VepFormatWriter(String filename) {
        this.filename = filename;
    }

    @Override
    public boolean open() {
        try {
            bw = Files.newBufferedWriter(Paths.get(filename), Charset.defaultCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean close() {
        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean pre() {

        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        try {
            bw.write("## VARIANT EFFECT FORMAT GENERATOR\n");
            bw.write("## Output produced at "+dateFormat.format(date)+"\n");
            bw.write("#Uploaded_variation\tLocation\tAllele\tGene\tFeature Feature_type\tConsequence\tcDNA_position\tCDS_position\tProtein_position\tAmino_acids\tCodons\tExisting_variation\tExtra\n");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Variant variant) {

        VariantAnnotation variantAnnotation = variant.getAnnotation();
        String id;
        if((id=variantAnnotation.getId())==null) {
            id = "-";
        }
        String alt;
        String pos;

        switch (variant.getType()) {
            case SNV:
                alt = variant.getAlternate();
                pos = Integer.toString(variant.getStart());
                break;
            case INDEL:
                // Short deletion
                if (StringUtils.isBlank(variant.getAlternate())) {
                    alt = "-";
                    if(variant.getReference().length()>1) {
                        pos = variant.getStart() + "-" + variant.getEnd();
                    } else {
                        pos = Integer.toString(variant.getStart());
                    }
                // Short insertion
                } else if (StringUtils.isBlank(variant.getReference())){
                    // Short insertion
                    alt = variant.getAlternate();
                    pos = variant.getEnd() + "-" + variant.getStart();
                // MNV
                } else {
                    alt = variant.getAlternate();
                    pos = variant.getStart() + "-" + variant.getEnd();
                }
                break;
            default:
                alt = SymbolicToVepTag.get(variant.getType());
                pos = variant.getStart() + "-" + variant.getEnd();
        }



//        // Short deletion
//        if(variantAnnotation.getAlternate().isEmpty()) {
//            alt = "-";
//            if(variantAnnotation.getReference().length()>1) {
//                pos = variantAnnotation.getStart() + "-" + (variantAnnotation.getStart() + variantAnnotation.getReference().length() - 1);
//            } else {
//                pos = Integer.toString(variantAnnotation.getStart());
//            }
//        } else if(variantAnnotation.getReference().isEmpty()) {
//            // Short insertion
//            alt = variantAnnotation.getAlternate();
//            pos = (variantAnnotation.getStart()-1) + "-" + variantAnnotation.getStart();
//            // SNV
//        } else {
//            alt = variantAnnotation.getAlternate();
//            pos = Integer.toString(variantAnnotation.getStart()-1);
//        }

        for(ConsequenceType consequenceType : variantAnnotation.getConsequenceTypes()) {
            String gene;
            if((gene=consequenceType.getEnsemblGeneId())==null) {
                gene = "-";
            }
            String feature;
            if((feature=consequenceType.getEnsemblTranscriptId())==null) {
                feature = "-";
            }
            String featureType;
            if((featureType=consequenceType.getBiotype())==null) {
                featureType = "-";
            }
            String consequences = consequenceType.getSequenceOntologyTerms().get(0).getName();
            for(int i=1; i<consequenceType.getSequenceOntologyTerms().size(); i++) {
                consequences += ","+consequenceType.getSequenceOntologyTerms().get(i).getName();
            }
            Integer cdnaPosition;
            String cdnaPositionString;
            if((cdnaPosition=consequenceType.getCdnaPosition())==null) {
                cdnaPositionString = "-";
            } else {
                cdnaPositionString = cdnaPosition.toString();
            }
            Integer cdsPosition;
            String cdsPositionString;
            if((cdsPosition=consequenceType.getCdsPosition())==null) {
                cdsPositionString = "-";
            } else {
                cdsPositionString = cdsPosition.toString();
            }
            Integer aaPosition;
            String aaPositionString;
            if((consequenceType.getProteinVariantAnnotation() == null)
                    || ((aaPosition=consequenceType.getProteinVariantAnnotation().getPosition())==null)) {
                aaPositionString = "-";
            } else {
                aaPositionString = aaPosition.toString();
            }
            String aaChange;
            if(consequenceType.getProteinVariantAnnotation() == null
                    || consequenceType.getProteinVariantAnnotation().getAlternate()==null) {
                aaChange = "-";
            } else {
                aaChange = consequenceType.getProteinVariantAnnotation().getReference() + "/"
                        + consequenceType.getProteinVariantAnnotation().getAlternate();
            }
            String codon;
            if((codon=consequenceType.getCodon())==null) {
                codon = "-";
            }
            try {
                bw.write(id+"\t"+variantAnnotation.getChromosome()+":"+pos+"\t"+alt+"\t"+gene+"\t"+feature+"\t"+
                        featureType+"\t"+consequences+"\t"+cdnaPositionString+"\t"+cdsPositionString+"\t"+
                        aaPositionString+"\t"+aaChange+"\t"+codon+"\t-\t-\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return true;
    }

    @Override
    public boolean write(List<Variant> list) {

        for(Variant variant : list) {
            write(variant);
        }
        return true;
    }

}
