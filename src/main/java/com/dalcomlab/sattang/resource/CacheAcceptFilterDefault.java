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

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class CacheAcceptFilterDefault implements CacheAcceptFilter {

    /**
     *
     * @param path
     * @param resource
     * @return
     */
    @Override
    public boolean accept(String path, Resource resource) {
        if (path == null || path.length() == 0) {
            return false;
        }

        if (resource == null) {
            return false;
        }

        if (resource.isDirectory()) {
            return false;
        }

        int dot = path.lastIndexOf(".");

        if (dot == -1) {
            return true;
        }

        String extension = path.substring(dot + 1).toLowerCase();
        if (extension.equals("jar") ||
                extension.endsWith("class") ||
                extension.endsWith("zip") ||
                extension.equals("eof") ||
                extension.equals("ttf") ||
                extension.equals("otf") ||
                extension.equals("woff") ||
                extension.equals("woff2")) {
            return false;
        }

        return true;
    }
}
