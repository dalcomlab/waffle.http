/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server.parser;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
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
public class HttpFormParser implements Parser {

    private HttpFormScanner scanner;
    private BiConsumer<String, String> listener;
    private String name = null;
    private int parameterCount = 0;
    private int maxParameterCount = -1;
    private boolean closed = false;

    /**
     * Create new instance.
     */
    public HttpFormParser(final BiConsumer<String, String> listener) {
        assert (listener != null);
        this.listener = listener;
        this.scanner = new HttpFormScanner(new ByteArrayOutputStream(), new NameHandler());
    }

    /**
     * Create new instance.
     */
    public HttpFormParser(final Map<String, List<String>> parameters) {
        assert (parameters != null);
        this.listener = (name, value) -> {
            List<String> values = parameters.get(name);
            if (values == null) {
                values = new ArrayList<>(1);
                parameters.put(name, values);
            }
            values.add(value);
        };

        this.scanner = new HttpFormScanner(new ByteArrayOutputStream(), new NameHandler());
    }

    /**
     *
     * @param maxParameterCount
     */
    public void setMaxParameterCount(int maxParameterCount) {
        this.maxParameterCount = maxParameterCount;
    }

    /**
     * Parses the form by using then given buffer.
     *
     * @param buffer
     */
    @Override
    public void parse(final ByteBuffer buffer) throws Exception {
        assert (buffer != null);
        if (closed) {
            throw new IllegalStateException("The parser is closed.");
        }

        scanner.scan(buffer);
    }

    /**
     *
     */
    @Override
    public void close() throws Exception {
        if (!closed) {
            scanner.notifyComplete();
        }
        closed = true;
    }


    /**
     * Adds the parameter.
     *
     * @param name
     * @param value
     */
    private void addParameter(String name, String value) throws TooManyParametersException {
        if (name == null || name.length() == 0) {
            return;
        }

        if (maxParameterCount != -1 && maxParameterCount < parameterCount) {
            throw new TooManyParametersException(parameterCount);
        }

        if (listener != null) {
            listener.accept(decodeURL(name), decodeURL(value));
        }

        parameterCount++;
    }

    /**
     * Decodes the given text.
     *
     * @param text
     * @return
     */
    private String decodeURL(final String text) {
        try {
            return URLDecoder.decode(text, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            // ignore
        }
        return text;
    }

    /**
     * This class handles the name in the form.
     */
    private final class NameHandler implements HttpFormScanner.Listener {

        @Override
        public void onComplete(HttpFormScanner.State state, OutputStream output, HttpFormScanner scanner) throws Exception {
            ByteArrayOutputStream out = (ByteArrayOutputStream) output;
            if (out != null) {
                name = new String(out.toByteArray());
            }

            if (state == HttpFormScanner.State.VALUE) {
                scanner.listen(new ByteArrayOutputStream(), new ValueHandler());
            }

            // This case can happen if the name have no value.
            // a&b&c
            if (state == HttpFormScanner.State.NAME) {
                addParameter(name, "");
                scanner.listen(new ByteArrayOutputStream(), new NameHandler());
            }
        }
    }

    /**
     * This class handles the value in the form.
     */
    private final class ValueHandler implements HttpFormScanner.Listener {
        @Override
        public void onComplete(HttpFormScanner.State state, OutputStream output, HttpFormScanner scanner) throws Exception {
            ByteArrayOutputStream out = (ByteArrayOutputStream) output;
            if (out != null) {
                addParameter(name, new String(out.toByteArray()));
            }
            scanner.listen(new ByteArrayOutputStream(), new NameHandler());
        }
    }


    /**
     *
     */
    public static class TooManyParametersException extends Exception {
        private TooManyParametersException(int count) {
            super(String.format("Too many parameters [%d]", count));
        }
    }
}
