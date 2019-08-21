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

import com.dalcomlab.sattang.net.event.EventDispatcher;
import com.dalcomlab.sattang.net.event.EventExecutor;
import com.dalcomlab.sattang.net.event.nio.NioEventDispatcherGroup;
import com.dalcomlab.sattang.net.socket.SocketSelectorPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class SocketTransport implements Transport {
    private ServerSocketChannel server;
    private ExecutorService executor = null;
    private SocketSelectorPool selectorPool = new SocketSelectorPool(100);
    private InetSocketAddress address = null;
    private AtomicReference<State> state = new AtomicReference(State.STOPPED);
    private EventDispatcher dispatcher;

    /**
     *
     */
    public SocketTransport() {
        this.executor = createExecutorService(100);
        this.dispatcher = new NioEventDispatcherGroup(this.executor, 10);
    }

    /**
     *
     */
    @Override
    public void reuse() {

    }

    /**
     * Returns the {@link ExecutorService}.
     *
     * @return
     */
    @Override
    public ExecutorService getExecutor() {
        return executor;
    }


    /**
     * Listens
     *
     * @param hostname
     * @param port
     */
    @Override
    public void listen(String hostname, int port) throws IllegalStateException {
        listen(new InetSocketAddress(hostname, port));
    }


    /**
     * Listens
     *
     * @param address
     */
    @Override
    public void listen(InetSocketAddress address) throws IllegalStateException {
        if (isStarted()) {
            throw new IllegalStateException("the transport is already started.");
        }

        this.address = address;
    }


    /**
     * @param event
     */
    public void async(EventExecutor<SocketChannel> event) {
        if (event != null) {
            dispatcher.register(event);
        }
    }


    /**
     * Creates the connection related to this transport.
     *
     * @param channel
     * @param
     * @return
     */
    protected abstract Connection createConnection(SocketChannel channel);

    /**
     * Returns the state of this service.
     *
     * @return
     */
    @Override
    public State getState() {
        return state.get();
    }


    /**
     * Starts the transport.
     *
     * @throws IOException
     */
    @Override
    public void start() throws IOException {
        if (!state.compareAndSet(State.STOPPED, State.STARTING)) {
            return;
        }
        try {
            server = ServerSocketChannel.open();
            server.configureBlocking(true);
            ServerSocket socket = server.socket();
            socket.bind(address);
        } catch (Exception e) {
            stop();
            throw e;
        }

        dispatcher.start();
        state.set(State.STARTED);

        try {
            while (true) {
                if (state.get() == State.STOPPING || state.get() == State.STOPPED) {
                    break;
                }
                System.out.println("-------------------------------------------");
                System.out.println("* waiting accept client.");
                System.out.println("-------------------------------------------");
                SocketChannel channel = server.accept();
                if (channel != null) {
                    channel.configureBlocking(false);
                    Connection connection = createConnection(channel);
                    if (connection != null) {
                        if (connection.handshake()) {
                            connection.start();
                        }
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    /**
     * Stops the transport and close all connections.
     *
     * @return
     */
    @Override
    public void stop() {
        state.set(State.STOPPING);
        try {
            server.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();
        state.set(State.STOPPED);
    }

    /**
     * Stops the service and close all connections.
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
     * Returns the default size of a buffer, which will be allocated for
     * reading data.
     *
     * @return
     */
    @Override
    public int getReadBufferSize() {
        return 1024;
    }

    /**
     * Sets the default size of a buffer, which will be allocated for
     * reading data.
     *
     * @param readBufferSize
     */
    @Override
    public void setReadBufferSize(int readBufferSize) {

    }

    /**
     * Returns the default size of a buffer, which will be allocated for
     * writing data.
     *
     * @return
     */
    public int getWriteBufferSize() {
        return 1024;
    }

    /**
     * Sets the default size of a buffer, which will be allocated for
     * writing data.
     *
     * @param writeBufferSize
     */
    public void setWriteBufferSize(int writeBufferSize) {

    }


    /**
     * Returns the current value for the blocking read timeout.
     *
     * @param timeUnit
     * @return the read timeout value
     */
    @Override
    public long getReadTimeout(TimeUnit timeUnit) {
        return 0;
    }

    /**
     * Sets the timeout for the blocking reads.
     *
     * @param timeout
     * @param timeUnit
     */
    @Override
    public void setReadTimeout(long timeout, TimeUnit timeUnit) {

    }

    /**
     * Returns the current value for the blocking write timeout.
     *
     * @param timeUnit
     * @return the write timeout value
     */
    public long getWriteTimeout(TimeUnit timeUnit) {
        return 0;
    }

    /**
     * Sets the timeout for the blocking writes.
     *
     * @param timeout
     * @param timeUnit
     */
    @Override
    public void setWriteTimeout(long timeout, TimeUnit timeUnit) {

    }


    public SocketSelectorPool getSelectorPool() {
        return this.selectorPool;
    }

    /**
     * @param count
     * @return
     */
    private ExecutorService createExecutorService(int count) {
        final ThreadFactory factory = new ThreadFactory() {
            private int counter;

            @Override
            public Thread newThread(Runnable r) {
                final String name = getName() + " : socket.event.worker <" + counter++ + ">";
                Thread t = new Thread(r, name);
                t.setDaemon(true);
                return t;
            }
        };

        return Executors.newFixedThreadPool(count, factory);
    }
}
