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
package com.dalcomlab.sattang.server;

import com.dalcomlab.sattang.net.Connection;
import com.dalcomlab.sattang.net.SocketTransport;
import com.dalcomlab.sattang.protocol.http.HttpConnection;
import com.dalcomlab.sattang.protocol.http.HttpFilter;
import com.dalcomlab.sattang.protocol.http.HttpHandler;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpServer extends SocketTransport implements Server {
    private Map<String, HttpHandler> mapping = new HashMap();
    private List<HttpFilter> filters = new LinkedList();
    private ServerListener listener = null;

    /**
     * @param hostname
     * @param port
     */
    public HttpServer(String hostname, int port) {
        listen(hostname, port);
    }


    /**
     * Returns the name of this transport.
     *
     * @return
     */
    @Override
    public String getName() {
        return "HTTP";
    }

    /**
     * Creates the connection related to this transport.
     *
     * @param channel
     * @param
     * @return
     */
    @Override
    protected Connection createConnection(SocketChannel channel) {
        return new HttpConnection(this, channel);
    }

    /**
     * Stars the HTTP server.
     *
     * @param options
     */
    @Override
    public void start(ServerOptions options) throws IOException {
        start();
        if (listener != null) {
            listener.onStart();
        }
    }

    /**
     * Stops the HTTP server.
     */
    @Override
    public void stop() {
        super.stop();
        if (listener != null) {
            listener.onStop();
        }
    }

    /**
     * Sets the new {@link ServerListener} instance on the server.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link ServerListener#onStart()} event.</li>
     *     <li>Calling {@link #stop} method fires the {@link ServerListener#onStop()} event.</li>
     * </ul>
     * @param listener
     */
    @Override
    public Server listen(ServerListener listener) {
        this.listener = listener;
        return this;
    }

    /**
     * @param context
     * @param handler
     * @return
     */
    public HttpServer handle(String context, HttpHandler handler) {
        if (context == null || handler == null) {
            return this;
        }

        mapping.put(context, handler);
        return this;
    }

    /**
     * @param filter
     * @return
     */
    public HttpServer filter(HttpFilter filter) {
        filters.add(filter);
        return this;
    }

    /**
     * @param context
     * @return
     */
    public HttpHandler getHandler(String context) {
        return mapping.get(context);
    }

    /**
     * @return
     */
    public List<HttpFilter> getFilters() {
        return filters;
    }
}
