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
public class RequestChunkFilter extends RequestFilter {
    private ByteBuffer buffer = ByteBuffer.allocate(1024);
    private State state = State.CHUNK_LENGTH;
    private long remaining = 0;

    /**
     *
     */
    public RequestChunkFilter() {
        buffer.flip();
    }

    /**
     * @param channel
     * @param out
     * @return
     * @throws IOException
     */
    @Override
    public int read(ReadChannel channel, ByteBuffer out) throws IOException {
        if (next() == null) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        // if the buffer is empty, read a buffer from a next filters.
        int consume = fillBuffer(channel);
        if (consume == 0 || consume == -1) {
            return consume;
        }


        while (out.hasRemaining() && buffer.hasRemaining()) {
            switch (state) {
                case CHUNK_LENGTH: {
                    consume = readLength(channel);
                    break;
                }

                case CHUNK_EXTENSION: {
                    consume = readExtension(channel);
                    break;
                }

                case CHUNK_BODY: {
                    consume = readBody(channel, out);
                    break;
                }

                case CHUNK_CRLF: {
                    consume = readCrlf(channel);
                    break;
                }

                case CHUNK_TRAILER: {
                    consume = readTrailer(channel);
                    return 0;
                }
            }
        }


        return consume;
    }

    /**
     * Reads the buffer size from the header.
     *
     * @param channel
     * @return
     */
    private int readLength(ReadChannel channel) {
        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if ((b >= '0' && b <= '9') || (b >= 'a' && b <= 'f') || (b >= 'A' && b <= 'F')) {
                remaining <<= 4;
                remaining += Character.digit((char) b, 16);
            } else {
                if (b == ' ') {
                    continue;
                }

                if (b == ';') {
                    changeState(State.CHUNK_EXTENSION);
                    break;
                } else if (b == '\n') {
                    if (remaining == 0)
                        changeState(State.CHUNK_TRAILER);
                    else
                        changeState(State.CHUNK_BODY);
                    break;
                }
            }
        }
        return 0;
    }


    /**
     * Reads the extension from the header.
     *
     * @param channel
     * @return
     */
    private int readExtension(ReadChannel channel) {
        while (buffer.hasRemaining()) {
            if (buffer.get() == '\n') {
                if (remaining == 0)
                    changeState(State.CHUNK_TRAILER);
                else
                    changeState(State.CHUNK_BODY);
                break;
            }
        }
        return 0;
    }


    /**
     * Reads the buffer body.
     *
     * @param channel
     * @param out
     * @return
     * @throws IOException
     */
    private int readBody(ReadChannel channel, ByteBuffer out) throws IOException {

        int consume = 0;
        // read the buffer from a buffered
        if (remaining > 0 && buffer.hasRemaining()) {
            consume = min((int) remaining, buffer.remaining(), out.remaining());
            out.put(buffer.array(), buffer.arrayOffset(), consume);

            buffer.position(buffer.position() + consume);
            remaining -= consume;
        }

        int limit = out.limit();

        //
        // don't call the updateChunk() method to update the empty buffer.
        // we use directly the given buffer to reduce unnecessary copying.
        //
        while (remaining > 0 && out.hasRemaining()) {
            consume = Math.min((int) remaining, out.remaining());

            // change the limit of the buffer
            out.limit(out.position() + consume);

            consume = next().read(channel, out);

            if (consume == 0 || consume == -1) {
                break;
            }

            remaining -= consume;
        }

        out.limit(limit);

        if (remaining == 0) {
            changeState(State.CHUNK_CRLF);
        }

        return consume;
    }


    /**
     * @param channel
     * @return
     */
    private int readCrlf(ReadChannel channel) {
        while (buffer.hasRemaining()) {
            if (buffer.get() == '\n') {
                changeState(State.CHUNK_LENGTH);
                break;
            }
        }
        return 0;
    }

    /**
     * Reads the trailer.
     *
     * @param channel
     * @return
     */
    private int readTrailer(ReadChannel channel) {
        return 0;
    }


    /**
     * @param channel
     * @return
     * @throws IOException
     */
    private int fillBuffer(ReadChannel channel) throws IOException {
        int bytes = 0;
        // if the buffer is empty, read a buffer from a next filters.
        if (!buffer.hasRemaining()) {
            buffer.clear();
            bytes = next().read(channel, buffer);
            if (bytes == -1 || bytes == 0) {
                return bytes;
            }
            buffer.flip();
        }
        return bytes;
    }


    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
    }

    /**
     * @param a
     * @param b
     * @param c
     * @return
     */
    private int min(int a, int b, int c) {
        return Math.min(Math.min(a, b), c);
    }

    private enum State {
        CHUNK_LENGTH,
        CHUNK_EXTENSION,
        CHUNK_BODY,
        CHUNK_CRLF,
        CHUNK_TRAILER
    }
}
