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

import com.dalcomlab.sattang.protocol.Decoder;
import com.dalcomlab.sattang.protocol.HttpTooLongException;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpQueryStringDecoder implements Decoder<BiConsumer<String, String>> {
    private static final int MAXSIZE = -1;
    private int maxParameterCount = MAXSIZE;
    private int parameterCount = 0;
    private State state = State.NAME;
    private Context context = new Context();
    private BiConsumer<String, String> listener = null;

    /**
     * Creates new instance
     */
    public HttpQueryStringDecoder() {
    }

    /**
     * Creates new instance
     *
     * @param maxParameterCount
     */
    public HttpQueryStringDecoder(int maxParameterCount) {
        this.maxParameterCount = maxParameterCount;
        if (this.maxParameterCount <= 0) {
            this.maxParameterCount = MAXSIZE;
        }
    }


    /**
     * @param listener
     */
    @Override
    public HttpQueryStringDecoder listen(BiConsumer<String, String> listener) {
        assert (listener != null) : "the listener cannot be null pointer.";
        this.listener = listener;
        return this;
    }


    /**
     * @param parameters
     */
    public HttpQueryStringDecoder listen(Map<String, List<String>> parameters) {
        assert (parameters != null) : "the parameter cannot be null pointer.";
        final BiConsumer<String, String> listener = (name, value) -> {
            if (!parameters.containsKey(name)) {
                parameters.put(name, new ArrayList<>());
            }
            parameters.get(name).add(value);
        };

        return listen(listener);
    }

    /**
     * Decodes the query string.
     *
     * @param buffer
     * @throws Exception
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            switch (state) {
                case NAME:
                    decodeName(buffer);
                    break;
                case VALUE:
                    decodeValue(buffer);
                    break;
            }
        }
        return true;
    }

    /**
     *
     */
    public void close() throws Exception {
        addParameter();
    }


    /**
     * Resets all states of the parser.
     */
    public void reset() {
        context.reset();
        state = State.NAME;
    }


    /**
     * Decodes the name part in the query string.
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeName(ByteBuffer buffer) throws Exception {
        byte b = 0;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '=' || b == '&') {
                context.done = true;
                break;
            } else if (b == '+' || b == '%' || b > 127) {
                context.nameNeedDecode = true;
            }
        }

        int len = buffer.position() - head;
        if (context.done) {
            len--;
        }

        if (len > 0) {
            context.name.write(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            if (b == '&') {
                addParameter();
                context.reset();
                state = State.NAME;
            } else {
                state = State.VALUE;
            }
            context.done = false;
        }
    }

    /**
     * Decodes the value part in the query string.
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeValue(ByteBuffer buffer) throws Exception {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '&') {
                context.done = true;
                break;
            } else if (b == '+' || b == '%' || b > 127) {
                context.valueNeedDecode = true;
            }
        }

        int len = buffer.position() - head;
        if (context.done) {
            len--;
        }

        if (len > 0) {
            context.value.write(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            addParameter();
            context.reset();
            state = State.NAME;
            context.done = false;
        }
    }

    /**
     * @throws Exception
     */
    private void addParameter() throws Exception {
        if (listener != null) {
            if (context.name.size() > 0) {
                String name = context.name.toString();
                String value = context.value.toString();
                listener.accept(decodeURL(name, context.nameNeedDecode), decodeURL(value, context.valueNeedDecode));
            }
        }

        if (maxParameterCount != MAXSIZE) {
            if (parameterCount >= maxParameterCount) {
                throw new HttpTooLongException("Too many parameters");
            }
        }
        if (context.name.size() > 0) {
            parameterCount++;
        }
    }

    /**
     * Decodes the given text.
     *
     * @param text
     * @return
     */
    private String decodeURL(String text, boolean needDecode) {
        if (!needDecode) {
            return text;
        }

        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return text;
    }

    public enum State {
        NAME,
        VALUE
    }


    /**
     *
     */
    public static class Context {
        public boolean done = false;
        public boolean nameNeedDecode = false;
        public boolean valueNeedDecode = false;
        public ByteArrayOutputStream name = new ByteArrayOutputStream();
        public ByteArrayOutputStream value = new ByteArrayOutputStream();

        public void reset() {
            this.nameNeedDecode = false;
            this.valueNeedDecode = false;
            this.value.reset();
            this.name.reset();
            this.done = false;
        }
    }
}
