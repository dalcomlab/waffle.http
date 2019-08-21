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
import com.dalcomlab.sattang.protocol.HttpIllegalCharacterException;
import com.dalcomlab.sattang.protocol.HttpTooLongException;
import com.dalcomlab.sattang.protocol.http.HttpCharacter;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.function.BiConsumer;


/**
 * This class is a decoder for the HTTP header.
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpHeaderDecoder implements Decoder<BiConsumer<String, String>> {
    private static final int MAXSIZE = -1;
    private int maxHeaderBytes = MAXSIZE;
    private int maxHeaderCount = MAXSIZE;
    private int consumeBytes = 0;
    private int headerCount = 0;
    private BiConsumer<String, String> listener = null;
    private State state = State.NAME;
    private Context context = new Context();

    /**
     *
     */
    public HttpHeaderDecoder() {

    }

    /**
     * @param maxHeaderBytes
     * @param maxHeaderCount
     */
    public HttpHeaderDecoder(int maxHeaderBytes, int maxHeaderCount) {
        setMaxHeaderBytes(maxHeaderBytes);
        setMaxHeaderCount(maxHeaderCount);
    }

    /**
     * Sets the maximum header bytes.
     * <p>
     * If the value of maxHeaderBytes is 0 or -1, there is no limit to the size of
     * header bytes. An {@link HttpTooLongException} exception is thrown if the total
     * bytes of the header are larger than the maximum bytes during decoding.
     *
     * @param maxHeaderBytes
     * @return
     */
    public HttpHeaderDecoder setMaxHeaderBytes(int maxHeaderBytes) {
        this.maxHeaderBytes = maxHeaderBytes;
        if (this.maxHeaderBytes <= 0) {
            this.maxHeaderBytes = MAXSIZE;
        }
        return this;
    }

    /**
     * Sets the maximum header count.
     * <p>
     * If maxHeaderCount is 0 or -1, there is no limit to the number of headers.
     * An {@link HttpTooLongException} exception is thrown if the number of headers
     * is greater than the maximum number of headers during decoding.
     *
     * @param maxHeaderCount
     * @return
     */
    public HttpHeaderDecoder setMaxHeaderCount(int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
        if (this.maxHeaderCount <= 0) {
            this.maxHeaderCount = MAXSIZE;
        }
        return this;
    }

    /**
     * Sets the listener{@link BiConsumer<String, String>} for the decoder.
     *
     * If the header is found in the decode process, the given listener is
     * called with the header name and value.
     *
     * The listener can not be null.
     *
     * @param listener
     */
    @Override
    public HttpHeaderDecoder listen(BiConsumer<String, String> listener) {
        assert (listener != null) : "the listener cannot be null pointer.";
        this.listener = listener;
        return this;
    }

    /**
     * Sets the {@link Map<String, String>} for the decoder.
     *
     * If the header is found in the decode process, add the header name and
     * value to the map. This method will call {@link #listen(BiConsumer)}
     * internally.
     *
     * The headers can not be null.
     *
     * @param headers
     */
    public void listen(Map<String, String> headers) {
        assert (headers != null) : "the header cannot be null pointer.";
        final BiConsumer<String, String> listener = (name, value) -> {
            headers.put(name, value);
        };

        listen(listener);
    }

    /**
     * Decodes the HTTP header.
     * <p>
     * An {@link HttpTooLongException} exception is thrown if the number of
     * headers is greater than the maximum number of headers during decoding.
     * <p>
     * An {@link HttpTooLongException} exception is thrown if the total bytes of t
     * he header are larger than the maximum bytes during decoding.
     *
     * @param buffer
     * @return
     * @throws Exception
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        while (buffer.hasRemaining()) {
            switch (state) {
                case NEXT:
                    decodeNext(buffer);
                    break;
                case NAME:
                    decodeName(buffer);
                    break;
                case VALUE:
                    decodeValue(buffer);
                    break;
                default:
                    break;
            }
        }
        return true;
    }

    /**
     * @param buffer
     * @throws Exception
     */
    private void decodeNext(ByteBuffer buffer) throws Exception {
        if (!buffer.hasRemaining()) {
            return;
        }

        //
        // the folded header start with the space(' ') or the tab('\t')
        // the content-type below is a folded header value.

        // Content-Type : text/html;
        //                charset=UTF-8   <------ folded
        // Content-Length : 1024
        //

        byte b = consumeByte(buffer);

        // it is a folded value, so change a state to a value.
        if (b == ' ' || b == '\t') {
            context.value.write(b);
            context.folded = true;
            state = State.VALUE;
            return;
        }

        addHeader();

        // it is not a folded head, so change a state to a name.
        context.reset();
        buffer.position(buffer.position() - 1);
        consumeByte(-1);
        state = State.NAME;
    }


    /**
     * Decodes the name part in the HTTP header.
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeName(ByteBuffer buffer) throws Exception {
        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ':') {
                context.skip = true;
                context.done = true;
                state = State.VALUE;
                break;
            }

            if (!HttpCharacter.isAllowedHeadCharacter((char) b)) {
                throw new HttpIllegalCharacterException("The illegal character[" + (char) b + "] exists in header name.");
            }
        }

        update(head, buffer, context.name);
    }

    /**
     * Decodes the value part in the HTTP header.
     *
     * @param buffer
     * @throws Exception
     */
    private void decodeValue(ByteBuffer buffer) throws Exception {
        // remove linear white space if the value is folded.
        //
        // Content-Type : text/html;
        //                charset=UTF-8   <------ folded
        // ~~~~~~~~~~~~~~
        //       ^
        //   remove here.
        //
        if (context.folded) {
            if (!skipWhitespace(buffer)) {
                return;
            }
            context.folded = false;
        }

        byte b;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == '\r') {
                context.skip = true;
                break;
            } else if (b == '\n') {
                context.skip = true;
                context.done = true;
                state = State.NEXT;
                break;
            }
        }

        update(head, buffer, context.value);

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
            context.done = false;
        }
    }


    /**
     * Add the header.
     * <p>
     * An {@link HttpTooLongException} exception is thrown if the number of headers is
     * greater than the maximum number of headers.
     *
     * @throws HttpTooLongException
     */
    private void addHeader() throws HttpTooLongException {
        // don't do this. we allow the empty header name to be.
        //if (context.name.size() == 0) {
        //    return;
        //}

        if (maxHeaderCount != MAXSIZE) {
            if (headerCount >= maxHeaderCount) {
                throw new HttpTooLongException("The HTTP header count exceeds [" + maxHeaderCount + "].");
            }
        }

        headerCount++;

        if (listener != null) {
            String name = context.name.toString();
            String value = context.value.toString().trim();
            listener.accept(name, value);
        }
    }

    /**
     * @param buffer
     * @return
     * @throws Exception
     */
    private boolean skipWhitespace(ByteBuffer buffer) throws Exception {
        byte b;
        while (buffer.hasRemaining()) {
            b = consumeByte(buffer);
            if (b == ' ' || b == '\t') {
                continue;
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
        if (maxHeaderBytes != MAXSIZE) {
            if (consumeBytes >= maxHeaderBytes) {
                throw new HttpTooLongException("The HTTP header exceeds [" + maxHeaderBytes + "] bytes.");
            }
        }
        consumeByte(1);
        return buffer.get();
    }

    /**
     * @param amount
     */
    private void consumeByte(int amount) {
        consumeBytes += amount;
    }

    /**
     * Closes the parser.
     *
     * @return
     */
    public void close() throws Exception {
        addHeader();
        reset();
    }


    /**
     * Resets all states of the parser.
     */
    public void reset() {
        context.reset();
        state = State.NAME;
    }

    public enum State {
        NEXT,
        NAME,
        VALUE,
        COMPLETE
    }

    /**
     *
     */
    public static class Context {
        public boolean done = false;
        public ByteArrayOutputStream name = new ByteArrayOutputStream();
        public ByteArrayOutputStream value = new ByteArrayOutputStream();
        public boolean folded = true;
        public boolean skip = false;

        public void reset() {
            this.value.reset();
            this.name.reset();
            this.done = false;
            this.folded = true;
            this.skip = false;
        }
    }

}
