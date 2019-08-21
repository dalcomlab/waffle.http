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
package com.dalcomlab.sattang.protocol;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public abstract class HttpInputStream extends InputStream {

    /**
     *
     */
    private final static HttpInputStream EMPTY = new HttpInputStream() {

        @Override
        public int read() throws IOException {
            return -1;
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setReadListener(HttpReadListener listener) throws Exception {

        }
    };

    /**
     * @return
     */
    public final static HttpInputStream empty() {
        return EMPTY;
    }


    /**
     * This method can be used to determine if data can be read without blocking.
     *
     * @return <code>true</code> if a read to this <code>HttpInputStream</code>
     * will succeed, otherwise returns <code>false</code>.
     */
    public abstract boolean isReady();

    /**
     * Sets the {@link HttpReadListener} to invoke when it is possible to read
     * The <code>IllegalStateException</code> can be thrown if one of the following
     * conditions is true.
     * <ul>
     * <li>the associated request is neither upgraded nor the async started
     * <li>setReadListener is called more than once within the scope of the same request.
     * </ul>
     *
     * @param listener
     * @throws Exception
     */
    public abstract void setReadListener(HttpReadListener listener) throws Exception;


}
