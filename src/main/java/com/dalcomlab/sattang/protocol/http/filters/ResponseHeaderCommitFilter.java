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
package com.dalcomlab.sattang.protocol.http.filters;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.HttpStatus;
import com.dalcomlab.sattang.protocol.http.ResponseFilter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseHeaderCommitFilter extends ResponseFilter {
    private final HttpResponse response;
    private State state = State.RESPONSE_STATUS;
    private ByteBuffer statusBuffer = null;
    private ByteBuffer headerBuffer = null;

    /**
     * @param response
     */
    public ResponseHeaderCommitFilter(HttpResponse response) {
        assert (response != null) : "the response cannot be null!";
        this.response = response;
    }

    /**
     * Writes the given buffer to this filters.
     *
     * @param channel
     * @param source
     * @return
     * @throws IOException
     */
    @Override
    public int write(WriteChannel channel, ByteBuffer source) throws IOException {
        if (next() == null) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (source.remaining() == 0 || !source.hasArray()) {
            return 0;
        }

        if (!state.isCommitted()) {
            flushHeader(channel);
        }

        int consume = 0;
        if (state.isCommitted()) {
            consume = next().write(channel, source);
        }

        return consume;
    }

    /**
     * Flushes the buffer.
     *
     * @param channel
     * @throws IOException
     */
    @Override
    public void flush(WriteChannel channel) throws IOException {
        if (next() == null) {
            return;
        }
        if (!state.isCommitted()) {
            flushHeader(channel);
        }

        next().flush(channel);
    }

    /**
     * @param channel
     * @throws IOException
     */
    private void flushHeader(WriteChannel channel) throws IOException {
        if (state.isStatus()) {
            writeStatus(channel);
        }

        if (state.isHeader()) {
            writeHeaders(channel);
        }
    }


    /**
     * @return
     * @throws IOException
     */
    private int writeStatus(WriteChannel channel) throws IOException {
        if (statusBuffer == null) {
            fillStatusBuffer();
        }

        int consume = 0;
        if (statusBuffer.hasRemaining()) {
            consume = next().write(channel, statusBuffer);
        }

        if (statusBuffer.remaining() == 0) {
            changeState(State.RESPONSE_HEADER);
        }

        return consume;
    }

    /**
     *
     */
    private void fillStatusBuffer() throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write("HTTP/1.1 ".getBytes());

        HttpStatus status = response.getStatus();
        if (status == null) {
            status = HttpStatus.OK;
        }

        output.write(Integer.toString(status.getCode()).getBytes());
        output.write(" ".getBytes());
        output.write(status.getMessage().getBytes());
        output.write("\r\n".getBytes());

        statusBuffer = ByteBuffer.allocate(output.size());
        statusBuffer.clear();
        statusBuffer.put(output.toByteArray());
        statusBuffer.flip();
    }

    /**
     * @return
     * @throws IOException
     */
    private int writeHeaders(WriteChannel channel) throws IOException {
        if (headerBuffer == null) {
            fillHeaderBuffer();
        }

        int consume = 0;
        if (headerBuffer.hasRemaining()) {
            consume = next().write(channel, headerBuffer);
        }

        if (headerBuffer.remaining() == 0) {
            changeState(State.RESPONSE_COMMITTED);
        }

        return consume;
    }

    /**
     * @throws IOException
     */
    private void fillHeaderBuffer() throws IOException {
        assert (response.getHeaderMaps() != null) : "the header cannot be null.";

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
        headerBuffer = ByteBuffer.allocate(output.size());
        headerBuffer.clear();
        headerBuffer.put(output.toByteArray());
        headerBuffer.flip();

    }


    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
    }

    /**
     *
     */
    private enum State {
        RESPONSE_STATUS,
        RESPONSE_HEADER,
        RESPONSE_COMMITTED;

        public boolean isStatus() {
            return this == RESPONSE_STATUS;
        }

        public boolean isHeader() {
            return this == RESPONSE_HEADER;
        }

        public boolean isCommitted() {
            return this == RESPONSE_COMMITTED;
        }

    }
}