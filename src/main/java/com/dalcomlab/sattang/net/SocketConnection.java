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

import com.dalcomlab.sattang.net.io.channel.AbstractSocket;
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.net.io.read.channels.SocketReadChannel;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.channels.SocketWriteChannel;

import java.io.IOException;
import java.net.SocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class SocketConnection implements Connection {
    protected final Transport transport;
    protected final AbstractSocket socket;
    protected final SocketReadChannel readChannel;
    protected final SocketWriteChannel writeChannel;
    protected AtomicReference<State> state = new AtomicReference(State.STOPPED);

    /**
     * @param transport
     */
    public SocketConnection(Transport transport, AbstractSocket socket) {
        this.transport = transport;
        this.socket = socket;
        this.readChannel = new SocketReadChannel(socket);
        this.writeChannel = new SocketWriteChannel(socket);
    }

    /**
     * @return
     */
    @Override
    public State getState() {
        return state.get();
    }

    /**
     * @return
     */
    @Override
    public boolean isConnected() {
        return socket.isConnected();
    }

    /**
     * Returns the {@link Transport} that this connection belongs to.
     *
     * @return
     */
    @Override
    public Transport getTransport() {
        return transport;
    }


    /**
     * Closes the this connection.
     */
    @Override
    public void close() {
        try {
            readChannel.close();
            writeChannel.close();
            socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Returns the read buffer size in bytes.
     *
     * @return
     */
    @Override
    public int getReadBufferSize() {
        return transport.getReadBufferSize();
    }

    /**
     * Returns the write buffer size in bytes.
     *
     * @return
     */
    @Override
    public int getWriteBufferSize() {
        return transport.getWriteBufferSize();
    }

    /**
     * Returns the read timeout in millisecond.
     *
     * @return
     */
    @Override
    public long getReadTimeoutMillis() {
        return transport.getReadTimeout(TimeUnit.MILLISECONDS);
    }

    /**
     * Returns the write timeout in millisecond.
     *
     * @return
     */
    @Override
    public long getWriteTimeoutMillis() {
        return transport.getReadTimeout(TimeUnit.MILLISECONDS);
    }


    /**
     * @return
     */
    @Override
    public ReadQueue getReadQueue() {
        return null;
    }


    /**
     * @return
     */
    @Override
    public WriteQueue getWriteQueue() {
        return null;
    }


    /**
     * Returns the {@link ReadChannel} object belonging to the connection.
     * The {@link ReadChannel#read} can be used to read the data.
     *
     * @return
     */
    @Override
    public ReadChannel getReadChannel() {
        return readChannel;
    }

    /**
     * Returns the {@link WriteChannel} object belonging to the connection.
     * The {@link WriteChannel#write} can be used to write the data.
     *
     * @return
     */
    @Override
    public WriteChannel getWriteChannel() {
        return writeChannel;
    }


    /**
     * Starts the connection.
     *
     * @throws IOException
     */
    @Override
    public void start() throws Exception {

    }

    /**
     * Stops the connection.
     *
     * @return
     */
    @Override
    public void stop() {
        state.set(State.STOPPING);
        close();
        state.set(State.STOPPED);
    }

    /**
     * Stops the connection.
     *
     * @param timeout
     * @param timeUnit
     * @return
     */
    @Override
    public void stop(long timeout, TimeUnit timeUnit) {
        stop();
    }

    /**
     * Returns the local address that this connection is connected to.
     *
     * @return the local address
     */
    @Override
    public SocketAddress getLocalAddress() throws IOException {
        return null;
        //return socket.getLocalAddress();
    }

    /**
     * Returns the remote address that this connections is connected to.
     *
     * @return
     */
    @Override
    public SocketAddress getRemoteAddress() throws IOException {
        return null;
        //return channel.getRemoteAddress();
    }
}
