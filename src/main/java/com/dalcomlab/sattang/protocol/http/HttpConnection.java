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
package com.dalcomlab.sattang.protocol.http;

import com.dalcomlab.sattang.net.ChannelCloseException;
import com.dalcomlab.sattang.net.SocketConnection;
import com.dalcomlab.sattang.net.event.EventDispatcher;
import com.dalcomlab.sattang.net.event.SocketEvent;
import com.dalcomlab.sattang.net.event.nio.NioEventExecutor;
import com.dalcomlab.sattang.net.io.channel.nio.NioSocket;
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.net.io.read.ReadChannelListener;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.WriteChannelListener;
import com.dalcomlab.sattang.protocol.*;
import com.dalcomlab.sattang.protocol.http.decoder.HttpRequestDecoder;
import com.dalcomlab.sattang.server.HttpServer;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.List;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpConnection extends SocketConnection {
    private final HttpServer server;
    private final HttpRequestDecoder decoder = new HttpRequestDecoder();
    private final HttpRequest request;
    private final HttpResponse response;
    private final SocketChannel channel;

    /**
     * @param server
     * @param channel
     */
    public HttpConnection(HttpServer server, SocketChannel channel) {
        super(server, new NioSocket(channel));
        this.channel = channel;
        this.server = server;
        this.request = new DefaultHttpRequest(new DefaultHttpInputStream(readChannel));
        this.response = new DefaultHttpResponse(new DefaultHttpOutputStream(writeChannel));
        this.request.setResponse(this.response);
        this.response.setRequest(this.request);
    }

    /**
     * Performs the handshake operation.
     *
     * @return <code>true</code> if the handshake is ok, otherwise returns
     * <code>false</code>.
     */
    public boolean handshake() {
        return true;
    }

    /**
     * Starts the connection.
     *
     * @throws Exception
     */
    @Override
    public void start() throws Exception {
        if (state.get() == State.STARTING || state.get() == State.STARTED) {
            return;
        }
        state.set(State.STARTING);

        readChannel.listen(new ReadChannelListener() {
            @Override
            public void onStart(ReadChannel channel) {
                channel.addFilter(RequestFilterBuilder.build(request));
            }
        });

        writeChannel.listen(new WriteChannelListener() {
            @Override
            public void onStart(WriteChannel channel) {
                channel.addFilter(ResponseFilterBuilder.build(response));
            }
        });

        decodeRequestAsync();

        state.set(State.STARTED);
    }

    /**
     *
     */
    private void decodeRequestAsync() {
        decoder.listen(request);
        transport.async(new NioEventExecutor(channel, SocketEvent.READ) {
            @Override
            public void execute(SocketChannel channel, EventDispatcher dispatcher) {
                try {
                    if (!decode(channel)) {
                        dispatcher.register(this);
                    } else {
                        // TODO: 비동기 파싱 때문에 반드시 호출해야 하지만 꼭 이렇게 해야 하나 고민이 필요.
                        decoder.close();
                        handle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Closes the this connection.
     */
    @Override
    public void close() {
        super.close();
    }

    /**
     *
     */
    @Override
    public void reuse() {
        readChannel.reuse();
        writeChannel.reuse();
        decoder.reset();
        request.reuse();
        response.reuse();
    }

    /**
     *
     */
    private boolean decode(SocketChannel channel) throws Exception {
        ByteBuffer buffer = readChannel.getReadBuffer();
        buffer.clear();

        int consume = channel.read(buffer);
        if (consume == -1) {
            throw new ChannelCloseException("The channel is closed.");
        }
        buffer.flip();
        return decoder.decode(buffer);
    }

    /**
     *
     */
    private void handle() {
        HttpHandler handler = server.getHandler(getContextPath());
        if (handler == null) {
            handler = server.getHandler("/");
        }

        if (handler == null) {
            close();
            return;
        }

        this.readChannel.start();

        List<HttpFilter> filters = server.getFilters();
        for (HttpFilter filter : filters) {
            // filter.filter(SocketChannelConnection, request);
        }

        boolean isKeepAlive = request.getHeader(HttpHeader.CONNECTION).equalsIgnoreCase("keep-alive");
        if(isKeepAlive) {
            response.addHeader(HttpHeader.CONNECTION, "keep-alive");
            response.addHeader(HttpHeader.KEEP_ALIVE, "timeout=5, max=1000");
        } else {
            response.addHeader(HttpHeader.CONNECTION, "close");
        }

        response.addHeader(HttpHeader.CACHE_CONTROL, "no-cache");
        handler.handle(request, response);

        isKeepAlive = false;
        if (isKeepAlive) {
            try {
                state.set(State.STOPPED);
                reuse();
                start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            close();
        }
    }

    /**
     * @return
     */
    public String getContextPath() {
        String contextPath = request.getUri();
        if (contextPath == "" || contextPath == "/") {
            return "/";
        }

        int slash = contextPath.indexOf("/", 1);

        if (slash != -1) {
            contextPath = contextPath.substring(0, slash);
        }

        return contextPath;
    }
}
