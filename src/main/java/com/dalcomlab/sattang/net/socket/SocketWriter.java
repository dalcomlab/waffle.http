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

import com.dalcomlab.sattang.concurrent.CompletionHandler;

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface SocketWriter {

    /**
     * Write some data to the socket.
     * <p>
     * Writing may be blocking or asynchronous depending on the implementation.
     * When the write is complete, {@link CompletionHandler#completed} method be
     * called. If an error occurs in processing, call {@link CompletionHandler#failed}
     * method be called.
     *
     * @param buffer
     * @param handler
     */
    void write(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler);
}
