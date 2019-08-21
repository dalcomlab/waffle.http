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
package com.dalcomlab.sattang.protocol.http;

import java.util.HashMap;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public final class HttpProtocol {

    public static final HttpProtocol HTTP09 = new HttpProtocol("HTTP/0.9");
    public static final HttpProtocol HTTP10 = new HttpProtocol("HTTP/1.0");
    public static final HttpProtocol HTTP11 = new HttpProtocol("HTTP/1.1");
    public static final HttpProtocol HTTP20 = new HttpProtocol("HTTP/2.0");
    private static final Map<String, HttpProtocol> map = new HashMap<>();

    static {
        map.put(HTTP09.name(), HTTP09);
        map.put(HTTP10.name(), HTTP10);
        map.put(HTTP11.name(), HTTP11);
        map.put(HTTP20.name(), HTTP20);
    }

    private final String protocol;

    /**
     * @param protocol
     */
    private HttpProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * @param protocol
     * @return
     */
    public static HttpProtocol valueOf(String protocol) {
        if (protocol == null || protocol.isEmpty()) {
            return null;
        }
        return map.get(protocol.trim().toUpperCase());
    }

    /**
     * Returns the name of the protocol("HTTP/1.1").
     *
     * @return
     */
    public String name() {
        return protocol;
    }
}
