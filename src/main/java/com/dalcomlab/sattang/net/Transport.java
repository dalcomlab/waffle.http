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
package com.dalcomlab.sattang.net;

import com.dalcomlab.sattang.Reusable;
import com.dalcomlab.sattang.net.event.EventExecutor;

import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Transport extends Service, Reusable {
    int DEFAULT_READ_BUFFER_SIZE = -1;
    int DEFAULT_WRITE_BUFFER_SIZE = -1;
    int DEFAULT_READ_TIMEOUT = 30;
    int DEFAULT_WRITE_TIMEOUT = 30;

    /**
     * Returns the name of this transport.
     *
     * @return
     */
    String getName();

    /**
     * Returns the {@link ExecutorService}.
     *
     * @return
     */
    ExecutorService getExecutor();

    /**
     * Listens
     *
     * @param hostname
     * @param port
     */
    void listen(String hostname, int port) throws IllegalStateException;


    /**
     * Listens
     *
     * @param address
     */
    void listen(InetSocketAddress address) throws IllegalStateException;

    /**
     * @param event
     */
    void async(EventExecutor<SocketChannel> event);


    /**
     * Returns the default size of a buffer, which will be allocated for
     * reading data.
     *
     * @return
     */
    int getReadBufferSize();

    /**
     * Sets the default size of a buffer, which will be allocated for
     * reading data.
     *
     * @param readBufferSize
     */
    void setReadBufferSize(int readBufferSize);

    /**
     * Returns the default size of a buffer, which will be allocated for
     * writing data.
     *
     * @return
     */
    int getWriteBufferSize();

    /**
     * Sets the default size of a buffer, which will be allocated for
     * writing data.
     *
     * @param writeBufferSize
     */
    void setWriteBufferSize(int writeBufferSize);


    /**
     * Returns the current value for the blocking read timeout.
     *
     * @param timeUnit
     * @return the read timeout value
     */
    long getReadTimeout(TimeUnit timeUnit);

    /**
     * Sets the timeout for the blocking reads.
     *
     * @param timeout
     * @param timeUnit
     */
    void setReadTimeout(long timeout, TimeUnit timeUnit);

    /**
     * Returns the current value for the blocking write timeout.
     *
     * @param timeUnit
     * @return the write timeout value
     */
    long getWriteTimeout(TimeUnit timeUnit);

    /**
     * Sets the timeout for the blocking writes.
     *
     * @param timeout
     * @param timeUnit
     */
    void setWriteTimeout(long timeout, TimeUnit timeUnit);
}
