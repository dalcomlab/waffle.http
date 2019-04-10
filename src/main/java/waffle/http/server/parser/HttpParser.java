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
public class HttpParser implements Parser {

    private HttpScanner scanner;
    private HttpHeader headers = null;
    private ByteArrayOutputStream line = new ByteArrayOutputStream();
    private ByteArrayOutputStream header = new ByteArrayOutputStream();
    private ByteArrayOutputStream body = new ByteArrayOutputStream();

    /**
     *
     */
    public HttpParser() {
        this.scanner = new HttpScanner(line, new RequestLineHandler());
    }


    /**
     * @param buffer
     * @throws Exception
     */
    @Override
    public void parse(ByteBuffer buffer) throws Exception {
        this.scanner.scan(buffer);
    }

    /**
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        this.scanner.complete();
    }

    /**
     *
     */
    public final class RequestLineHandler implements HttpScanner.Listener {

        @Override
        public void onComplete(HttpScanner.State state, OutputStream output, HttpScanner scanner) throws Exception {
            scanner.listen(header, new RequestHeaderHandler());
        }
    }

    /**
     *
     */
    public final class RequestHeaderHandler implements HttpScanner.Listener {

        @Override
        public void onComplete(HttpScanner.State state, OutputStream output, HttpScanner scanner) throws Exception {
            if (header != null) {
                HttpHeaderParser p = new HttpHeaderParser();
                headers = new HttpHeader(p.parse(header.toByteArray()));
                if (headers.getContentType().startsWith("multipart")) {
                    scanner.bodyParser(new HttpPartParser(headers.getBoundary(), new MockPartBuilder(), (part) -> {
                        String uploadPath = System.getProperty("user.dir");
                        uploadPath += "/src/test/resource/uploadfiles/";
                        uploadPath += part.getFileName();
                        try {
                            System.out.println("write to :" + uploadPath);
                            part.writeFile(uploadPath);
                            System.out.println("write!!");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }));
                }
            }
            scanner.listen(body, new RequestBodyHandler());
        }
    }

    /**
     *
     */
    public final class RequestBodyHandler implements HttpScanner.Listener {

        @Override
        public void onComplete(HttpScanner.State state, OutputStream output, HttpScanner scanner) throws Exception {
        }
    }


    private class MockPartBuilder implements HttpPartBuilder {

        /**
         * Creates a part.
         *
         * @param header the multipart header
         * @return
         */
        @Override
        public HttpPart createPart(HttpPartHeader header) {
            return new HttpPart(header);
        }
    }
}
