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
package com.dalcomlab.sattang.net.io.write.filters;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class WriteBufferFilter extends WriteAbstractFilter {
    private final ByteBuffer buffer;

    /**
     *
     */
    public WriteBufferFilter(int size) {
        if (size <= 0) {
            buffer = ByteBuffer.allocate(1024);
        } else {
            buffer = ByteBuffer.allocate(size);
        }
    }

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
    @Override
    public int write(WriteChannel channel, ByteBuffer source) throws IOException {
        if (next() == null) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (source.remaining() == 0 || !source.hasArray()) {
            return 0;
        }

        int consume = source.remaining();

        if (buffer.remaining() > source.remaining()) {
            buffer.put(source);
        } else {
            int remaining = buffer.remaining();
            buffer.put(source.array(), source.arrayOffset() + source.position(), remaining);
            flushBuffer(channel);
            source.position(source.position() + remaining);
            consume = remaining;
        }

        return consume;
    }

    /**
     * Flushes the remaining buffer in this filter.
     * This method will be called in the {@link WriteChannel#flush}
     * method to flush the remaining buffer.
     * <p>
     * If there is an next filter and it is necessary, call the {@linke #flush} method
     * of the next filter.
     *
     * @throws IOException
     */
    @Override
    public void flush(WriteChannel channel) throws IOException {
        flushBuffer(channel);
        if (next() != null) {
            next().flush(channel);
        }
    }

    /**
     * @return
     * @throws IOException
     */
    private int flushBuffer(WriteChannel channel) throws IOException {
        buffer.flip();
        while (buffer.hasRemaining()) {
            next().write(channel, buffer);
        }
        buffer.clear();
        return 0;
    }


}
