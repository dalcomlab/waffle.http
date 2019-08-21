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
package com.dalcomlab.sattang.protocol.ajp;

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpPacket {
    public static final ByteBuffer CPONG_RESPONSE_CHUNK;
    public static final ByteBuffer END_RESPONSE_CHUNK;
    public static final ByteBuffer GET_BODY_CHUNK;

    static {
        // A, B : The AJP response header
        // 0, 1 : The AJP packet length
        // 9    : The AJP CPONG response code
        CPONG_RESPONSE_CHUNK = ByteBuffer.wrap(new byte[]{'A', 'B', 0, 1, 9});
        END_RESPONSE_CHUNK = ByteBuffer.wrap(new byte[]{'A', 'B', 0, 2, 5, 1});

        // A, B : The AJP response header
        // 6 : send signal to get more data
        // 31, -7 : byte values for int 8185 = (8 * 1024) - 7 : max data
        GET_BODY_CHUNK = ByteBuffer.wrap(new byte[]{'A', 'B', 0, 3, 6, 31, -7});
    }
}
