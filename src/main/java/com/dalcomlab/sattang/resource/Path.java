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

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class Path {

    private String path;
    private String query;
    private File file;

    /**
     * @param path
     */
    public Path(String path) {
       // this.path = Path.normalize(path);
        int i = path.indexOf('?');
        if (i > 0) {
            this.path = path.substring(0, i);
            this.query = path.substring(i + 1);
        } else {
            this.path = path;
        }
    }

    /**
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     * @return
     */
    public boolean isDirectory() {
        if (path == null || path.length() == 0) {
            return false;
        }

        if (this.file == null) {
            this.file = new File(this.path);
        }
        if (!file.exists()) {
            return false;
        }

        return file.isDirectory();
    }

    /**
     * @return
     */
    public boolean isFile() {
        if (path == null || path.length() == 0) {
            return false;
        }

        if (this.file == null) {
            this.file = new File(this.path);
        }

        if (!file.exists()) {
            return false;
        }

        return file.isFile();
    }

    /**
     * @return
     */
    public String getFileName() {
        if (path == null || path.length() == 0) {
            return "";
        }

        int i = path.lastIndexOf(File.separatorChar);
        if (i != -1)
            return path.substring(i + 1);
        return path;
    }


    /**
     * @return
     */
    public String getFileNameWithoutExtension() {
        String name = getFileName();
        int i = name.lastIndexOf(".");
        if (i != -1)
            return name.substring(0, i);
        return name;
    }

    /**
     * @param path
     * @return
     */
    public static String normalize(String path) {
        if (path == null || path.length() == 0) {
            return "";
        }
        ;
        //
        path = path.trim().replace('/', File.separatorChar).replace('\\', File.separatorChar);

        //
        // fix a malformed URL(e.g URLs of the form jar:file: ... .war!/ ... .jar)
        //
        String[] component = path.split("!");
        path = component[0];
        for (int i = 1; i < component.length; i++) {
            if (component[i].length() == 0) {
                continue;
            }

            path += "!";
            if (component[i].charAt(0) != '/') {
                path += "/";
            }
            path += component[i];
        }

        //
        // remove a empty path(e.g /User///Library//servlet.jar)
        //
        component = path.split("/");
        path = component[0];
        for (int i = 1; i < component.length; i++) {
            if (component[i].length() == 0) {
                continue;
            }
            path += "/" + component[i];
        }

        //
        // fixed a malformed path
        //
        //if (path.endsWith("/")) {
        //    path = path.substring(0, path.length() - 1);
       // }

        return path;
    }
}
