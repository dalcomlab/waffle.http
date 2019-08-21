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
package com.dalcomlab.sattang.protocol.http.decoder;


import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;
import com.dalcomlab.sattang.protocol.http.form.HttpFormFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This parser travels multipart structure in the depth first search(DFS) manner.
 * <pre>
 *      ┌────┐
 * (1)..│    │
 *      └────┘
 *      ┌────┐
 *      │    │ ------------------ multipart/mixed
 *      └────┘
 *            ┌────┐
 * (2)........│    │
 *            └────┘
 *            ┌────┐
 *            │    │  ---------- multipart/mixed
 *            └────┘
 *                  ┌────┐
 * (3)..............│    │
 *                  └────┘
 *                  ┌────┐
 * (4)..............│    │
 *                  └────┘
 *                  ┌────┐
 * (5)..............│    │
 *                  └────┘
 *            ┌────┐
 * (6)........│    │
 *            └────┘
 *      ┌────┐
 * (7)..│    │
 *      └────┘
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpMultiPartFormDecoder implements HttpFormDecoder<Consumer<HttpForm>> {
    private final HttpHeaderDecoder headerDecoder = new HttpHeaderDecoder();
    private final Stack<byte[]> stack = new Stack<>();
    private Scanner scanner = null;
    private Consumer<HttpForm> listener = null;
    private Function<HttpHeader, HttpForm> builder = null;
    private byte[] boundary = null;
    private boolean closed = false;


    /**
     *
     * @param boundary
     */
    public HttpMultiPartFormDecoder(byte[] boundary) {
        this.boundary = boundary;
    }

    /**
     *
     * @param builder
     */
    public void setFormBuilder(Function<HttpHeader, HttpForm> builder) {
        this.builder = builder;
    }
    /**
     *
     * @param listener
     * @return
     */
    @Override
    public HttpMultiPartFormDecoder listen(Consumer<HttpForm> listener) {
        assert (boundary != null);
        this.listener = listener;
        this.scanner = new Scanner(createPreambleBoundary(), new ByteArrayOutputStream(), new PreambleHandler());
        return this;
    }

    /**
     *
     * @param parts
     * @return
     */
    public HttpMultiPartFormDecoder listen(List<HttpForm> parts) {
        final Consumer<HttpForm> listener = (part) -> parts.add(part);
        return listen(listener);
    }

    /**
     * Decodes the multipart by using then given buffer.
     * @param buffer
     * @return
     * @throws Exception
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        assert (buffer != null);
        if (closed) {
            throw new IllegalStateException("The decoder is closed.");
        }

        scanner.scan(buffer);
        return true;
    }

    /**
     *
     */
    public void close() {
        if (!closed) {
        }
        closed = true;
    }

    /**
     * Creates the boundary started with a '--' (double dash)
     *
     * @return
     */
    private byte[] createPreambleBoundary() {
        int length = this.boundary.length;
        byte[] boundary = new byte[length + 2];
        boundary[0] = '-';
        boundary[1] = '-';
        System.arraycopy(this.boundary, 0, boundary, 2, length);
        return boundary;
    }

    /**
     * Creates the boundary started with a '\r\n--'
     *
     * @return
     */
    private byte[] createBodyBoundary() {
        int length = this.boundary.length;
        byte[] boundary = new byte[length + 4];
        boundary[0] = '\r';
        boundary[1] = '\n';
        boundary[2] = '-';
        boundary[3] = '-';
        System.arraycopy(this.boundary, 0, boundary, 4, length);
        return boundary;
    }

    /**
     * Changes the boundary used for parsing the multipart. The given boundary
     * must start with a '--' (double dash). The length of the given boundary
     * must be equal to the length of the previous boundary.
     *
     * @param boundary
     * @throws Exception
     */
    private void changeBoundary(byte[] boundary) throws Exception {
        assert (boundary != null);
        if (this.boundary.length != boundary.length) {
            throw new Exception("The length of a boundary can not be changed.");
        }
        this.boundary = boundary;
    }

    /**
     * Push the current boundary into the stack for parsing nested multipart.
     */
    private void pushBoundary() {
        stack.push(this.boundary);
    }

    /**
     * @return
     */
    private boolean popBoundary() {
        if (stack.empty()) {
            return false;
        }
        boundary = stack.pop();
        return true;
    }

    /**
     * @param header
     * @return
     */
    private HttpForm createPart(HttpHeader header) {
        if (builder != null) {
            return builder.apply(header);
        }
        return new HttpFormFile(header);
    }

    /**
     * @param part
     */
    private void addPart(HttpForm part) {
        if (listener != null) {
            listener.accept(part);
        }
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
        void onComplete(int type, OutputStream output, Scanner scanner) throws Exception;
    }


    /**
     *
     */
    public class Scanner {

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
        public Scanner(byte[] boundary, OutputStream output, Listener listener) {
            listen(boundary, output, listener);
        }

        /**
         * @param boundary
         * @param output
         * @param listener
         */
        public void listen(byte[] boundary, OutputStream output, Listener listener) {
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
        private int[] kmp(byte[] boundary) {
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
        public void scan(ByteBuffer buffer) throws Exception {
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
                        index = table[index - 1];
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
             * Writes consumed bytes into an output streams.
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
                write(buffer.array(), buffer.arrayOffset() + head, len);
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

    }

    /**
     * This class handles the preamble section in the multipart.
     */
    private final class PreambleHandler implements Listener {

        @Override
        public void onComplete(int type, OutputStream output, Scanner scanner) throws Exception {
            scanner.listen("\r\n".getBytes(), new ByteArrayOutputStream(), new HeaderHandler());
        }
    }

    /**
     * This class handles the header section in the multipart.
     */
    private final class HeaderHandler implements Listener {

        @Override
        public void onComplete(int type, OutputStream output, Scanner scanner) throws Exception {
            // create header
            HttpHeader header = createHeader((ByteArrayOutputStream) output);

            if (header.getContentType() != null && header.getContentType().startsWith("multipart/")) {
                // push nested multipart boundary into the stack.
                //
                // │          │
                // ├──────────┤
                // │ boundary │ <-- nested multipart boundary
                // ├──────────┤
                // │ boundary │ <-- root multipart boundary
                // └──────────┘
                //    stack
                pushBoundary();
                changeBoundary(header.getBoundary());
                scanner.listen(createPreambleBoundary(), new ByteArrayOutputStream(), new PreambleHandler());
            } else {
                HttpForm part = createPart(header);
                if (part != null) {
                    scanner.listen(createBodyBoundary(), part.getOutputStream(), new BodyHandler(part));
                } else {
                    scanner.listen(createBodyBoundary(), null, new BodyHandler(null));
                }
            }
        }

        public HttpHeader createHeader(ByteArrayOutputStream output) {
            final Map<String, String> headers = new HashMap<>();
            headerDecoder.listen((name, value) -> {
                headers.put(name, value);
            });
            try {
                headerDecoder.decode(output.toByteArray());
                headerDecoder.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return new HttpHeader(headers, true);
        }
    }

    /**
     * This class handles the body section in the multipart.
     */
    private final class BodyHandler implements Listener {
        private HttpForm part;

        public BodyHandler(HttpForm part) {
            this.part = part;
        }

        @Override
        public void onComplete(int type, OutputStream output, Scanner scanner) throws Exception {
            if (part != null) {
                addPart(part);
            }
            // There are two boundaries.
            //
            //  boundary : --0x123456
            //
            //  case 1) section boundary
            //         ┌───────────────────┐
            //         │ --0x123456\r\n    │
            //         └───────────────────┘
            //
            //  case 2) final boundary (end with '--')
            //         ┌───────────────────┐
            //         │ --0x123456--\r\n  │
            //         └───────────────────┘
            //
            if (type == 0) {
                scanner.listen("\r\n".getBytes(), new ByteArrayOutputStream(), new HeaderHandler());
            } else {
                if (popBoundary()) {
                    scanner.listen(createBodyBoundary(), null, new BodyHandler(null));
                }
            }
        }
    }

}