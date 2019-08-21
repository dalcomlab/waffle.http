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
package com.dalcomlab.sattang.resource.stream;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ResourceStream {

    /**
     * Gets the length of this stream.
     *
     * @return {@link java.io.File#length()}.
     */
    default long getContentLength() {
        byte[] content = getContent();
        if (content == null) {
            return 0;
        }
        return content.length;
    }


    /**
     * Gets the binary content of this stream.
     *
     * @return the cached binary content of this stream.
     */
    default byte[] getContent() {
        InputStream input = this.getInputStream();
        if (input == null) {
            return null;
        }

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int read;
        byte[] data = new byte[1024];
        try {
            while (true) {
                read = input.read(data, 0, data.length);
                if (read == -1)
                    break;
                buffer.write(data, 0, read);
            }
            buffer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer.toByteArray();
    }

    /**
     * Gets the input stream of this stream.
     *
     * @return the input stream object
     */
    InputStream getInputStream();
}
