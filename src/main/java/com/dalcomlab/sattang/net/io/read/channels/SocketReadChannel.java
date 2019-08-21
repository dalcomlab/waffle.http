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
package com.dalcomlab.sattang.net.io.read.channels;

import com.dalcomlab.sattang.concurrent.CompletionHandler;
import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.Connection;
import com.dalcomlab.sattang.net.io.ChannelState;
import com.dalcomlab.sattang.net.io.channel.AbstractSocket;
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.net.io.read.ReadChannelListener;
import com.dalcomlab.sattang.net.io.read.ReadFilter;
import com.dalcomlab.sattang.net.io.read.filters.ReadBufferFilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class SocketReadChannel implements ReadChannel {
    private final AbstractSocket socket;
    private final Deque<ReadFilter> filters = new ConcurrentLinkedDeque();
    private final ReadFilter readFilter;
    private final ReadBufferFilter bufferFilter;
    private ReadChannelListener listener = null;
    private ByteBuffer buffer;
    private ChannelState state = ChannelState.INIT;

    /**
     * @param socket
     */
    public SocketReadChannel(AbstractSocket socket) {
        this.socket = socket;
        this.readFilter = new ReadFilter() {
            @Override
            public ReadFilter next() {
                return null;
            }

            @Override
            public ReadFilter next(ReadFilter next) {
                return null;
            }

            public int read(ReadChannel channel, ByteBuffer dst) throws IOException {
                return socket.readBlocking(dst);
            }
        };

        this.buffer = ByteBuffer.allocate(1024 * 8);
        this.bufferFilter = new ReadBufferFilter(this.buffer);
        bufferFilter.next(readFilter);
    }

    /**
     * Returns the {@link ByteBuffer} for this read channel.
     *
     * @return
     */
    @Override
    public ByteBuffer getReadBuffer() {
        return buffer;
    }

    /**
     * @return
     */
    @Override
    public Deque<ReadFilter> getFilters() {
        return filters;
    }

    /**
     * Returns the {@link Connection} that this outbound channel belongs to.
     *
     * @return
     */
    @Override
    public Connection getConnection() {
        return null;
    }

    /**
     * Reads some data from this read channel in async mode.
     * <p>
     * The read channel internally includes several {@link ReadFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * outbound channel will calling the first filter's {@link ReadFilter#read}
     * method.
     *
     * @param buffer
     * @param useFilter
     * @param handler
     * @return
     */
    @Override
    public int read(ByteBuffer buffer, boolean useFilter, CompletionHandler<ByteBuffer> handler) {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        int consume = 0;
        try {
            if (useFilter) {
                ReadFilter last = getLastFilter();
                if (last != null) {
                    last.next(bufferFilter);
                }

                ReadFilter filter = getFirstFilter();
                if (filter != null) {
                    consume = filter.read(this, buffer);
                }
            } else {
                readFilter.read(this, buffer);
            }
        } catch (IOException e) {
            consume = ChannelConstants.END_OF_CHANNEL;
            error(e);
        }
        return consume;
    }

    /**
     * Reads some data from this read channel in blocking mode.
     * <p>
     * The read channel internally includes several {@link ReadFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * outbound channel will calling the first filter's {@link ReadFilter#read}
     * method.
     *
     * @param buffer
     * @return
     */
    @Override
    public int readBlocking(ByteBuffer buffer, boolean useFilter) {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        int consume = 0;
        try {
            if (useFilter) {
                ReadFilter last = getLastFilter();
                if (last != null) {
                    last.next(bufferFilter);
                }

                ReadFilter filter = getFirstFilter();
                if (filter != null) {
                    consume = filter.read(this, buffer);
                }
            } else {
                readFilter.read(this, buffer);
            }
        } catch (IOException e) {
            consume = ChannelConstants.END_OF_CHANNEL;
            error(e);
        }
        return consume;
    }

    /**
     * This method calls the {@link ReadChannelListener#onStart} method.
     *
     * <pre>
     *     readChannel.listen(new ReadChannelListener() {
     *         public void onStart(ReadChannel channel) {
     *             channel.addFilter(new ReadFilterXXX());
     *             channel.addFilter(new ReadFilterYYY());
     *             channel.addFilter(new ReadFilterZZZ());
     *         }
     *     }
     * </pre>
     */
    @Override
    public void start() {
        if (state.isStart() || state.isClose()) {
            return;
        }

        state = ChannelState.START;

        if (listener != null) {
            listener.onStart(this);
        }
    }

    /**
     * This method calls the {@link ReadChannelListener#onEnd} method.
     */
    @Override
    public void end() {
        if (state.isEnd() || state.isClose()) {
            return;
        }

        state = ChannelState.END;

        if (listener != null) {
            listener.onEnd(this);
        }
    }

    /**
     * This method calls the {@link ReadChannelListener#onError} method.
     */
    @Override
    public void error(Throwable t) {
        System.out.println(t.toString());

        if (listener != null) {
            listener.onError(this, t);
        }

        close();
    }


    /**
     * Closes the read channel.
     * This method calls the {@link ReadChannelListener#onClose} method.
     */
    @Override
    public void close() {
        if (state.isClose()) {
            return;
        }

        if (state.isStart()) {
            end();
        }

        state = ChannelState.CLOSE;

        if (listener != null) {
            listener.onClose(this);
        }
    }

    /**
     * Sets the new {@link ReadChannelListener} instance on the read channel.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link ReadChannelListener#onStart} event.</li>
     *     <li>Calling {@link #end} method fires the {@link ReadChannelListener#onEnd} event.</li>
     *     <li>Calling {@link #close} method fires the {@link ReadChannelListener#onClose} event.</li>
     * </ul>
     * If there is an existing listener, fires the {@link ReadChannelListener#onDrop} event.
     *
     * @param listener
     */
    @Override
    public void listen(ReadChannelListener listener) {
        this.listener = listener;
    }

    /**
     * @return
     */
    private boolean isEndOfChannel() {
        if (state.isEnd() || state.isClose()) {
            return true;
        }
        return false;
    }
}
