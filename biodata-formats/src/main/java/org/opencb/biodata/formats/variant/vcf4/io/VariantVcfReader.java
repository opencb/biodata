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
import com.google.common.base.Splitter;

import htsjdk.variant.variantcontext.LazyGenotypesContext;
import htsjdk.variant.variantcontext.VariantContext;
import htsjdk.variant.vcf.VCFFileReader;
import htsjdk.variant.vcf.VCFHeader;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.vcf4.*;
import org.opencb.biodata.models.variant.VariantVcfFactory;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFactory;
import org.opencb.biodata.models.variant.VariantSource;
import org.opencb.biodata.models.variant.exceptions.NotAVariantException;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;

/**
 * @author Alejandro Aleman Ramos &lt;aaleman@cipf.es&gt;
 * @author Cristina Yenyxe Gonzalez Garcia &lt;cyenyxe@ebi.ac.uk&gt;
 */
public class VariantVcfReader implements VariantReader {

    private Vcf4 vcf4;
    private BufferedReader reader;
    private Path path;

    private String filePath;

    private VariantSource source;
    private VariantFactory factory;
    private VcfHeaderFactory heaederFactory = new VcfHeaderFactory();
    private String header;

    public VariantVcfReader(VariantSource source, String filePath) {
        this(source, filePath, new VariantVcfFactory());
    }

    public VariantVcfReader(VariantSource source, String filePath, VariantFactory factory) {
        this.source = source;
        this.filePath = filePath;
        this.factory = factory;
    }

    @Override
    public boolean open() {
        try {
            path = Paths.get(filePath);
            Files.exists(path);

            vcf4 = new Vcf4();
            if (path.toFile().getName().endsWith(".gz")) {
                this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
            } else {
                this.reader = Files.newBufferedReader(path, Charset.defaultCharset());
            }

        }
        catch (IOException  ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }


        return true;
    }

    @Override
    public boolean pre() {
        try {
            processHeader();

            // Copy all the read metadata to the VariantSource object
            // TODO May it be that Vcf4 wasn't necessary anymore?

            // This Vcf4 object is not necessary anymore. Do not include it's information.
            // The header parser contains bugs and misses information.
            // Use htsjdk parser instead

//            source.addMetadata("fileformat", vcf4.getFileFormat());
//            source.addMetadata("INFO", vcf4.getInfo().values());
//            source.addMetadata("FILTER", vcf4.getFilter().values());
//            source.addMetadata("FORMAT", vcf4.getFormat().values());
//            for (Map.Entry<String, String> otherMeta : vcf4.getMetaInformation().entrySet()) {
//                source.addMetadata(otherMeta.getKey(), otherMeta.getValue());
//            }
            source.setSamples(vcf4.getSampleNames());
        } catch (IOException | FileFormatException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        try {
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public List<Variant> read() {
        String line;
        try {
            while ((line = reader.readLine()) != null && (line.trim().equals("") || line.startsWith("#"))) ;

            Boolean isReference=true;
            List<Variant> variants = null;
            // Look for a non reference position (alternative != '.')
            while (line != null && isReference) {
                try {
                    variants = factory.create(source, line);
                    isReference = false;
                } catch (NotAVariantException e) {  // This line represents a reference position (alternative = '.')
                    line = reader.readLine();
                }
            }
            return variants;

        } catch (IOException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public List<Variant> read(int batchSize) {
        List<Variant> listRecords = new ArrayList<>(batchSize);

        int i = 0;
        List<Variant> variants;
        while ((i < batchSize) && (variants = this.read()) != null) {
            listRecords.addAll(variants);
            i += variants.size();
        }

        return listRecords;
    }

    @Override
    public List<String> getSampleNames() {
        return this.vcf4.getSampleNames();
    }

    @Override
    public String getHeader() {
        return header;
    }

    private void processHeader() throws IOException, FileFormatException {
        BufferedReader localBufferedReader;

        String contentProbe = Files.probeContentType(path);
        if ((null != contentProbe && contentProbe.contains("gzip")) || path.toString().endsWith(".gz")) {
            localBufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
        } else {
            localBufferedReader = new BufferedReader(new FileReader(path.toFile()));
        }

        StringBuilder buffer = new StringBuilder();
        String line;
        while ((line = localBufferedReader.readLine()) != null && line.startsWith("#")) {
            buffer.append(line).append('\n');
        }

        localBufferedReader.close();

        header = buffer.toString();
        vcf4 = heaederFactory.parseHeader(new BufferedReader(new StringReader(header)));

        if (vcf4 == null) {
            System.err.println("VCF Header must be provided.");
//            System.exit(-1);
        }

    }
}
