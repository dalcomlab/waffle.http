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

import com.dalcomlab.sattang.protocol.HttpRequest;

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpRequestDecoder {
    private State state = State.LINE;
    private HttpRequestLineDecoder lineDecoder;
    private HttpHeaderDecoder headerDecoder;
    private HttpRequest request;

    /**
     *
     */
    public HttpRequestDecoder() {
        this.lineDecoder = new HttpRequestLineDecoder();
        this.headerDecoder = new HttpHeaderDecoder();
    }

    /**
     * @param maxRequestLineBytes
     * @param maxHeaderBytes
     * @param maxHeaderCount
     */
    public HttpRequestDecoder(int maxRequestLineBytes, int maxHeaderBytes, int maxHeaderCount) {
        this.lineDecoder = new HttpRequestLineDecoder(maxRequestLineBytes);
        this.headerDecoder = new HttpHeaderDecoder(maxHeaderBytes, maxHeaderCount);
    }

    /**
     * @param request
     */
    public void listen(HttpRequest request) {
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
    }

    /**
     * Parses the request line.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
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
                    decodeHead(buffer);
                    break;
            }

            if (state == State.COMPLETE) {
                return true;
            }
        }

        return false;
    }


    /**
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
     * @param buffer
     * @return
     */
    private void decodeHead(ByteBuffer buffer) throws Exception {
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