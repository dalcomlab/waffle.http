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
package com.dalcomlab.sattang.protocol.http.filters;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.protocol.http.ResponseFilter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseContentLengthFilter extends ResponseFilter {
    private final long length;
    private long written;

    /**
     *
     */
    public ResponseContentLengthFilter(long length) {
        this.length = length;
        this.written = 0;
    }


    /**
     * Writes the given buffer to this filters.
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

        if (written > length) {
            return ChannelConstants.END_OF_CHANNEL;
        }


        int consume = source.remaining();
        if ((written + consume) > length) {
            consume = (int) (length - written);
            written = length;
        }

        source.limit(source.position() + consume);
        consume = next().write(channel, source);
        return consume;
    }
}
