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

/**
 * The Listener for a life-cycle events of the {@link WriteChannel} instance.
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface WriteChannelListener {

    /**
     * This event is fired when the {@link WriteChannel#start()} method is called.
     * This event is usually used to add {@link WriteFilter} to the write channel.
     *
     * <pre>
     *     writeChannel.listen(new WriteChannelListener() {
     *         public void onStart(WriteChannel channel) {
     *             channel.addFilter(new WriteFilterXXX());
     *             channel.addFilter(new WriteFilterYYY());
     *             channel.addFilter(new WriteFilterXXX());
     *         }
     *     }
     * </pre>
     *
     * @param channel
     */
    default void onStart(WriteChannel channel) {

    }

    /**
     * This event is fired when the {@link WriteChannel#end()} method is called.
     *
     * @param channel
     */
    default void onEnd(WriteChannel channel) {

    }

    /**
     * This event is fired when the {@link WriteChannel#error()} method is called.
     *
     * @param channel
     * @param t
     */
    default void onError(WriteChannel channel, Throwable t) {

    }

    /**
     * This event is fired when the {@link WriteChannel#close()} method is called.
     *
     * @param channel
     */
    default void onClose(WriteChannel channel) {

    }

    /**
     * This event is fired when this listener is dropped from the write channel.
     *
     * @param channel
     */
    default void onDrop(WriteChannel channel) {

    }
}
