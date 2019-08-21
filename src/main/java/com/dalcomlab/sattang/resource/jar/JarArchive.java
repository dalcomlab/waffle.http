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

import com.dalcomlab.sattang.common.LRUCache;

import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.zip.ZipEntry;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class JarArchive {

    protected LRUCache<String, JarEntry> cache = new LRUCache<>(64);

    /**
     * Returns the path name of the Jar file.
     *
     * @return the path name of the Jar file
     */
    public abstract String getName();


    /**
     * Gets the jar entry with the given name.
     *
     * @param name
     * @return
     */
    public JarEntry getEntry(String name) {

        JarEntry entry = null;
        if(name == null || name.length() == 0) {
            return entry;
        }

        // 1) find a entry from a cache.
        entry = getEntryFromCache(name);
        if (entry != null) {
            return entry;
        }

        // 2) find an entry from a jar file.
        entry = getEntryFromFile(name);

        if (entry != null) {
            putEntryToCache(name, entry);
        }

        return entry;

    }

    /**
     * Gets the jar entry with the given name from the cache.
     *
     * @param name
     * @return
     */
    protected JarEntry getEntryFromCache(String name) {
        return cache.get(name);
    }

    /**
     * Gets the jar entry with the given name from the file.
     *
     * @param name
     * @return
     */
    protected abstract JarEntry getEntryFromFile(String name);


    /**
     *
     * @param name
     * @param entry
     */
    public void putEntryToCache(String name, JarEntry entry) {
        cache.put(name, entry);
    }

    /**
     * Returns an enumeration of the jar file keeps.
     *
     * @return enumeration of the jar file keeps
     */
    public abstract Enumeration<JarEntry> entries();

    /**
     * Returns an input stream for reading the contents of the specified
     * zip file entry.
     *
     * @param entry the zip file entry
     * @return an input stream for reading the contents of the specified
     * zip file entry
     */
    public abstract InputStream getInputStream(ZipEntry entry);


    /**
     * Returns an input stream for reading the contents of the specified
     * entry name.
     *
     * @param name the entry name
     * @return an input stream for reading the contents of the specified
     * entry name
     */
    public InputStream getInputStream(String name) {
        JarEntry entry = getEntry(name);
        if (entry != null) {
            return this.getInputStream(entry);
        }
        return null;
    }

}
