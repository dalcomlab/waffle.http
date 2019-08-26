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
package com.dalcomlab.sattang.net.io.write.channels;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.Connection;
import com.dalcomlab.sattang.net.io.ChannelState;
import com.dalcomlab.sattang.net.io.channel.AbstractSocket;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.WriteChannelListener;
import com.dalcomlab.sattang.net.io.write.WriteFilter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class SocketWriteChannel implements WriteChannel {
    private final AbstractSocket socket;
    private final Deque<WriteFilter> filters = new ConcurrentLinkedDeque();
    private final WriteFilter writeFilter;
    private WriteChannelListener listener = null;
    private boolean isCommitted = false;
    private ByteBuffer buffer;
    private ChannelState state = ChannelState.INIT;

    /**
     * @param socket
     */
    public SocketWriteChannel(AbstractSocket socket) {
        this.socket = socket;
        this.writeFilter = new WriteFilter() {
            public int write(WriteChannel channel, ByteBuffer source) throws IOException {
               return socket.writeBlocking(source);
            }
        };

        this.buffer = ByteBuffer.allocate(1024 * 8);
    }

    /**
     *
     */
    public void reuse() {
        this.end();
        this.buffer.clear();
        this.filters.clear();
        this.buffer = ByteBuffer.allocate(1024 * 8);
        state = ChannelState.INIT;
        isCommitted = false;
    }

    /**
     * Returns the {@link ByteBuffer} for this write channel.
     *
     * @return
     */
    @Override
    public ByteBuffer getWriteBuffer() {
        return this.buffer;
    }

    /**
     * @return
     */
    @Override
    public Deque<WriteFilter> getFilters() {
        return filters;
    }

    /**
     * Returns the {@link Connection} that this write channel belongs to.
     *
     * @return
     */
    @Override
    public Connection getConnection() {
        return null;
    }

    /**
     * Write some data to this write channel in async mode.
     * <p>
     * The write channel internally includes several {@link WriteFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * write channel will calling the first filter's {@link WriteFilter#write}
     * method.
     *
     * @param buffer
     * @param useFilter
     * @return
     */
    @Override
    public int write(ByteBuffer buffer, boolean useFilter) {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (!isCommitted) {
            start();
            isCommitted = true;
        }
        int consume = 0;
        try {
            if (useFilter) {
                WriteFilter last = getLastFilter();
                if (last != null) {
                    last.next(writeFilter);
                }

                WriteFilter filter = getFirstFilter();
                if (filter != null) {
                    consume = filter.write(this, buffer);
                }
            } else {
                writeFilter.write(this, buffer);
            }
        } catch (IOException e) {
            consume = ChannelConstants.END_OF_CHANNEL;
            error(e);
        }
        return consume;
    }

    /**
     * Write some data to this write channel in blocking mode.
     * <p>
     * The write channel internally includes several {@link WriteFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * write channel will calling the first filter's {@link WriteFilter#write}
     * method.
     *
     * @param buffer
     * @param useFilter
     * @return
     */
    @Override
    public int writeBlocking(ByteBuffer buffer, boolean useFilter) {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (!isCommitted) {
            start();
            isCommitted = true;
        }
        int consume = 0;
        try {
            if (useFilter) {
                WriteFilter last = getLastFilter();
                if (last != null) {
                    last.next(writeFilter);
                }

                WriteFilter filter = getFirstFilter();
                if (filter != null) {
                    filter.write(this, buffer);
                }
            } else {
                writeFilter.write(this, buffer);
            }
        } catch (IOException e) {
            consume = ChannelConstants.END_OF_CHANNEL;
            error(e);
        }
        return consume;
    }


    /**
     * Flushes the remaining buffer in this write channel.
     * <p>
     * The write channel internally includes several {@link WriteFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * write channel will calling the first filter's {@link WriteFilter#flush}
     * method.
     *
     * @throws IOException
     */
    @Override
    public void flush() throws IOException {
        if (state.isEnd() || state.isClose()) {
            return;
        }

        WriteFilter filter = getFirstFilter();
        if (filter != null) {
            filter.flush(this);
        }
    }

    /**
     * This method calls the {@link WriteChannelListener#onStart} method.
     *
     * <pre>
     *     writeChannel.listen(new WriteChannelListener() {
     *         public void onStart(WriteChannel channel) {
     *             channel.addFilter(new WriteFilterXXX());
     *             channel.addFilter(new WriteFilterYYY());
     *             channel.addFilter(new WriteFilterZZZ());
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
     * This method calls the {@link WriteChannelListener#onError} method.
     */
    @Override
    public void error(Throwable t) {
        System.out.println("write error!!!!!");
        System.out.println(t.toString());
        if (listener != null) {
            listener.onError(this, t);
        }

        close();
    }

    /**
     * This method calls the {@link WriteChannelListener#onEnd} method.
     */
    @Override
    public void end() {
        if (state.isEnd() || state.isClose()) {
            return;
        }

        state = ChannelState.END;

        try {
            WriteFilter filter = getFirstFilter();
            if (filter != null) {
                filter.flush(this);
                filter.end(this);
            }
        } catch (IOException e) {
            error(e);
        } finally {
            if (listener != null) {
                listener.onEnd(this);
            }
        }
    }

    /**
     * Closes the write channel.
     * This method calls the {@link WriteChannelListener#onClose} method.
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
     * Sets the new {@link WriteChannelListener} instance on the write channel.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link WriteChannelListener#onStart} event.</li>
     *     <li>Calling {@link #end} method fires the {@link WriteChannelListener#onEnd} event.</li>
     *     <li>Calling {@link #close} method fires the {@link WriteChannelListener#onClose} event.</li>
     * </ul>
     * If there is an existing listener, fires the {@link WriteChannelListener#onDrop} event.
     *
     * @param listener
     */
    @Override
    public void listen(WriteChannelListener listener) {
        if (this.listener != null) {
            this.listener.onDrop(this);
        }
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
