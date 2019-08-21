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
public class AjpDataWriter {

    private ByteBuffer buffer;
    private int pos = 0;

    public AjpDataWriter(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    /**
     *
     * @param p
     * @param b
     */
    public void putByte(int p, byte b) {
        buffer.put(p, b);
    }

    /**
     *
     * @param p
     * @param i
     */
    public void putInt(int p, int i) {
        putByte(p + 0, (byte) ((i >>> 8) & 0xFF));
        putByte(p + 1, (byte) (i & 0xFF));
    }

    /**
     *
     * @param str
     */
    public void writeString(String str) {
        writeInt(str.length());
        writeBytes(str.getBytes());
        writeByte((byte) 0);
    }

    /**
     *
     * @param b
     */
    public void writeBoolean(boolean b) {
        if (b)
            writeByte((byte) 1);
        else
            writeByte((byte) 0);
    }

    /**
     *
     * @param b
     */
    public void writeByte(byte b) {
        buffer.put(b);
        pos++;
    }

    /**
     *
     * @param bytes
     */
    public void writeBytes(byte[] bytes) {
        buffer.put(bytes);
        pos += bytes.length;
    }

    /**
     *
     * @param bytes
     * @param offset
     * @param length
     */
    public void writeBytes(byte[] bytes, int offset, int length) {
        buffer.put(bytes, offset, length);
        pos += length;
    }

    /**
     *
     * @param i
     */
    public void writeInt(int i) {
        writeByte((byte) ((i >>> 8) & 0xFF));
        writeByte((byte) (i & 0xFF));
    }

    /**
     *
     */
    public void flush() {
        putInt(2, pos - 4);
    }

}
