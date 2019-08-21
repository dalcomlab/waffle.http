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

import java.util.HashMap;
import java.util.Map;
import java.util.jar.JarEntry;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class JarMap {
    public Map<String, JarMap> map = null;
    public JarEntry entry;

    /**
     *
     */
    public JarMap() {

    }

    /**
     *
     * @param name
     * @return
     */
    public JarMap get(String name) {
        if (map == null) {
            return null;
        }
        return map.get(name);
    }

    /**
     *
     * @param name
     * @param node
     * @return
     */
    public JarMap put(String name, JarMap node) {
        if (map == null) {
            map = new HashMap<>();
        }
        map.put(name, node);
        return node;
    }

    /**
     *
     * @param entry
     */
    public void setEntry(JarEntry entry) {
        this.entry = entry;
    }

    /**
     *
     * @return
     */
    public JarEntry getEntry() {
        return this.entry;
    }

    public void print() {
        print(this, 0);
    }

    public void print(JarMap node, int level) {
        if (node == null) {
            return;
        }

        if (node.map == null) {
            return;
        }
        for( String key : node.map.keySet() ){
            space(level);
            System.out.print(key);
            if(node.entry != null) {
                System.out.print(" *");
            }
            System.out.println("");
            JarMap child = node.map.get(key);
            if (child != null) {
                print(child, level + 1);
            }
        }
    }

    public void space(int count) {
        count = count * 2;
        while(count  > 0) {
            System.out.print(" ");
            count--;
        }
    }
}
