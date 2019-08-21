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
import com.dalcomlab.sattang.protocol.http.HttpMethod;
import com.dalcomlab.sattang.protocol.http.HttpProtocol;
import com.dalcomlab.sattang.protocol.http.decoder.HttpEncodedFormDecoder;
import com.dalcomlab.sattang.protocol.http.decoder.HttpMultiPartFormDecoder;
import com.dalcomlab.sattang.protocol.http.form.HttpForm;

import java.io.InputStream;
import java.util.*;

/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class DefaultHttpRequest implements HttpRequest {
    private HttpMethod method = null;
    private HttpProtocol protocol = null;
    private HttpResponse response = null;
    private String uri;
    private String scheme;
    private HttpHeader header = new HttpHeader(true);
    private Map<String, List<String>> parameters = new HashMap<>();
    private List<HttpForm> httpForms = null;
    private HttpInputStream stream;
    private boolean usingStream = false;
    private boolean usingParser = false;
    private Map<String, Object> attributes = new HashMap<>();

    /**
     * @param stream
     */
    public DefaultHttpRequest(InputStream stream) {
        // this.stream = stream;
    }

    /**
     * @param stream
     */
    public DefaultHttpRequest(HttpInputStream stream) {
        this.stream = stream;
    }

    /**
     * Initializes the object for reuse.
     */
    @Override
    public void reuse() {
        method = null;
        protocol = null;
        uri = "";
        scheme = "";
        header.clear();
        parameters.clear();
        httpForms = null;
        usingStream = false;
        usingParser = false;
    }

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
    @Override
    public HttpResponse getResponse() {
        return response;
    }

    /**
     * Sets the {@link HttpResponse} object associated with this request.
     *
     * @param response
     */
    @Override
    public void setResponse(HttpResponse response) {
        this.response = response;
    }

    /**
     * @param name
     * @return
     */
    @Override
    public Object getAttribute(String name) {
        return attributes.get(name);
    }

    /**
     * @param name
     * @param value
     */
    @Override
    public void setAttribute(String name, Object value) {
        attributes.put(name, value);
    }

    /**
     * Returns the name of the HTTP scheme. For example, "http" or "https".
     *
     * @return
     */
    @Override
    public String getScheme() {
        return this.scheme;
    }

    /**
     * Sets the name of the HTTP scheme.
     *
     * @param scheme
     */
    @Override
    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    /**
     * Returns the name of the HTTP method. For example, GET, POST, or PUT.
     *
     * @return {@link HttpMethod} of this request.
     */
    @Override
    public HttpMethod getMethod() {
        return method;
    }

    /**
     * Sets the name of the HTTP method. This method return a <code>null</code>
     * if the given method name is invalid.
     *
     * @param method
     * @return {@link HttpMethod} of this request.
     */
    @Override
    public HttpMethod setMethod(String method) {
        this.method = HttpMethod.valueOf(method);
        return this.method;
    }

    /**
     * Returns the URI of the HTTP request. The URI does not include the query
     * string.
     *
     * @return {@code String} the URI of this request.
     */
    @Override
    public String getUri() {
        return this.uri;
    }

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
    @Override
    public void setUri(String uri) {
        if (uri.startsWith("//")) { //ajp style
            this.uri = uri.substring(1);
        } else {
            this.uri = uri;
        }
    }

    /**
     * Returns the name of the HTTP protocol. For example, HTTP/0.9, HTTP/1.0 or
     * HTTP/1.1
     *
     * @return {@link HttpProtocol} of this request.
     */
    @Override
    public HttpProtocol getProtocol() {
        return protocol;
    }

    /**
     * Sets the name of the HTTP protocol. This method returns a <code>null</code>
     * if the given protocol name is invalid.
     *
     * @param protocol
     * @return {@link HttpProtocol} of this request.
     */
    @Override
    public HttpProtocol setProtocol(String protocol) {
        this.protocol = HttpProtocol.valueOf(protocol);
        return this.protocol;
    }

    /**
     * Returns the server name.
     *
     * @return
     */
    @Override
    public String getServerName() {
        String host = getHeader(HttpHeader.HOST);
        if (host == null) {
            host = ""; // getDestinationAddress().getHostString();
        } else {
            if (host.startsWith("[")) {
                host = host.substring(1, host.indexOf(']'));
            } else if (host.indexOf(':') != -1) {
                host = host.substring(0, host.indexOf(':'));
            }
        }
        return host;
    }

    /**
     * Returns the server port.
     *
     * @return
     */
    @Override
    public int getServerPort() {
        String host = getHeader(HttpHeader.HOST);
        if (host != null) {
            final int colon;
            if (host.startsWith("[")) {
                colon = host.indexOf(':', host.indexOf(']'));
            } else {
                colon = host.indexOf(':');
            }
            if (colon != -1) {
                try {
                    return Integer.parseInt(host.substring(colon + 1));
                } catch (NumberFormatException ignore) {
                }
            }

            if (scheme != null) {
                if (scheme.equalsIgnoreCase("https")) {
                    return 443;
                } else if (scheme.equalsIgnoreCase("http")) {
                    return 80;
                }
            }
        }

        return 0;
    }

    /**
     * Returns the MIME type of the body of the request, or <code>null</code>
     * if the type is not known.
     *
     * @return
     */
    @Override
    public String getContentType() {
        return header.getContentType();
    }

    /**
     * Returns the length of the request body in bytes. This method will return -1
     * if the length is not known.
     *
     * @return
     */
    @Override
    public long getContentLength() {
        return header.getContentLength();
    }

    /**
     * Adds the header in this request. The request can include multiple headers
     * with the same name.
     * <p>
     * The header name is case insensitive.
     *
     * @param name
     * @param value
     */
    @Override
    public DefaultHttpRequest addHeader(String name, String value) {
        header.addHeader(name, value);
        return this;
    }

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
    @Override
    public String getHeader(String name) {
        return header.getHeader(name);
    }


    /**
     * Returns the names of the headers of this request. If the request
     * has no headers, this method returns an empty set.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this request.
     */
    @Override
    public Set<String> getHeaderNames() {
        return header.getHeaderNames();
    }

    /**
     * Adds the parameter.
     *
     * @param name
     * @param value
     */
    @Override
    public DefaultHttpRequest addParameter(String name, String value) {
        if (name == null || value == null) {
            return this;
        }

        List<String> values;
        if (parameters.containsKey(name)) {
            values = parameters.get(name);
        } else {
            values = new ArrayList<>();
            parameters.put(name, values);
        }
        values.add(value);
        return this;
    }

    /**
     * Returns the value of the given parameter name. If there is no
     * parameter in this request, returns the <code>null</code>.
     *
     * <p>
     * This method internally call {@link #getInputStream()} method. After
     * calling this method, you can not access the HTTP body.
     *
     * @param name
     * @return
     * @see #getParameterMap()
     * @see #getHttpForms()
     * @see #getInputStream()
     */
    @Override
    public String getParameter(String name) throws Exception {
        addParameterFromEncodedForm();
        if (!parameters.containsKey(name)) {
            return null;
        }
        return parameters.get(name).get(0);
    }


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
    @Override
    public Map<String, String[]> getParameterMap() throws Exception {
        addParameterFromEncodedForm();
        Map<String, String[]> maps = new HashMap<>(parameters.size());
        parameters.forEach((name, values) -> {
            maps.put(name, values.stream().toArray(String[]::new));
        });
        return maps;
    }

    /**
     * Parses the encoded form. This method call the {@link #addParameter(String, String)}
     * method for adding parameter.
     *
     * @throws Exception
     */
    private void addParameterFromEncodedForm() throws Exception {
        // If the getInputStream() method be called before calling this method,
        // we can not proceed.
        if (usingStream) {
            return;
        }

        if (usingParser) {
            return;
        }
        usingParser = true;

        InputStream input = getInputStream();
        if (input == null) {
            return;
        }

        if (method == null || method != HttpMethod.POST) {
            return;
        }


        String contentType = getContentType();
        if (contentType == null || !contentType.equals("application/x-www-form-urlencoded")) {
            return;
        }

        HttpEncodedFormDecoder decoder = new HttpEncodedFormDecoder();
        decoder.listen(this::addParameter);
        decoder.decode(input);
        decoder.close();
    }

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
    @Override
    public List<HttpForm> getHttpForms() throws Exception {
        if (httpForms != null) {
            return Collections.unmodifiableList(httpForms);
        }

        httpForms = Collections.emptyList();

        // If the getInputStream() method be called before calling this method,
        // we can not proceed.
        if (usingStream) {
            return httpForms;
        }

        usingParser = true;

        HttpInputStream input = getInputStream();
        if (input == null) {
            return httpForms;
        }

        if (method == null || method != HttpMethod.POST) {
            return httpForms;
        }

        String contentType = getContentType();
        // TODO : need to improve.
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            return httpForms;
        }

        byte[] boundary = header.getBoundary();
        if (boundary == null) {
            return httpForms;
        }

        httpForms = new ArrayList<>();
        HttpMultiPartFormDecoder decoder = new HttpMultiPartFormDecoder(boundary);
        decoder.listen(httpForms);
        decoder.decode(input);
        decoder.close();

        return Collections.unmodifiableList(httpForms);
    }

    /**
     * This method can be used to determine if the request is in an async mode.
     *
     * @return <code>true</code> if the request is in an async mode, otherwise
     * returns <code>false</code>.
     */
    @Override
    public boolean isAsync() {
        return false;
    }


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
    @Override
    public HttpInputStream getInputStream() {
        usingStream = true;
        return stream;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return header.toString();
    }

}
