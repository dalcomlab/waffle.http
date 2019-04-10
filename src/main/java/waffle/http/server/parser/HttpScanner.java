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

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpScanner {
    private OutputStream output;
    private Listener listener;
    private ByteBuffer buffer;
    private State state = State.REQUEST_LINE;
    private int count = 0;
    private byte[] ending = new byte[4];
    private Parser bodyParser = null;

    /**
     * @param output
     * @param listener
     */
    public HttpScanner(final OutputStream output, final Listener listener) {
        listen(output, listener);
    }

    /**
     * @param output
     * @param listener
     */
    public void listen(final OutputStream output, final Listener listener) {
        this.output = output;
        this.listener = listener;
    }

    public void bodyParser(Parser parser) {
        this.bodyParser = parser;
    }

    /**
     * The method scan single line until meeting a end of line.
     *
     * @throws IOException
     */
    public void scan(final ByteBuffer buffer) throws Exception {
        this.buffer = buffer;
        // Each state should consumes a sequence of bytes to improve parsing performance.
        while (buffer.hasRemaining()) {
            switch (state) {
                case REQUEST_LINE:
                    scanRequestLine();
                    break;
                case REQUEST_HEADER:
                    scanRequestHeader();
                    break;
                case REQUEST_BODY:
                    scanRequestBody();
                    break;
            }
        }
    }

    /**
     *
     */
    public void scanRequestLine() throws Exception {
        byte b;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                count++;
                find = true;
                break;
            }
            if (b == '\r') {
                count++;
            }
        }

        int len = buffer.position() - head;
        len -= count; // remove '/r/n' or '/n'

        // flush buffer
        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            complete();
            changeState(State.REQUEST_HEADER);
        }
    }

    /**
     *
     */
    public void scanRequestHeader() throws Exception {
        byte b;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\r' || b == '\n') {
                ending[count] = b;
                // case 1) header + '\n\n' : maybe an informal case
                if (count == 1) {
                    if (ending[0] == '\n' && ending[1] == '\n') {
                        find = true;
                        break;
                    }
                }

                // case 2) header + '\r\n\n' : maybe an informal case
                if (count == 2) {
                    if (ending[0] == '\r' && ending[1] == '\n' && ending[2] == '\n') {
                        find = true;
                        break;
                    }
                }

                // case 2) header + '\r\n\r\n'
                if (count == 3) {
                    if (ending[0] == '\r' && ending[1] == '\n' && ending[2] == '\r' && ending[3] == '\n') {
                        find = true;
                        break;
                    }
                }

                count++;
            } else {
                count = 0;
            }
        }

        int len = buffer.position() - head;

        if (find) {
            len -= count; // remove '/r/n/r/n'
        }

        // flush buffer
        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            complete();
            changeState(State.REQUEST_BODY);
        }
    }

    /**
     *
     */
    public void scanRequestBody() throws Exception {
        if (bodyParser == null) {
            if (buffer.hasRemaining()) {
                int len = buffer.remaining();
                write(buffer.array(), buffer.position(), len);
                buffer.position(buffer.limit());
            }
        } else {
            bodyParser.parse(buffer);
        }
    }

    /**
     * @param state
     */
    private void changeState(final State state) {
        this.state = state;
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    private void write(byte b[], int off, int len) throws Exception {
        if (output != null) {
            output.write(b, off, len);
        }
    }

    /**
     *
     */
    public void complete() throws Exception {
        if (listener != null) {
            listener.onComplete(this.state, output, this);
        }
    }

    public enum State {
        REQUEST_LINE,
        REQUEST_HEADER,
        REQUEST_BODY
    }

    /**
     *
     */
    public interface Listener {
        void onComplete(State state, OutputStream output, HttpScanner scanner) throws Exception;
    }
}
