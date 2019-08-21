package com.dalcomlab.sattang.resource.war;


import com.dalcomlab.sattang.resource.Resource;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class ResourceURLStreamHandler extends URLStreamHandler {
    private Resource resource;

    public ResourceURLStreamHandler(Resource resource) {
        this.resource = resource;
    }

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        return new ResourceURLConnection(url, resource);
    }
}