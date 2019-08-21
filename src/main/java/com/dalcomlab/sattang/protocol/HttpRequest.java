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
import com.dalcomlab.sattang.protocol.http.HttpMethod;
import com.dalcomlab.sattang.protocol.http.HttpProtocol;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public interface HttpRequest extends Reusable {

    /**
     * Returns the {@link HttpResponse} object associated with this request.
     * <p>
     * The {@link HttpRequest} and {@link HttpResponse} objects can be cross-referenced
     * using the method below.
     * <ul>
     * <li>{@link HttpRequest#getResponse()}</li>
     * <li>{@link HttpResponse#getRequest()}</li>
     * </ul>
     *
     * @return {@link HttpResponse} object associated with this request.
     */
    HttpResponse getResponse();

    /**
     * Sets the {@link HttpResponse} object associated with this request.
     *
     * @param response
     */
    void setResponse(HttpResponse response);

    /**
     * Returns the value of the named attribute as an <code>Object</code>,
     * or <code>null</code> if no attribute of the given name exists.
     *
     * @param name
     * @return
     */
    Object getAttribute(String name);

    /**
     * Sets the attribute in this request.
     *
     * @param name
     * @param value
     */
    void setAttribute(String name, Object value);


    /**
     * Returns the name of the HTTP scheme. For example, "http" or "https".
     *
     * @return
     */
    String getScheme();

    /**
     * Sets the name of the HTTP scheme.
     *
     * @param scheme
     */
    void setScheme(String scheme);


    /**
     * Returns the name of the HTTP method. For example, GET, POST, or PUT.
     *
     * @return {@link HttpMethod} of this request.
     */
    HttpMethod getMethod();

    /**
     * Sets the name of the HTTP method. This method return a <code>null</code>
     * if the given method name is invalid.
     *
     * @param method
     * @return {@link HttpMethod} of this request.
     */
    HttpMethod setMethod(String method);

    /**
     * Returns the URI of the HTTP request. The URI does not include the query
     * string.
     *
     * @return {@code String} the URI of this request.
     */
    String getUri();

    /**
     * Sets the URI of the HTTP request. The URI does ont include the query
     * string. The caller must separate the query string from the URI before
     * calling this method.
     * <p>
     * This method does not check if the URI contains a query string.
     * <p>
     * For example,
     * /context/test.jsp?name=Yoo -> /context/test.jsp
     *
     * @param uri
     */
    void setUri(String uri);


    /**
     * Returns the name of the HTTP protocol. For example, HTTP/0.9, HTTP/1.0 or
     * HTTP/1.1
     *
     * @return {@link HttpProtocol} of this request.
     */
    HttpProtocol getProtocol();

    /**
     * Sets the name of the HTTP protocol. This method returns a <code>null</code>
     * if the given protocol name is invalid.
     *
     * @param protocol
     * @return {@link HttpProtocol} of this request.
     */
    HttpProtocol setProtocol(String protocol);


    /**
     * Returns the server name.
     *
     * @return
     */
    String getServerName();


    /**
     * Returns the server port.
     *
     * @return
     */
    int getServerPort();

    /**
     * Returns the MIME type of the body of the request, or <code>null</code>
     * if the type is not known.
     *
     * @return
     */
    String getContentType();

    /**
     * Returns the length of the request body in bytes. This method will return -1
     * if the length is not known.
     *
     * @return
     */
    long getContentLength();

    /**
     * Adds the header in this request. The request can include multiple headers
     * with the same name.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @param value
     */
    HttpRequest addHeader(String name, String value);


    /**
     * Returns the value of the specified request header as a <code>String</code>.
     * If the request did not include a header of the specified name, this method
     * returns <code>null</code>.
     * <p>
     * If there are multiple headers with the same name, this method returns the
     * first head in the request.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @return
     */
    String getHeader(String name);


    /**
     * Returns the names of the headers of this request. If the request
     * has no headers, this method returns an empty set.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this request.
     */
    Set<String> getHeaderNames();

    /**
     * Adds the parameter.
     *
     * @param name
     * @param value
     */
    HttpRequest addParameter(String name, String value);

    /**
     * Returns the value of the given parameter name. If there is no
     * parameter in this request, returns the <code>null</code>.
     *
     * <p>
     * This method internally call {@link #getInputStream()} method.
     * After calling this method, you can not access the HTTP body.
     *
     * @param name
     * @return
     * @see #getParameterMap()
     * @see #getHttpForms()
     * @see #getInputStream()
     */
    String getParameter(String name) throws Exception;


    /**
     * Returns the parameters as {@code Map<String, String[]} type. If
     * the request has no parameters, this method returns an empty map.
     *
     * <p>
     * This method internally call {@link #getInputStream()} method. After
     * calling this method, you can not access the HTTP body.
     *
     * @return
     * @see #getHttpForms()
     * @see #getInputStream()
     */
    Map<String, String[]> getParameterMap() throws Exception;


    /**
     * Returns the forms the HTTP request as the {@link HttpForm}. If
     * the request has no forms, this method returns an empty list.
     * <p>
     * The HTTP form exists only if the content-type is 'multipart/form-data'
     * and the HTTP method is 'POST'.
     * <p>
     * This method internally call {@link #getInputStream()} method. After
     * calling this method, you can not access the HTTP body.
     *
     * @return
     * @see #getInputStream()
     */
    List<HttpForm> getHttpForms() throws Exception;

    /**
     * This method can be used to determine if the request is in an async mode.
     *
     * @return <code>true</code> if the request is in an async mode, otherwise
     * returns <code>false</code>.
     */
    boolean isAsync();


    /**
     * Returns the {@link HttpInputStream} object associated with this request.
     * <p>
     * The returned  {@link HttpInputStream} object can be used to access the
     * HTTP request body.
     *
     * <pre>
     *
     *     HTTP protocol
     * ┌────────────────────┐
     * │   request header   │
     * ├────────────────────┤
     * │                    │
     * │                    │
     * │   request body     │ <- access here
     * │                    │
     * │                    │
     * └────────────────────┘
     *
     * </pre>
     * <p>
     * The {@link #getParameter} and {@link #getHttpForms} methods internally call
     * this method. Therefore, after calling these methods, no longer be able to
     * access the HTTP body by using this method.
     *
     * @return {@link HttpInputStream} object associated with this request.
     * @see #getParameter(String)
     * @see #getParameterMap()
     * @see #getHttpForms()
     */
    HttpInputStream getInputStream();
}
