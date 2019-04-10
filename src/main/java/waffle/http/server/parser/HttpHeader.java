/*
 * Copyright WAFFLE. 2019
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
package waffle.http.server.parser;


import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;


/**
 * @author ByungChang Yoo (dalcomlab@gmail.com)
 */
public class HttpHeader {

    public enum HttpMethod {
        GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE
    }

    /**
     * The HTTP header field names. This code is copied from a Spring framework.
     */
    public static final String ACCEPT = "Accept";
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ACCEPT_ENCODING = "Accept-Encoding";
    public static final String ACCEPT_LANGUAGE = "Accept-Language";
    public static final String ACCEPT_RANGES = "Accept-Ranges";
    public static final String ACCESS_CONTROL_ALLOW_CREDENTIALS = "Access-Control-Allow-Credentials";
    public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
    public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
    public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
    public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
    public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
    public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";
    public static final String AGE = "Age";
    public static final String ALLOW = "Allow";
    public static final String AUTHORIZATION = "Authorization";
    public static final String CACHE_CONTROL = "Cache-Control";
    public static final String CONNECTION = "Connection";
    public static final String CONTENT_ENCODING = "Content-Encoding";
    public static final String CONTENT_DISPOSITION = "Content-Disposition";
    public static final String CONTENT_LANGUAGE = "Content-Language";
    public static final String CONTENT_LENGTH = "Content-Length";
    public static final String CONTENT_LOCATION = "Content-Location";
    public static final String CONTENT_RANGE = "Content-Range";
    public static final String CONTENT_TYPE = "Content-Type";
    public static final String COOKIE = "Cookie";
    public static final String DATE = "Date";
    public static final String ETAG = "ETag";
    public static final String EXPECT = "Expect";
    public static final String EXPIRES = "Expires";
    public static final String FROM = "From";
    public static final String HOST = "Host";
    public static final String IF_MATCH = "If-Match";
    public static final String IF_MODIFIED_SINCE = "If-Modified-Since";
    public static final String IF_NONE_MATCH = "If-None-Match";
    public static final String IF_RANGE = "If-Range";
    public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";
    public static final String LAST_MODIFIED = "Last-Modified";
    public static final String LINK = "Link";
    public static final String LOCATION = "Location";
    public static final String MAX_FORWARDS = "Max-Forwards";
    public static final String ORIGIN = "Origin";
    public static final String PRAGMA = "Pragma";
    public static final String PROXY_AUTHENTICATE = "Proxy-Authenticate";
    public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String RANGE = "Range";
    public static final String REFERER = "Referer";
    public static final String RETRY_AFTER = "Retry-After";
    public static final String SERVER = "Server";
    public static final String SET_COOKIE = "Set-Cookie";
    public static final String SET_COOKIE2 = "Set-Cookie2";
    public static final String TE = "TE";
    public static final String TRAILER = "Trailer";
    public static final String TRANSFER_ENCODING = "Transfer-Encoding";
    public static final String UPGRADE = "Upgrade";
    public static final String USER_AGENT = "User-Agent";
    public static final String VARY = "Vary";
    public static final String VIA = "Via";
    public static final String WARNING = "Warning";
    public static final String WWW_AUTHENTICATE = "WWW-Authenticate";

    private HttpMethod method;
    private String url;
    private Map<String, String> headers = new LinkedHashMap<>();

    /**
     * @param headers
     */
    public HttpHeader(final Map<String, String> headers) {
        assert (headers != null);
        headers.forEach((name, value) -> {
            // make all header names a lower case
            this.headers.put(name.toLowerCase(), value);
        });
    }

    /**
     * Returns the url in this http header.
     *
     * @return
     */
    public String getUrl() {
        return url;
    }

    /**
     * Sets the url in this http header.
     *
     * @param url
     */
    public void setUrl(String url) {
        if (url == null || url.length() == 0) {
            return;
        }

        this.url = url;
    }

    /**
     * Returns the type of the method in this http header.
     *
     * @return
     */
    public HttpMethod getMethod() {
        return this.method;
    }

