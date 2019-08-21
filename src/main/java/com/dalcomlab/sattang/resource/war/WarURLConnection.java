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
package com.dalcomlab.sattang.resource.war;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class WarURLConnection extends URLConnection {

    private URLConnection connection;
    protected WarURLConnection(URL url) {
        super(url);
    }

    @Override
    public void connect() throws IOException {
        this.connection.connect();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (this.url != null) {
            System.out.println(this.url.getFile());
        }
        return null;
    }
}
