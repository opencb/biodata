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


import org.opencb.biodata.formats.variant.io.VariantReader;
import org.opencb.biodata.formats.variant.io.VariantWriter;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.zip.GZIPOutputStream;
import org.opencb.biodata.models.variant.Variant;

/**
 * Created with IntelliJ IDEA.
 * User: aleman
 * Date: 9/15/13
 * Time: 3:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class VariantVcfGzipDataWriter implements VariantWriter {

    private VariantReader reader;
    private BufferedWriter printer;
    private String filename;


    public VariantVcfGzipDataWriter(VariantReader reader, String filename) {
        this.filename = filename;
        this.reader = reader;
    }

    @Override
    public boolean open() {
        try {
            printer = new BufferedWriter(new OutputStreamWriter(new GZIPOutputStream(new FileOutputStream(this.filename))));
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public boolean close() {
        try {
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean pre() {
        try {
            printer.append(reader.getHeader()).append("\n");
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean post() {
        return true;
    }

    @Override
    public boolean write(Variant elem) {
        try {
            printer.append(elem.toString()).append("\n");
        } catch (IOException e) {
            return false;
        }
        
        return true;
    }

    @Override
    public boolean write(List<Variant> batch) {
        try {
            for (Variant record : batch) {
                printer.append(record.toString()).append("\n");
            }
        } catch (IOException e) {
            return false;
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