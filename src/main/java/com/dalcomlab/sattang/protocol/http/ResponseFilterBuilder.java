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

import com.dalcomlab.sattang.protocol.HttpResponse;
import com.dalcomlab.sattang.protocol.http.filters.ResponseChunkFilter;
import com.dalcomlab.sattang.protocol.http.filters.ResponseContentLengthFilter;
import com.dalcomlab.sattang.protocol.http.filters.ResponseHeaderCommitFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class ResponseFilterBuilder {
    /**
     * @param response
     * @return
     */
    public static ResponseFilter[] build(HttpResponse response) {
        List<ResponseFilter> filters = new ArrayList<>();
        String encoding = response.getHeader(HttpHeader.TRANSFER_ENCODING);
        if (encoding != null && encoding.equalsIgnoreCase("chunked")) {
            filters.add(new ResponseChunkFilter());
        } else {
            long contentLength = response.getContentLength();
            if (contentLength > 0) {
                filters.add(new ResponseContentLengthFilter(contentLength));
            } else {
                response.addHeader(HttpHeader.TRANSFER_ENCODING, "chunked");
                filters.add(new ResponseChunkFilter());
            }
        }
        filters.add(new ResponseHeaderCommitFilter(response));
        return filters.stream().toArray(ResponseFilter[]::new);
    }
}
