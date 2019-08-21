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
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceFileDirectory extends ResourceFile {

    /**
     * Creates a new <code>ResourceFileDirectory</code> instance with a given file handle.
     *
     * @param file a file
     */
    public ResourceFileDirectory(File file) {
        super(file);
    }

    /**
     * Checks that this resource is a file.
     *
     * @return
     */
    @Override
    public boolean isFile() {
        return false;
    }

    /**
     * Checks that this resource is a directory.
     *
     * @return
     */
    @Override
    public boolean isDirectory() {
        return true;
    }


    /**
     * Lists all child resources of this resource.
     *
     * @return
     */
    @Override
    public Resource[] list() {
        File[] files = this.file.listFiles();
        if (files == null) {
            return null;
        }

        Resource[] resources = new Resource[files.length];
        int i = 0;
        for (File file : files) {
            if (file.isFile()) {
                resources[i] = new ResourceFileFile(file);
            } else if (file.isDirectory()) {
                resources[i] = new ResourceFileDirectory(file);
            }
            i++;
        }

        return resources;
    }

    /**
     * Returns resource paths in the given paths.
     *
     * @return
     */
    @Override
    public Set<String> getResourcePaths() {
        File[] files = this.file.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }

        Set<String> paths = new HashSet<>();
        int i = 0;
        for (File file : files) {
            String name = file.getName();
            if (file.isDirectory()) {
                name += File.separator;
            }
            paths.add(name);
            i++;
        }
        return paths;
    }

    /**
     * Gets the input stream of this resource.
     *
     * @return the input stream object
     */
    @Override
    public InputStream getInputStream() {
        return null;
    }

}
