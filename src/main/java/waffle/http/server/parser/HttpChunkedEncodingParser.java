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

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpChunkedEncodingParser implements Parser {

    private HttpChunkedEncodingScanner scanner;
    private OutputStream chunk = null;
    private OutputStream trailer = null;

    /**
     *
     */
    public HttpChunkedEncodingParser() {
        this.scanner = new HttpChunkedEncodingScanner(new ByteArrayOutputStream(), new LengthHandler());
    }

    /**
     *
     */
    public HttpChunkedEncodingParser(OutputStream chunk) {
        this.scanner = new HttpChunkedEncodingScanner(new ByteArrayOutputStream(), new LengthHandler());
        this.chunk = chunk;
    }


    /**
     * @param buffer
     * @return
     */
    @Override
    public void parse(ByteBuffer buffer) throws Exception {
        this.scanner.scan(buffer);
    }

    /**
     *
     * @throws Exception
     */
    @Override
    public void close() throws Exception {

    }

    /**
     * @param output
     * @return
     */
    private int parseChunkLength(ByteArrayOutputStream output) {
        if (output == null) {
            return 0;
        }

        String length = new String(output.toByteArray());

        // remove chunk-extension
        // chunk           = chunk-size [ chunk-extension ] CHUNK_CRLF
        // chunk-extension = *( ";" chunk-ext-name [ "=" chunk-ext-val ] )
        int semicolon = length.indexOf(";");
        if (semicolon != -1) {
            length = length.substring(0, semicolon);
        }
        return Integer.decode("0x" + length.trim());
    }

    /**
     * This class handles the chunk length in the chunked encoding.
     */
    private final class LengthHandler implements HttpChunkedEncodingScanner.Listener {

        @Override
        public void onComplete(HttpChunkedEncodingScanner.State state, OutputStream output, HttpChunkedEncodingScanner scanner) throws Exception {
            int length = parseChunkLength((ByteArrayOutputStream) output);
            if (length > 0) {
                scanner.setChunkLength(length);
                scanner.listen(chunk, new ChunkHandler());
            } else {
                scanner.listen(trailer, new TrailerHandler());
            }
        }
    }

    /**
     * This class handles the chunk content in the chunked encoding.
     */
    private final class ChunkHandler implements HttpChunkedEncodingScanner.Listener {
        @Override
        public void onComplete(HttpChunkedEncodingScanner.State state, OutputStream output, HttpChunkedEncodingScanner scanner) throws Exception {
            scanner.listen(new ByteArrayOutputStream(), new LengthHandler());
        }
    }


    /**
     * This class handles the trailer in the chunked encoding.
     */
    private final class TrailerHandler implements HttpChunkedEncodingScanner.Listener {
        @Override
        public void onComplete(HttpChunkedEncodingScanner.State state, OutputStream output, HttpChunkedEncodingScanner scanner) throws Exception {
        }
    }
}
