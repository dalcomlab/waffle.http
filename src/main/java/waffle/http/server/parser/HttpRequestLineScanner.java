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
public class HttpRequestLineScanner {
    private OutputStream output;
    private Listener listener;
    private ByteBuffer buffer;
    private State state = State.REQUEST_METHOD;

    public enum State {
        REQUEST_METHOD,
        REQUEST_URI,
        REQUEST_PROTOCOL
    }

    /**
     * @param output
     * @param listener
     */
    public HttpRequestLineScanner(final OutputStream output, final Listener listener) {
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

    /**
     * The method scan single line until meeting a end of line.
     *
     * @throws IOException
     */
    public boolean scan(final ByteBuffer buffer) throws Exception {
        this.buffer = buffer;
        // Each state should consumes a sequence of bytes to improve parsing performance.
        while (buffer.hasRemaining()) {
            switch (state) {
                case REQUEST_METHOD:
                    scanRequestMethod();
                    break;
                case REQUEST_URI:
                    scanRequestUri();
                    break;
                case REQUEST_PROTOCOL:
                    scanRequestProtocol();
                    break;
            }
        }
        return false;
    }

    /**
     *
     */
    public void scanRequestMethod() throws Exception {
        if (skipLinearWhiteSpace() == 0) {
            return;
        }

        scanUntilSpaceOrTab(() -> {
            changeState(State.REQUEST_URI);
        });
    }

    /**
     *
     */
    public void scanRequestUri() throws Exception {
        if (skipLinearWhiteSpace() == 0) {
            return;
        }

        scanUntilSpaceOrTab(() -> {
            changeState(State.REQUEST_PROTOCOL);
        });
    }

    /**
     *
     */
    public void scanRequestProtocol() throws Exception {

        if (skipLinearWhiteSpace() == 0) {
            return;
        }

        byte b = 0;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == '\n') {
                find = true;
                break;
            }
        }

        int len = buffer.position() - head;
        if (find) {
            len--; // remove space
        }

        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            notifyComplete();
        }
    }


    /**
     * @throws IOException
     */
    private void scanUntilSpaceOrTab(final Runnable action) throws Exception {
        byte b;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b == ' ' || b == '\t') {
                find = true;
                break;
            }
        }

        int len = buffer.position() - head;
        if (find) {
            len--; // remove space
        }

        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            if (action != null) {
                action.run();
            }
            notifyComplete();
        }
    }

    /**
     * @return
     */
    private int skipLinearWhiteSpace() {
        byte b = 0;
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (b != ' ' && b != '\t') {
                // move back for next parsing
                buffer.position(buffer.position() - 1);
                break;
            }
        }

        return buffer.remaining();
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
    public void notifyComplete() throws Exception {
        if (listener != null) {
            listener.onComplete(state, output, this);
        }
    }

    /**
     *
     */
    public interface Listener {
        void onComplete(State state, OutputStream output, HttpRequestLineScanner scanner) throws Exception;
    }
}
