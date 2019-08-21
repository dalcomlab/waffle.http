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

import com.dalcomlab.sattang.protocol.Decoder;
import com.dalcomlab.sattang.protocol.HttpRequest;

import java.nio.ByteBuffer;

/**
 * This class is a decoder for the HTTP request header.
 *
 * <pre>
 *
 * ┌────────────────────┐
 * │    request line    │ <- decode by using {@link HttpRequestLineDecoder} decoder
 * ├────────────────────┤
 * │                    │
 * │                    │
 * │   request header   │ <- decode by using {@link HttpHeaderDecoder} decoder.
 * │                    │
 * │                    │
 * └────────────────────┘
 *
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpRequestHeaderDecoder implements Decoder<HttpRequest> {
    private State state = State.LINE;
    private HttpRequestLineDecoder lineDecoder;
    private HttpHeaderDecoder headerDecoder;
    private HttpRequest request;

    /**
     *
     */
    public HttpRequestHeaderDecoder() {
        this.lineDecoder = new HttpRequestLineDecoder();
        this.headerDecoder = new HttpHeaderDecoder();
    }

    /**
     * @param maxRequestLineBytes
     * @param maxHeaderBytes
     * @param maxHeaderCount
     */
    public HttpRequestHeaderDecoder(int maxRequestLineBytes, int maxHeaderBytes, int maxHeaderCount) {
        this.lineDecoder = new HttpRequestLineDecoder(maxRequestLineBytes);
        this.headerDecoder = new HttpHeaderDecoder(maxHeaderBytes, maxHeaderCount);
    }

    /**
     * Sets the maximum request line bytes.
     *
     * @param maxRequestLineBytes
     * @return
     */
    public HttpRequestHeaderDecoder setMaxRequestLineBytes(int maxRequestLineBytes) {
        lineDecoder.setMaxRequestLineBytes(maxRequestLineBytes);
        return this;
    }


    /**
     * Sets the maximum header bytes.
     *
     * @param maxHeaderBytes
     * @return
     */
    public HttpRequestHeaderDecoder setMaxHeaderBytes(int maxHeaderBytes) {
        headerDecoder.setMaxHeaderBytes(maxHeaderBytes);
        return this;
    }

    /**
     * Sets the maximum header count.
     *
     * @param maxHeaderCount
     * @return
     */
    public HttpRequestHeaderDecoder setMaxHeaderCount(int maxHeaderCount) {
        headerDecoder.setMaxHeaderCount(maxHeaderCount);
        return this;
    }

    /**
     * @param request
     */
    @Override
    public HttpRequestHeaderDecoder listen(HttpRequest request) {
        assert (request != null) : "the request cannot be null pointer.";

        this.request = request;
        this.headerDecoder.listen(request::addHeader);
        this.lineDecoder.listen(new HttpRequestLineDecoder.Listener() {
            @Override
            public void setMethod(String method) throws Exception {
                request.setMethod(method);
            }

            @Override
            public void setUri(String uri) throws Exception {
                request.setUri(uri);
            }

            @Override
            public void setProtocol(String protocol) throws Exception {
                request.setProtocol(protocol);
            }

            @Override
            public void addParameter(String name, String value) throws Exception {
                request.addParameter(name, value);
            }
        });
        return this;
    }

    /**
     * Decodes the request header.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            switch (state) {
                case LINE:
                    decodeLine(buffer);
                    break;
                case NEXT:
                    decodeNext(buffer);
                    break;
                case HEAD:
                    decodeHeader(buffer);
                    break;
            }

            if (state == State.COMPLETE) {
                return true;
            }
        }

        return false;
    }


    /**
     * Decodes the request line part.
     *
     * @param buffer
     * @return
     */
    private void decodeLine(ByteBuffer buffer) throws Exception {
        int head = buffer.position();
        boolean done = scanLine(buffer);

        int len = buffer.position() - head;
        if (len > 0) {
            lineDecoder.decode(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (done) {
            state = State.NEXT;
        }
    }

    /**
     * @param buffer
     * @return
     */
    private void decodeNext(ByteBuffer buffer) {
        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                state = State.COMPLETE;
                break;
            } else if (b == '\r') {
                continue;
            } else {
                buffer.position(buffer.position() - 1);
                state = State.HEAD;
                break;
            }
        }
    }

    /**
     * Decodes the request header part.
     *
     * @param buffer
     * @return
     */
    private void decodeHeader(ByteBuffer buffer) throws Exception {
        int head = buffer.position();
        boolean done = scanLine(buffer);

        int len = buffer.position() - head;
        if (len > 0) {
            headerDecoder.decode(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (done) {
            state = State.NEXT;
        }
    }


    /**
     * @param buffer
     * @return
     * @throws Exception
     */
    private boolean scanLine(ByteBuffer buffer) {
        byte b;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                return true;
            }
        }
        return false;
    }

    /**
     * @throws Exception
     */
    public void close() throws Exception {
        lineDecoder.close();
        headerDecoder.close();
    }

    /**
     * Resets all states of the parser.
     */
    public void reset() {
        state = State.LINE;
        lineDecoder.reset();
        headerDecoder.reset();
    }

    private enum State {
        LINE,
        NEXT,
        HEAD,
        COMPLETE
    }

}