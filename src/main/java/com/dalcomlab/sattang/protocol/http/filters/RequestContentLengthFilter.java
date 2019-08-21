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
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.protocol.http.RequestFilter;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class RequestContentLengthFilter extends RequestFilter {
    private final long length;
    private long remain;

    /**
     *
     */
    public RequestContentLengthFilter(long length) {
        if (length < 0) {
            this.length = 0;
        } else {
            this.length = length;
        }
        this.remain = this.length;
    }

    /**
     * @param channel
     * @param dst
     * @return
     * @throws IOException
     */
    @Override
    public int read(ReadChannel channel, ByteBuffer dst) throws IOException {
        if (next() == null) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (remain <= 0) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        // limit content length
        if (remain < dst.remaining()) {
            dst.limit((int) remain);
        }

        int consume = next().read(channel, dst);

        if (consume == -1) {
            endChannel(channel);
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (remain > consume) {
            remain -= consume;
        } else {
            consume = (int) remain;
            remain = 0;
            dst.position(consume);
        }

        if (remain == 0) {
            endChannel(channel);
        }

        return consume;
    }

    /**
     *
     */
    private void endChannel(ReadChannel channel) {
        if (channel != null) {
            channel.end();
        }
    }
}
