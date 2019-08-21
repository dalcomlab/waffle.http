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

import java.nio.ByteBuffer;

/**
 * This class is responsible for decoding the AJP data packet.
 *
 * <p>
 * ╔════════════════════╦══════════════╗
 * ║      Meaning       ║     Type     ║
 * ╠════════════════════╬══════════════╣
 * ║ magic number       ║ byte         ║
 * ║ packet size        ║ integer      ║
 * ║ data size          ║ integer      ║
 * ║                    ║              ║
 * ║                    ║              ║
 * ║                    ║              ║
 * ║                    ║              ║
 * ╚════════════════════╩══════════════╝
 * <p>
 * The class does not store the AJP data, and just passes
 * it to the {@link AjpDataDecoder.Listener}. Therefore, to handle the
 * data, you must call the {@link #listen} method with the
 * instance of the {@link AjpDataDecoder.Listener} implementation.
 *
 * <pre>
 *     AjpDataDecoder decoder = new AjpDataDecoder();
 *     decoder.listen(new AjpDataDecoder.Listener {
 *          @Override
 *            public void setPacketSize(int size) {};
 *          @Override
 *            public void setDataSize(int size) {};
 *          @Override
 *            public void addData(String data) {};
 *      });
 *
 *      decoder.decode();
 * </pre>
 *
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpDataDecoder extends AjpDecoder {
    private int size = 0;
    private int amount = 0;
    private Listener listener = null;
    private State state = State.MAGIC_NUMBER;

    /**
     *
     */
    public AjpDataDecoder() {

    }

    /**
     * @param listener
     */
    public void listen(Listener listener) {
        this.listener = listener;
    }

    /**
     *
     */
    public void reset() {
        size = 0;
        amount = 0;
        state = State.MAGIC_NUMBER;
    }

    /**
     * @param buffer
     * @return
     * @throws Exception
     */
    public boolean decode(ByteBuffer buffer) throws Exception {
        if (state == State.COMPLETE) {
            return true;
        }

        while (buffer.hasRemaining()) {
            switch (state) {
                case MAGIC_NUMBER:
                    decodeMagicNumber(buffer);
                    break;
                case PACKET_SIZE:
                    decodePacketSize(buffer);
                    break;
                case DATA_SIZE:
                    decodeDataSize(buffer);
                    break;
                case DATA:
                    decodeData(buffer, listener);
                    break;
                case COMPLETE:
                    break;
            }

            if (state == State.COMPLETE) {
                return true;
            }
        }

        return false;
    }

    /**
     * @param buffer
     * @return
     */
    private boolean decodeMagicNumber(ByteBuffer buffer) {
        return readInt(buffer).isDone((magic -> {
            state = State.PACKET_SIZE;
        }));
    }

    /**
     * @param buffer
     * @return
     */
    private boolean decodePacketSize(ByteBuffer buffer) {
        return readInt(buffer).isDone(size -> {
            state = State.DATA_SIZE;
            if (listener != null) {
                listener.setPacketSize(size);
            }
        });
    }


    /**
     * @param buffer
     * @return
     */
    private boolean decodeDataSize(ByteBuffer buffer) {
        return readInt(buffer).isDone(size -> {
            this.state = State.DATA;
            this.size = size;
            if (listener != null) {
                listener.setDataSize(this.size);
            }
        });
    }


    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeData(ByteBuffer buffer, Listener listener) {
        int length = buffer.remaining();
        int remain = size - amount;
        if (remain > length) {
            if (listener != null) {
                listener.addData(buffer);
            }
            buffer.position(buffer.position() + length);
            amount += length;
        } else {
            int limit = buffer.limit();
            if (listener != null) {
                buffer.limit(buffer.position() + remain);
                listener.addData(buffer);
            }
            buffer.limit(limit);
            state = State.COMPLETE;
        }
        return true;
    }

    /**
     *
     */
    public enum State {
        MAGIC_NUMBER,
        PACKET_SIZE,
        DATA_SIZE,
        DATA,
        ERROR,
        COMPLETE
    }


    /**
     *
     */
    public interface Listener {
        /**
         * Sets the size of the packet.
         *
         * @param size
         */
        void setPacketSize(int size);

        /**
         * Sets the size of the data.
         *
         * @param size
         */
        void setDataSize(int size);

        /**
         * Adds the chunk data of the packet.
         *
         * @param buffer
         */
        void addData(ByteBuffer buffer);
    }
}
