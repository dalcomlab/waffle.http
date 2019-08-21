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

import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Decoder<T> {
    /**
     * @param o
     */
    Decoder<T> listen(T o);

    /**
     * Decodes the {@link ByteBuffer}.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    boolean decode(ByteBuffer buffer) throws Exception;


    /**
     * Decodes the byte array.
     *
     * @param buffer
     * @return
     */
    default boolean decode(byte[] buffer) throws Exception {
        return decode(ByteBuffer.wrap(buffer));
    }

    /**
     * Decodes the byte array.
     *
     * @param buffer
     */
    default boolean decode(byte[] buffer, int offset, int len) throws Exception {
        return decode(ByteBuffer.wrap(buffer, offset, len));
    }

    /**
     * Decodes the {@link InputStream}.
     *
     * @param input
     */
    default boolean decode(InputStream input) throws Exception {
        return decode(input, 1024 * 8);
    }

    /**
     * Decodes the {@link InputStream}.
     *
     * @param input
     * @param bufferSize
     */
    default boolean decode(InputStream input, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        int read;
        while ((read = input.read(buffer, 0, bufferSize)) != -1) {
            if (!decode(buffer, 0, read)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Closes the decode operation and clear all.
     *
     * @throws Exception
     */
    default void close() throws Exception {

    }
}
