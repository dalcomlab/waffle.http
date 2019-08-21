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
package com.dalcomlab.sattang.net.io.write;

import com.dalcomlab.sattang.net.Connection;
import com.dalcomlab.sattang.net.Filterable;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface WriteChannel extends Filterable<WriteFilter> {

    /**
     * Returns the {@link ByteBuffer} for this write channel.
     * @return
     */
    ByteBuffer getWriteBuffer();

    /**
     * Returns the {@link Connection} that this write channel belongs to.
     *
     * @return
     */
    Connection getConnection();

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
    int write(ByteBuffer buffer, boolean useFilter);

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
    int writeBlocking(ByteBuffer buffer, boolean useFilter);


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
    void flush() throws IOException;

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
    void start();


    /**
     * This method calls the {@link WriteChannelListener#onEnd} method.
     */
    void end();

    /**
     * This method calls the {@link WriteChannelListener#onError} method.
     */
    void error(Throwable t);

    /**
     * Closes the write channel.
     * This method calls the {@link WriteChannelListener#onClose} method.
     */
    void close();

    /**
     * Sets the new {@link WriteChannelListener} instance on the write channel.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link WriteChannelListener#onStart} event.</li>
     *     <li>Calling {@link #end} method fires the {@link WriteChannelListener#onEnd} event.</li>
     *     <li>Calling {@link #error} method fires the {@link WriteChannelListener#onError} event.</li>
     *     <li>Calling {@link #close} method fires the {@link WriteChannelListener#onClose} event.</li>
     * </ul>
     * If there is an existing listener, fires the {@link WriteChannelListener#onDrop} event.
     *
     * @param listener
     */
    void listen(WriteChannelListener listener);
}
