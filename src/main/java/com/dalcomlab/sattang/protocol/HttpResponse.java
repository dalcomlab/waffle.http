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

import com.dalcomlab.sattang.Reusable;
import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface HttpResponse extends Reusable {

    /**
     * Returns the {@link HttpRequest} object associated with this response.
     * <p>
     * The {@link HttpRequest} and {@link HttpResponse} objects can be cross-referenced
     * using the method below.
     * <ul>
     * <li>{@link HttpRequest#getResponse()}</li>
     * <li>{@link HttpResponse#getRequest()}</li>
     * </ul>
     *
     * @return {@link HttpRequest} object associated with this response.
     */
    HttpRequest getRequest();

    /**
     * Sets the {@link HttpRequest} object associated with this response.
     *
     * @param request
     */
    void setRequest(HttpRequest request);

    /**
     * @return
     */
    long getContentLength();

    /**
     * Adds the header in this response. The response can include multiple headers
     * with the same name.
     * <p>
     * The header can not be added if the response is committed.
     *
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @param value
     * @return
     */
    HttpResponse addHeader(String name, String value);


    /**
     * Removes the header in this response. The header can not be removed if
     * the response is committed.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @return
     */
    HttpResponse removeHeader(String name);


    /**
     * Returns the value of the specified response header as a <code>String</code>.
     * If the response did not include a header of the specified name, this method
     * returns <code>null</code>.
     * <p>
     * If there are multiple headers with the same name, this method returns the
     * first head in the response.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @return
     */
    String getHeader(String name);


    /**
     * Adds the trailer in this response. The response can include multiple headers
     * with the same name.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @param value
     * @return
     */
    HttpResponse addTrailer(String name, String value);


    /**
     * @return
     */
    Map<String, List<String>> getHeaderMaps();

    /**
     * Returns the status of this response as {@link HttpStatus} type.
     *
     * @return
     */
    HttpStatus getStatus();

    /**
     * Sets the status of this response as {@link HttpStatus} type.
     *
     * @param status
     * @return
     */
    HttpResponse setStatus(HttpStatus status);

    /**
     * @param file
     * @throws IOException
     */
    void sendFile(String file) throws IOException;


    /**
     * @param file
     * @throws IOException
     */
    default void sendFile(String file, String contentType) throws IOException {
        addHeader(HttpHeader.CONTENT_TYPE, contentType);
        sendFile(file);
    }


    /**
     *
     */
    void end() throws IOException;

    /**
     * This method can be used to determine if the request is in an async mode.
     *
     * @return <code>true</code> if the request is in an async mode, otherwise
     * returns <code>false</code>.
     */
    boolean isAsync();

    /**
     * Returns the {@link HttpOutputStream} object associated with this response.
     *
     * <p>
     * The returned  {@link HttpOutputStream} object can be used to access the
     * HTTP response body.
     *
     * <pre>
     *
     *     HTTP protocol
     * ┌────────────────────┐
     * │   response header  │
     * ├────────────────────┤
     * │                    │
     * │                    │
     * │   response body    │ <- access here
     * │                    │
     * │                    │
     * └────────────────────┘
     *
     * </pre>
     *
     * @return {@link HttpOutputStream} object associated with this response.
     */
    HttpOutputStream getOutputStream();


    /**
     * @return
     */
    boolean isCommitted();
}
