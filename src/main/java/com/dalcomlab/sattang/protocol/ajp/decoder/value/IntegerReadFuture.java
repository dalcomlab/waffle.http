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

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class IntegerReadFuture extends ReadFuture<Integer> {
    private int i = 0;

    @Override
    public Integer get() {
        return i;
    }

    @Override
    public void reset() {
        i = 0;
        state = 0;
    }

    @Override
    public void read(ByteBuffer buffer) throws Exception {
        byte b = 0;
        // ┌─────────┐
        // │ integer │
        // ├────┼────┤
        // │ hi │ lo │
        // └────┴────┘
        //   ^
        //  here
        if (state == 0) {
            if (!buffer.hasRemaining()) {
                return;
            }
            b = buffer.get();
            i = b & 0xff;
            state = 1;
        }

        // ┌─────────┐
        // │ integer │
        // ├────┼────┤
        // │ hi │ lo │
        // └────┴────┘
        //        ^
        //       here
        if (state == 1) {
            if (!buffer.hasRemaining()) {
                return;
            }
            b = buffer.get();
            i = (i << 8) | (b & 0xff);
            done = true;
        }
    }
}
