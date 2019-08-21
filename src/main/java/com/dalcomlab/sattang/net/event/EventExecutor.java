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
package com.dalcomlab.sattang.net.event;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface EventExecutor<T> {

    /**
     * @return
     */
    default T channel() {
        return null;
    }

    /**
     * @return
     */
    default long timeout() {
        return 0;
    }

    /**
     * @return
     */
    SocketEvent event();

    /**
     * @param channel
     * @param dispatcher
     */
    default void execute(T channel, EventDispatcher dispatcher) {

    }

    /**
     *
     */
    default void cancel() {

    }

    /**
     * @return
     */
    default boolean isRead() {
        return event().isRead();
    }

    /**
     * @return
     */
    default boolean isWrite() {
        return event().isWrite();
    }
}
