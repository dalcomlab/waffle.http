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
import com.dalcomlab.sattang.net.event.EventDispatcherGroup;
import com.dalcomlab.sattang.net.event.EventExecutor;
import com.dalcomlab.sattang.net.event.EventFuture;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class NioEventDispatcherGroup implements EventDispatcherGroup {
    private ExecutorService executor;
    private List<EventDispatcher> dispatchers = new ArrayList();
    private AtomicReference<Service.State> state = new AtomicReference(Service.State.STOPPED);
    private int counter = 0;

    /**
     * @param count
     */
    public NioEventDispatcherGroup(ExecutorService executor, int count) {
        this.executor = executor;
        this.dispatchers = createEventDispatchers(count);
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

        for (EventDispatcher dispatcher : dispatchers) {
            executor.submit(() -> dispatcher.start());
        }

        state.set(Service.State.STARTED);
    }


    /**
     * @param event
     * @return
     */
    @Override
    public EventFuture register(EventExecutor event) {
        final EventDispatcher dispatcher = takeEventDispatcher();
        if (dispatcher != null) {
            dispatcher.register(event);
        } else {
            event.cancel();
        }
        return null;
    }

    /**
     * Stops the event dispatcher.
     */
    @Override
    public void stop() {
        state.set(Service.State.STOPPING);
        for (EventDispatcher dispatcher : dispatchers) {
            dispatcher.stop();
        }
        state.set(Service.State.STOPPED);
    }

    /**
     * @return
     */
    private EventDispatcher takeEventDispatcher() {
        int i = counter % dispatchers.size();
        if (counter >= Integer.MAX_VALUE) {
            counter = 0;
        } else {
            counter++;
        }
        return dispatchers.get(i);
    }

    /**
     * @param count
     * @return
     */
    private List<EventDispatcher> createEventDispatchers(int count) {
        List<EventDispatcher> dispatchers = new ArrayList(count);
        for (int i = 0; i < count; i++) {
            dispatchers.add(new NioEventDispatcher(this.executor));
        }
        return dispatchers;
    }

}
