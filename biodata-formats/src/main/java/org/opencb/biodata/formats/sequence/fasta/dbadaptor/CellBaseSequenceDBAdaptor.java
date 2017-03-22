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

package org.opencb.biodata.formats.sequence.fasta.dbadaptor;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.opencb.biodata.models.core.Region;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;
import java.util.Properties;

/**
 * Created by jacobo on 14/08/14.
 */
public class CellBaseSequenceDBAdaptor extends SequenceDBAdaptor {

    private static final String DEFAULT_CELLBASEHOST = "http://ws.bioinfo.cipf.es/cellbase/rest/latest";
    private static final String DEFAULT_SPECIES = "hsa";

    private long queryTime = 0;
    private final String cellbaseHost;
    private String species;

    private ObjectMapper mapper;
    private JsonFactory factory;

    public CellBaseSequenceDBAdaptor() {
        cellbaseHost = DEFAULT_CELLBASEHOST;
        species = DEFAULT_SPECIES;
    }
    public CellBaseSequenceDBAdaptor(Path credentialsPath) {
        super(credentialsPath);
        Properties properties = new Properties();
        try {
            properties.load(new InputStreamReader(new FileInputStream(credentialsPath.toString())));
        } catch (IOException e) {
            e.printStackTrace();
        }
        cellbaseHost = properties.getProperty("cellbasehost", DEFAULT_CELLBASEHOST);
        species = properties.getProperty("species", DEFAULT_SPECIES);
    }

    @Override
    public String getSequence(Region region) throws IOException {
        return getSequence(region, species);
    }
    @Override
    public String getSequence(Region region, String species) throws IOException {
        long start = System.currentTimeMillis();

        String urlString = cellbaseHost + "/" + species + "/genomic/region/" + region.toString() + "/sequence?of=json";

        URL url = new URL(urlString);
        InputStream is = url.openConnection().getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));


        JsonParser jp = factory.createParser(br);
        JsonNode o = mapper.readTree(jp);

        JsonNode seqNode = o.get(0);
        if(seqNode == null){
            System.out.println("Error in "+this.getClass().getName()+".getSequence Region = " + region + " : " + o.get("error").asText());
            throw new IOException(this.getClass().getName()+".getSequence Region = " + region + o.get("error").asText());
        }
        String sequence = o.get(0).get("sequence").asText();
        br.close();


        if(sequence.length() == 0 && region.getEnd()-region.getStart() > 0) { //FIXME JJ: Recursive call to solve one undocumented feature of cellbase (AKA: bug)
            return getSequence(new Region(region.getChromosome(), region.getStart(), region.getEnd() - (region.getEnd()-region.getStart())* 9 / 10), species);
        }

        long end = System.currentTimeMillis();
        queryTime += end-start;
        return sequence;
    }

    public long getQueryTime(){
        return queryTime;
    }
    public void resetQueryTime(){
        queryTime = 0;
    }

    @Override
    public void close() throws IOException {
    }

    @Override
    public void open() throws IOException {
        //TODO: Check service connection
        mapper = new ObjectMapper();
        factory = mapper.getFactory();
    }
}
