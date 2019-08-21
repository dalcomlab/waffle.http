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

import com.dalcomlab.sattang.common.Paths;
import com.dalcomlab.sattang.resource.jar.JarArchive;
import com.dalcomlab.sattang.resource.jar.JarArchiveFile;
import com.dalcomlab.sattang.resource.jar.JarArchiveInputStream;
import com.dalcomlab.sattang.resource.war.ResourceURLStreamHandler;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class ResourceJar implements Resource {

    protected JarArchive jar = null;
    protected JarEntry entry = null;

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar file.
     *
     * @param file a Jar file
     */
    public ResourceJar(JarFile file) {
        this(new JarArchiveFile(file));
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar file.
     *
     * @param file  a Jar file
     * @param entry
     */
    public ResourceJar(JarFile file, JarEntry entry) {
        this(new JarArchiveFile(file), entry);
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar file.
     *
     * @param jar a Jar file
     */
    public ResourceJar(JarArchive jar) {
        this.jar = jar;
        this.entry = null;
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar file.
     *
     * @param jar   a Jar file
     * @param entry
     */
    public ResourceJar(JarArchive jar, JarEntry entry) {
        if (jar == null || entry == null) {
            return;
        }
        this.jar = jar;
        this.entry = entry;
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar input stream.
     *
     * @param input a Jar input stream
     */
    public ResourceJar(InputStream input) {
        if (input == null) {
            return;
        }
        this.jar = new JarArchiveInputStream(input);
        this.entry = null;
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given jar input stream.
     *
     * @param input a Jar input stream
     * @param entry
     */
    public ResourceJar(InputStream input, JarEntry entry) {
        if (input == null || entry == null) {
            return;
        }
        this.jar = new JarArchiveInputStream(input);
        this.entry = entry;
    }


    /**
     * Creates a new <code>ResourceJar</code> instance with a given path.
     *
     * @param filePath  the file path of the jar to be opened for reading
     * @param entryPath
     * @return
     */
    public static Resource create(String filePath, String entryPath) {
        JarFile file;
        try {
            file = new JarFile(filePath);
        } catch (Exception e) {
            return null;
        }

        if (entryPath == null || entryPath.length() == 0 || entryPath.equals("/")) {
            return new ResourceJarDirectory(file);
        }

        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(entryPath)) {
                if (entry.isDirectory()) {
                    return new ResourceJarDirectory(file, entry);
                } else {
                    return new ResourceJarFile(file, entry);
                }
            }
        }
        return null;
    }

    /**
     * Creates a new <code>ResourceJar</code> instance with a given path.
     *
     * @param input     the file path of the jar to be opened for reading
     * @param entryPath
     * @return
     */
    public static Resource create(InputStream input, String entryPath) {
        JarInputStream file;
        try {
            file = new JarInputStream(input);
        } catch (Exception e) {
            return null;
        }

        if (entryPath == null || entryPath.length() == 0 || entryPath.equals("/")) {
            return new ResourceJarDirectory(input);
        }
        return null;
    }


    /**
     * Returns the name of this file.
     *
     * @return the FileName.
     */
    @Override
    public String getName() {
        if (entry != null) {
            String name = entry.getName();
            if (isFile()) {
                return Paths.getFileName(name);
            } else {
                // the name of directory is ending with a slash('/'), and remove the slash.
                name = name.substring(0, name.length() - 1);
                return Paths.getFileName(name);
            }
        }
        return "";
    }

    /**
     * Gets the canonical path of this file.
     *
     * @return {@link File#getCanonicalPath()}.
     */
    @Override
    public String getCanonicalPath() {
        String canonicalPath = "jar:file:";
        canonicalPath += jar.getName();
        canonicalPath += "!/";
        if (entry != null) {
            canonicalPath += entry.getName();
        }
        return canonicalPath;
    }

    /**
     * Gets the URL to access this resource or null
     *
     * @return the URL to access this resource
     */
    public URL getURL() {
        if (jar == null) {
            return null;
        }

        URL url = null;
        try {
            String path = getCanonicalPath();
            //url = new URL(path);
            url = new URL("war", "", 0, "", new ResourceURLStreamHandler(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
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
            input = jar.getInputStream(entry);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return input;
    }


    /**
     * Gets the last modified date of this file
     *
     * @return {@link File#lastModified()}.
     */
    @Override
    public long getLastModified() {
        if (entry != null) {
            return entry.getLastModifiedTime().toMillis();
        }
        return 0;
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

        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            if (entry.getName().equals(path)) {
                if (entry.isDirectory()) {
                    return new ResourceJarDirectory(jar, entry);
                } else {
                    return new ResourceJarFile(jar, entry);
                }
            }
        }
        return null;
    }
}