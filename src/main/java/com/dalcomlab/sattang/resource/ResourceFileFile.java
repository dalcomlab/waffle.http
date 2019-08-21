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
package com.dalcomlab.sattang.resource;

import com.dalcomlab.sattang.resource.jar.JarArchiveFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceFileFile extends ResourceFile {

    /**
     * Creates a new <code>ResourceFileFile</code> instance with a given file handle.
     *
     * @param file a file
     */
    public ResourceFileFile(File file) {
        super(file);
    }

    /**
     * Checks that this resource is a file.
     *
     * @return
     */
    @Override
    public boolean isFile() {
        return true;
    }

    /**
     * Checks that this resource is a directory.
     *
     * @return
     */
    @Override
    public boolean isDirectory() {
        return false;
    }

    /**
     * Gets the input stream of this resource.
     *
     * @return the input stream object
     */
    @Override
    public InputStream getInputStream() {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }

    /**
     * Creates new resource
     * TODO: change the method name.... properly because this name doesn't express its behavior.
     *
     * @return
     */
    @Override
    public Resource create() {
        return new ResourceJarDirectory(new JarArchiveFile(getCanonicalPath()));
    }
}
