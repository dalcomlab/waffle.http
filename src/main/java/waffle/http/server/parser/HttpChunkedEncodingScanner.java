/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server.parser;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * This class scan chunked encoding. The state following a below flow.
 *
 * <pre>
 *
 * State.Length ─────> State.Chunk ─────> State.Crlf
 *   │   ^                                  │
 *   │   │                                  │
 *   │   └──────────────────────────────────┘
 *   │
 *   └───────────────> State.Trailer
 *
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpChunkedEncodingScanner {

    private OutputStream output = null;
    private ByteBuffer buffer = null;
    private Listener listener;
    private State state = State.CHUNK_LENGTH;
    private int chunkLength;
    private byte[] ending = new byte[2];
    private int count = 0;

    /**
     *
     */
    public HttpChunkedEncodingScanner(final OutputStream output, final Listener listener) {
        listen(output, listener);
    }

    /**
     * @param output
     * @param listener
     */
    public void listen(final OutputStream output, final Listener listener) {
        this.output = output;
        this.listener = listener;
    }

    /**
     * @param chunkLength
     */
    public void setChunkLength(int chunkLength) {
        this.chunkLength = chunkLength;
    }

    /**
     * The method scan single line until meeting a end of line.
     *
     * @throws IOException
     */
    public void scan(final ByteBuffer buffer) throws Exception {
        this.buffer = buffer;
        // Each state should consumes a sequence of bytes to improve parsing performance.
        while (buffer.hasRemaining()) {
            switch (state) {
                case CHUNK_LENGTH:
                    scanLength();
                    break;
                case CHUNK_BODY:
                    scanChunk();
                    break;
                case CHUNK_TRAILER:
                    scanTrailer();
                    break;
                case CHUNK_CRLF:
                    scanCrlf();
                    break;
            }
        }
    }


    /**
     * @throws Exception
     */
    public void scanLength() throws Exception {
        byte b;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                count++;
                find = true;
                break;
            }
            if (b == '\r') {
                count++;
            }
        }

        int len = buffer.position() - head;

        if (find) {
            len -= count; // remove '/r/n' or '/n'
        }
        // flush buffer
        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            notifyComplete();
            // we assume that the listener that received the event to set the appropriate chunk length.
            if (chunkLength == 0)
                changeState(State.CHUNK_TRAILER);
            else
                changeState(State.CHUNK_BODY);
        }
    }

    /**
     * @throws Exception
     */
    public void scanChunk() throws Exception {
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            if (chunkLength == 0) {
                find = true;
                break;
            }
            buffer.get();
            chunkLength--;
        }

        int len = buffer.position() - head;

        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            changeState(State.CHUNK_CRLF);
            notifyComplete();
        }
    }


    /**
     * @throws Exception
     */
    public void scanTrailer() throws Exception {
        if (buffer.hasRemaining()) {
            int len = buffer.remaining();
            write(buffer.array(), buffer.position(), len);
            buffer.position(buffer.limit());
        }
    }

    /**
     * @throws Exception
     */
    public void scanCrlf() throws Exception {
        boolean find = false;
        while (buffer.hasRemaining() && count < 2) {
            ending[count] = buffer.get();
            if (count == 0) {
                // case of ending with '\n'
                if (ending[0] == '\n') {
                    find = true;
                    break;
                }

                if (ending[0] == '\r') {
                    count++;
                    continue;
                }

                throw new IllegalCharacterException(state, (char)ending[0]);
            }

            if (count == 1) {
                // case of ending with '\r\n'
                if (ending[1] == '\n') {
                    find = true;
                    break;
                }

                throw new IllegalCharacterException(state, (char)ending[1]);
            }
            count++;
        }

        if (find) {
            changeState(State.CHUNK_LENGTH);
        }
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    private void write(byte b[], int off, int len) throws IOException {
        if (output != null) {
            output.write(b, off, len);
        }
    }


    /**
     * @param state
     */
    public void changeState(final State state) {
        this.state = state;
        this.count = 0;
    }

    /**
     *
     */
    public void notifyComplete() throws Exception {
        if (listener != null) {
            listener.onComplete(this.state, output, this);
        }
    }


    public enum State {
        CHUNK_LENGTH,
        CHUNK_BODY,
        CHUNK_TRAILER,
        CHUNK_CRLF
    }


    /**
     *
     */
    public interface Listener {
        void onComplete(State state, OutputStream output, HttpChunkedEncodingScanner scanner) throws Exception;
    }

    /**
     *
     */
    public static class IllegalCharacterException extends Exception {
        private IllegalCharacterException(State state, char c) {
            super(String.format("Illegal character '%c' in state %s", c, state));
        }
    }
}
