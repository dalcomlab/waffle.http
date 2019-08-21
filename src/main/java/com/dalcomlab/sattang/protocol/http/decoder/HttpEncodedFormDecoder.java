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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpEncodedFormDecoder implements HttpFormDecoder<BiConsumer<String, String>> {
    private HttpQueryStringDecoder decoder = new HttpQueryStringDecoder();

    /**
     * Creates new instance
     */
    public HttpEncodedFormDecoder() {
    }

    /**
     * @param parameters
     */
    public HttpEncodedFormDecoder(Map<String, List<String>> parameters) {
        listen(parameters);
    }


    /**
     * @param listener
     */
    @Override
    public HttpEncodedFormDecoder listen(BiConsumer<String, String> listener) {
        decoder.listen(listener);
        return this;
    }

    /**
     * @param parameters
     */
    public void listen(Map<String, List<String>> parameters) {
        final BiConsumer<String, String> listener = (name, value) -> {
            if (!parameters.containsKey(name)) {
                parameters.put(name, new ArrayList<>());
            }
            parameters.get(name).add(value);
        };

        listen(listener);
    }


    /**
     * Parses
     *
     * @param buffer
     * @return
     */
    @Override
    public boolean decode(ByteBuffer buffer) throws Exception {
        return decoder.decode(buffer);
    }

    /**
     * Closes the parser.
     *
     * @return
     */
    public void close() throws Exception {
        decoder.close();
    }

}
