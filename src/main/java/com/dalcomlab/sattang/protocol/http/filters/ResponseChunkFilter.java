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
public class ResponseChunkFilter extends ResponseFilter {

    private static final byte[] HEX = "0123456789abcdef".getBytes();
    private final ByteBuffer lastChunk = ByteBuffer.wrap("0\r\n\r\n".getBytes());
    private ByteBuffer length = ByteBuffer.allocate(8); // the maximum length is 8(eg. FFFF FFFF)
    private ByteBuffer crlf = ByteBuffer.allocate(2);
    private State state = State.CHUNK_LENGTH;

    /**
     *
     */
    public ResponseChunkFilter() {
        length.compact();
        crlf.compact();
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

        if (source.remaining() == 0 || !source.hasArray()) {
            return 0;
        }


        // write the length of the chunk.
        if (state.isLength()) {
            writeLength(channel, source);
        }

        // write the CR/LF at the end of the length.
        if (state.isLengthCrlf()) {
            writeLengthCrlf(channel);
        }

        int consume = 0;

        // write the chunk body.
        if (state.isBody()) {
            consume = writeBody(channel, source);
        }

        // write the CR/LF at the end of the chunk body.
        if (state.isBodyCrlf()) {
            writeBodyCrlf(channel);
        }

        return consume;
    }

    /**
     * @param source
     * @return
     * @throws IOException
     */
    private int writeLength(WriteChannel channel, ByteBuffer source) throws IOException {
        if (length.remaining() == 0) {
            fillLengthBuffer(length, source.remaining());
        }

        int consume = next().write(channel, length);
        if (length.remaining() == 0) {
            changeState(State.CHUNK_LENGTH_CRLF);
        }
        return consume;
    }

    /**
     * @param buffer
     * @param length
     * @throws IOException
     */
    private void fillLengthBuffer(ByteBuffer buffer, int length) throws IOException {
        buffer.clear();
        boolean leading = true;
        for (int i = 1; i <= 8; i++) {
            byte nibble = (byte) (length >> (32 - 4 * i));
            if (nibble == 0 && leading)
                continue;
            leading = false;
            buffer.put(HEX[nibble & 0x0F]);
        }
        buffer.flip();
    }

    /**
     * @return
     * @throws IOException
     */
    private int writeLengthCrlf(WriteChannel channel) throws IOException {
        int consume = writeCrlf(channel);
        if (crlf.remaining() == 0) {
            changeState(State.CHUNK_BODY);
        }
        return consume;
    }

    /**
     * @param source
     * @return
     * @throws IOException
     */
    private int writeBody(WriteChannel channel, ByteBuffer source) throws IOException {
        int consume = next().write(channel, source);
        if (source.remaining() == 0) {
            changeState(State.CHUNK_BODY_CRLF);
        }
        return consume;
    }

    /**
     * @return
     * @throws IOException
     */
    private int writeBodyCrlf(WriteChannel channel) throws IOException {
        int consume = writeCrlf(channel);
        if (crlf.remaining() == 0) {
            changeState(State.CHUNK_LENGTH);
        }

        return consume;
    }

    /**
     * @return
     * @throws IOException
     */
    private int writeCrlf(WriteChannel channel) throws IOException {
        if (crlf.remaining() == 0) {
            crlf.clear();
            crlf.put((byte) '\r');
            crlf.put((byte) '\n');
            crlf.flip();
        }
        return next().write(channel, crlf);
    }

    /**
     * Ends
     *
     * @throws IOException
     */
    @Override
    public void end(WriteChannel channel) throws IOException {
        if (next() != null) {
            while (lastChunk.hasRemaining()) {
                next().write(channel, lastChunk);
            }
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
     *
     */
    private enum State {
        CHUNK_LENGTH,
        CHUNK_LENGTH_CRLF,
        CHUNK_BODY,
        CHUNK_BODY_CRLF;

        public boolean isLength() {
            return this == CHUNK_LENGTH;
        }

        public boolean isLengthCrlf() {
            return this == CHUNK_LENGTH_CRLF;
        }

        public boolean isBody() {
            return this == CHUNK_BODY;
        }

        public boolean isBodyCrlf() {
            return this == CHUNK_BODY_CRLF;
        }
    }

}
