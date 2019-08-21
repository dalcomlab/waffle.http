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
package com.dalcomlab.sattang.net.event.nio;

import com.dalcomlab.sattang.net.Service;
import com.dalcomlab.sattang.net.event.EventDispatcher;
import com.dalcomlab.sattang.net.event.EventExecutor;
import com.dalcomlab.sattang.net.event.EventFuture;

import java.io.IOException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class NioEventDispatcher implements EventDispatcher {
    private final Queue<EventExecutor<SocketChannel>> events = new ConcurrentLinkedQueue<>();
    private AtomicReference<Service.State> state = new AtomicReference(Service.State.STOPPED);
    private final ExecutorService executor;
    private Selector selector = null;

    /**
     * @param executor
     */
    public NioEventDispatcher(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Starts the event dispatcher.
     *
     * @throws IOException
     */
    @Override
    public void start() {
        if (!state.compareAndSet(Service.State.STOPPED, Service.State.STARTING)) {
            return;
        }

        if (selector == null) {
            try {
                selector = Selector.open();
            } catch (Exception e) {
                selector = null;
            }
        }

        if (selector == null) {
            state.set(Service.State.STOPPED);
            return;
        }

        System.out.println("The NIO channel event dispatcher is starting in " + Thread.currentThread());

        try {
            while (true) {
                if (isStop()) {
                    break;
                }

                registerEvents();
                selector.select();

                if (isStop()) {
                    break;
                }

                Set<SelectionKey> keys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = keys.iterator();
                while (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    if (!key.isValid()) {
                        continue;
                    }

                    key.interestOps(key.interestOps() & (~key.readyOps()));
                    EventExecutor event = (EventExecutor) key.attachment();
                    if (event == null) {
                        continue;
                    }

                    if (executor != null) {
                        executor.submit(() -> {
                            event.execute(key.channel(), NioEventDispatcher.this);
                        });
                    } else {
                        event.execute(key.channel(), NioEventDispatcher.this);
                    }

                }
            }
        } catch (Exception e) {
            stop();
        }
    }

    /**
     * Registers a {@link EventExecutor} in this event dispatcher.
     * When the event of interest of the {@link EventExecutor}  occurs, this dispatcher
     * calls the {@link EventExecutor#execute} method of the given EventExecutor.
     *
     * @param event
     * @return
     */
    @Override
    public EventFuture register(EventExecutor event) {
        events.offer(event);
        if (selector != null) {
            selector.wakeup();
        }
        return null;
    }

    /**
     * Stops the event dispatcher.
     * <p>
     * If there are registered {@link EventExecutor} events, this dispatcher calls
     * the {@link EventExecutor#cancel} method of the EventExecutor.
     */
    @Override
    public void stop() {

        state.set(Service.State.STOPPING);
        for (EventExecutor event : events) {
            event.cancel();
        }

        events.clear();

        if (selector != null) {
            selector.wakeup();
            // need to wait until the state is changed to State.STOPPED
            try {
                selector.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        state.set(Service.State.STOPPED);

    }

    /**
     * @throws IOException
     */
    private void registerEvents() throws IOException {
        while (!events.isEmpty()) {
            final EventExecutor<SocketChannel> event = events.poll();
            final SocketChannel channel = event.channel();
            if (!channel.isConnected()) {
                continue;
            }

            SelectionKey key = channel.keyFor(selector);
            if (key == null) {
                if (event.isRead()) {
                    key = channel.register(selector, SelectionKey.OP_READ);
                } else if (event.isWrite()) {
                    key = channel.register(selector, SelectionKey.OP_WRITE);
                }
            } else {
                if (event.isRead()) {
                    key.interestOps(SelectionKey.OP_READ);
                } else if (event.isWrite()) {
                    key.interestOps(SelectionKey.OP_WRITE);
                }
            }

            if (key != null) {
                key.attach(event);
            }
        }
    }

    /**
     * @return
     */
    private boolean isStop() {
        if (state.get() == Service.State.STOPPING || state.get() == Service.State.STOPPED) {
            return true;
        }

        if (selector == null) {
            return true;
        }
        return false;
    }
}
