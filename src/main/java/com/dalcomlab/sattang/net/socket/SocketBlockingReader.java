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
package com.dalcomlab.sattang.net.socket;

import com.dalcomlab.sattang.concurrent.CompletionHandler;
import com.dalcomlab.sattang.common.Timeout;
import com.dalcomlab.sattang.net.ChannelCloseException;

import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class SocketBlockingReader implements SocketReader {
    private SocketChannel channel;
    private SocketSelectorPool pool;

    /**
     * @param channel
     * @param pool
     */
    public SocketBlockingReader(SocketChannel channel, SocketSelectorPool pool) {
        this.channel = channel;
        this.pool = pool;
    }

    /**
     * Reads some data to the socket in blocking mode.
     * <p>
     * Reading may be blocking or asynchronous depending on the implementation.
     * When the read is complete, {@link CompletionHandler#completed} method be
     * called. If an error occurs in processing, call {@link CompletionHandler#failed}
     * method be called.
     *
     * @param buffer
     * @param handler
     */
    @Override
    public int read(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) {
        if (handler == null) {
            handler = CompletionHandler.INSTANCE;
        }
        final int total = buffer.remaining();
        final Timeout timeout = new Timeout(3000);
        final Selector selector = pool.poll();
        try {
            while (buffer.hasRemaining()) {
                long consume = channel.read(buffer);
                if (consume == -1) {
                    throw new ChannelCloseException("");
                }

                if (consume > 0) {
                    timeout.reset();
                } else {
                    selector.selectedKeys().clear();
                    channel.register(selector, SelectionKey.OP_READ);

                    if (selector.select(timeout.duration()) == 0) {
                        // The elapse() method may throw the TimeoutException.
                        timeout.elapse();
                    }
                }
            }
        } catch (Exception e) {
            handler.failed(e);
            return -1;
        } finally {
            pool.offer(selector);
        }

        handler.completed(buffer);
        return total;
    }
}
