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

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.filters.WriteAbstractFilter;
import com.dalcomlab.sattang.protocol.ajp.AjpConstants;
import com.dalcomlab.sattang.protocol.ajp.AjpDataWriter;
import com.dalcomlab.sattang.protocol.ajp.AjpPacket;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseBodyFilter extends WriteAbstractFilter {
    private ByteBuffer bodyBuffer = null;
    private ByteBuffer endBuffer = null;
    private State state = State.RESPONSE_BODY;

    /**
     *
     */
    public ResponseBodyFilter() {
    }

    /**
     * Writes the given buffer to this filters.
     *
     * @param source
     * @return
     * @throws IOException
     */
    @Override
    public int write(WriteChannel channel, ByteBuffer source) throws IOException {
        if (isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        int consume = 0;
        while (source.hasRemaining()) {
            consume = writeBody(channel, source);
        }
        return consume;
    }

    /**
     * @param source
     * @return
     * @throws IOException
     */
    private int writeBody(WriteChannel channel, ByteBuffer source) throws IOException {
        if (bodyBuffer == null || bodyBuffer.remaining() == 0) {
            fillBodyBuffer(source);
        }

        int consume = 0;
        if (bodyBuffer.hasRemaining()) {
            consume = next().write(channel, bodyBuffer);
        }

        return consume;
    }


    /**
     * @param source
     * @throws IOException
     */
    private void fillBodyBuffer(ByteBuffer source) throws IOException {
        int packetSize = 1024 * 8;
        if (bodyBuffer == null) {
            bodyBuffer = ByteBuffer.allocate(packetSize);
        }

        bodyBuffer.clear();

        AjpDataWriter writer = new AjpDataWriter(bodyBuffer);

        writer.writeInt(0x4142);
        writer.writeInt(0);
        writer.writeByte(AjpConstants.JK_AJP13_SEND_BODY_CHUNK);

        int length = source.remaining();
        if (source.remaining() > packetSize - 10) {
            length = packetSize - 10;
        }

        writer.writeInt(length);
        writer.writeBytes(source.array(), source.position(), length);
        source.position(source.position() + length);

        writer.writeByte((byte) 0);
        writer.flush();

        bodyBuffer.flip();
    }


    /**
     * @return
     * @throws IOException
     */
    private int writeEnd(WriteChannel channel) throws IOException {
        if (endBuffer == null) {
            fillEndBuffer();
        }

        int consume = 0;
        if (endBuffer.hasRemaining()) {
            consume = next().write(channel, endBuffer);
        }

        if (endBuffer.remaining() == 0) {
            changeState(State.RESPONSE_COMMITTED);
        }

        return consume;
    }

    /**
     *
     */
    private void fillEndBuffer(){
        endBuffer = AjpPacket.END_RESPONSE_CHUNK.duplicate();
    }

    /**
     * Ends
     *
     * @throws IOException
     */
    @Override
    public void end(WriteChannel channel) throws IOException {
        if (next() == null) {
            return;
        }

        writeEnd(channel);

        if (next() != null) {
            next().end(channel);
        }
    }


    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
    }


    /**
     * @return
     */
    private boolean isEndOfChannel() {
        if (next() == null) {
            return true;
        }

        return false;
    }

    /**
     *
     */
    private enum State {
        RESPONSE_BODY,
        RESPONSE_END,
        RESPONSE_COMMITTED;

        public boolean isBody() {
            return this == RESPONSE_BODY;
        }

        public boolean isEnd() {
            return this == RESPONSE_END;
        }

        public boolean isCommitted() {
            return this == RESPONSE_COMMITTED;
        }

    }

}
