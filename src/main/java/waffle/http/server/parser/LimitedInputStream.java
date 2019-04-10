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
import java.io.InputStream;
import java.util.function.Consumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class LimitedInputStream extends DelegationInputStream {

    private long limit;
    private final Consumer<Long> handler;

    /**
     * @param in
     * @param limit
     */
    public LimitedInputStream(final InputStream in, final long limit, final Consumer<Long> handler) {
        super(in);
        this.limit = limit;
        this.handler = handler;
    }

    /**
     * @return
     * @throws IOException
     */
    @Override
    public int read() throws IOException {
        if (validate(1)) {
            return in.read();
        }
        return -1;
    }

    /**
     * @param b
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte[] b) throws IOException {
        if (validate(b.length)) {
            return in.read(b);
        }
        return -1;
    }

    /**
     * @param b
     * @param off
     * @param len
     * @return
     * @throws IOException
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        if (validate(len)) {
            return in.read(b, off, len);
        }
        return -1;
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
