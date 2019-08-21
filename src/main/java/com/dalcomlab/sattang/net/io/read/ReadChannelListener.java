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

/**
 * The Listener for a life-cycle events of the {@link ReadChannel} instance.
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface ReadChannelListener {
    /**
     * This event is fired when the {@link ReadChannel#start()} method is called.
     * This event is usually used to add {@link ReadFilter} to the read channel.
     *
     * <pre>
     *     readChannel.listen(new ReadChannelListener() {
     *         public void onStart(ReadChannel channel) {
     *             channel.addFilter(new ReadFilterXXX());
     *             channel.addFilter(new ReadFilterYYY());
     *             channel.addFilter(new ReadFilterXXX());
     *         }
     *     }
     * </pre>
     *
     * @param channel
     */
    default void onStart(ReadChannel channel) {

    }

    /**
     * This event is fired when the {@link ReadChannel#end()} method is called.
     *
     * @param channel
     */
    default void onEnd(ReadChannel channel) {

    }

    /**
     * This event is fired when the {@link ReadChannel#error()} method is called.
     *
     * @param channel
     * @param t
     */
    default void onError(ReadChannel channel, Throwable t) {

    }

    /**
     * This event is fired when the {@link ReadChannel#close()} method is called.
     *
     * @param channel
     */
    default void onClose(ReadChannel channel) {

    }

    /**
     * This event is fired when this listener is dropped from the read channel.
     *
     * @param channel
     */
    default void onDrop(ReadChannel channel) {

    }
}
