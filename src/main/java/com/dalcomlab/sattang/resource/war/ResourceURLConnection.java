package com.dalcomlab.sattang.resource.war;


import com.dalcomlab.sattang.resource.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class ResourceURLConnection extends URLConnection {

    private Resource resource;

    public ResourceURLConnection(URL url, Resource resource) {
        super(url);
        this.resource = resource;
    }
    @Override
    public void connect() throws IOException {
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.resource != null) {
            return this.resource.getInputStream();
        }
        return null;
    }
}