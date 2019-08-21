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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResourceCache implements Resource {

    private String path;
    private Resource resource;
    private long cachedContentLength;
    private byte[] cachedContent;
    private long cachedLastModified;
    private long lastAccessTime;
    private long createTime;

    /**
     *
     */
    public ResourceCache(String path, Resource resource) {
        this.path = path;
        this.resource = resource;
        this.cachedContent = resource.getContent();
        this.cachedContentLength = resource.getContentLength();
        this.cachedLastModified = resource.getLastModified();
        this.createTime = System.currentTimeMillis();
        this.lastAccessTime = createTime;
    }

    /**
     * Gets the name of this resource.
     *
     * @return the resource name.
     */
    @Override
    public String getName() {
        return resource.getName();
    }

    /**
     * Gets the canonical path of this resource.
     *
     * @return {@link File#getCanonicalPath()}.
     */
    @Override
    public String getCanonicalPath() {
        return resource.getCanonicalPath();
    }

    /**
     * Gets the URL to access this resource or null
     *
     * @return the URL to access this resource
     */
    @Override
    public URL getURL() {
        return resource.getURL();
    }

    /**
     * Returns the resource located at the given path.
     *
     * @param path
     * @return
     */
    @Override
    public Resource getResource(String path) {
        return resource.getResource(path);
    }

    /**
     * Gets the length of this resource.
     *
     * @return {@link File#length()}.
     */
    @Override
    public long getContentLength() {
        return this.cachedContentLength;
    }

    /**
     * Gets the binary content of this resource.
     *
     * @return the cached binary content of this resource.
     */
    @Override
    public byte[] getContent() {
        return this.cachedContent;
    }

    /**
     * Gets the last modified date of this resource.
     *
     * @return {@link File#lastModified()}.
     */
    @Override
    public long getLastModified() {
        return this.cachedLastModified;
    }

    /**
     * Gets the input stream of this resource.
     *
     * @return the input stream object
     */
    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(this.cachedContent);
    }

    /**
     *
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     *
     * @return
     */
    public long getCreateTime() {
        return createTime;
    }

    /**
     *
     * @return
     */
    public long getLastAccessTime() {
        return lastAccessTime;
    }

    /**
     *
     * @return
     */
    public long getLiveTime() {
        return System.currentTimeMillis() - getCreateTime();
    }

    /**
     *
     * @return
     */
    public long touch() {
        lastAccessTime = System.currentTimeMillis();
        return lastAccessTime;
    }

}
