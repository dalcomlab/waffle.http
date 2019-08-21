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
package com.dalcomlab.sattang.protocol.websocket;

/**
 * https://github.com/fereidoonsavadkoohi/maGap/blob/0738b45999a51bc96791635da8a65c01fad08f98/websocket/src/main/java/com/neovisionaries/ws/client/WebSocketFrame.java
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
 * The decision to fragment an application message into multiple frames is made by the
 * underlying implementation of the client and server framing code. Hence, the applications
 * remain blissfully unaware of the individual WebSocket frames or how the framing is
 * performed. Having said that, it is still useful to understand the highlights of how each
 * WebSocket frame is represented on the wire:
 *
 * <ul>
 * <li>
 * The first bit of each frame (FIN) indicates whether the frame is a final fragment of a
 * message. A message may consist of just a single frame.
 * </li>
 *
 * <li>
 * The opcode (4 bits) indicates type of transferred frame: text (1) or binary (2) for
 * transferring application data or a control frame such as connection close (8), ping (9),
 * and pong (10) for connection liveness checks.
 * </li>
 *
 * <li>
 * The mask bit indicates whether the payload is masked (for messages sent from the
 * client to the server only).
 * </li>
 *
 * <li>
 * Payload length is represented as a variable-length field:
 * <p>
 * - If 0–125, then that is the payload length.
 * - If 126, then the following 2 bytes represent a 16-bit unsigned integer indicating
 * the frame length.
 * - If 127, then the following 8 bytes represent a 64-bit unsigned integer indicating
 * the frame length.
 * </li>
 *
 * <li>
 * Masking key contains a 32-bit value used to mask the payload.
 * </li>
 *
 * <li>
 * Payload contains the application data and custom extension data if the client and server
 * negotiated an extension when the connection was established.
 * </li>
 * </ul>
 * - https://hpbn.co/websocket/
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class WebSocketFrame {

    private int opcode;

    /**
     *
     * @return
     */
    public int getOpcode() {
        return opcode;
    }

    /**
     *
     * @param opcode
     * @return
     */
    public WebSocketFrame setOpcode(int opcode) {
        this.opcode = opcode;
        return this;
    }



    /**
     * Check if this frame is a continuation frame.
     * This method returns {@code true} when the value of the opcode is 0x0.
     *
     * @return {@code true} if this frame is a continuation frame
     */
    public boolean isContinuationFrame() {
        return (opcode == WebSocketOpcode.CONTINUATION);
    }


    /**
     * Check if this frame is a text frame.
     * This method returns {@code true} when the value of the opcode is 0x1.
     *
     * @return {@code true} if this frame is a text frame
     */
    public boolean isTextFrame() {
        return (opcode == WebSocketOpcode.TEXT);
    }


    /**
     * Check if this frame is a binary frame.
     * This method returns {@code true} when the value of the opcode is 0x2.
     *
     * @return {@code true} if this frame is a binary frame
     */
    public boolean isBinaryFrame() {
        return (opcode == WebSocketOpcode.BINARY);
    }


    /**
     * Check if this frame is a close frame.
     * This method returns {@code true} when the value of the opcode is 0x8.
     *
     * @return {@code true} if this frame is a close frame
     */
    public boolean isCloseFrame() {
        return (opcode == WebSocketOpcode.CLOSE);
    }


    /**
     * Check if this frame is a ping frame.
     * This method returns {@code true} when the value of the opcode is 0x9.
     *
     * @return {@code true} if this frame is a ping frame
     */
    public boolean isPingFrame() {
        return (opcode == WebSocketOpcode.PING);
    }


    /**
     * Check if this frame is a pong frame.
     * This method returns {@code true} when the value of the opcode is 0xA.
     *
     * @return {@code true} if this frame is a pong frame
     */
    public boolean isPongFrame() {
        return (opcode == WebSocketOpcode.PONG);
    }


    /**
     * Check if this frame is a data frame.
     * This method returns {@code true} when the value of the opcode is in between 0x1 and 0x7.
     *
     * @return {@code true} if this frame is a data frame
     */
    public boolean isDataFrame() {
        return (0x1 <= opcode && opcode <= 0x7);
    }


    /**
     * Check if this frame is a control frame.
     * This method returns {@code true} when the value of the opcode is in between 0x8 and 0xF.
     *
     * @return {@code true} if this frame is a control frame
     */
    public boolean isControlFrame() {
        return (0x8 <= opcode && opcode <= 0xF);
    }
}
