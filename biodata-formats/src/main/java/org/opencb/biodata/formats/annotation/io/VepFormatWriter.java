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

package org.opencb.biodata.formats.annotation.io;

import org.opencb.biodata.models.variant.annotation.ConsequenceType;
import org.opencb.biodata.models.variant.annotation.VariantAnnotation;
import org.opencb.commons.io.DataWriter;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by fjlopez on 12/02/15.
 */
public class VepFormatWriter implements DataWriter<VariantAnnotation> {

    String filename;
    BufferedWriter bw;

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
    public boolean write(VariantAnnotation variantAnnotation) {

        String id;
        if((id=variantAnnotation.getId())==null) {
            id = "-";
        }
        String alt;
        String pos;
        // Short deletion
        if(variantAnnotation.getAlternateAllele().equals("-")) {
            alt = "-";
            if(variantAnnotation.getReferenceAllele().length()>1) {
                pos = variantAnnotation.getStart() + "-" + (variantAnnotation.getStart() + variantAnnotation.getReferenceAllele().length() - 1);
            } else {
                pos = Integer.toString(variantAnnotation.getStart());
            }
        } else if(variantAnnotation.getReferenceAllele().equals("-")) {
            // Short insertion
            alt = variantAnnotation.getAlternateAllele();
            pos = (variantAnnotation.getStart()-1) + "-" + variantAnnotation.getStart();
            // SNV
        } else {
            alt = variantAnnotation.getAlternateAllele();
            pos = Integer.toString(variantAnnotation.getStart()-1);
        }

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
            String consequences = consequenceType.getSoTerms().get(0).getSoName();
            for(int i=1; i<consequenceType.getSoTerms().size(); i++) {
                consequences += ","+consequenceType.getSoTerms().get(i).getSoName();
            }
            Integer cdnaPosition;
            String cdnaPositionString;
            if((cdnaPosition=consequenceType.getcDnaPosition())==null) {
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
            if((aaPosition=consequenceType.getAaPosition())==null) {
                aaPositionString = "-";
            } else {
                aaPositionString = aaPosition.toString();
            }
            String aaChange;
            if((aaChange=consequenceType.getAaChange())==null) {
                aaChange = "-";
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
    public boolean write(List<VariantAnnotation> list) {

        for(VariantAnnotation variantAnnotation : list) {
            write(variantAnnotation);
        }
        return true;
    }
}
