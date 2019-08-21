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

public class HttpResponseHeaderSanitizeFilter implements HttpFilter {

    /**
     *
     * @param request
     * @return
     * @throws IOException
     */
    @Override
    public boolean filter(HttpRequest request) throws IOException {
        return true;
    }

    /**
     *
     * @param response
     * @return
     * @throws IOException
     */
    @Override
    public boolean filter(HttpResponse response) throws IOException {
        boolean removeContentLength = false;
        boolean removeTransferEncoding = false;

        HttpStatus status = response.getStatus();

        // https://tools.ietf.org/html/rfc7230#section-3.3.1
        // A server MUST NOT send a Transfer-Encoding header field in any
        // response with a status code of 1xx (Informational) or 204 (No
        // Content).  A server MUST NOT send a Transfer-Encoding header field in
        // any 2xx (Successful) response to a CONNECT request (Section 4.3.6 of
        // [RFC7231]).
        if (status.isInformation() || status == HttpStatus.NO_CONTENT) {
            removeContentLength = true;
            removeTransferEncoding = true;
        } else if (status == HttpStatus.RESET_CONTENT) {
            // Set Content-Length: 0
            // https://httpstatuses.com/205
            response.addHeader(HttpHeader.CONTENT_LENGTH, "0");
            removeTransferEncoding = true;
        }

        HttpRequest request = response.getRequest();
        if (request.getMethod().equals("connect") && status.isSuccess()) {
            removeTransferEncoding = true;
        }

        // https://tools.ietf.org/html/rfc7230#section-3.3.1
        // Transfer-Encoding was added in HTTP/1.1.  It is generally assumed
        // that implementations advertising only HTTP/1.0 support will not
        // understand how to process a transfer-encoded payload.  A client MUST
        // NOT send a request containing Transfer-Encoding unless it knows the
        // server will handle HTTP/1.1 (or later) requests; such knowledge might
        // be in the form of specific user configuration or by remembering the
        // version of a prior received response.  A server MUST NOT send a
        // response containing Transfer-Encoding unless the corresponding
        // request indicates HTTP/1.1 (or later).
        //if (request.isHttp09() || request.isHttp10()) {
        removeTransferEncoding = true;
        // }


        if (removeContentLength) {
            response.removeHeader(HttpHeader.CONTENT_LENGTH);
        }

        if (removeTransferEncoding) {
            response.removeHeader(HttpHeader.TRANSFER_ENCODING);
        }

        return false;
    }
}
