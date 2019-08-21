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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * LRU (Least Recently Used) cache
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class LRUCache<T, U> extends LinkedHashMap<T, U> {

    private final int cacheSize;


    /**
     * Creates a new instance with a given cache size.
     *
     * @param cacheSize
     */
    public LRUCache(int cacheSize) {
        this.cacheSize = cacheSize;
    }

    /**
     * @param eldest
     * @return
     */
    @Override
    protected boolean removeEldestEntry(Map.Entry<T, U> eldest) {
        return size() > cacheSize;
    }

}