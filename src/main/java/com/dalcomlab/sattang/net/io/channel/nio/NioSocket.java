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
package com.dalcomlab.sattang.net.io.channel.nio;

import com.dalcomlab.sattang.concurrent.CompletionHandler;
import com.dalcomlab.sattang.net.ChannelCloseException;
import com.dalcomlab.sattang.net.io.channel.AbstractSocket;
import com.dalcomlab.sattang.net.io.channel.SocketOptions;

import java.io.IOException;
import java.net.StandardSocketOptions;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class NioSocket implements AbstractSocket {
    private final SocketChannel channel;

    /**
     * @param channel
     */
    public NioSocket(SocketChannel channel) {
        this.channel = channel;
    }

    /**
     * Configs the channel.
     *
     * @param options
     */
    @Override
    public void config(SocketOptions options) {
        try {
            if (options.getBroadcast().get() != null) {
                channel.setOption(StandardSocketOptions.SO_BROADCAST, options.getBroadcast().get());
            }

            if (options.getSolLinger().get() != null) {
                channel.setOption(StandardSocketOptions.SO_LINGER, options.getSolLinger().get());
            }

            if (options.getReceiveBufferSize().get() != null) {
                channel.setOption(StandardSocketOptions.SO_RCVBUF, options.getReceiveBufferSize().get());
            }

            if (options.getSendBufferSize().get() != null) {
                channel.setOption(StandardSocketOptions.SO_SNDBUF, options.getSendBufferSize().get());
            }

            if (options.getKeepAlive().get() != null) {
                channel.setOption(StandardSocketOptions.SO_KEEPALIVE, options.getKeepAlive().get());
            }

            if (options.getTcpNodelay().get() != null) {
                channel.setOption(StandardSocketOptions.TCP_NODELAY, options.getTcpNodelay().get());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return
     */
    public boolean isConnected() {
        return channel.isConnected();
    }

    /**
     * Write some data to this write channel in async mode.
     *
     * @param buffer
     * @param handler
     * @return
     */
    @Override
    public int write(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) throws IOException {
        return channel.write(buffer);
    }

    /**
     * Write some data to this write channel in blocking mode.
     *
     * @param buffer
     * @return
     */
    @Override
    public int writeBlocking(ByteBuffer buffer) throws IOException {
        int consume = buffer.remaining();

        while (buffer.hasRemaining()) {
            if (channel.write(buffer) == -1) {
                throw new ChannelCloseException("");
            }
        }

        return consume;
    }

    /**
     * Reads some data from this read channel in async mode.
     *
     * @param buffer
     * @param handler
     * @return
     */
    @Override
    public int read(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) throws IOException {
        return channel.read(buffer);
    }

    /**
     * Reads some data from this read channel in blocking mode.
     *
     * @param buffer
     * @return
     */
    @Override
    public int readBlocking(ByteBuffer buffer) throws IOException {
        int consume = buffer.remaining();

        while (buffer.hasRemaining()) {
            if (channel.read(buffer) == -1) {
                throw new ChannelCloseException("");
            }
        }

        return consume;
    }

    /**
     * Closes the socket and clears all resources.
     *
     * @throws IOException
     */
    @Override
    public void close() throws IOException {
       this.channel.close();
    }
}
