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

package com.dalcomlab.sattang.common;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.stream.Collectors;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class Paths {

    /**
     *
     */
    private Paths() {
    }

    /**
     * Gets the file name from the given full path.
     *
     * @param path
     * @return
     */
    public final static String getFileName(String path) {
        int last = Math.max(path.lastIndexOf('/'), path.lastIndexOf('\\'));
        return path.substring(last + 1);
    }

    /**
     * Normalize the given path.
     *
     * @param path
     * @return
     */
    public final static String normalize(String path) {
        if (path == null) {
            return "";
        }

        path = path.trim().replace('\\', '/');
        Deque<String> queue = new ArrayDeque<>();
        String[] parts = path.split("/"); //
        queue.add(parts[0]);
        for (int i = 1; i < parts.length; i++) {
            // remove empty path(e.g /User///Library//servlet.jar).
            if (parts[i].length() == 0) {
                continue;
            }

            // remove single dot(e.g /User/./Library./servlet.jar).
            if (parts[i].equals(".")) {
                continue;
            }

            // apply double dot.
            if (parts[i].equals("..")) {
                if (!queue.isEmpty()) {
                    queue.removeLast();
                }
                continue;
            }

            queue.add(parts[i]);
        }

        String normalized = queue.stream().map(Object::toString).collect(Collectors.joining("/"));
        if (path.endsWith("/")) {
            normalized += "/";
        }
        return normalized;
    }
}
