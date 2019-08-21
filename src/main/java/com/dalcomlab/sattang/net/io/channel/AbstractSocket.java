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
package com.dalcomlab.sattang.net.io.channel;

import com.dalcomlab.sattang.concurrent.CompletionHandler;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface AbstractSocket {

    /**
     * Configs the channel.
     *
     * @param options
     */
    void config(SocketOptions options);

    /**
     * @return
     */
    boolean isConnected();

    /**
     * Write some data to this write channel in async mode.
     *
     * @param buffer
     * @param handler
     * @return
     * @throws Exception
     */
    int write(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) throws IOException;


    /**
     * Write some data to this write channel in blocking mode.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    int writeBlocking(ByteBuffer buffer) throws IOException;

    /**
     * Reads some data from this read channel in async mode.
     *
     * @param buffer
     * @param handler
     * @return
     * @throws Exception
     */
    int read(ByteBuffer buffer, CompletionHandler<ByteBuffer> handler) throws IOException;

    /**
     * Reads some data from this read channel in blocking mode.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    int readBlocking(ByteBuffer buffer) throws IOException;

    /**
     * Closes the socket and clears all resources.
     *
     * @throws IOException
     */
    void close() throws IOException;
}
