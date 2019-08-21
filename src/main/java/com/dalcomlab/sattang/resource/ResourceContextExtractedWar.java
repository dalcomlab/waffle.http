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

import java.io.*;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceContextExtractedWar {

    File rootFile = null;
    String tempPath = null;

    /**
     * The Constructor of a ResourceContextExtractedWar class
     *
     * @param rootPath
     */
    public ResourceContextExtractedWar(String rootPath) {

    }

    /**
     * This method bindings a new filesystem that is rooted at the given path object.
     *
     * @param rootPath
     * @return <code>true</code> if this method is success to mount; <code>false</code> otherwise
     */
    protected boolean mountImplement(Path rootPath) {
        if(!rootPath.isFile()) {
            return false;
        }

        String path = extractWar(rootPath);
        if (path != null && path.length() != 0) {

        }
        return true;
    }


    /**
     * @return
     */
    public boolean unmount() {
        deleteFile(rootFile);
        rootFile = null;
        return true;
    }

    public void deleteFile(File file) {
        if (file == null)
            return;
        if (file.isFile()) {
            file.delete();
        } else {
            File[] list = file.listFiles();
            for (File f : list) {
                deleteFile(f);
            }
            file.delete();
        }
    }

    /**
     *
     * @param path
     * @return
     */
    protected String extractWar(Path path) {
        String extractPath = System.getProperty("java.io.tmpdir") + "/waffle/";

        try (JarFile file = new JarFile(path.getPath())) {
            extractPath += path.getFileNameWithoutExtension();
            rootFile = new File(extractPath);
            rootFile.mkdir();

            //
            // The temp folder is inconsistent in a mac. The path could be different with the real path in a mac.
            // /var/folders/ --> /private/var/folders
            //
            extractPath = rootFile.getCanonicalPath();
            Enumeration<JarEntry> entries = file.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String name = entry.getName();
                File expandedFile = new File(rootFile, name);
                int last = name.lastIndexOf('/');
                if (last >= 0) {
                    File parent = new File(rootFile, name.substring(0, last));
                    if (!parent.mkdirs() && !parent.isDirectory()) {
                    }
                }

                if (name.endsWith("/")) {
                    continue;
                }

                InputStream input = file.getInputStream(entry);
                if (input != null) {
                    extract(input, expandedFile);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return extractPath;
    }

    /**
     *
     * @param input
     * @param file
     */
    private void extract(InputStream input, File file) {
        try (BufferedOutputStream output = new BufferedOutputStream(new FileOutputStream(file))) {
            byte buffer[] = new byte[2048];
            while (true) {
                int n = input.read(buffer);
                if (n <= 0)
                    break;
                output.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
