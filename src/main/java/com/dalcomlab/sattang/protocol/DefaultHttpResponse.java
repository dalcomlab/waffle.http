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

import com.dalcomlab.sattang.protocol.http.HttpHeader;
import com.dalcomlab.sattang.protocol.http.HttpStatus;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class DefaultHttpResponse implements HttpResponse {
    protected HttpHeader header = new HttpHeader(true);
    protected HttpStatus status = HttpStatus.OK;
    protected HttpRequest request = null;
    protected HttpOutputStream stream;
    protected boolean committed = false;

    /**
     *
     */
    public DefaultHttpResponse(HttpOutputStream stream) {
        this.stream = stream;
    }

    /**
     * Initializes the object for reuse.
     */
    @Override
    public void reuse() {
        header.clear();
        status = HttpStatus.OK;
        committed = false;
    }

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
    @Override
    public HttpRequest getRequest() {
        return request;
    }

    /**
     * Sets the {@link HttpRequest} object associated with this response.
     *
     * @param request
     */
    @Override
    public void setRequest(HttpRequest request) {
        this.request = request;
    }


    /**
     * @return
     */
    public long getContentLength() {
        return header.getContentLength();
    }

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
    @Override
    public DefaultHttpResponse addHeader(String name, String value) {
        if (isCommitted()) {
            return this;
        }

        header.addHeader(name, value);
        return this;
    }

    /**
     * Removes the header in this response. The header can not be removed if
     * the response is committed.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @return
     */
    @Override
    public HttpResponse removeHeader(String name) {
        if (isCommitted()) {
            return this;
        }
        header.removeHeader(name);
        return this;
    }

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
    @Override
    public String getHeader(String name) {
        return header.getHeader(name);
    }

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
    @Override
    public DefaultHttpResponse addTrailer(String name, String value) {
        return this;
    }


    /**
     * @return
     */
    @Override
    public Map<String, List<String>> getHeaderMaps() {
        return header.getHeaderMaps();
    }

    /**
     * Sets the status of this response as {@link HttpStatus} type.
     *
     * @param status
     * @return
     */
    @Override
    public HttpResponse setStatus(HttpStatus status) {
        if (isCommitted()) {
            return this;
        }
        this.status = status;
        return this;
    }

    /**
     * Returns the status of this response as {@link HttpStatus} type.
     *
     * @return
     */
    @Override
    public HttpStatus getStatus() {
        return status;
    }

    /**
     * This method can be used to determine if the request is in an async mode.
     *
     * @return <code>true</code> if the request is in an async mode, otherwise
     * returns <code>false</code>.
     */
    @Override
    public boolean isAsync() {
        return getRequest().isAsync();
    }


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
    @Override
    public HttpOutputStream getOutputStream() {
        return stream;
    }


    /**
     * @param file
     * @throws IOException
     */
    @Override
    public void sendFile(String file) throws IOException {
        Path path = FileSystems.getDefault().getPath(file);
        byte[] images = Files.readAllBytes(path);
        OutputStream output = getOutputStream();
        if (output != null) {
            output.write(images);
            output.flush();
        }
    }

    /**
     *
     */
    @Override
    public void end() throws IOException {
        this.stream.flush();
    }

    /**
     * @return
     */
    @Override
    public boolean isCommitted() {
        return committed;
    }

}