    /**
     * Set the type of the method in this http header.
     *
     * @param method
     */
    public void setMethod(String method) {
        if (method == null || method.length() == 0) {
            return;
        }

        for (HttpMethod enumMethod : HttpMethod.values()) {
            if (enumMethod.name().equalsIgnoreCase(method)) {
                this.method = enumMethod;
            }
        }
    }


    /**
     * Set the type of the method in this http method.
     *
     * @param method
     */
    public void setMethod(HttpMethod method) {
        if (method == null) {
            return;
        }
        this.method = method;
    }


    /**
     * Returns the content type in this http header.
     *
     * @return
     */
    public String getContentType() {
        return getHeader(CONTENT_TYPE);
    }

    /**
     * Set the length of the body in bytes in this http header.
     *
     * @param length
     */
    public void setContentLength(long length) {
        setHeader(CONTENT_LENGTH, Long.toString(length));
    }

    /**
     * Returns the content disposition in this http header.
     *
     * @return
     */
    public String getContentDisposition() {
        return getHeader(CONTENT_DISPOSITION);
    }

    /**
     * Sets the content disposition in this http header.
     *
     * @param contentDisposition
     */
    public void setContentDisposition(String contentDisposition) {
        setHeader(CONTENT_DISPOSITION, contentDisposition);
    }

    /**
     * Return the length of the body in bytes in this http header.
     *
     * @return the length of the body in bytes, or -1 if the content-length is unknown.
     */
    public long getContentLength() {
        String value = getHeader(CONTENT_LENGTH);
        if (value == null) {
            return -1;
        }
        return Long.parseLong(value);
    }


    /**
     * Set the transfer encoding in bytes in this http header.
     *
     * @param encoding
     */
    public void setTransferEncoding(String encoding) {
        setHeader(TRANSFER_ENCODING, encoding);
    }


    /**
     * Returns the transfer encoding in this http header.
     *
     * @return
     */
    public String getTransferEncoding() {
        return getHeader(TRANSFER_ENCODING);
    }


    /**
     * Returns the boundary in the content-type header.
     *
     * @return
     */
    public byte[] getBoundary() {
        String boundary = getParameter(CONTENT_TYPE, "boundary");
        if (boundary == null) {
            return null;
        }
        return boundary.getBytes();
    }

    /**
     * Returns the names of the headers of this http header or multipart header.
     *
     * @return a (possibly empty) <code>Set</code> of the names
     * of the headers of this multipart.
     */
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    /**
     * Returns the header's value, or null if the header does not exist.
     *
     * @param name
     * @return
     */
    public String getHeader(String name) {
        if (name == null || name.length() == 0) {
            return null;
        }

        // Header names are not case sensitive.
        // From RFC 2616 - "Hypertext Transfer Protocol -- HTTP/1.1", Section 4.2, "Message Headers":
        // Each header field consists of a name followed by a colon (":") and the field value.
        // Field names are case-insensitive

        return headers.get(name.toLowerCase());
    }

    /**
     * Changes the header's value. The header is removed if the value is null.
     *
     * @param name
     * @param value
     */
    public void setHeader(String name, String value) {
        if (name == null || name.length() == 0) {
            return;
        }

        if (value != null) {
            headers.put(name, value);
        } else {
            removeHeader(name);
        }
    }

    /**
     * @param name
     */
    public void removeHeader(String name) {
        if (name == null || name.length() == 0) {
            return;
        }

        headers.remove(name);
    }

    /**
     * Returns the header's attribute, or, the header's value if the given attribute
     * is null, or null if the header does not exist.
     *
     * @param name
     * @param parameter
     * @return
     */
    public String getParameter(String name, String parameter) {
        String header = getHeader(name);
        if (header == null || header.length() == 0) {
            return null;
        }


        HttpParameterParser parser = new HttpParameterParser(null, (value) -> {
            return new MimeHeaderDecoder().decode(value);
        });

        Map<String, String> parameters = parser.parse(header, ';');
        if (parameters == null) {
            return null;
        }

        return parameters.get(parameter);
    }

}
