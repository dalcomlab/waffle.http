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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Resource {

    /**
     * Gets the name of this resource.
     *
     * @return the resource name.
     */
    String getName();


    /**
     * Gets the canonical path of this resource.
     *
     * @return {@link java.io.File#getCanonicalPath()}.
     */
    String getCanonicalPath();

    /**
     * Gets the URL to access this resource or null
     *
     * @return the URL to access this resource
     */
    URL getURL();

    /**
     *  Determines whether the resource is a plain file.
     *
     * @return
     */
    default boolean isFile() {
        return false;
    }

    /**
     * Determines whether the resource is a directory.
     *
     * @return
     */
    default boolean isDirectory() {
        return false;
    }

    /**
     * Gets the resource with the given name.
     *
     * @param name
     * @return
     */
    default Resource get(String name) {
        Resource[] resources = this.list();
        if (resources == null) {
            return null;
        }
        for (Resource resource : resources) {
            if (resource.getName().equals(name)) {
                return resource;
            }
        }

        return null;
    }

    /**
     * Lists all child resources of this resource.
     *
     * @return
     */
    default Resource[] list() {
        return null;
    }


    /**
     * Lists all child resources of this resource.
     *
     * @return
     */
    default Resource[] list(String path) {
        if (isFile()) {
            return null;
        }

        Resource resource = getResource(path);
        if (resource == null) {
            return null;
        }
        return resource.list();
    }


    /**
     * Returns resource paths in the given paths.
     *
     * @return
     */
    default Set<String> getResourcePaths() {
        return Collections.emptySet();
    }

    /**
     * Returns the resource located at the given path.
     *
     * @param path
     * @return
     */
    Resource getResource(String path);

    /**
     * Gets the length of this resource.
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
     * Gets the binary content of this resource.
     *
     * @return the cached binary content of this resource.
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
     * Gets the last modified date of this resource.
     *
     * @return {@link java.io.File#lastModified()}.
     */
    long getLastModified();

    /**
     * Gets the input stream of this resource.
     *
     * @return the input stream object
     */
    InputStream getInputStream();


    /**
     * Creates new resource
     *
     * @return
     */
    default Resource create() {
        return null;
    }

    /**
     * Visits to all child resources in this resource.
     *
     * @param visitor
     */
    default void accept(ResourceVisitor visitor) {
        if (visitor == null) {
            return;
        }

        Resource[] resources = this.list();

        if (resources == null) {
            return;
        }

        for (Resource resource : resources) {
            if (resource.isFile()) {
                visitor.visitFile(resource);
            } else {
                if (visitor.visitDirectory(resource)) {
                    resource.accept(visitor);
                }
            }
            if (!visitor.moreVisit()) {
                break;
            }
        }
    }

}
