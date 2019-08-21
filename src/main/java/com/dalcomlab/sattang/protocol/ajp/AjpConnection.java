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
package com.dalcomlab.sattang.protocol.ajp;

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
import com.dalcomlab.sattang.protocol.ajp.decoder.AjpRequestDecoder;
import com.dalcomlab.sattang.protocol.ajp.filters.RequestBodyFilter;
import com.dalcomlab.sattang.protocol.ajp.filters.ResponseBodyFilter;
import com.dalcomlab.sattang.protocol.ajp.filters.ResponseHeaderFilter;
import com.dalcomlab.sattang.protocol.http.HttpHandler;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.server.AjpServer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpConnection extends SocketConnection {
    private AjpRequestDecoder decoder = new AjpRequestDecoder();
    private AjpServer server;
    private HttpRequest request;
    private HttpResponse response;
    private SocketChannel channel;

    /**
     * @param server
     * @param channel
     */
    public AjpConnection(AjpServer server, SocketChannel channel) {
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
     * Starts the transport.
     *
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        if (state.get() == State.STARTING || state.get() == State.STARTED) {
            return;
        }
        state.set(State.STARTING);

        readChannel.listen(new ReadChannelListener() {
            @Override
            public void onStart(ReadChannel channel) {
                channel.addFilter(new RequestBodyFilter(request.getContentLength()));
            }
        });

        writeChannel.listen(new WriteChannelListener() {
            @Override
            public void onStart(WriteChannel channel) {
                response.removeHeader(HttpHeader.CONNECTION);
                response.removeHeader(HttpHeader.TRANSFER_ENCODING);
                channel.addFilter(new ResponseBodyFilter());
                channel.addFilter(new ResponseHeaderFilter(response));
            }
        });

        decodeRequestAsync();

        state.set(State.STARTED);
    }

    /**
     *
     */
    private void decodeRequestAsync() {

        decoder.listen(new AjpRequestDecoder.Listener() {
            @Override
            public void setMethod(String method) {
                request.setMethod(method);
            }

            @Override
            public void setUri(String uri) {
                request.setUri(uri);
            }

            @Override
            public void setProtocol(String protocol) {
                request.setProtocol(protocol);
            }

            @Override
            public void setScheme(String scheme) {
                request.setScheme(scheme);
            }

            @Override
            public void addHeader(String name, String value) {
                request.addHeader(name, value);
            }

            @Override
            public void addParameter(String name, String value) {
                request.addParameter(name, value);
            }
        });

        transport.async(new NioEventExecutor(channel, SocketEvent.READ) {
            @Override
            public void execute(SocketChannel channel, EventDispatcher dispatcher) {
                try {
                    if (!decode(channel)) {
                        dispatcher.register(this);
                    } else {
                        handle();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     *
     */
    @Override
    public void reuse() {
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
            throw new ChannelCloseException("The channel is closed");
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
            return;
        }

        this.readChannel.start();

        handler.handle(request, response);

        close();
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
