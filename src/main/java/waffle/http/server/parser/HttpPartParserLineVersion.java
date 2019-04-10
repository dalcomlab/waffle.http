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
public class HttpPartParserLineVersion implements Parser {

    private final static byte[] EMPTY_LINE = "".getBytes();
    private final HttpHeaderParser headerParser = new HttpHeaderParser();
    private final Stack<byte[]> stack = new Stack<>();
    private HttpPartScanner scanner;
    private HttpPartBuilder builder;
    private Consumer<HttpPart> listener;
    private byte[] boundary;
    private boolean completed = false;

    /**
     * @param boundary
     */
    public HttpPartParserLineVersion(final byte[] boundary, final HttpPartBuilder builder, final Consumer<HttpPart> listener) {
        setup(boundary, builder, listener);
    }

    /**
     * @param boundary
     */
    public HttpPartParserLineVersion(final byte[] boundary, final HttpPartBuilder builder, final List<HttpPart> parts) {
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
        this.boundary = createDoubleDashBoundary(boundary);
        this.builder = builder;
        this.listener = listener;
        this.scanner = new HttpPartScanner(this.boundary, null, new PreambleHandler());
    }

    /**
     * Parses the multipart by using then given buffer.
     *
     * @param buffer
     */
    @Override
    public void parse(final ByteBuffer buffer) throws Exception {
        assert (buffer != null);
        if (completed) {
            throw new IllegalStateException("The parser is already completed.");
        }

        scanner.scan(buffer);
    }

    /**
     *
     */
    @Override
    public void close() {
        if (!completed) {
        }
        completed = true;
    }

    /**
     * Creates the boundary started with a double dash '--'.
     *
     * @param boundary
     * @return
     */
    private byte[] createDoubleDashBoundary(final byte[] boundary) {
        byte[] doubleDashBoundary = new byte[boundary.length + 2];
        doubleDashBoundary[0] = '-';
        doubleDashBoundary[1] = '-';
        System.arraycopy(boundary, 0, doubleDashBoundary, 2, boundary.length);
        return doubleDashBoundary;
    }


    /**
     * @param boundary
     */
    private void pushBoundary(final byte[] boundary) {
        this.stack.push(this.boundary);
        this.boundary = createDoubleDashBoundary(boundary);
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


    private HttpPart createPart(final HttpPartHeader header) {
        if (builder != null) {
            return builder.createPart(header);
        }
        return null;
    }

    private void addPart(HttpPart part) {
        if (listener != null) {
            listener.accept(part);
        }
    }

    /**
     * This class handles the preamble section in the multipart.
     */
    private class PreambleHandler implements HttpPartScanner.Listener {

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) {
            scanner.listen(EMPTY_LINE, new ByteArrayOutputStream(), new HeaderHandler());
        }
    }

    /**
     * This class handles the header section in the multipart.
     */
    private class HeaderHandler implements HttpPartScanner.Listener {

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) {
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
                pushBoundary(header.getBoundary());
                scanner.listen(boundary, null, new PreambleHandler());
            } else {
                HttpPart part = createPart(header);
                if (part != null) {
                    scanner.listen(boundary, part.getOutputStream(), new BodyHandler(part));
                } else {
                    scanner.listen(boundary, null, new BodyHandler(null));
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
    private class BodyHandler implements HttpPartScanner.Listener {
        private HttpPart part;

        public BodyHandler(HttpPart part) {
            this.part = part;
        }

        @Override
        public void onComplete(int type, OutputStream output, HttpPartScanner scanner) {
            if (part != null) {
                addPart(part);
            }

            if (type == 0) {
                scanner.listen(EMPTY_LINE, new ByteArrayOutputStream(), new HeaderHandler());
            } else {
                popBoundary();
                scanner.listen(boundary, null, new BodyHandler(null));
            }
        }
    }

}