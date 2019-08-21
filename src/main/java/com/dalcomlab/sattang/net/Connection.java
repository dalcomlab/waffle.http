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
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.net.io.write.WriteChannel;

import java.io.IOException;
import java.net.SocketAddress;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Connection extends Service, Reusable {

    /**
     * @return
     */
    boolean isConnected();

    /**
     * Returns the {@link Transport} that this connection belongs to.
     *
     * @return
     */
    Transport getTransport();

    /**
     * Performs the handshake operation.
     *
     * @return <code>true</code> if the handshake is ok, otherwise returns
     * <code>false</code>.
     */
    boolean handshake();

    /**
     * Closes the this connection.
     */
    void close();

    /**
     * Returns the read buffer size in bytes.
     *
     * @return
     */
    int getReadBufferSize();

    /**
     * Returns the write buffer size in bytes.
     *
     * @return
     */
    int getWriteBufferSize();

    /**
     * Returns the read timeout in millisecond.
     *
     * @return
     */
    long getReadTimeoutMillis();

    /**
     * Returns the write timeout in millisecond.
     *
     * @return
     */
    long getWriteTimeoutMillis();

    /**
     * Returns the local address that this connection is connected to.
     *
     * @return
     * @throws IOException
     */
    SocketAddress getLocalAddress() throws IOException;

    /**
     * Returns the remote address that this connections is connected to.
     *
     * @return
     * @throws IOException
     */
    SocketAddress getRemoteAddress() throws IOException;

    /**
     * @return
     */
    ReadQueue getReadQueue();


    /**
     * @return
     */
    WriteQueue getWriteQueue();

    /**
     * Returns the {@link ReadChannel} object belonging to the connection.
     * The {@link ReadChannel#read} can be used to read the data.
     *
     * @return
     */
    ReadChannel getReadChannel();

    /**
     * Returns the {@link WriteChannel} object belonging to the connection.
     * The {@link WriteChannel#write} can be used to write the data.
     *
     * @return
     */
    WriteChannel getWriteChannel();

}
