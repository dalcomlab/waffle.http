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

import java.io.File;
import java.net.URL;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class ResourceFile implements Resource {

    protected File file;

    /**
     * Creates a new <code>ResourceFile</code> instance with a given file handle.
     *
     * @param file a file
     */
    protected ResourceFile(File file) {
        this.file = file;
    }

    /**
     * Creates a new <code>ResourceFile</code> instance with a given path.
     *
     * @param path a path
     */
    public static Resource create(String path) {
        File file;
        try {
            file = new File(path);
        } catch (Exception e) {
            return null;
        }
        if (file.isFile()) {
            return new ResourceFileFile(file);
        }
        return new ResourceFileDirectory(file);
    }

    /**
     * Returns the name of this file.
     *
     * @return the FileName.
     */
    @Override
    public String getName() {
        return file.getName();
    }


    /**
     * Return the canonical path of this file.
     *
     * @return {@link File#getCanonicalPath()}.
     */
    @Override
    public String getCanonicalPath() {
        String path = "";
        try {
            path = file.getCanonicalPath();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return path;
    }

    /**
     * Gets the URL to access this resource or null
     *
     * @return the URL to access this resource
     */
    public URL getURL() {
        if (file == null) {
            return null;
        }
        URL url = null;
        try {
            url = file.toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * Return the last modified date of this file
     *
     * @return {@link File#lastModified()}.
     */
    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    /**
     * Returns the resource located at the given path.
     *
     * @param path
     * @return
     */
    @Override
    public Resource getResource(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }

        if (this.isFile()) {
            return null;
        }

        Resource resource = this;
        String[] names = path.split("/");
        for (String name : names) {
            if (name.length() == 0) {
                continue;
            }

            resource = resource.get(name);
            if (resource == null) {
                return null;
            }
        }

        return resource;
    }

    /**
     * Gets the length of this resource.
     *
     * @return {@link File#length()}.
     */
    public long getContentLength() {
        if (file == null) {
            return 0;
        }
        return file.length();
    }

}