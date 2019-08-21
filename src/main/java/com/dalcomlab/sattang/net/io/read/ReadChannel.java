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
package com.dalcomlab.sattang.net.io.read;

import com.dalcomlab.sattang.concurrent.CompletionHandler;
import com.dalcomlab.sattang.net.Connection;
import com.dalcomlab.sattang.net.Filterable;

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ReadChannel extends Filterable<ReadFilter> {

    /**
     * Returns the {@link ByteBuffer} for this read channel.
     *
     * @return
     */
    ByteBuffer getReadBuffer();


    /**
     * Returns the {@link Connection} that this read channel belongs to.
     *
     * @return
     */
    Connection getConnection();

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
    int read(ByteBuffer buffer, boolean useFilter, CompletionHandler<ByteBuffer> handler);


    /**
     * Reads some data from this read channel in blocking mode.
     * <p>
     * The read channel internally includes several {@link ReadFilter} that
     * can be added by call the {@lin #addFilter} method. If there are filters, the
     * outbound channel will calling the first filter's {@link ReadFilter#read}
     * method.
     *
     * @param buffer
     * @param buffer
     * @return
     */
    int readBlocking(ByteBuffer buffer, boolean useFilter);


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
    void start();

    /**
     * This method calls the {@link ReadChannelListener#onEnd} method.
     */
    void end();

    /**
     * This method calls the {@link ReadChannelListener#onError} method.
     */
    void error(Throwable t);

    /**
     * Closes the read channel.
     * This method calls the {@link ReadChannelListener#onClose} method.
     */
    void close();

    /**
     * Sets the new {@link ReadChannelListener} instance on the read channel.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link ReadChannelListener#onStart} event.</li>
     *     <li>Calling {@link #end} method fires the {@link ReadChannelListener#onEnd} event.</li>
     *     <li>Calling {@link #error} method fires the {@link ReadChannelListener#onError} event.</li>
     *     <li>Calling {@link #close} method fires the {@link ReadChannelListener#onClose} event.</li>
     * </ul>
     * If there is an existing listener, fires the {@link ReadChannelListener#onDrop} event.
     *
     * @param listener
     */
    void listen(ReadChannelListener listener);
}
