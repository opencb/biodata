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

package org.opencb.biodata.formats.io;

import org.apache.commons.lang3.StringUtils;
import org.opencb.commons.utils.FileUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class BeanReader<T> {

    private Path path;
    private Class<T> clazz;
    private String separator;

    private BufferedReader bufferedReader;
    @SuppressWarnings("rawtypes")
    private Class[] argsClass;
    @SuppressWarnings("rawtypes")
    private Constructor constructor;
    private String commentLines;

    private static final String COMMENT_CHARACTER = "#";

//    public BeanReader(String filename, Class<T> c) throws IOException, SecurityException, NoSuchMethodException {
//        this(new File(filename), c);
//    }

    public BeanReader(Path path, Class<T> c) throws IOException, SecurityException, NoSuchMethodException {
        this(path, c, "\t");
    }

    public BeanReader(Path path, Class<T> clazz, String separator) throws IOException, SecurityException {
        this.path = path;
        this.clazz = clazz;
        this.separator = separator;
        this.commentLines = "";

        init();
    }

    @SuppressWarnings("rawtypes")
    private void init() throws IOException, SecurityException {
        FileUtils.checkFile(path);
        String firstLine = getFirstLineUncommented();
        if (StringUtils.isNotEmpty(firstLine)) {
            Constructor[] constructors = clazz.getConstructors();
            if (constructors.length == 1) {
                constructor = constructors[0];
            } else {
                boolean hasPrimitive;
                for (Constructor constructor : constructors) {
                    if (firstLine.split(separator, -1).length == constructor.getParameterTypes().length) {
                        hasPrimitive = false;
                        for (Class c : constructor.getParameterTypes()) {
                            if (c.isPrimitive()) {
                                hasPrimitive = true;
                                break;
                            }
                        }
                        if (!hasPrimitive) {
                            this.constructor = constructor;
                            break;
                        }
                    }
                }
            }

            // Init argClass array
            if (constructor != null) {
                argsClass = constructor.getParameterTypes();
            }

            // FileUtils method already checks if file ends with .gz
            bufferedReader = FileUtils.newBufferedReader(path, Charset.defaultCharset());
        } else {
            new IOException("Empty path provided");
        }
    }


    public T read() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        String line = "";
        while ((line = bufferedReader.readLine()) != null && (line.trim().equals("") || line.startsWith(COMMENT_CHARACTER))) {
            ;
        }
        return stringLineToObject(line);
    }

    public T read(String pattern) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        return read(Pattern.compile(pattern));
    }

    public List<T> read(int number) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<>(number);
        T record;
        int cont = 0;
        // read() method already avoids empty and commentLines
        while ((record = read()) != null && cont < number) {
            records.add(record);
            cont++;
        }
        return records;
    }

    public List<T> readAll() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<>();
        T record;
        while ((record = read()) != null) {
            records.add(record);
        }
        return records;
    }

    public List<T> readAll(String pattern) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<>();
        T record;
        Pattern pat = Pattern.compile(pattern);
        while ((record = read(pat)) != null) {
            records.add(record);
        }
        return records;
    }

    public List<T> readAllForced() throws IOException {
        List<T> records = new ArrayList<>();
        String line;
        T t;
        int cont = 0;
        while ((line = bufferedReader.readLine()) != null) {
            cont++;
            if (!line.startsWith(COMMENT_CHARACTER) && !line.trim().isEmpty()) {
                try {
                    t = stringLineToObject(line);
                    if (t != null) {
                        records.add(t);
                    }
                } catch (Exception e) {
                    System.out.println("Error in line: " + cont + "   Error message: " + e.toString());
                }
            }
        }
        return records;
    }

    public void close() throws IOException {
        bufferedReader.close();
    }


    @SuppressWarnings("unchecked")
    private T stringLineToObject(String line) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (constructor == null) {
            return null;
        }
        if (line != null) {
            String[] fields = line.split(separator, -1);
            Object[] obj = new Object[fields.length];
            if (argsClass == null) {
                argsClass = constructor.getParameterTypes();
            }
            try {
                for (int i = 0; i < fields.length; i++) {
                    obj[i] = argsClass[i].getConstructor(String.class).newInstance(fields[i]);
                }
                return (T) constructor.newInstance(obj);
            } catch (SecurityException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFirstLineUncommented() throws IOException {
        String line = "";
        StringBuilder commentLineBuilder = new StringBuilder();
        BufferedReader reader = FileUtils.newBufferedReader(path, Charset.defaultCharset());
        while ((line = reader.readLine()) != null && line.startsWith(COMMENT_CHARACTER)) {
            commentLineBuilder.append(line).append("\n");
        }
        reader.close();
        commentLines = commentLineBuilder.toString();
        return line;
    }

    private T read(Pattern pat) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        String line = "";
        while ((line = bufferedReader.readLine()) != null && (line.trim().equals("") || line.startsWith(COMMENT_CHARACTER) || !pat.matcher(line).matches())) {
            ;
        }
        return stringLineToObject(line);
    }

    /**
     * @return the constructor
     */
    @SuppressWarnings("rawtypes")
    public Constructor getConstructor() {
        return constructor;
    }

    /**
     * @param constructor the constructor to set
     */
    @SuppressWarnings("rawtypes")
    public BeanReader<T> setConstructor(Constructor constructor) {
        this.constructor = constructor;
        if (constructor != null) {
            argsClass = constructor.getParameterTypes();
        }
        return this;
    }

    public Path getPath() {
        return path;
    }

    public BeanReader<T> setPath(Path path) {
        this.path = path;
        return this;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public BeanReader<T> setClazz(Class<T> clazz) {
        this.clazz = clazz;
        return this;
    }

    public String getSeparator() {
        return separator;
    }

    public BeanReader<T> setSeparator(String separator) {
        this.separator = separator;
        return this;
    }

    public String getCommentLines() {
        return commentLines;
    }

    public BeanReader<T> setCommentLines(String commentLines) {
        this.commentLines = commentLines;
        return this;
    }
}
