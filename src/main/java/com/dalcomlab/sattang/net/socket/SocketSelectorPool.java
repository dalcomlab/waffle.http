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
package com.dalcomlab.sattang.net.socket;

import java.nio.channels.Selector;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class SocketSelectorPool {
    public static final int DEFAULT_MAX_COUNT = 32;
    private final Queue<Selector> selectors = new ConcurrentLinkedQueue();
    private volatile int maxCount;
    private final AtomicInteger count;

    /**
     * Creates new instance.
     *
     * @param maxCount
     */
    public SocketSelectorPool(int maxCount) {
        this.maxCount = maxCount;
        if (this.maxCount <= 0) {
            this.maxCount = DEFAULT_MAX_COUNT;
        }
        count = new AtomicInteger();
    }

    /**
     * Polls the {@link Selector} from the queue. If the queue is empty,
     * a new {@link Selector} is created and return it.
     *
     * @return the {@link Selector} instance.
     */
    public Selector poll() {
        Selector selector = selectors.poll();
        if (selector != null) {
            count.decrementAndGet();
        } else {
            selector = openSelector();
        }
        return selector;
    }

    /**
     * Offers the given the {@link Selector} in the queue.  If the maximum size
     * of the queue is exceeded, the given the {@link Selector} is closed without
     * being added to the queue.
     *
     * @param selector
     */
    public void offer(Selector selector) {
        if (selector == null) {
            return;
        }

        if (count.getAndIncrement() < maxCount) {
            selectors.offer(selector);
        } else {
            closeSelector(selector);
            count.decrementAndGet();
        }
    }

    /**
     *
     */
    public void close() {
        Selector selector;
        while ((selector = selectors.poll()) != null) {
            closeSelector(selector);
        }
    }

    /**
     * @return
     */
    private Selector openSelector() {
        try {
            return Selector.open();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @param selector
     */
    private void closeSelector(Selector selector) {
        if (selector == null) {
            return;
        }
        try {
            selector.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
