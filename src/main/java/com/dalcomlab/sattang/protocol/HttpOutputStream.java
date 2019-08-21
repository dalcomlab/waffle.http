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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class HttpOutputStream extends OutputStream {
    private final byte[] one = new byte[1];

    /**
     *
     */
    private final static HttpOutputStream EMPTY = new HttpOutputStream() {
        @Override
        public void write(ByteBuffer source) throws IOException {
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(HttpWriteListener listener) throws Exception {

        }
    };

    /**
     *
     * @return
     */
    public final static HttpOutputStream empty() {
        return EMPTY;
    }

    /**
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        one[0] = (byte) b;
        write(one);
    }


    /**
     * @param b
     * @throws IOException
     */
    @Override
    public void write(byte b[]) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    @Override
    public void write(byte b[], int off, int len) throws IOException {
        if (b == null) {
            throw new NullPointerException();
        } else if ((off < 0) || (off > b.length) || (len < 0) ||
                ((off + len) > b.length) || ((off + len) < 0)) {
            throw new IndexOutOfBoundsException();
        } else if (len == 0) {
            return;
        }
        ByteBuffer buffer = ByteBuffer.wrap(b, off, len);
        write(buffer);
    }

    /**
     *
     * @param source
     * @throws IOException
     */
    public abstract void write(ByteBuffer source) throws IOException;


    /**
     * This method can be used to determine if data can be written without blocking.
     *
     * @return <code>true</code> if a write to this <code>HttpOutputStream</code>
     * will succeed, otherwise returns <code>false</code>.
     */
    public abstract boolean isReady();

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
    public abstract void setWriteListener(HttpWriteListener listener) throws Exception;
}
