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
package com.dalcomlab.sattang.protocol.ajp.filters;

import com.dalcomlab.sattang.net.ChannelConstants;
import com.dalcomlab.sattang.net.io.write.WriteChannel;
import com.dalcomlab.sattang.net.io.write.filters.WriteAbstractFilter;
import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.ajp.AjpConstants;
import com.dalcomlab.sattang.protocol.ajp.AjpDataWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseHeaderFilter extends WriteAbstractFilter {
    private final HttpResponse http;
    private ByteBuffer headers = null;
    private State state = State.RESPONSE_HEADER;

    /**
     * @param http
     */
    public ResponseHeaderFilter(HttpResponse http) {
        assert (http != null) : "the http cannot be null!";
        this.http = http;
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
        if(isEndOfChannel()) {
            return ChannelConstants.END_OF_CHANNEL;
        }

        if (state.isHeader()) {
            writeHeaders(channel);
        }

        int consume = 0;
        if (state.isCommitted()) {
            consume = next().write(channel, source);
        }

        return consume;
    }


    /**
     * @return
     * @throws IOException
     */
    private int writeHeaders(WriteChannel channel) throws IOException {
        if (headers == null) {
            fillHeaderBuffer();
        }

        int consume = 0;
        if (headers.hasRemaining()) {
            consume = next().write(channel, headers);
        }

        if (headers.remaining() == 0) {
            changeState(State.RESPONSE_COMMITTED);
        }

        return consume;
    }


    /**
     * @throws IOException
     */
    private void fillHeaderBuffer() throws IOException {
        assert (http.getHeaderMaps() != null) : "the header cannot be null.";

        headers = ByteBuffer.allocate(1024 * 8);
        headers.clear();

        AjpDataWriter writer = new AjpDataWriter(headers);
        writer.writeInt(0x4142);
        writer.writeInt(0);

        writer.writeByte(AjpConstants.JK_AJP13_SEND_HEADERS); // packet type
        writer.writeInt(http.getStatus().getCode()); // http status code
        writer.writeString(http.getStatus().getMessage()); // http status message
        writer.writeInt(http.getHeaderMaps().size()); // the number of http headers

        for (Map.Entry<String, List<String>> header : http.getHeaderMaps().entrySet()) {
            String name = header.getKey();
            List<String> values = header.getValue();
            for (String value : values) {
                int code = 0xB00;
                if (AjpConstants.isResponseHeader(name.toLowerCase())) {
                    code = AjpConstants.getResponseHeaderCode(name.toLowerCase());
                }
                writer.writeInt(code);
                if (code == 0xB00) {
                    writer.writeString(name);
                }
                writer.writeString(value);
            }
        }
        writer.flush();
        headers.flip();
    }


    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
    }

    /**
     *
     * @return
     */
    private boolean isEndOfChannel() {
        if (next() == null) {
            return true;
        }

        return false;
    }

    /**
     *
     */
    private enum State {
        RESPONSE_HEADER,
        RESPONSE_COMMITTED;

        public boolean isHeader() {
            return this == RESPONSE_HEADER;
        }

        public boolean isCommitted() {
            return this == RESPONSE_COMMITTED;
        }

    }
}
