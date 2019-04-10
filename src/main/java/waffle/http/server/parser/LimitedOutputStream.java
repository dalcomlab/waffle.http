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
import java.util.function.Consumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class LimitedOutputStream extends DelegationOutputStream {

    private long limit;
    private final Consumer<Long> handler;

    /**
     * @param out
     * @param limit
     * @param handler
     */
    public LimitedOutputStream(final OutputStream out, final long limit, final Consumer<Long> handler) {
        super(out);
        this.limit = limit;
        this.handler = handler;
    }

    /**
     * @param b
     * @throws IOException
     */
    @Override
    public void write(int b) throws IOException {
        if (validate(1)) {
            out.write(b);
        }
    }

    /**
     * @param b
     * @param off
     * @param len
     * @throws IOException
     */
    @Override
    public void write(byte b[], int off, int len) throws IOException {
        if (validate(len)) {
            out.write(b, off, len);
        }
    }

    /**
     * @param len
     */
    private boolean validate(long len) {
        if (limit < len) {
            if (handler != null) {
                handler.accept(limit);
            }
            return false;
        }
        limit -= len;
        return true;
    }

}