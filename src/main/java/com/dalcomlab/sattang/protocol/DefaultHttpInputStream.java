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
package com.dalcomlab.sattang.protocol;

import com.dalcomlab.sattang.net.io.read.ReadChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class DefaultHttpInputStream extends HttpInputStream {
    private HttpRequest request;
    private ReadChannel channel;
    private HttpReadListener listener = null;
    private final byte[] one = new byte[1];

    /**
     * @param channel
     */
    public DefaultHttpInputStream(ReadChannel channel) {
        this.channel = channel;
    }

    /**
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(one);
        read(buffer);
        return one[0];
    }

    /**
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte b[], int off, int len) throws IOException {
        ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
        if (read(buffer) == -1) {
            return -1;
        }

        buffer.flip();
        return buffer.remaining();
    }

    /**
     * @param buffer
     * @throws Exception
     */
    public int read(ByteBuffer buffer) throws IOException {
        final boolean useFilter = true;
        int consume;
        if (listener != null) {
            consume = channel.read(buffer, useFilter, null);
        } else {
            consume = channel.readBlocking(buffer, useFilter);
        }
        return consume;
    }

    /**
     * This method can be used to determine if data can be read without blocking.
     *
     * @return <code>true</code> if a read to this <code>HttpInputStream</code>
     * will succeed, otherwise returns <code>false</code>.
     */
    @Override
    public boolean isReady() {
        if (!request.isAsync()) {
            return false;
        }

        return false;
    }

    /**
     * Sets the {@link HttpReadListener} to invoke when it is possible to read
     * The <code>IllegalStateException</code> can be thrown if one of the following
     * conditions is true.
     * <ul>
     * <li>the associated request is neither upgraded nor the async started
     * <li>setReadListener is called more than once within the scope of the same request.
     * </ul>
     *
     * @param listener
     * @throws Exception
     */
    @Override
    public void setReadListener(HttpReadListener listener) throws Exception {
        if (this.listener != null) {
            throw new IllegalStateException("");
        }
        this.listener = listener;
    }
}
