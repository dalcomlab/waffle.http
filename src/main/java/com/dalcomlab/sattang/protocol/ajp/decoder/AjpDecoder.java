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
package com.dalcomlab.sattang.protocol.ajp.decoder;

import com.dalcomlab.sattang.protocol.ajp.decoder.value.ByteReadFuture;
import com.dalcomlab.sattang.protocol.ajp.decoder.value.IntegerReadFuture;
import com.dalcomlab.sattang.protocol.ajp.decoder.value.StringReadFuture;
import com.dalcomlab.sattang.protocol.ajp.decoder.value.ReadFuture;

import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpDecoder {
    private ReadFuture<String> stringFuture = new StringReadFuture();
    private ReadFuture<Integer> integerFuture = new IntegerReadFuture();
    private ReadFuture<Byte> byteFuture = new ByteReadFuture();

    /**
     *
     */
    public AjpDecoder() {

    }

    /**
     * Reads byte form the AJP packet.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    protected ReadFuture<Byte> readByte(ByteBuffer buffer) {
        try {
            byteFuture.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteFuture;
    }

    /**
     * Reads integer(2 bytes) from the AJP packet.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    protected ReadFuture<Integer> readInt(ByteBuffer buffer) {
        try {
            integerFuture.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return integerFuture;
    }

    /**
     * Reads string from the AJP packet.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    protected ReadFuture<String> readString(ByteBuffer buffer) {
        try {
            stringFuture.read(buffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringFuture;
    }

}
