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
import java.util.function.Consumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpFormScanner {

    private OutputStream output;
    private Listener listener;
    private ByteBuffer buffer;
    private State state;

    /**
     * @param output
     * @param listener
     */
    public HttpFormScanner(final OutputStream output, final Listener listener) {
        state = State.NAME;
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
     * The method scans bytes to parse http form.
     *
     * @throws IOException
     */
    public void scan(final ByteBuffer buffer) throws Exception {
        this.buffer = buffer;

        // Each state should consumes a sequence of bytes to improve parsing performance.
        while (buffer.hasRemaining()) {
            switch (state) {
                case NAME:
                    scanName();
                    break;
                case VALUE:
                    scanValue();
                    break;
            }
        }
    }

    /**
     * This method scans bytes until the '=' or '&' character is encountered for parsing
     * name field in http form.
     *
     * @throws IOException
     */
    private void scanName() throws Exception {
        scanUntil(new char[]{'=', '&'}, (Character stop) -> {
            if (stop == '=') {
                changeState(State.VALUE);
            }

            // This case can happen if the name have no value.
            // a&b&c
            if (stop == '&') {
                changeState(State.NAME);
            }
        });
    }

    /**
     * This method scans bytes until the '&' character is encountered for parsing
     * value field in http form.
     *
     * @throws IOException
     */
    private void scanValue() throws Exception {
        scanUntil(new char[]{'&'}, (Character stop) -> {
            changeState(State.NAME);
        });
    }

    /**
     * This mehtod scans bytes until one of the given separators is encountered.
     *
     * @throws IOException
     */
    private void scanUntil(final char[] separators, final Consumer<Character> action) throws Exception {
        byte b = 0;
        boolean find = false;
        int head = buffer.position();
        while (buffer.hasRemaining()) {
            b = buffer.get();
            if (oneOf(separators, (char) b)) {
                find = true;
                break;
            }
        }

        int len = buffer.position() - head;
        if (find) {
            len--; // remove separator
        }

        if (len > 0)
            write(buffer.array(), head, len);

        if (find) {
            if (action != null) {
                action.accept((char) b);
            }
            notifyComplete();
        }
    }

    /**
     * @param separators
     * @param c
     * @return
     */
    private boolean oneOf(final char[] separators, char c) {
        for (int i = 0; i < separators.length; i++) {
            if (separators[i] == c) {
                return true;
            }
        }
        return false;
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
    private void write(byte b[], int off, int len) throws IOException {
        if (output != null) {
            output.write(b, off, len);
        }
    }

    /**
     *
     */
    public void notifyComplete() throws Exception {
        if (listener != null) {
            listener.onComplete(this.state, output, this);
        }
    }

    public enum State {
        NAME,
        VALUE
    }

    /**
     *
     */
    public interface Listener {
        void onComplete(State state, OutputStream output, HttpFormScanner scanner) throws Exception;
    }
}
