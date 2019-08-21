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
package com.dalcomlab.sattang.protocol.http.decoder;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.function.BiConsumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpAcceptHeaderDecoder {

    private static final BitSet ALLOWED_QUALITY_CHARACTER = new BitSet();

    static {
        "q.0123456789=".chars().forEach(c -> ALLOWED_QUALITY_CHARACTER.set(c));
    }

    private BiConsumer<String, Double> listener;
    private State state = State.NAME;
    private Context context = new Context();


    /**
     *
     */
    public HttpAcceptHeaderDecoder() {

    }

    /**
     * @param listener
     */
    public void listen(BiConsumer<String, Double> listener) {
        this.listener = listener;
    }

    /**
     * @param buffer
     */
    public void decode(ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            switch (state) {
                case NAME:
                    parseName(buffer);
                    break;
                case QUALITY:
                    parseQuality(buffer);
                    break;
            }
        }
    }

    /**
     * @param buffer
     * @throws Exception
     */
    private void parseName(ByteBuffer buffer) throws Exception {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == ',') {
                context.done = true;
                context.skip = true;
                state = State.NAME;
                break;
            } else if (b == ';') {
                context.skip = true;
                state = State.QUALITY;
                break;
            }
        }

        update(head, buffer, context.name);
    }

    /**
     * @param buffer
     * @throws Exception
     */
    private void parseQuality(ByteBuffer buffer) throws Exception {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == ',') {
                context.skip = true;
                context.done = true;
                state = State.NAME;
                break;
            } else if (b == ' ' || b == '\t' || b == ';') {
                // TODO : thinking ';'
                context.skip = true;
                break;
            } else if (!isAllowedQualityCharacter((char) b)) {
                context.error = true;
            }
        }

        update(head, buffer, context.quality);
    }

    /**
     * @param head
     * @param buffer
     * @param output
     * @throws Exception
     */
    private void update(int head, ByteBuffer buffer, ByteArrayOutputStream output) throws Exception {

        int len = buffer.position() - head;
        if (context.skip) {
            context.skip = false;
            len--;
        }

        if (len > 0) {
            output.write(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            if (!context.error) {
                addLocale();
            }
            context.reset();
        }
    }

    /**
     * @throws Exception
     */
    public void close() throws Exception {
        if (!context.error) {
            addLocale();
        }
        state = State.NAME;
        context.reset();
    }

    /**
     *
     */
    private void addLocale() {
        if (listener == null) {
            return;
        }

        String name = context.name.toString().trim();
        String quality = context.quality.toString().trim();

        if (name.isEmpty()) {
            return;
        }

        if (quality.isEmpty()) {
            listener.accept(name, 1.0);
            return;
        }

        if (quality.startsWith("q=")) {
            try {
                Double q = Double.parseDouble(quality.substring(2));
                // the quality must be less than 1.
                if (q <= 1.0) {
                    listener.accept(name, q);
                }
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }

    /**
     * @param c
     * @return
     */
    private boolean isAllowedQualityCharacter(char c) {
        return ALLOWED_QUALITY_CHARACTER.get(c);
    }

    private enum State {
        NAME,
        QUALITY
    }

    /**
     *
     */
    public static class Context {
        private boolean done = false;
        private boolean skip = false;
        private boolean error = false;
        private ByteArrayOutputStream name = new ByteArrayOutputStream();
        private ByteArrayOutputStream quality = new ByteArrayOutputStream();

        public void reset() {
            this.done = false;
            this.skip = false;
            this.error = false;
            this.name.reset();
            this.quality.reset();
        }

    }
}
