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
package com.dalcomlab.sattang.protocol.websocket.decoder;

import com.dalcomlab.sattang.protocol.websocket.WebSocketOpcode;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;


// https://github.com/apache/tomcat/blob/3e5ce3108e2684bc25013d9a84a7966a6dcd6e14/java/org/apache/tomcat/websocket/WsFrameBase.java
// https://github.com/undertow-io/undertow/blob/master/core/src/main/java/io/undertow/websockets/core/protocol/version07/WebSocket07Channel.java
// https://github.com/undertow-io/undertow/blob/master/core/src/main/java/io/undertow/websockets/core/protocol/version07/WebSocket07Channel.java

/**
 * <p>
 * A Base Frame as seen in <a href="https://tools.ietf.org/html/rfc6455#section-5.2">RFC 6455. Sec 5.2</a>
 *
 * <pre>
 *    0                   1                   2                   3
 *    0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
 *   +-+-+-+-+-------+-+-------------+-------------------------------+
 *   |F|R|R|R| opcode|M| Payload len |    Extended payload length    |
 *   |I|S|S|S|  (4)  |A|     (7)     |             (16/64)           |
 *   |N|V|V|V|       |S|             |   (if payload len==126/127)   |
 *   | |1|2|3|       |K|             |                               |
 *   +-+-+-+-+-------+-+-------------+ - - - - - - - - - - - - - - - +
 *   |     Extended payload length continued, if payload len == 127  |
 *   + - - - - - - - - - - - - - - - +-------------------------------+
 *   |                               |Masking-key, if MASK set to 1  |
 *   +-------------------------------+-------------------------------+
 *   | Masking-key (continued)       |          Payload Data         |
 *   +-------------------------------- - - - - - - - - - - - - - - - +
 *   :                     Payload Data continued ...                :
 *   + - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - +
 *   |                     Payload Data continued ...                |
 *   +---------------------------------------------------------------+
 * </pre>
 * <p>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class WebSocketFrameDecoder {

    private final byte[] mask = new byte[4];
    private State state = State.WS_HEADER_FIRST;
    private boolean fin = false;
    private int rsv = 0;
    private byte opCode = 0;
    private boolean masked = false;
    private long length = 0;
    private ByteArrayOutputStream payload = null;

    /**
     * @param buffer
     * @throws Exception
     */
    public void parse(ByteBuffer buffer) throws Exception {

        if (state.isHeaderFirst()) {
            readHeaderFirst(buffer);
        }

        if (state.isHeaderSecond()) {
            readHeaderSecond(buffer);
        }

        if (state.isExtendedPayloadLength2Bytes()) {
            readExtendedPayloadLength2Bytes(buffer);
        }

        if (state.isExtendedPayloadLength4Bytes()) {
            readExtendedPayloadLength4Bytes(buffer);
        }

        if (state.isMasking()) {
            readMasking(buffer);
        }

        if (state.isPayload()) {
            readPayload(buffer);
        }
    }

    /**
     * @param buffer
     */
    public void readHeaderFirst(ByteBuffer buffer) {
        if (buffer.remaining() == 0) {
            return;
        }

        int b = buffer.get();
        fin = (b & 0x80) != 0;  // 1000 0000
        rsv = (b & 0x70) >>> 4; // 0111 0000
        opCode = (byte) (b & 0x0F);


        if (opCode == WebSocketOpcode.CONTINUATION) {
            System.out.println("the op code is CONTINUATION");
        }

        if (opCode == WebSocketOpcode.TEXT) {
            System.out.println("the op code is TEXT");
        }

        if (opCode == WebSocketOpcode.BINARY) {
            System.out.println("the op code is BINARY");
        }

        if (opCode == WebSocketOpcode.CLOSE) {
            System.out.println("the op code is CLOSE");
        }

        if (opCode == WebSocketOpcode.PING) {
            System.out.println("the op code is PING");
        }

        if (opCode == WebSocketOpcode.PONG) {
            System.out.println("the op code is PONG");
        }

        changeState(State.WS_HEADER_SECOND);

    }

    /**
     * @param buffer
     */
    public void readHeaderSecond(ByteBuffer buffer) {
        if (buffer.remaining() == 0) {
            return;
        }

        int b = buffer.get();
        masked = (b & 0x80) != 0;
        length = b & 0x7F;

        System.out.println("masked : " + masked);
        System.out.println("length : " + length);

        //
        //[1][000][opcode][1][60][mask][payload]
        //        1   3      4    1  7    32    ...
        //
        //[1][000][opcode][1][126][14075][mask][payload]
        //        1   3      4    1   7    16     32     ...
        //
        //[1][000][opcode][1][127][18000000][mask][payload]
        //        1   3      4    1   7      64      32     ...
        //
        //
        //
        //
        //[1][000][opcode][1][60][payload]
        //        1   3      4    1  7     ...
        //
        //[1][000][opcode][1][126][14075][payload]
        //        1   3      4    1   7    16      ...
        //
        //[1][000][opcode][1][127][18000000][payload]
        //        1   3      4    1   7      64       ...
        //
        //
        //
        if (length == 126) {
            length = 0;
            changeState(State.WS_EXTENDED_PAYLOAD_LENGTH_2BYTES);
        } else if (length == 127) {
            length = 0;
            changeState(State.WS_EXTENDED_PAYLOAD_LENGTH_4BYTES);
        } else {
            if (masked) {
                changeState(State.WS_MASKING);
            } else {
                changeState(State.WS_DONE);
            }
        }
    }

    /**
     * @param buffer
     */
    public void readExtendedPayloadLength2Bytes(ByteBuffer buffer) {
        while (buffer.hasRemaining() && state.count < 2) {
            int b = buffer.get();
            length = (length << 8) | (b & 0xff);
            state.count++;
        }

        if (state.count == 2) {
            changeState(State.WS_MASKING);
        }
    }


    /**
     * @param buffer
     */
    public void readExtendedPayloadLength4Bytes(ByteBuffer buffer) {
        while (buffer.hasRemaining() && state.count < 4) {
            int b = buffer.get();
            length = (length << 8) | (b & 0xff);
            state.count++;
        }

        if (state.count == 4) {
            changeState(State.WS_MASKING);
        }
    }


    /**
     * @param buffer
     */
    public void readMasking(ByteBuffer buffer) {
        while (buffer.hasRemaining() && state.count < 4) {
            mask[state.count++] = buffer.get();
        }

        if (state.count == 4) {
            changeState(State.WS_READ_PAYLOAD);
        }
    }

    /**
     * @param buffer
     */
    public void readPayload(ByteBuffer buffer) {
        if (buffer.remaining() == 0) {
            return;
        }

        if (payload == null) {
            payload = new ByteArrayOutputStream((int) length);
        }


        while (buffer.hasRemaining()) {
            byte b = buffer.get();
            payload.write(mask[state.count % 4] ^ b);
            state.count++;
            if (state.count == length) {
                break;
            }
        }

        if (state.count == length) {
            System.out.println("payload : " + new String(payload.toByteArray()));
            changeState(State.WS_DONE);
        }
    }

    /**
     * @param state
     */
    private void changeState(State state) {
        this.state = state;
        this.state.count = 0;
    }

    /**
     *
     */
    public enum State {
        WS_HEADER_FIRST,
        WS_HEADER_SECOND,
        WS_EXTENDED_PAYLOAD_LENGTH_2BYTES,
        WS_EXTENDED_PAYLOAD_LENGTH_4BYTES,
        WS_MASKING,
        WS_READ_PAYLOAD,
        WS_DONE;

        public int count;

        public boolean isHeaderFirst() {
            return this == WS_HEADER_FIRST;
        }

        public boolean isHeaderSecond() {
            return this == WS_HEADER_SECOND;
        }

        public boolean isExtendedPayloadLength2Bytes() {
            return this == WS_EXTENDED_PAYLOAD_LENGTH_2BYTES;
        }

        public boolean isExtendedPayloadLength4Bytes() {
            return this == WS_EXTENDED_PAYLOAD_LENGTH_4BYTES;
        }

        public boolean isMasking() {
            return this == WS_MASKING;
        }

        public boolean isPayload() {
            return this == WS_READ_PAYLOAD;
        }

        public boolean isDone() {
            return this == WS_DONE;
        }
    }
}
