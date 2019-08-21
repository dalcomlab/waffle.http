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

import java.io.*;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface HttpForm {

    /**
     * Returns the content type in the header.
     * <pre>
     *     Content-Type: text/plain
     * </pre>
     *
     * @return
     */
    String getContentType();

    /**
     * Returns the content disposition in the header.
     *
     * @return
     */
    String getContentDisposition();

    /**
     * Returns the name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; name="file"
     * </pre>
     *
     * @return
     */
    String getName();

    /**
     * Returns the file name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; filename="test.txt"
     * </pre>
     *
     * @return
     */
    String getFileName();

    /**
     * Returns the names of the headers of this multipart.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this multipart.
     */
    Set<String> getHeaderNames();

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    String getHeader(String name);

    /**
     * Returns an {@link java.io.OutputStream OutputStream} that can
     * be used for access the contents of the this part.
     *
     * @return
     */
    OutputStream getOutputStream() throws IOException;

    /**
     * @param dest
     * @throws Exception
     */
    default void writeFile(String dest) throws IOException {
        writeFile(new File(dest));
    }

    /**
     * @throws Exception
     */
    void writeFile(File dest) throws IOException;
}
