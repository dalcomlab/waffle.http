/*
 * Copyright SATTANG 2019
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package com.dalcomlab.sattang.protocol.http.form;

import com.dalcomlab.sattang.protocol.http.HttpHeader;

import java.io.*;
import java.nio.file.Files;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpFormFile extends HttpFormAbstract {
    private FileOutputStream output = null;
    private File file = null;

    /**
     * Creates new HttpFormFile object.
     *
     * @param header
     */
    public HttpFormFile(HttpHeader header) {
        super(header);
    }

    /**
     * Returns an {@link java.io.OutputStream OutputStream} that can
     * be used for access the contents of the this part.
     *
     * @return
     */
    @Override
    public OutputStream getOutputStream() throws IOException {
        if (output == null) {
            file = File.createTempFile("demo", ".waffle.tmp");
            this.output = new FileOutputStream(file);
        }
        return output;
    }

    /**
     * @throws Exception
     */
    @Override
    public void writeFile(File dest) throws IOException {
        if (file != null) {
            Files.move(file.toPath(), dest.toPath());
        } else {
            throw new IOException("Cannot write a file to disk!");
        }
    }


    /**
     * @return
     */
    @Override
    public String toString() {
        return output.toString();
    }
}
