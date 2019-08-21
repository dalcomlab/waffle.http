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


import java.io.InputStream;
import java.util.jar.JarEntry;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceStreamJarEntry implements ResourceStream {

    protected JarEntry entry;

    /**
     *
     * @param entry
     */
    public ResourceStreamJarEntry(final JarEntry entry) {
        this.entry = entry;
    }

    /**
     * Gets the length of this resource.
     *
     * @return {@link java.io.File#length()}.
     */
    @Override
    public long getContentLength() {
        byte[] content = getContent();
        if (content == null) {
            return 0;
        }
        return content.length;
    }


    /**
     * Gets the binary content of this resource.
     *
     * @return the cached binary content of this resource.
     */
    @Override
    public byte[] getContent() {
        return null;
    }

    /**
     * Gets the input stream of this stream.
     *
     * @return the input stream object
     */
    @Override
    public InputStream getInputStream() {
        return null;
    }
}
