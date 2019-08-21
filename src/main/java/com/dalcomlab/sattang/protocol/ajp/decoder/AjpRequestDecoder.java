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

import com.dalcomlab.sattang.protocol.ajp.AjpConstants;
import com.dalcomlab.sattang.protocol.http.decoder.HttpQueryStringDecoder;

import java.nio.ByteBuffer;

/**
 * This class is responsible for decoding the AJP forward request packet.
 * <p>
 * ╔════════════════════╦══════════════╗
 * ║      Meaning       ║     Type     ║
 * ╠════════════════════╬══════════════╣
 * ║ method             ║ byte         ║
 * ║ protocol           ║ string       ║
 * ║ req_uri            ║ string       ║
 * ║ remote_addr        ║ string       ║
 * ║ remote_host        ║ string       ║
 * ║ server_name        ║ string       ║
 * ║ server_port        ║ integer      ║
 * ║ is_ssl             ║ boolean      ║
 * ║ num_headers        ║ integer      ║
 * ║ request_headers    ║ array[]      ║
 * ║ attributes         ║ array[]      ║
 * ║ request_terminator ║ (byte)OxFF   ║
 * ╚════════════════════╩══════════════╝
 * <p>
 * The class does not store the AJP request information, and just passes
 * it to the {@link AjpRequestDecoder.Listener}. Therefore, to handle the
 * HTTP request, you must call the {@link #listen} method with the
 * instance of the {@link AjpRequestDecoder.Listener} implementation.
 *
 * <pre>
 *     AjpRequestDecoder decode = new AjpRequestDecoder();
 *     decode.listen(new AjpRequestDecoder.Listener {
 *          @Override
 *            public void setMethod(String method) throws Exception {};
 *          @Override
 *            public void setUri(String uri) throws Exception {};
 *          @Override
 *            public void setProtocol(String protocol) throws Exception {}
 *          .....
 *      });
 * </pre>
 *
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class AjpRequestDecoder extends AjpDecoder {
    private State state = State.MAGIC_NUMBER;
    private Context context = new Context();
    private Listener listener = null;

    /**
     *
     */
    public AjpRequestDecoder() {
    }

    /**
     * @param listener
     */
    public void listen(Listener listener) {
        this.listener = listener;
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
                    decodeMagicNumber(buffer, listener);
                    break;
                case DATA_SIZE:
                    decodeDataSize(buffer, listener);
                    break;
                case PREFIX_CODE:
                    decodePrefixCode(buffer, listener);
                    break;
                case METHOD:
                    decodeMethod(buffer, listener);
                    break;
                case PROTOCOL:
                    decodeProtocol(buffer, listener);
                    break;
                case REQUEST_URI:
                    decodeRequestUri(buffer, listener);
                    break;
                case REMOTE_ADDR:
                    decodeRemoteAddr(buffer, listener);
                    break;
                case REMOTE_HOST:
                    decodeRemoteHost(buffer, listener);
                    break;
                case SERVER_NAME:
                    decodeServerName(buffer, listener);
                    break;
                case SERVER_PORT:
                    decodeServerPort(buffer, listener);
                    break;
                case IS_SSL:
                    decodeIsSSL(buffer, listener);
                    break;
                case NUM_HEADERS:
                    decodeNumHeaders(buffer, listener);
                    break;
                case HEADERS:
                    decodeHeaders(buffer, listener);
                    break;
                case ATTRIBUTES:
                    decodeAttributes(buffer, listener);
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
     * @param listener
     * @return
     */
    private boolean decodeMagicNumber(ByteBuffer buffer, Listener listener) {
        return readInt(buffer).isDone((magic -> {
            state = State.DATA_SIZE;
        }));
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeDataSize(ByteBuffer buffer, Listener listener) {
        return readInt(buffer).isDone(size -> {
            state = State.PREFIX_CODE;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodePrefixCode(ByteBuffer buffer, Listener listener) {
        return readByte(buffer).isDone(code -> {
            state = State.METHOD;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeMethod(ByteBuffer buffer, Listener listener) {
        return readByte(buffer).isDone((code) -> {
            if (listener != null) {
                listener.setMethod(AjpConstants.getMethod(code));
            }
            state = State.PROTOCOL;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeProtocol(ByteBuffer buffer, Listener listener) {
        return readString(buffer).isDone(protocol -> {
            if (listener != null) {
                listener.setProtocol(protocol);
            }
            state = State.REQUEST_URI;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeRequestUri(ByteBuffer buffer, Listener listener) {
        return readString(buffer).isDone(uri -> {
            if (listener != null) {
                listener.setUri(uri);
            }
            state = State.REMOTE_ADDR;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeRemoteAddr(ByteBuffer buffer, Listener listener) {
        return readString(buffer).isDone(addr -> {
            state = State.REMOTE_HOST;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeRemoteHost(ByteBuffer buffer, Listener listener) {
        return readString(buffer).isDone(host -> {
            state = State.SERVER_NAME;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeServerName(ByteBuffer buffer, Listener listener) {
        return readString(buffer).isDone(name -> {
            state = State.SERVER_PORT;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeServerPort(ByteBuffer buffer, Listener listener) {
        return readInt(buffer).isDone(port -> {
            state = State.IS_SSL;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeIsSSL(ByteBuffer buffer, Listener listener) {
        return readByte(buffer).isDone(isSsl -> {
            if (listener != null) {
                if (isSsl != 0) {
                    listener.setScheme("https");
                } else {
                    listener.setScheme("http");
                }
            }
            state = State.NUM_HEADERS;
        });
    }

    /**
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeNumHeaders(ByteBuffer buffer, Listener listener) {
        return readInt(buffer).isDone(count -> {
            context.count = count;
            state = State.HEADERS;
        });
    }

    /**
     * https://tomcat.apache.org/connectors-doc/ajp/ajpv13a.html
     *
     * <pre>
     * ╔═════════════════╦════════════╦════════════════════════╗
     * ║      name       ║    code    ║       code name        ║
     * ╠═════════════════╬════════════╬════════════════════════╣
     * ║ accept          ║ 0xA001     ║ SC_REQ_ACCEPT          ║
     * ║ accept-charset  ║ 0xA002     ║ SC_REQ_ACCEPT_CHARSET  ║
     * ║ accept-encoding ║ 0xA003     ║ SC_REQ_ACCEPT_ENCODING ║
     * ║ accept-language ║ 0xA004     ║ SC_REQ_ACCEPT_LANGUAGE ║
     * ║ authorization   ║ 0xA005     ║ SC_REQ_AUTHORIZATION   ║
     * ║ connection      ║ 0xA006     ║ SC_REQ_CONNECTION      ║
     * ║ content-type    ║ 0xA007     ║ SC_REQ_CONTENT_TYPE    ║
     * ║ content-length  ║ 0xA008     ║ SC_REQ_CONTENT_LENGTH  ║
     * ║ cookie          ║ 0xA009     ║ SC_REQ_COOKIE          ║
     * ║ cookie2         ║ 0xA00A     ║ SC_REQ_COOKIE2         ║
     * ║ host            ║ 0xA00B     ║ SC_REQ_HOST            ║
     * ║ pragma          ║ 0xA00C     ║ SC_REQ_PRAGMA          ║
     * ║ referer         ║ 0xA00D     ║ SC_REQ_REFERER         ║
     * ║ user-agent      ║ 0xA00E     ║ SC_REQ_USER_AGENT      ║
     * ╚═════════════════╩════════════╩════════════════════════╝
     *
     *                        (yes)
     * start with 0xA0 ────┬─────── ╔════════════════╦══════════╗
     *                     │        ║ meaning        ║   type   ║
     *                     │        ╠════════════════╬══════════╣
     *                     │        ║ header value   ║  string  ║
     *                     │        ╚════════════════╩══════════╝
     *                     │
     *                     │   (no)
     *                     └──────── ╔════════════════╦══════════╗
     *                               ║ meaning        ║   type   ║
     *                               ╠════════════════╬══════════╣
     *                               ║ header name    ║  string  ║
     *                               ║ header value   ║  string  ║
     *                               ╚════════════════╩══════════╝
     * </pre>
     * <p>
     * the header parsing state diagram
     *
     * <pre>
     * State.Code ───> State.Name ───> State.Value  ───> State.Next
     *                                                       │
     *       <───────────────────────────────────────────────┘
     * </pre>
     *
     * @param buffer
     * @param listener
     * @return
     */
    private boolean decodeHeaders(ByteBuffer buffer, Listener listener) {
        Context header = context;
        while (header.count > 0) {
            boolean done = true;
            if (header.state.isCode()) {
                done = readInt(buffer).isDone(code -> {
                    header.code = code;
                    if ((code & 0xFF00) == 0xA000) {
                        String name = AjpConstants.getHeader(code & 0xFF); // remove 0xA000
                        header.name = name;
                        header.state = ContextState.VALUE;
                    } else {
                        // in this case, the code means the length of a header name.
                        byte[] length = new byte[2];
                        length[0] = (byte) ((code >>> 8) & 0xFF);
                        length[1] = (byte) (code & 0xFF);
                        readString(ByteBuffer.wrap(length));
                        header.state = ContextState.NAME;
                    }
                });

            } else if (header.state.isName()) {
                done = readString(buffer).isDone(name -> {
                    header.name = name;
                    header.state = ContextState.VALUE;
                });
            } else if (header.state.isValue()) {
                done = readString(buffer).isDone(value -> {
                    header.value = value;
                    header.state = ContextState.DONE;
                });
            } else if (header.state.isDone()) {
                //
                // we find the header and handle it.
                //
                handleHeader(header.code, header.name, header.value, listener);
                header.count--;
                header.reset();
            }

            if (!done) {
                return false;
            }
        }
        state = State.ATTRIBUTES;
        return true;
    }

    /**
     * @param code
     * @param name
     * @param listener
     */
    private void handleHeader(int code, String name, String value, Listener listener) {
        if (listener != null) {
            listener.addHeader(name, value);
        }
    }


    /**
     * https://tomcat.apache.org/connectors-doc/ajp/ajpv13a.html
     *
     * <pre>
     *   ╔═══════════════╦════════════╗
     *   ║     name      ║    code    ║
     *   ╠═══════════════╬════════════╣
     *   ║ context       ║ 0x01       ║
     *   ║ servlet_path  ║ 0x02       ║
     *   ║ remote_user   ║ 0x03       ║
     *   ║ auth_type     ║ 0x04       ║
     *   ║ query_string  ║ 0x05       ║
     *   ║ route         ║ 0x06       ║
     *   ║ ssl_cert      ║ 0x07       ║
     *   ║ ssl_cipher    ║ 0x08       ║
     *   ║ ssl_session   ║ 0x09       ║
     *   ║ req_attribute ║ 0x0A       ║
     *   ║ ssl_key_size  ║ 0x0B       ║
     *   ║ secret        ║ 0x0C       ║
     *   ║ stored_method ║ 0x0D       ║
     *   ║ are_done      ║ 0xFF       ║
     *   ╚═══════════════╩════════════╝
     *
     *                     (no)
     *  code == 0x0A ────┬─────────  ╔═════════════════╦══════════╗
     *                   │           ║ meaning         ║   type   ║
     *                   │           ╠═════════════════╬══════════╣
     *                   │           ║ attribute value ║  string  ║
     *                   │           ╚═════════════════╩══════════╝
     *                   │
     *                   │  (yes)
     *                   └────────── ╔═════════════════╦══════════╗
     *                               ║ meaning         ║   type   ║
     *                               ╠═════════════════╬══════════╣
     *                               ║ attribute name  ║  string  ║
     *                               ║ attribute value ║  string  ║
     *                               ╚═════════════════╩══════════╝
     * </pre>
     * <p>
     * the attribute parsing state diagram
     *
     * <pre>
     *              ┌─────> State.Stop
     *              │
     *  State.Code ─┴─────> State.Name ───> State.Value  ───> State.Next
     *                                                        │
     *        <───────────────────────────────────────────────┘
     * </pre>
     *
     * @param buffer
     * @param listener
     */
    private boolean decodeAttributes(ByteBuffer buffer, Listener listener) {
        Context attribute = context;
        while (true) {
            boolean done = true;
            if (attribute.state.isCode()) {
                done = readByte(buffer).isDone(code -> {
                    attribute.code = code;

                    //
                    // the code 0xFF means that the end of attributes.
                    //
                    if ((code & 0xFF) == 0xFF) {
                        attribute.state = ContextState.STOP;
                        return;
                    }

                    if (code == 0x0A) {
                        attribute.state = ContextState.NAME;
                    } else {
                        String name = AjpConstants.getAttribute(code);
                        attribute.name = name;
                        attribute.state = ContextState.VALUE;
                    }
                });

            } else if (attribute.state.isName()) {
                done = readString(buffer).isDone(name -> {
                    attribute.name = name;
                    attribute.state = ContextState.VALUE;
                });
            } else if (attribute.state.isValue()) {
                done = readString(buffer).isDone(value -> {
                    attribute.value = value;
                    attribute.state = ContextState.DONE;
                });
            } else if (attribute.state.isDone()) {
                //
                // we find the attribute and handle it.
                //
                handleAttribute(attribute.code, attribute.name, attribute.value, listener);
                attribute.reset();
            } else {
                break;
            }

            if (!done) {
                return false;
            }
        }

        state = State.COMPLETE;
        return true;
    }


    /**
     * @param code
     * @param name
     * @param value
     */
    private void handleAttribute(int code, String name, String value, Listener listener) {
        if (listener == null) {
            return;
        }

        switch (code) {
            case AjpConstants.ATTRIBUTE_CODE_CONTEXT:
                break;
            case AjpConstants.ATTRIBUTE_CODE_SERVLET_PATH:
                break;
            case AjpConstants.ATTRIBUTE_CODE_REMOTE_USER:
                break;
            case AjpConstants.ATTRIBUTE_CODE_AUTH_TYPE:
                break;
            case AjpConstants.ATTRIBUTE_CODE_QUERY_STRING: {
                try {
                    HttpQueryStringDecoder queryStringDecoder = new HttpQueryStringDecoder();
                    queryStringDecoder.listen(listener::addParameter);
                    queryStringDecoder.decode(value.getBytes());
                    queryStringDecoder.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            break;
            case AjpConstants.ATTRIBUTE_CODE_ROUTE:
                break;
            case AjpConstants.ATTRIBUTE_CODE_SSL_CERT:
                break;
            case AjpConstants.ATTRIBUTE_CODE_SSL_CIPHER:
                break;
            case AjpConstants.ATTRIBUTE_CODE_SSL_SESSION:
                break;
            case AjpConstants.ATTRIBUTE_CODE_REQ_ATTRIBUTE:
                break;
            case AjpConstants.ATTRIBUTE_CODE_KEY_SIZE:
                break;
            case AjpConstants.ATTRIBUTE_CODE_SECRET:
                break;
            case AjpConstants.ATTRIBUTE_CODE_STORED_METHOD:
                break;
            case AjpConstants.ATTRIBUTE_CODE_DONE:
                break;
        }
    }

    public enum State {
        MAGIC_NUMBER,
        DATA_SIZE,
        PREFIX_CODE,
        METHOD,
        PROTOCOL,
        REQUEST_URI,
        REMOTE_ADDR,
        REMOTE_HOST,
        SERVER_NAME,
        SERVER_PORT,
        IS_SSL,
        NUM_HEADERS,
        HEADERS,
        ATTRIBUTES,
        ERROR,
        COMPLETE
    }

    private enum ContextState {
        CODE,
        NAME,
        VALUE,
        DONE,
        STOP;

        public boolean isCode() {
            return this == CODE;
        }

        public boolean isName() {
            return this == NAME;
        }

        public boolean isValue() {
            return this == VALUE;
        }

        public boolean isDone() {
            return this == DONE;
        }

        public boolean isStop() {
            return this == STOP;
        }
    }


    /**
     *
     */
    public interface Listener {

        /**
         * Sets the HTTP method.
         *
         * @param method
         */
        void setMethod(String method);

        /**
         * Sets the HTTP URI.
         *
         * @param uri
         */
        void setUri(String uri);

        /**
         * Sets the HTTP protocol.
         *
         * @param protocol
         */
        void setProtocol(String protocol);

        /**
         * Sets the HTTP scheme.
         *
         * @param scheme
         */
        void setScheme(String scheme);

        /**
         * Adds the HTTP header.
         *
         * @param name
         * @param value
         */
        void addHeader(String name, String value);

        /**
         * Adds the HTTP parameter.
         *
         * @param name
         * @param value
         */
        void addParameter(String name, String value);
    }



    private final class Context {
        public String name;
        public String value;
        public int code = 0;
        public int count = 0;
        public ContextState state = ContextState.CODE;

        public void reset() {
            this.name = "";
            this.value = "";
            this.state = ContextState.CODE;
        }
    }
}
