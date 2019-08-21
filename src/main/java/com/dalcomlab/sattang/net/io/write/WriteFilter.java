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

import com.dalcomlab.sattang.net.Filter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface WriteFilter extends Filter<WriteFilter> {

    /**
     * Writes the given buffer to this filter.
     * This method will be called in the {@link WriteChannel#write}
     * method to filter a written data.
     * <p>
     * If there is an next filter and it is necessary, call the {@linke #write} method
     * of the next filter.
     *
     * @param channel
     * @param source
     * @return
     * @throws IOException
     */
    default int write(WriteChannel channel, ByteBuffer source) throws IOException {
        if (next() != null) {
            return next().write(channel, source);
        }
        return 0;
    }

    /**
     * Flushes the remaining buffer in this filter.
     * This method will be called in the {@link WriteChannel#flush}
     * method to flush the remaining buffer.
     * <p>
     * If there is an next filter and it is necessary, call the {@linke #flush} method
     * of the next filter.
     *
     * @param channel
     * @throws IOException
     */
    default void flush(WriteChannel channel) throws IOException {
        if (next() != null) {
            next().flush(channel);
        }
    }


    /**
     * Ends
     * @param channel
     * @throws IOException
     */
    default void end(WriteChannel channel) throws IOException {
        if (next() != null) {
            next().end(channel);
        }
    }
}
