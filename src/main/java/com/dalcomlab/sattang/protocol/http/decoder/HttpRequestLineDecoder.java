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
import com.dalcomlab.sattang.protocol.HttpBadRequestException;
import com.dalcomlab.sattang.protocol.HttpIllegalCharacterException;
import com.dalcomlab.sattang.protocol.HttpTooLongException;
import com.dalcomlab.sattang.protocol.http.HttpCharacter;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;


/**
 * This class is responsible for decoding the HTTP request line.
 *
 * <pre>
 *
 * ┌────────────────────┐
 * │    request line    │ <- decode by using {@link HttpRequestLineDecoder} decoder
 * ├────────────────────┤
 * │                    │
 * │                    │
 * │   request header   │
 * │                    │
 * │                    │
 * └────────────────────┘
 *
 * </pre>
 * <p>
 * The HTTP request line consists of four parts, as shown below.
 * <ul>
 *     <li>Method</li>
 *     <li>Uri</li>
 *     <li>Query string</li>
 *     <li>Protocol</li>
 * </ul>
 * <p>
 * The class does not store the HTTP request line, and just passes it to
 * the {@link HttpRequestLineDecoder.Listener}. Therefore, to handle the
 * HTTP request line, you must call the {@link #listen} method with the
 * instance of the {@link HttpRequestLineDecoder.Listener} implementation.
 *
 * <pre>
 *     HttpRequestLineDecoder decode = new HttpRequestLineDecoder();
 *     decode.listen(new HttpRequestLineDecoder.Listener {
 *          @Override
 *            public void setMethod(String method) throws Exception {};
 *          @Override
 *            public void setUri(String uri) throws Exception {};
 *          @Override
 *            public void setProtocol(String protocol) throws Exception {}
 *          @Override
 *            public void addParameter(String name, String value) throws Exception {}
 *      });
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpRequestLineDecoder implements Decoder<HttpRequestLineDecoder.Listener> {
    private final static int MAXSIZE = -1;
    private int maxRequestLineBytes = MAXSIZE;
    private int consumeBytes = 0;

    private Context context = new Context();
    private State state = State.METHOD;
    private HttpQueryStringDecoder queryDecoder;
    private Listener listener = null;

    /**
     *
     */
    public HttpRequestLineDecoder() {
        this.queryDecoder = new HttpQueryStringDecoder();
        this.context.reset();
    }

    /**
     * @param maxRequestLineBytes
     */
    public HttpRequestLineDecoder(int maxRequestLineBytes) {
        this.queryDecoder = new HttpQueryStringDecoder();
        this.context.reset();
        setMaxRequestLineBytes(maxRequestLineBytes);
    }

    /**
     * Sets the maximum request line bytes.
     *
     * @param maxRequestLineBytes
     * @return
     */
    public HttpRequestLineDecoder setMaxRequestLineBytes(int maxRequestLineBytes) {
        this.maxRequestLineBytes = maxRequestLineBytes;
        if (this.maxRequestLineBytes <= 0) {
            this.maxRequestLineBytes = MAXSIZE;
        }
        return this;
    }

    /**
     * Sets the listener{@link HttpRequestLineDecoder.Listener} for the decoder.
     * <p>
     * When an element of the request line is found in the decode process, the appropriate
     * method of the listener is called with the element. Specifically, the querystring
     * part is separated by name and value, and the {@link HttpRequestLineDecoder#addParameter}
     * method is called.
     * <p>
     * <ol>
     * <li>method : the {@link HttpRequestLineDecoder.Listener#setMethod(String)} method will be called.</li>
     * <li>uri : the {@link HttpRequestLineDecoder.Listener#setUri(String)} method will be called. </li>
     * <li>query string : the {@link HttpRequestLineDecoder#addParameter(String, String)} method will be called. </li>
     * <li>protocol : the {@link HttpRequestLineDecoder#setProtocol(String)} method will be called.</li>
     * </ol>
     * <p>
     * The listener can not be null.
     *
     * @param listener
     */
    @Override
    public HttpRequestLineDecoder listen(Listener listener) {
        assert (listener != null) : "the listener cannot be null pointer.";
        this.listener = listener;
        return this;
    }


    /**
     * Decodes the request line.
     *
     * @param buffer
     * @throws Exception
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            switch (state) {
                case METHOD:
                    decodeMethod(buffer);
                    break;
                case URI:
                    decodeUri(buffer);
                    break;
                case QUERYSTRING:
                    decodeQueryString(buffer);
                    break;
                case PROTOCOL:
                    decodeProtocol(buffer);
                    break;
            }

            if (state == State.COMPLETE) {
                return true;
            }
        }
        return false;
    }

    /**
     *
     */
    public void reset() {
        state = State.METHOD;
        queryDecoder.reset();
        context.reset();
    }


    /**
     * @throws Exception
     */
    @Override
    public void close() throws Exception {
        queryDecoder.close();
    }


    /**
     * Decodes the METHOD in a request line.
     * <pre>
     * [space][method][space][uri][?][querystring][space][protocol][cr/lf]
     *  ~~~~~~~~~~~~~
     * </pre>
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeMethod(ByteBuffer buffer) throws Exception {
        //
        // the spec don't allow multiple spaces or tabs, but we allow some clients to include
        // multiple spaces. The tomcat also works this way.
        //
        if (context.skipWhitespace) {
            boolean includeCrlf = true;
            if (!skipWhitespace(buffer, includeCrlf)) {
                return;
            }
            context.skipWhitespace = false;
        }

        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ' ' || b == '\t') {
                context.skip = true;
                context.done = true;
                break;
            } else if (b == '\n') {
                throw new HttpBadRequestException("The HTTP InboundChannel Line missing URI.");
            }
        }

        int len = buffer.position() - head;
        if (context.skip) {
            context.skip = false;
            len--;
        }

        if (len > 0) {
            context.output.write(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            setMethod(context.output.toString());
            context.reset();
            state = State.URI;
        }

    }


    /**
     * Decodes the URI part in a request line.
     * <pre>
     * [space][method][space][uri][?][querystring][space][protocol][cr/lf]
     *                ~~~~~~~~~~~~~~~
     * </pre>
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeUri(ByteBuffer buffer) throws Exception {
        //
        // the spec don't allow multiple spaces or tabs, but we allow some clients to include
        // multiple spaces. The tomcat also works this way.
        //
        if (context.skipWhitespace) {
            boolean includeCrlf = false;
            if (!skipWhitespace(buffer, includeCrlf)) {
                return;
            }
        }
        context.skipWhitespace = false;

        //
        // the URI has three patterns below.
        // case 1) /context/test.jsp HTTP/1.1          : without a query string
        // case 2) /context/test.jsp?a=a&b=b HTTP/1.1  : with a query string
        // case 3) /context/test.jsp?a=a&b=b\r\n       : HTTP/0.9
        //
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ' ' || b == '\t') {
                context.done = true;
                context.skip = true;
                state = State.PROTOCOL;
                break;
            } else if (b == '?') {
                context.done = true;
                context.skip = true;
                state = State.QUERYSTRING;
                break;
            } else if (b == '\r') {
                context.skip = true;
                break;
            } else if (b == '\n') {
                // assume that missing protocol is the HTTP/0.9
                setProtocol("HTTP/0.9");
                context.done = true;
                context.skip = true;
                state = State.COMPLETE;
                break;
            } else if (b == '+' || b == '%' || b > 127) {
                context.decode = true;
            } else if (!HttpCharacter.isAllowedPathCharacter((char) b)) {
                throw new HttpIllegalCharacterException("The illegal character[" + (char) b + "] exists in a uri.");
            }
        }

        int len = buffer.position() - head;
        if (context.skip) {
            context.skip = false;
            len--;
        }

        if (len > 0) {
            context.output.write(buffer.array(), buffer.arrayOffset() + head, len);
        }


        if (context.done) {
            if (context.output.size() == 0) {
                throw new HttpBadRequestException("The HTTP InboundChannel Line missing URI.");
            }
            setUri(decodeURL(context.output.toString(), context.decode));
            context.reset();
        }
    }


    /**
     * Decodes the QUERYSTRING part in a request line.
     * <pre>
     * [space][method][space][uri][?][querystring][space][protocol][cr/lf]
     *                               ~~~~~~~~~~~~~
     * </pre>
     *
     * @param buffer
     * @return
     */
    private void decodeQueryString(ByteBuffer buffer) throws Exception {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ' ' || b == '\t') {
                context.done = true;
                context.skip = true;
                state = State.PROTOCOL;
                break;
            } else if (b == '\r') {
                context.skip = true;
                break;
            } else if (b == '\n') {
                // assume that missing protocol is the HTTP/0.9
                setProtocol("HTTP/0.9");
                context.done = true;
                context.skip = true;
                state = State.COMPLETE;
                break;
            }
            //else if (!HttpCharacter.isAllowedQueryCharacter((char) b)) {
            //    throw new HttpIllegalCharacterException("The illegal character[" + (char) b + "] exists in a query string.");
            //}

        }

        int len = buffer.position() - head;
        if (context.skip) {
            context.skip = false;
            len--;
        }

        if (len > 0) {
            queryDecoder.listen(this::addParameter);
            queryDecoder.decode(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            queryDecoder.close();
            context.reset();
        }
    }


    /**
     * Decodes the PROTOCOL part in a request line.
     * <pre>
     * [space][method][space][uri][?][querystring][space][protocol][cr/lf]
     *                                           ~~~~~~~~~~~~~~~~~~~~~~~~
     * </pre>
     *
     * @param buffer
     * @return
     */
    private void decodeProtocol(ByteBuffer buffer) throws Exception {
        //
        // The spec don't allow multiple spaces or tabs, but we allow some clients to include
        // multiple spaces. The tomcat also works in this way.
        //
        if (context.skipWhitespace) {
            boolean includeCrlf = false;
            if (!skipWhitespace(buffer, includeCrlf)) {
                return;
            }
            context.skipWhitespace = false;
        }

        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == '\r') {
                context.skip = true;
                break;
            } else if (b == '\n') {
                context.done = true;
                context.skip = true;
                break;
            }
        }

        int len = buffer.position() - head;
        if (context.skip) {
            context.skip = false;
            len--;
        }


        if (len > 0) {
            context.output.write(buffer.array(), buffer.arrayOffset() + head, len);
        }

        if (context.done) {
            setProtocol(context.output.toString());
            context.reset();
            state = State.COMPLETE;
        }
    }


    /**
     * Skips the white spaces in the given byte buffer.
     *
     * @param buffer
     * @param includeCrlf
     * @return
     */
    private boolean skipWhitespace(ByteBuffer buffer, boolean includeCrlf) {
        byte b;
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ' ' || b == '\t') {
                continue;
            }

            if (includeCrlf) {
                if (b == '\r' || b == '\n') {
                    continue;
                }
            }
            buffer.position(buffer.position() - 1); // move back for next parsing.
            consumeByte(-1);
            return true;
        }

        return false;
    }

    /**
     * @param buffer
     * @return
     * @throws HttpTooLongException
     */
    private byte consumeByte(ByteBuffer buffer) throws HttpTooLongException {
        if (maxRequestLineBytes != MAXSIZE) {
            if (consumeBytes >= consumeBytes) {
                throw new HttpTooLongException("The HTTP request line exceeds [" + maxRequestLineBytes + "] bytes.");
            }
        }
        consumeByte(1);
        return buffer.get();
    }

    /**
     *
     */
    private void consumeByte(int amount) {
        consumeBytes += amount;
    }


    /**
     * Sets the HTTP method. This method must throw exception if the method
     * is invalid.
     *
     * @param method
     * @return
     */
    private void setMethod(String method) throws Exception {
        if (listener == null) {
            return;
        }
        listener.setMethod(method);
    }

    /**
     * Sets the HTTP URI. This method must throw exception if the URI is
     * invalid.
     *
     * @param uri
     * @return
     */
    private void setUri(String uri) throws Exception {
        if (listener == null) {
            return;
        }
        listener.setUri(uri);
    }

    /**
     * Sets the HTTP protocol. This method must throw exception false if the
     * protocol is invalid.
     *
     * @param protocol
     * @return
     */
    private void setProtocol(String protocol) throws Exception {
        if (listener == null) {
            return;
        }
        listener.setProtocol(protocol);
    }


    /**
     * Adds the HTTP parameter. This method must throw exception false if the
     * parameter is invalid.
     *
     * @param name
     * @param value
     * @return
     */
    private void addParameter(String name, String value) {
        if (listener == null) {
            return;
        }
        try {
            listener.addParameter(name, value);
        } catch (Exception e) {

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

    private enum State {
        METHOD,
        URI,
        QUERYSTRING,
        PROTOCOL,
        COMPLETE
    }

    /**
     *
     */
    public interface Listener {

        /**
         * Sets the HTTP method. This method must throw exception if the method
         * is invalid.
         *
         * @param method
         */
        void setMethod(String method) throws Exception;

        /**
         * Sets the HTTP URI. This method must throw exception if the URI is
         * invalid.
         *
         * @param uri
         */
        void setUri(String uri) throws Exception;

        /**
         * Sets the HTTP protocol. This method must throw exception false if the
         * protocol is invalid.
         *
         * @param protocol
         */
        void setProtocol(String protocol) throws Exception;

        /**
         * Adds the HTTP parameter. This method must throw exception false if the
         * parameter is invalid.
         *
         * @param name
         * @param value
         */
        void addParameter(String name, String value) throws Exception;
    }

    /**
     *
     */
    public static class Context {
        private ByteArrayOutputStream output = new ByteArrayOutputStream();
        private boolean done = false;
        private boolean skip = false;
        private boolean decode = false;
        private boolean skipWhitespace = true;

        public void reset() {
            this.output.reset();
            this.done = false;
            this.skip = false;
            this.decode = false;
            this.skipWhitespace = true;
        }

    }
}