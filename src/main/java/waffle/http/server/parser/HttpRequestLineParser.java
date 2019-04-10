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

import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpRequestLineParser implements Parser {
    private HttpRequestLineScanner scanner;
    private OutputStream method = null;
    private OutputStream uri = null;
    private OutputStream protocol = null;

    /**
     *
     */
    public HttpRequestLineParser(final OutputStream method, final OutputStream uri, final OutputStream protocol) {
        this.method = method;
        this.uri = uri;
        this.protocol = protocol;
        this.scanner = new HttpRequestLineScanner(this.method, new MethodHandler());
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
     * This class handles the method in the request line.
     */
    private final class MethodHandler implements HttpRequestLineScanner.Listener {
        @Override
        public void onComplete(HttpRequestLineScanner.State state, OutputStream output, HttpRequestLineScanner scanner) throws Exception {
            scanner.listen(uri, new UriHandler());
        }
    }


    /**
     * This class handles the method in the request line.
     */
    private final class UriHandler implements HttpRequestLineScanner.Listener {
        @Override
        public void onComplete(HttpRequestLineScanner.State state, OutputStream output, HttpRequestLineScanner scanner) throws Exception {
            scanner.listen(protocol, new ProtocolHandler());
        }
    }


    /**
     * This class handles the protocol in the request line.
     */
    private final class ProtocolHandler implements HttpRequestLineScanner.Listener {
        @Override
        public void onComplete(HttpRequestLineScanner.State state, OutputStream output, HttpRequestLineScanner scanner) throws Exception {
        }
    }

}
