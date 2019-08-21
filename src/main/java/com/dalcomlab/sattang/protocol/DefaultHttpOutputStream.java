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

import com.dalcomlab.sattang.net.io.write.WriteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class DefaultHttpOutputStream extends HttpOutputStream {
    private HttpResponse response;
    private WriteChannel channel;
    private HttpWriteListener listener;

    /**
     * @param channel
     */
    public DefaultHttpOutputStream(WriteChannel channel) {
        this.channel = channel;
    }

    /**
     * This method can be used to determine if data can be written without blocking.
     *
     * @return <code>true</code> if a write to this <code>HttpOutputStream</code>
     * will succeed, otherwise returns <code>false</code>.
     */
    @Override
    public boolean isReady() {
        if (!response.isAsync()) {
            return false;
        }

        return true;
    }

    /**
     * Sets the {@link HttpWriteListener} to invoke when it is possible to write.
     * The <code>IllegalStateException</code> can be thrown if one of the following
     * conditions is true.
     * <ul>
     * <li>the associated request is neither upgraded nor the async started
     * <li>setWriteListener is called more than once within the scope of the same request.
     * </ul>
     *
     * @param listener
     * @throws Exception
     */
    public void setWriteListener(HttpWriteListener listener) throws Exception {
        if (this.listener != null) {
            throw new IllegalStateException("");
        }

        if (response.isCommitted()) {
            throw new IllegalStateException("");
        }

        if (response.isAsync()) {
            throw new IllegalStateException("");
        }

        this.listener = listener;
    }


    /**
     * @param source
     * @throws Exception
     */
    public void write(ByteBuffer source) throws IOException {
        final boolean useFilter = true;
        if (listener != null) {
            channel.write(source, useFilter);
        } else {
            channel.writeBlocking(source, useFilter);
        }
    }

    @Override
    public void flush() {
        try {
            channel.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
