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
package waffle.http.server;

import waffle.http.server.parser.HttpParser;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpRequestHandler {

    private final ByteBuffer buffer = ByteBuffer.allocate(1024 * 10);
    private final HttpParser httpParser = new HttpParser();

    public HttpRequestHandler() {
    }

    /**
     * @param socketChannel
     * @throws IOException
     */
    public void read(ReadableByteChannel socketChannel) throws IOException {
        int read = socketChannel.read(buffer);
        if (read == -1) {
            throw new IOException("End of stream");
        }

        if (read == 0) {
            return;
        }

        buffer.flip();
        try {
            httpParser.parse(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        buffer.clear();
    }

//
//    public void read(ReadableByteChannel socketChannel) throws IOException {
//        while (true) {
//            int read = socketChannel.read(buffer);
//            if (read == -1) {
//                throw new IOException("End of stream");
//            }
//
//            if (read == 0) {
//                return;
//            }
//
//            buffer.flip();
//            try {
//                httpParser.parse(buffer);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            buffer.clear();
//        }
//    }
}