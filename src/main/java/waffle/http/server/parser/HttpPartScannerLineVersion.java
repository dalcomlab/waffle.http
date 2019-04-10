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
 * <pre>
 *
 *       ┌─────────────────────────────────────┐
 *       │                                     │
 *       *                                     │
 * State.Boundary ─────> State.Ending ─────> State.Line
 *       │                                     ^
 *       │                                     │
 *       └─────────────────────────────────────┘
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpPartScannerLineVersion {

    private byte[] boundary = null;
    private byte[] ending = new byte[4];
    private State state = State.BOUNDARY;
    private int count = 0;
    private OutputStream output = null;
    private ByteBuffer buffer = null;
    private Listener listener;

    /**
     * @param listener
     */
    public HttpPartScannerLineVersion(final byte[] boundary, final OutputStream output, final Listener listener) {
        listen(boundary, output, listener);
    }

    /**
     * @param boundary
     * @param output
     * @param listener
     */
    public void listen(final byte[] boundary, final OutputStream output, final Listener listener) {
        assert (boundary != null);
        this.boundary = boundary;
        this.output = output;
        this.listener = listener;
    }

    /**
     * The method scan single line until meeting a end of line.
     *
     * @throws IOException
     */
    public void scan(final ByteBuffer buffer) throws IOException {
        this.buffer = buffer;
        // Each state should consumes a sequence of bytes to improve parsing performance.
        while (buffer.hasRemaining()) {
            switch (state) {
                case BOUNDARY:
                    scanBoundary();
                    break;
                case ENDING:
                    scanBoundaryEnding();
                    break;
                case LINE:
                    scanLine();
                    break;
            }
        }
    }

    /**
     * @throws IOException
     */
    private void scanBoundary() throws IOException {
        if (boundary.length == 0) {
            scanBoundaryEnding();
            return;
        }

        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (boundary[count] != b) {
                write(boundary, 0, count);
                write(b);

                if (b == '\n')
                    changeState(State.BOUNDARY);
                else
                    changeState(State.LINE);

                return;
            }

            count++;
            if (count == boundary.length) {
                changeState(State.ENDING);
                return;
            }
        }

    }

    /**
     * if a boundary found. checks an ending 4 bytes to confirm a real boundary or a final boundary
     * ended with a '--'.
     * <p>
     * case 1) boundary + '\n'        : maybe an informal case
     * case 2) boundary + '\r\n'
     * case 3) boundary + '--\n'      : maybe an informal case
     * case 4) boundary + '--\r\n'
     *
     * @throws IOException
     */
    private void scanBoundaryEnding() throws IOException {
        byte b;
        boolean more = false;
        while (buffer.hasRemaining() && count < 4) {
            b = buffer.get();
            ending[count] = b;
            if (count == 0) {
                // case 1) boundary + '\n' : maybe an informal case
                if (b == '\n') {
                    complete(0);
                    return;
                }

                if (b == '\r' || b == '-') {
                    count++;
                    more = true;
                    continue;
                }
                break;

            } else if (count == 1) {
                // case 2) boundary + '\r\n'
                if (ending[0] == '\r' && ending[1] == '\n') {
                    complete(0);
                    return;
                }

                if (ending[0] == '-' && ending[1] == '-') {
                    count++;
                    more = true;
                    continue;
                }
                break;

            } else if (count == 2) {
                // case 3) boundary + '--\n' : maybe an informal case
                if (b == '\n') {
                    complete(1);
                    return;
                }

                if (b == '\r') {
                    count++;
                    more = true;
                    continue;
                }
                break;

            } else if (count == 3) {
                // case 4) boundary + '--\r\n'
                if (b == '\n') {
                    complete(1);
                    return;
                }
                break;
            }
        }

        if (!more) {
            write(boundary, 0, boundary.length);
            write(ending, 0, count + 1);
            changeState(State.LINE);
        }
    }

    /**
     * @throws IOException
     */
    private void scanLine() throws IOException {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                changeState(State.BOUNDARY);
                break;
            }
        }

        int len = buffer.position() - head;
        if (len > 0)
            write(buffer.array(), head, len);
    }

    /**
     * @param state
     */
    private void changeState(final State state) {
        this.state = state;
        this.count = 0;
    }

    /**
     * @param b
     * @throws IOException
     */
    private void write(byte b) throws IOException {
        if (output != null) {
            output.write(b);
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
     * @param type
     */
    private void complete(int type) {
        if (listener != null) {
            listener.onComplete(type, output, this);
        }
        changeState(State.BOUNDARY);
    }


    private enum State {
        BOUNDARY,
        ENDING,
        LINE
    }


    /**
     *
     */
    public interface Listener {
        void onComplete(int type, OutputStream output, HttpPartScannerLineVersion scanner);
    }
}
