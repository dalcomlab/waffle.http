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

import com.dalcomlab.sattang.protocol.HttpRequest;
import com.dalcomlab.sattang.protocol.HttpResponse;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpUpgradeHandler implements HttpHandler {

    /**
     *
     */
    public HttpUpgradeHandler() {

    }

    /**
     *
     * @param request
     * @param response
     */
    @Override
    public void handle(HttpRequest request, HttpResponse response) {
        // RFC 2616 does not limit HTTP upgrade to GET requests but the Java
        // WebSocket spec 1.0, section 8.2 implies such a limitation and RFC
        // 6455 section 4.1 requires that a WebSocket Upgrade uses GET.
        // - from tomcat
        if(!request.getMethod().equals("GET")) {
            return;
        }

        String connection = request.getHeader(HttpHeader.CONNECTION);
        if (connection == null || !connection.equals("Upgrade")) {
            return;
        }

        String upgrade = request.getHeader(HttpHeader.UPGRADE);
        if (upgrade == null) {
            return;
        }

        if (upgrade.equals("websocket")) {
            return;
        }
    }
}
