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


import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.function.Consumer;

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
public class HttpPartParser implements Parser {

    private final static byte[] HEADER_BOUNDARY = "\r\n".getBytes();

    private final HttpHeaderParser headerParser = new HttpHeaderParser();
    private final Stack<byte[]> stack = new Stack<>();
    private HttpPartScanner scanner;
    private HttpPartBuilder builder;
    private Consumer<HttpPart> listener;
    private byte[] boundary;
    private boolean closed = false;

    /**
     * @param boundary
     */
    public HttpPartParser(final byte[] boundary, final HttpPartBuilder builder, final Consumer<HttpPart> listener) {
        setup(boundary, builder, listener);
    }

    /**
     * @param boundary
     */
    public HttpPartParser(final byte[] boundary, final HttpPartBuilder builder, final List<HttpPart> parts) {
        final Consumer<HttpPart> listener = (part) -> {
            parts.add(part);
        };

        setup(boundary, builder, listener);
    }

    /**
     * @param boundary
     * @param builder
     * @param listener
     */
    private void setup(final byte[] boundary, final HttpPartBuilder builder, final Consumer<HttpPart> listener) {
        assert (boundary != null);
        this.boundary = boundary;
        this.builder = builder;
        this.listener = listener;
        this.scanner = new HttpPartScanner(getPreambleBoundary(), new ByteArrayOutputStream(), new PreambleHandler());
    }

    /**
     * Parses the multipart by using then given buffer.
     *
     * @param buffer
     */
    @Override
    public void parse(final ByteBuffer buffer) throws Exception {
        assert (buffer != null);
        if (closed) {
            throw new IllegalStateException("The parser is closed.");
        }

        scanner.scan(buffer);
    }

    /**
     *
     */
    @Override
    public void close() {
        if (!closed) {
        }
        closed = true;
    }

    /**
     * Gets the boundary started with a '--' (double dash)
     *
     * @return
     */
    private byte[] getPreambleBoundary() {
        int length = this.boundary.length;
        byte[] boundary = new byte[length + 2];
        boundary[0] = '-';
        boundary[1] = '-';
        System.arraycopy(this.boundary, 0, boundary, 2, length);
        return boundary;
    }

    /**
     * Gets the boundary started with a '\r\n--'
     *
     * @return
     */
    private byte[] getBodyBoundary() {
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
     * Changes the boundary used for parsing the multipart.
     *
     * @param boundary
     * @throws Exception
     */
    private void changeBoundary(final byte[] boundary) throws Exception {
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
    private HttpPart createPart(final HttpPartHeader header) {
        if (builder != null) {
            return builder.createPart(header);
        }
        return null;
    }

    /**
     * @param part
     */
    private void addPart(HttpPart part) {
        if (listener != null) {
            listener.accept(part);
        }
    }

    /**
     * This class handles the preamble section in the multipart.
     */
    private final class PreambleHandler implements HttpPartScanner.Listener {

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) throws Exception {
            scanner.listen(HEADER_BOUNDARY, new ByteArrayOutputStream(), new HeaderHandler());
        }
    }

    /**
     * This class handles the header section in the multipart.
     */
    private final class HeaderHandler implements HttpPartScanner.Listener {

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) throws Exception {
            // create header
            HttpPartHeader header = createHeader((ByteArrayOutputStream) output);

            if (header.isMultipart()) {
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
                scanner.listen(getPreambleBoundary(), new ByteArrayOutputStream(), new PreambleHandler());
            } else {
                HttpPart part = createPart(header);
                if (part != null) {
                    scanner.listen(getBodyBoundary(), part.getOutputStream(), new BodyHandler(part));
                } else {
                    scanner.listen(getBodyBoundary(), null, new BodyHandler(null));
                }
            }
        }

        public HttpPartHeader createHeader(ByteArrayOutputStream output) {
            Map<String, String> headers = headerParser.parse(output.toByteArray());
            return new HttpPartHeader(headers);
        }
    }

    /**
     * This class handles the body section in the multipart.
     */
    private final class BodyHandler implements HttpPartScanner.Listener {
        private HttpPart part;

        public BodyHandler(HttpPart part) {
            this.part = part;
        }

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) throws Exception {
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
                scanner.listen(HEADER_BOUNDARY, new ByteArrayOutputStream(), new HeaderHandler());
            } else {
                if (popBoundary()) {
                    scanner.listen(getBodyBoundary(), null, new BodyHandler(null));
                }
            }
        }
    }

}