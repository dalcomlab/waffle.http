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

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpFormData extends HttpFormAbstract {
    /**
     * Creates new HttpFormData object.
     *
     * @param header
     */
    public HttpFormData(HttpHeader header) {
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
        return null;
    }

    /**
     * @throws Exception
     */
    @Override
    public void writeFile(File dest) throws IOException {
    }

}
