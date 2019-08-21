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

import java.io.IOException;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpSSLAuthFilter extends HttpAuthFilter {

    /**
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public boolean filter(HttpRequest request) throws IOException {
        return false;
    }

    /**
     *
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    public boolean filter(HttpResponse response) throws IOException {
        return false;
    }
}
