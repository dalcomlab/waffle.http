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
package com.dalcomlab.sattang.protocol.http.encoder;

import com.dalcomlab.sattang.protocol.Encoder;
import com.dalcomlab.sattang.protocol.HttpResponse;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpResponseHeaderEncoder implements Encoder<HttpResponse> {
    private HttpResponse response;
    private State state = State.STATUS;
    private ByteBuffer status = null;
    private ByteBuffer headers = null;

    /**
     *
     */
    public HttpResponseHeaderEncoder() {

    }

    /**
     *
     * @param response
     */
    public HttpResponseHeaderEncoder(HttpResponse response) {
        listen(response);
    }

    /**
     * @param response
     * @return
     */
    public HttpResponseHeaderEncoder listen(HttpResponse response) {
        this.response = response;
        this.status = null;
        this.headers = null;
        return this;
    }

    /**
     * Encode the object to the given {@link ByteBuffer}.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    public boolean encode(ByteBuffer buffer) throws Exception {
        if (state == State.COMPLETE) {
            return true;
        }

        while(buffer.hasRemaining()) {

            switch (state) {
                case STATUS:
                    encodeStatus(buffer);
                    break;
                case HEADER:
                    encodeHeader(buffer);
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
     * @throws IOException
     */
    private void encodeStatus(ByteBuffer buffer) throws IOException {
        if (status == null) {
            fillStatus();
        }

        transfer(status, buffer);

        if (status.remaining() == 0) {
            changeState(State.HEADER);
        }
    }


    /**
     *
     */
    private void fillStatus() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("HTTP/1.1 ".getBytes());

       // output.write(Integer.toString(response.getStatusCode()).getBytes());
        output.write(" ".getBytes());
       // output.write(response.getStatusMessage().getBytes());
        output.write("\r\n".getBytes());

        status = ByteBuffer.allocate(output.size());
        status.clear();
        status.put(output.toByteArray());
        status.flip();
    }

    /**
     * @param buffer
     * @return
     * @throws IOException
     */
    private void encodeHeader(ByteBuffer buffer) throws IOException {
        if (headers == null) {
            fillHeader();
        }

        transfer(headers, buffer);

        if (headers.remaining() == 0) {
            changeState(State.COMPLETE);
        }
    }

    /**
     * @throws IOException
     */
    private void fillHeader() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        for (Map.Entry<String, List<String>> header : response.getHeaderMaps().entrySet()) {
            String name = header.getKey();
            List<String> values = header.getValue();
            if (name == null || values == null) {
                continue;
            }

            if (!name.isEmpty()) {
                for (String value : values) {
                    output.write(name.getBytes());
                    output.write(":".getBytes());
                    output.write(value.getBytes());
                    output.write("\r\n".getBytes());
                }
            }
        }
        output.write("\r\n".getBytes());
        headers = ByteBuffer.allocate(output.size());
        headers.clear();
        headers.put(output.toByteArray());
        headers.flip();

    }


    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
    }

    /**
     * @param source
     * @param dest
     * @return
     */
    private int transfer(ByteBuffer source, ByteBuffer dest) {
        int amount = Math.min(dest.remaining(), source.remaining());
        if (amount > 0) {
            dest.put(source.array(), source.arrayOffset() + source.position(), amount);
            source.position(source.position() + amount);
        }
        return amount;
    }


    /**
     *
     */
    private enum State {
        STATUS,
        HEADER,
        COMPLETE
    }

}
