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
