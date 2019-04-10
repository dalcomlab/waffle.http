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


import java.io.InputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface Parser {

    /**
     * Parses
     *
     * @param buffer
     * @return
     */
    void parse(final ByteBuffer buffer) throws Exception;

    /**
     * Closes the parser.
     *
     * @return
     */
    void close() throws Exception;

    /**
     * Parses something by using then given buffer.
     *
     * @param buffer
     * @return
     */
    default void parse(final byte[] buffer) throws Exception {
        parse(ByteBuffer.wrap(buffer));
    }

    /**
     * Parses something by using then given buffer.
     *
     * @param buffer
     */
    default void parse(final byte[] buffer, int offset, int len) throws Exception {
        parse(ByteBuffer.wrap(buffer, offset, len));
    }

    /**
     * Parses something by using then given input stream.
     *
     * @param in
     */
    default void parse(final InputStream in) throws Exception {
        parse(in, 1024);
    }

    /**
     * Parses the something by using then given input stream.
     *
     * @param in
     * @param bufferSize
     */
    default void parse(final InputStream in, int bufferSize) throws Exception {
        byte[] buffer = new byte[bufferSize];
        int read = 0;
        while ((read = in.read(buffer, 0, bufferSize)) != -1) {
            parse(buffer, 0, read);
            if (in.available() == 0)
                break;
        }
    }
}
