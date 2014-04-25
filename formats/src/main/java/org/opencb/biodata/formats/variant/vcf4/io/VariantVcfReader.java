package org.opencb.biodata.formats.variant.vcf4.io;

import org.opencb.biodata.formats.variant.io.VariantReader;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.GZIPInputStream;
import org.opencb.biodata.formats.io.FileFormatException;
import org.opencb.biodata.formats.variant.vcf4.Vcf4;
import org.opencb.biodata.formats.variant.vcf4.VcfAlternateHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfFilterHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfFormatHeader;
import org.opencb.biodata.formats.variant.vcf4.VcfInfoHeader;
import org.opencb.biodata.models.variant.Variant;
import org.opencb.biodata.models.variant.VariantFactory;

/**
 * @author Alejandro Aleman Ramos <aaleman@cipf.es>
 * @author Cristina Yenyxe Gonzalez Garcia <cyenyxe@ebi.ac.uk>
 */
public class VariantVcfReader implements VariantReader {

    private Vcf4 vcf4;
    private BufferedReader reader;
    private Path path;
    
    private String filename;
    private String fileId;
    private String studyId;

    public VariantVcfReader(String fileName, String fileId, String studyId) {
        this.filename = fileName;
        this.fileId = fileId;
        this.studyId = studyId;
    }

    @Override
    public boolean open() {
        try {
            this.path = Paths.get(this.filename);
            Files.exists(this.path);

            vcf4 = new Vcf4();
            if (path.toFile().getName().endsWith(".gz")) {
                this.reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
            } else {
                this.reader = Files.newBufferedReader(path, Charset.defaultCharset());
            }

        } catch (IOException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

        return true;
    }

    @Override
    public boolean pre() {

        try {
            processHeader();
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
    public Variant read() {
        String line;
        try {
            while ((line = reader.readLine()) != null && (line.trim().equals("") || line.startsWith("#")));
            
            if (line != null) {
                String[] fields = line.split("\t");
                Variant variant;

                if (fields.length >= 8) {
                    // TODO Must return List<Variant> !!
//                    variant = VariantFactory.createVariantFromVcf(vcf4.getSampleNames(), fields);
                    variant = VariantFactory.createVariantFromVcf(filename, fileId, studyId, vcf4.getSampleNames(), fields).get(0);
                } else {
                    throw new IOException("Not enough fields in line (min. 8): " + line);
                }

                return variant;
            }
        } catch (IOException ex) {
            Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
        }

        return null;
    }

    @Override
    public List<Variant> read(int batchSize) {
        List<Variant> listRecords = new ArrayList<>(batchSize);
        String line;
        
        for (int i = 0; i < batchSize; i++) {
            try {
                while ((line = reader.readLine()) != null && (line.trim().equals("") || line.startsWith("#")));

                if (line == null) { // Nothing found
                    continue;
                }
                
                String[] fields = line.split("\t");
                if (fields.length >= 8) {
                    List<Variant> variants = VariantFactory.createVariantFromVcf(filename, fileId, studyId, vcf4.getSampleNames(), fields);
                    assert (variants.size() > 0);
                    listRecords.addAll(variants);
                } else {
                    throw new IOException("Not enough fields in line (min. 8): " + line);
                }
            } catch (IOException ex) {
                Logger.getLogger(VariantVcfReader.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        return listRecords;
    }

    @Override
    public List<String> getSampleNames() {
        return this.vcf4.getSampleNames();
    }

    @Override
    public String getHeader() {
        StringBuilder header = new StringBuilder();
        header.append("##fileformat=").append(vcf4.getFileFormat()).append("\n");

        Iterator<String> iter = vcf4.getMetaInformation().keySet().iterator();
        String headerKey;
        while (iter.hasNext()) {
            headerKey = iter.next();
            header.append("##").append(headerKey).append("=").append(vcf4.getMetaInformation().get(headerKey)).append("\n");
        }

        for (VcfAlternateHeader vcfAlternate : vcf4.getAlternate().values()) {
            header.append(vcfAlternate.toString()).append("\n");
        }

        for (VcfFilterHeader vcfFilter : vcf4.getFilter().values()) {
            header.append(vcfFilter.toString()).append("\n");
        }

        for (VcfInfoHeader vcfInfo : vcf4.getInfo().values()) {
            header.append(vcfInfo.toString()).append("\n");
        }

        for (VcfFormatHeader vcfFormat : vcf4.getFormat().values()) {
            header.append(vcfFormat.toString()).append("\n");
        }

        header.append("#").append(Joiner.on("\t").join(vcf4.getHeaderLine())).append("\n");

        return header.toString();
    }

    private void processHeader() throws IOException, FileFormatException {
        VcfInfoHeader vcfInfo;
        VcfFilterHeader vcfFilter;
        VcfFormatHeader vcfFormat;
        List<String> headerLine;
        String line;
        String[] fields;

        BufferedReader localBufferedReader;

        if (Files.probeContentType(path).contains("gzip")) {
            localBufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(path.toFile()))));
        } else {
            localBufferedReader = new BufferedReader(new FileReader(path.toFile()));
        }

        boolean header = false;
        while ((line = localBufferedReader.readLine()) != null && line.startsWith("#")) {

            if (line.startsWith("##fileformat")) {
                if (line.split("=").length > 1) {

                    vcf4.setFileFormat(line.split("=")[1].trim());
                } else {
                    throw new FileFormatException("");
                }
            } else if (line.startsWith("##INFO")) {

                vcfInfo = new VcfInfoHeader(line);
                vcf4.getInfo().put(vcfInfo.getId(), vcfInfo);
            } else if (line.startsWith("##FILTER")) {

                vcfFilter = new VcfFilterHeader(line);
                vcf4.getFilter().put(vcfFilter.getId(), vcfFilter);
            } else if (line.startsWith("##FORMAT")) {

                vcfFormat = new VcfFormatHeader(line);
                vcf4.getFormat().put(vcfFormat.getId(), vcfFormat);
            } else if (line.startsWith("#CHROM")) {
//                headerLine = StringUtils.toList(line.replace("#", ""), "\t");
                headerLine = Splitter.on("\t").splitToList(line.replace("#", ""));
                vcf4.setHeaderLine(headerLine);
                header |= true;
            } else {
                fields = line.replace("#", "").split("=", 2);
                vcf4.getMetaInformation().put(fields[0], fields[1]);
            }
        }
        if (!header) {
            System.err.println("VCF Header must be provided.");
            System.exit(-1);
        }
        localBufferedReader.close();
    }

}
