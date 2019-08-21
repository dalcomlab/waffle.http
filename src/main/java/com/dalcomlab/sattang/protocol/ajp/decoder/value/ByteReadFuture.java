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
public class ByteReadFuture extends ReadFuture<Byte> {
    private byte b = 0;

    @Override
    public Byte get() {
        return b;
    }


    @Override
    public void reset() {
        state = 0;
        b = 0;
    }

    @Override
    public void read(ByteBuffer buffer) throws Exception {
        if (!buffer.hasRemaining()) {
            return;
        }
        b = buffer.get();
        done = true;
    }
}
