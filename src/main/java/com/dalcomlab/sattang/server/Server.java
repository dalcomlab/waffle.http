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
package com.dalcomlab.sattang.server;

import com.dalcomlab.sattang.net.io.write.WriteChannelListener;

import java.io.IOException;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Server {

    /**
     * Starts the server.
     *
     * @param options
     */
    void start(ServerOptions options) throws IOException;

    /**
     * Stops the server.
     *
     */
    void stop();

    /**
     * Sets the new {@link ServerListener} instance on the server.
     * <ul>
     *     <li>Calling {@link #start} method fires the {@link ServerListener#onStart()} event.</li>
     *     <li>Calling {@link #stop} method fires the {@link ServerListener#onStop()} event.</li>
     * </ul>
     * @param listener
     */
    Server listen(ServerListener listener);
}
