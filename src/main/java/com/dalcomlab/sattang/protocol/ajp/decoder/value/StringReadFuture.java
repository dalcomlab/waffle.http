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
package com.dalcomlab.sattang.protocol.ajp.decoder.value;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class StringReadFuture extends ReadFuture<String> {
    private ByteArrayOutputStream output = new ByteArrayOutputStream();
    private int length = 0;

    @Override
    public String get() {
        return output.toString();
    }

    @Override
    public void reset() {
        state = 0;
        length = 0;
        output.reset();
    }

    @Override
    public void read(ByteBuffer buffer) throws Exception {
        byte b = 0;
        // ┌─────────┬────────┬────┐
        // │ length  │ string │ \0 │
        // ├────┼────┼────────┼────┤
        // │ hi │ lo │        │    │
        // └────┴────┴────────┴────┘
        //   ^
        //  here
        if (state == 0) {
            if (!buffer.hasRemaining()) {
                return;
            }
            b = buffer.get();
            length = b & 0xff;
            state = 1;
        }

        // ┌─────────┬────────┬────┐
        // │ length  │ string │ \0 │
        // ├────┼────┼────────┼────┤
        // │ hi │ lo │        │    │
        // └────┴────┴────────┴────┘
        //        ^
        //       here
        if (state == 1) {
            if (!buffer.hasRemaining()) {
                return;
            }
            b = buffer.get();
            length = (length << 8) + (b & 0xff);
            if (length == 0 || length == 0xffff) {
                done = true;
                return;
            } else {
                state = 2;
            }
        }

        // ┌─────────┬────────┬────┐
        // │ length  │ string │ \0 │
        // ├────┼────┼────────┼────┤
        // │ hi │ lo │        │    │
        // └────┴────┴────────┴────┘
        //                ^
        //              here
        if (state == 2) {
            if (!buffer.hasRemaining()) {
                return;
            }

            int remain = buffer.remaining();
            if (length > remain) {
                output.write(buffer.array(), buffer.arrayOffset() + buffer.position(), remain);
                length -= remain;
                buffer.position(buffer.position() + remain);
                return;
            } else {
                output.write(buffer.array(), buffer.arrayOffset() + buffer.position(), length);
                buffer.position(buffer.position() + length);
                state = 3;
            }
        }

        // ┌─────────┬────────┬────┐
        // │ length  │ string │ \0 │
        // ├────┼────┼────────┼────┤
        // │ hi │ lo │        │    │
        // └────┴────┴────────┴────┘
        //                      ^
        //                     here
        if (state == 3) {
            if (!buffer.hasRemaining()) {
                return;
            }
            char c = (char) buffer.get(); // read '/0'
            done = true;
        }
    }
}
