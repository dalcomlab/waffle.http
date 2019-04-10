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
 *                  ┌───────────────────────────────>
 *                  │
 * State.Boundary ──┴────> State.Partial ─────> State.Ending
 *                              │                    │
 *       <──────────────────────┘                    │
 *                                                   │
 *                                                   │
 *       <───────────────────────────────────────────┘
 *
 *
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpPartScanner {

    private byte[] boundary = null;
    private int[] table = null; // kmp table(Knuth–Morris–Pratt algorithm)
    private byte[] ending = new byte[4];
    private State state = State.PART_BOUNDARY;
    private int index = 0;
    private OutputStream output = null;
    private ByteBuffer buffer = null;
    private Listener listener;

    /**
     * @param listener
     */
    public HttpPartScanner(final byte[] boundary, final OutputStream output, final Listener listener) {
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
        this.table = kmp(boundary);
        this.output = output;
        this.listener = listener;
    }


    /**
     * Builds a KMP((Knuth–Morris–Pratt) table.
     *
     * @param boundary
     */
    private int[] kmp(final byte[] boundary) {
        int[] table = new int[boundary.length];
        table[0] = 0;
        int c = 0;  // candidate
        for (int i = 1; i < boundary.length; i++) {
            while (c > 0 && boundary[i] != boundary[c]) {
                c = table[c - 1];
            }
            if (boundary[i] == boundary[c]) {
                c++;
                table[i] = c;
            }
        }
        return table;
    }

    /**
     * @throws IOException
     */
    public void scan(final ByteBuffer buffer) throws Exception {
        this.buffer = buffer;
        while (buffer.hasRemaining()) {
            switch (state) {
                case PART_BOUNDARY_PARTIAL:
                    scanBoundaryPartial();
                    break;
                case PART_BOUNDARY:
                    scanBoundary();
                    break;
                case PART_BOUNDARY_ENDING:
                    scanBoundaryEnding();
                    break;
            }
        }
    }

    /**
     * There are two matching cases.
     *
     * <pre>
     *
     *  boundary : --0x123456
     *
     * case 1) perfect matching
     *       ┌────────────────┐ ┌────────────────┐
     *       │ data      --0x │ │ 123456         │
     *       └────────────────┘ └────────────────┘
     *                 ┌───────────────────┐
     *                 │ --0x     123456   │
     *                 └───────────────────┘
     *
     * case 2) partial matching
     *       ┌────────────────┐ ┌────────────────┐
     *       │ data      --0x │ │ 123456         │
     *       └────────────────┘ └────────────────┘
     *                 ┌───────────────────┐
     *                 │ --0x     123ABC   │
     *                 └───────────────────┘
     *                               ^
     *                             break
     * </pre>
     *
     * @throws IOException
     */
    private void scanBoundaryPartial() throws IOException {
        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (boundary[index] == b) {
                index++;
                // 1 case) perfect matching
                if (index == boundary.length) {
                    changeStateBoundaryEnding();
                    break;
                }
            } else {
                // 2 case) partial matching
                write(boundary, 0, index);
                write(b);
                changeStateBoundary();
                break;
            }
        }
    }


    /**
     * @throws IOException
     */
    private void scanBoundary() throws IOException {
        byte b;
        boolean changeStateEnding = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (boundary[index] == b) {
                index++;
                // change a state if it was boundary.
                if (index == boundary.length) {
                    changeStateEnding = true;
                    break;
                }
            } else {
                // search an index on a kmp table for next pattern matching.
                index = table[index];
                while (index > 0 && boundary[index] != b) {
                    index = table[index];
                }
                if (boundary[index] == b) {
                    index++;
                }
            }
        }

        int len = buffer.position() - head;

        // remove a boundary
        len -= index;

        /**
         * Writes consumed bytes into an output stream.
         *
         * <pre>
         *
         *  boundary : --0x123456
         *
         *       ┌───────────────────────────────────┐
         *       │ data             --0x  123456     │
         *       └───────────────────────────────────┘
         *                        ┌─────────────┐
         *                        │ --0x  123456│
         *                        └─────────────┘
         *       │<──────────────>|
         *             write
         * </pre>
         *
         */
        if (len > 0) {
            write(buffer.array(), head, len);
        }

        if (changeStateEnding) {
            changeStateBoundaryEnding();
        } else {
            // case of a boundary partial matching
            if (index > 0) {
                changeStateBoundaryPartial();
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
    private void scanBoundaryEnding() throws Exception {
        byte b;
        boolean needMore = false;
        while (buffer.hasRemaining() && index < 4) {
            needMore = false;
            b = buffer.get();
            ending[index] = b;
            if (b == '\n') {
                if (index > 1)
                    notifyComplete(1);
                else
                    notifyComplete(0);
                return;
            }

            if (index == 0) {
                if (b == '\r' || b == '-') {
                    index++;
                    needMore = true;
                    continue;
                }
                break;

            } else if (index == 1) {
                if (b == '-') {
                    index++;
                    needMore = true;
                    continue;
                }
                break;

            } else if (index == 2) {
                if (b == '\r') {
                    index++;
                    needMore = true;
                    continue;
                }
                break;
            }
        }


        if (!needMore) {
            write(boundary, 0, boundary.length);
            write(ending, 0, index + 1);
            changeStateBoundary();
        }

    }


    /**
     *
     */
    private void changeStateBoundary() {
        this.index = 0;
        this.state = State.PART_BOUNDARY;
    }

    /**
     *
     */
    private void changeStateBoundaryEnding() {
        this.index = 0;
        this.state = State.PART_BOUNDARY_ENDING;
    }

    /**
     *
     */
    private void changeStateBoundaryPartial() {
        this.state = State.PART_BOUNDARY_PARTIAL;
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
    private void notifyComplete(int type) throws Exception {
        if (listener != null) {
            listener.onComplete(type, output, this);
        }
        changeStateBoundary();
    }


    private enum State {
        PART_BOUNDARY_PARTIAL,
        PART_BOUNDARY,
        PART_BOUNDARY_ENDING
    }

    /**
     *
     */
    public interface Listener {
        void onComplete(int type, OutputStream output, HttpPartScanner scanner) throws Exception;
    }
}
