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

import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

// https://github.com/apache/tomcat/blob/trunk/java/org/apache/catalina/webresources/Cache.java

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class CacheManager {

    private final ConcurrentMap<String, ResourceCache> cache = new ConcurrentHashMap<>();
    private final CacheLRUComparator lruComparator = new CacheLRUComparator();
    protected CacheAcceptFilter filter = new CacheAcceptFilterDefault();
    protected long maxCacheSize = 1024 * 1024 * 10;
    protected long usedCacheSize = 0;

    /**
     *
     */
    public CacheManager() {
    }

    /**
     *
     */
    public CacheManager(final long maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }


    /**
     * @return
     */
    public long getAvailableCacheSize() {
        return this.maxCacheSize - this.usedCacheSize;
    }

    /**
     *
     */
    public void clear() {
        cache.clear();
    }

    /**
     * @param path
     * @return
     */
    public Resource get(String path) {
        ResourceCache resource = cache.get(path);
        if (resource != null) {
            System.out.println("* hits the resource cache : " + path);
            resource.touch();
        }
        return resource;
    }

    /**
     * @param path
     * @param resource
     */
    public void put(String path, Resource resource) {
        if (existCache(path)) {
            return;
        }

        if (!acceptCache(path, resource)) {
            return;
        }

        putCache(path, resource);
    }

    /**
     * @param path
     * @param resource
     * @return
     */
    private boolean acceptCache(String path, Resource resource) {
        return filter.accept(path, resource);
    }

    /**
     * @param path
     * @return
     */
    private boolean existCache(String path) {
        return this.cache.get(path) != null;
    }

    /**
     * @param path
     * @param resource
     */
    private void putCache(String path, Resource resource) {
        ResourceCache cached = new ResourceCache(path, resource);
        long availableCacheSize = this.getAvailableCacheSize() - cached.getContentLength();
        if (availableCacheSize <= 0) {
            System.out.println("The maximum cache size has been exceeded.");
            makeFreeSpace();
        }

        this.increaseUsedCacheSize(cached.getContentLength());
        cache.put(path, cached);

        System.out.println("* puts the resource into the cache : " + path);
    }

    /**
     * @param path
     */
    private void remove(String path) {
        ResourceCache resource = cache.remove(path);
        if (resource != null) {
            this.increaseUsedCacheSize(-1 * resource.getContentLength()); // decrease
        }
    }

    /**
     * @param allocatedSize
     */
    private void increaseUsedCacheSize(long allocatedSize) {
        this.usedCacheSize += allocatedSize;
    }

    /**
     *
     */
    private void makeFreeSpace() {
        TreeSet<ResourceCache> sortedCache = new TreeSet<>(lruComparator);

        sortedCache.addAll(cache.values());

        long targetAvailableSize = maxCacheSize / 3;
        for (ResourceCache resource : sortedCache) {
            if (getAvailableCacheSize() > targetAvailableSize) {
                break;
            }
            this.remove(resource.getPath());
        }

    }

    /**
     *
     */
    private static class CacheLRUComparator implements Comparator<ResourceCache> {
        @Override
        public int compare(ResourceCache cache1, ResourceCache cache2) {
            return (int) (cache2.getLastAccessTime() - cache1.getLastAccessTime());
        }
    }
}
