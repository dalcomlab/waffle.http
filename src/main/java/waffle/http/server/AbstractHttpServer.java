/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server;

import java.io.IOException;
import java.net.InetSocketAddress;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AbstractHttpServer implements HttpServer {

    protected InetSocketAddress inetSocketAddress;

    /**
     * Create a new server and immediately binds it.
     *
     * @param address the address to bind on
     */
    public AbstractHttpServer(InetSocketAddress address) {
        this.inetSocketAddress = address;
    }

    /**
     * @throws IOException
     */
    public void init() throws IOException {

    }

    /**
     * @throws IOException
     */
    public void start() throws IOException {

    }

    /**
     * @throws IOException
     */
    public void stop() throws IOException {

    }

    /**
     *
     */
    public void run() {
        try {
            init();
            start();
        } catch (IOException ex) {
            ex.printStackTrace();
            try {
                stop();
            } catch (IOException e) {
                throw new RuntimeException(ex);
            }

            throw new RuntimeException(ex);
        }
    }
}