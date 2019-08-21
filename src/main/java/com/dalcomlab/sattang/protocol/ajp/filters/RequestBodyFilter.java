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

package com.dalcomlab.sattang.protocol.ajp.filters;

import com.dalcomlab.sattang.common.ByteBufferUtils;
import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.read.ReadChannel;
import com.dalcomlab.sattang.net.io.read.filters.ReadAbstractFilter;
import com.dalcomlab.sattang.protocol.ajp.AjpPacket;
import com.dalcomlab.sattang.protocol.ajp.decoder.AjpDataDecoder;

import java.io.IOException;
import java.nio.ByteBuffer;

// https://github.com/javaee/grizzly/blob/master/modules/http-ajp/src/main/java/org/glassfish/grizzly/http/ajp/AjpHandlerFilter.java

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class RequestBodyFilter extends ReadAbstractFilter {
    private ByteBuffer packet = ByteBuffer.allocate(1024 * 8);
    private ByteBuffer chunk = ByteBuffer.allocate(1024 * 8);
    private AjpDataDecoder decoder = new AjpDataDecoder();
    private boolean isEnd = false;
    private long contentLength = 0;
    private boolean needSendGetBodyChunk = false;

    /**
     * @param contentLength
     */
    public RequestBodyFilter(long contentLength) {
        this.contentLength = contentLength;
        this.decoder.listen(new AjpDataDecoder.Listener() {
            @Override
            public void setPacketSize(int size) {
                if (size == 0) {
                    isEnd = true;
                }
            }

            @Override
            public void setDataSize(int size) {
                if (size == 0) {
                    isEnd = true;
                }
            }

            @Override
            public void addData(ByteBuffer buffer) {
                chunk.put(buffer);
            }
        });

        if (contentLength > 0) {
            // if content-length > 0 - the first data chunk will come immediately.
            needSendGetBodyChunk = false;
        } else {
            // if content-length < 0 - we don't know if there is any content in message.
            // but we're sure no message is following immediately.
            // send need more data message.
            needSendGetBodyChunk = true;
        }
    }

    /**
     * @param channel
     * @param out
     * @return
     * @throws IOException
     */
    @Override
    public int read(ReadChannel channel, ByteBuffer out) throws IOException {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (needSendGetBodyChunk) {
            sendGetBodyChunk(channel);
        }

        return readBodyChunk(channel, out);
    }

    /**
     * @param channel
     * @param out
     */
    private int readBodyChunk(ReadChannel channel, ByteBuffer out) throws IOException {
        // TODO : thinking that the socket sends less data.
        chunk.clear();
        packet.clear();
        next().read(channel, packet);
        int consume = 0;
        try {
            packet.flip();
            if (decoder.decode(packet)) {
                chunk.flip();
                consume = ByteBufferUtils.copy(chunk, out);
                if (isEnd) {
                    channel.end();
                    return consume;
                } else {
                    decoder.reset();
                }
                needSendGetBodyChunk = true;
            } else {
                needSendGetBodyChunk = false;
            }
        } catch (Exception e) {
            channel.error(e);
        }
        return consume;
    }

    /**
     * @param channel
     */
    private void sendGetBodyChunk(ReadChannel channel) {
        ByteBuffer getBodyChunk = AjpPacket.GET_BODY_CHUNK.duplicate();
        channel.getConnection().getWriteChannel().write(getBodyChunk, false);
    }

    /**
     *
     * @return
     */
    private boolean isEndOfChannel() {
        if (next() == null) {
            return true;
        }

        if (isEnd) {
            return true;
        }

        if (contentLength == 0) {
            return true;
        }

        return false;
    }
}