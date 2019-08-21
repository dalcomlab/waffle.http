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
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface WebSocketListener {

    /**
     * This method is called when a continuation frame (opcode = 0x0) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;


    /**
     * This method is called when a text frame (opcode = 0x1) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;


    /**
     * This method called when a binary frame (opcode = 0x2) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;


    /**
     * This method is called when a close frame (opcode = 0x8) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;

    /**
     * This method is called when a ping frame (opcode = 0x9) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;


    /**
     * This method is called when a pong frame (opcode = 0xA) was received.
     *
     * @param websocket
     * @param frame
     * @throws Exception
     */
    void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception;
}
