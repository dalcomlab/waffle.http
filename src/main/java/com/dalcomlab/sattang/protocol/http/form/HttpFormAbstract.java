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

import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class HttpFormAbstract implements HttpForm {
    protected final HttpHeader header;

    /**
     * @param header
     */
    public HttpFormAbstract(HttpHeader header) {
        this.header = header;
    }

    /**
     * Returns the content type in the header.
     * <pre>
     *     Content-Type: text/plain
     * </pre>
     *
     * @return
     */
    @Override
    public String getContentType() {
        return header.getContentType();
    }

    /**
     * Returns the content disposition in the header.
     *
     * @return
     */
    @Override
    public String getContentDisposition() {
        return header.getContentDisposition();
    }

    /**
     * Returns the name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; name="file"
     * </pre>
     *
     * @return
     */
    @Override
    public String getName() {
        return header.getAttribute(HttpHeader.CONTENT_DISPOSITION, "name");
    }

    /**
     * Returns the file name in the content-disposition header.
     * <pre>
     *     Content-Disposition: form-data; filename="test.txt"
     * </pre>
     *
     * @return
     */
    @Override
    public String getFileName() {
        return header.getAttribute(HttpHeader.CONTENT_DISPOSITION, "filename");
    }

    /**
     * Returns the names of the headers of this multipart.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this multipart.
     */
    @Override
    public Set<String> getHeaderNames() {
        return header.getHeaderNames();
    }

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    @Override
    public String getHeader(String name) {
        return header.getHeader(name);
    }
}
