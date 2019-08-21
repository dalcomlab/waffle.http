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
package com.dalcomlab.sattang.resource.jar;

import java.io.File;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class JarArchiveFile extends JarArchive {

    private JarFile file;

    /**
     * Creates new instance.
     *
     * @param jarPath
     */
    public JarArchiveFile(String jarPath) {
        try {
            this.file = new JarFile(jarPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates new instance.
     *
     * @param file
     */
    public JarArchiveFile(JarFile file) {
        this.file = file;
    }

    /**
     * Returns the path name of the Jar file.
     *
     * @return the path name of the Jar file
     */
    @Override
    public String getName() {
        if (file == null) {
            return null;
        }
        return file.getName();
    }

    /**
     * Gets the jar entry with the given name.
     *
     * @param name
     * @return
     */
    @Override
    protected JarEntry getEntryFromFile(String name) {
        Enumeration<JarEntry> entries = file.entries();
        if (entries == null) {
            return null;
        }

        //
        // the JarEntry doesn't start with a slash, so remove leading '/' char
        // from the given name.
        //
        if(name.startsWith(File.separator)) {
            name = name.substring(1);
        }

        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(name)) {
                return entry;
            }
        }
        return null;
    }

    /**
     * Returns an enumeration of the jar file keeps.
     *
     * @return enumeration of the jar file keeps
     */
    @Override
    public Enumeration<JarEntry> entries() {
        if (file == null) {
            return null;
        }

        return file.entries();
    }

    /**
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     *
     * @param entry the zip file entry
     * @return an input stream for reading the contents of the specified
     * zip file entry
     */
    @Override
    public InputStream getInputStream(ZipEntry entry) {
        if (file == null) {
            return null;
        }

        InputStream input = null;
        try {
            input = file.getInputStream(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return input;
    }

}
