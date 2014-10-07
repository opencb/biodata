package org.opencb.biodata.formats.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class BeanReader<T> {

    private BufferedReader bufferedReader;
    @SuppressWarnings("rawtypes")
    private Class[] argsClass;
    @SuppressWarnings("rawtypes")
    private Constructor constructor;

    private String separator;
    private String comment;

    private String commentLines;

//    public BeanReader(String filename, Class<T> c) throws IOException, SecurityException, NoSuchMethodException {
//        this(new File(filename), c);
//    }

    public BeanReader(Path path, Class<T> c) throws IOException, SecurityException, NoSuchMethodException {
        this(path, c, "\t");
    }

    public BeanReader(Path path, Class<T> c, String separator) throws IOException, SecurityException, NoSuchMethodException {
        Files.exists(path);
        this.separator = separator;
        this.comment = "#";
        this.commentLines = "";
        createConstructor(path, c);
    }

    @SuppressWarnings("rawtypes")
    private void createConstructor(Path path, Class beanClass) throws IOException, SecurityException, NoSuchMethodException {
        String firstLine = getFirstLineUncommented(path);
        if (firstLine != null) {
            if (beanClass.getConstructors().length == 1) {
                constructor = beanClass.getConstructors()[0];
            } else {
                Constructor[] constructors = beanClass.getConstructors();
                boolean hasPrimitive;
                for (int i = 0; i < constructors.length; i++) {
                    if (firstLine.split(separator, -1).length == constructors[i].getParameterTypes().length) {
                        hasPrimitive = false;
                        for (Class c : constructors[i].getParameterTypes()) {
                            if (c.isPrimitive()) {
                                hasPrimitive = true;
                                break;
                            }
                        }
                        if (!hasPrimitive) {
                            constructor = constructors[i];
                            break;
                        }
                    }
                }
            }
            if (constructor != null) {
                argsClass = constructor.getParameterTypes();
            }
            bufferedReader = Files.newBufferedReader(path, Charset.defaultCharset());
        } else {
            new IOException("Empty path provided");
        }
    }


    public T read() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        String line = "";
        while ((line = bufferedReader.readLine()) != null && (line.trim().equals("") || line.startsWith(comment))) {
            ;
        }
        return stringLineToObject(line);
    }

    public T read(String pattern) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        return read(Pattern.compile(pattern));
    }

    public List<T> read(int number) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<T>(number);
        T t = null;
        int cont = 0;
        // read() method already avoids empty and commentLines
        while ((t = read()) != null && cont < number) {
            records.add(t);
            cont++;
        }
        return records;
    }

    public List<T> readAll() throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<T>();
        T t = null;
        while ((t = read()) != null) {
            records.add(t);
        }
        return records;
    }

    public List<T> readAll(String pattern) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        List<T> records = new ArrayList<T>();
        T t = null;
        Pattern pat = Pattern.compile(pattern);
        while ((t = read(pat)) != null) {
            records.add(t);
        }
        return records;
    }

    public List<T> readAllForced() throws IOException {
        List<T> records = new ArrayList<T>();
        String line = null;
        T t = null;
        int cont = 0;
        while ((line = bufferedReader.readLine()) != null) {
            cont++;
            if (!line.startsWith(comment) && !line.trim().equals("")) {
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
    private T stringLineToObject(String line) throws IllegalArgumentException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
        if (constructor == null && argsClass == null) {
            return null;
        }
        if (line != null) {
            String[] fields = line.split(separator, -1);
            Object[] obj = new Object[fields.length];
            if (constructor != null && argsClass == null) {
                argsClass = constructor.getParameterTypes();
            }
            try {
                for (int i = 0; i < fields.length; i++) {
                    obj[i] = argsClass[i].getConstructor(String.class).newInstance(fields[i]);
                }
                //	return (T) constructor.newInstance((Object[])line.split(separator, -1));
                return (T) constructor.newInstance(obj);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private String getFirstLineUncommented(Path path) throws IOException {
        String line = "";
        StringBuilder commentLineBuilder = new StringBuilder();
        BufferedReader reader = Files.newBufferedReader(path, Charset.defaultCharset());
        while ((line = reader.readLine()) != null && line.startsWith(comment)) {
            commentLineBuilder.append(line).append("\n");
        }
        reader.close();
        commentLines = commentLineBuilder.toString();
        return line;
    }

    private T read(Pattern pat) throws IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException, IOException {
        String line = "";
        while ((line = bufferedReader.readLine()) != null && (line.trim().equals("") || line.startsWith(comment) || !pat.matcher(line).matches())) {
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
    public void setConstructor(Constructor constructor) {
        this.constructor = constructor;
        if (constructor != null) {
            argsClass = constructor.getParameterTypes();
        }
    }


    /**
     * @param separator the separator to set
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * @return the separator
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param commentLines the commentLines to set
     */
    public void setCommentLines(String commentLines) {
        this.commentLines = commentLines;
    }

    /**
     * @return the commentLines
     */
    public String getCommentLines() {
        return commentLines;
    }

}
