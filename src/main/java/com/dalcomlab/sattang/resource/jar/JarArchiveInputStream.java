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


import com.dalcomlab.sattang.common.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.zip.ZipEntry;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class JarArchiveInputStream extends JarArchive {

    private byte[] bytes;

    /**
     * Creates new instance.
     *
     * @param input
     */
    public JarArchiveInputStream(InputStream input) {
        try {
            bytes = IOUtils.toByteArray(input);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the path name of the Jar file.
     *
     * @return the path name of the Jar file
     */
    @Override
    public String getName() {
        return null;
    }


    /**
     * Gets the jar entry with the given name.
     *
     * @param name
     * @return
     */
    @Override
    protected JarEntry getEntryFromFile(String name) {
        try (JarInputStream input = new JarInputStream(new ByteArrayInputStream(this.bytes))) {
            JarEntry entry = input.getNextJarEntry();
            while (entry != null) {
                if (entry.getName().equals(name)) {
                    return entry;
                }
                entry = input.getNextJarEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
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

        class JarEntryEnumerator implements Enumeration<JarEntry> {
            private JarInputStream jar;
            private JarEntry entry;

            JarEntryEnumerator(byte[] bytes) {
                try {
                    jar = new JarInputStream(new ByteArrayInputStream(bytes));
                    entry = jar.getNextJarEntry();
                } catch (Exception e) {
                }
            }

            @Override
            public boolean hasMoreElements() {
                if (this.jar == null || this.entry == null) {
                    return false;
                }
                return true;
            }

            @Override
            public JarEntry nextElement() {
                if (this.jar == null) {
                    return null;
                }
                JarEntry next = entry;
                try {
                    entry = jar.getNextJarEntry();
                } catch (Exception e) {

                }
                return next;
            }

        }
        return new JarEntryEnumerator(this.bytes);
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
        if (this.bytes == null) {
            return null;
        }

        try (JarInputStream input = new JarInputStream(new ByteArrayInputStream(this.bytes))) {

            JarEntry next = input.getNextJarEntry();
            while (next != null) {
                if (next.getName().equals(entry.getName())) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    IOUtils.copy(input, out, next.getSize());
                    return new ByteArrayInputStream(out.toByteArray());
                }
                next = input.getNextJarEntry();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
