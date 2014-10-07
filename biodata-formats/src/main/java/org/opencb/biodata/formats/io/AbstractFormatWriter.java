package org.opencb.biodata.formats.io;

import java.io.File;
import java.io.IOException;
import java.util.List;

//TODO: Esta clase quizas sea innecesaria, pues podemos usar el TextFileWriter y el toString del formato
// 		que queramos guardar en fichero
public abstract class AbstractFormatWriter<T> {

    protected File file;

    protected AbstractFormatWriter(File f) throws IOException {
        // TODO: ¿chequear el archivo? Si no existe dará una excepcion, y quizas queremos
        // 		 crearlo en lugar de que falle
        //FileUtils.checkFile(f);
        this.file = f;
    }

    public abstract void write(T object) throws IOException;

    public abstract void writeAll(List<T> list) throws IOException;

    public abstract void close() throws IOException;
}
